package main;

import customs.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class News implements Activity {
    private KPanel present;
    private KScrollPane scrollPane;
    private KButton refreshButton;
    private KLabel accessLabel;
    private static String accessTime;
    private boolean isFirstView;
    private static final ArrayList<NewsSavior> NEWS_DATA = new ArrayList<NewsSavior>() {
        @Override
        public boolean contains(Object o) {
            for (NewsSavior savior : NEWS_DATA) {
                if (savior.equals((NewsSavior) o)) {
                    return true;
                }
            }
            return false;
        }
    };//unlike many of its kind, this does not explicitly delete.
    public static final String HOME_SITE = "https://www.utg.edu.gm/";
    public static final String NEWS_SITE = "https://www.utg.edu.gm/category/news/";


    public News() {
        accessLabel = KLabel.getPredefinedLabel("Last accessed: ", SwingConstants.LEFT);
        accessLabel.setStyle(KFontFactory.createPlainFont(15), Color.RED);
        accessTime = "Unknown";
        accessLabel.setText(accessTime);

        refreshButton = new KButton("Refresh");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.setCursor(MComponent.HAND_CURSOR);
        refreshButton.setMnemonic(KeyEvent.VK_F);
        refreshButton.addActionListener(e-> new Thread(()-> packAll(true)).start());

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(new KLabel("News Feeds", KFontFactory.bodyHeaderFont())), BorderLayout.WEST);
        northPanel.add(new KPanel(refreshButton), BorderLayout.EAST);

        present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 20), accessLabel));//then only add using addPenultimate(Component)

        isFirstView = true;
        scrollPane = new KScrollPane(present);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(northPanel, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        Board.addCard(activityPanel, "News");

        if (Dashboard.isFirst()) {
            new Thread(()-> packAll(false)).start();
        } else {
            Board.postProcesses.add(this::pushNews);
        }
    }

    @Override
    public void answerActivity() {
        Board.showCard("News");
        if (isFirstView) {
            SwingUtilities.invokeLater(()-> scrollPane.toTop());
            isFirstView = false;
        }
    }

    /**
     * Places all the separately organized newsPackages in the present.
     */
    public void packAll(boolean userClicked) {
        refreshButton.setEnabled(false);
        try {
            final Document doc = Jsoup.connect(NEWS_SITE).get();
            final List<Element> elements = doc.getElementsByTag("article");
            if (NEWS_DATA.isEmpty()) {
                for (Element element : elements) {
                    final String head = element.select("h2.entry-title").text();
                    final String body = element.getElementsByTag("p").text();
                    final String link = element.getElementsByTag("a").attr("href");
                    NEWS_DATA.add(new NewsSavior(head, body, link, null));
                    present.addPenultimate(packNews(head, body, link, null));
                    MComponent.ready(present);
                }
            } else {
                for (Element element : elements) {
                    final String head = element.select("h2.entry-title").text();
                    final String body = element.getElementsByTag("p").text();
                    final String link = element.getElementsByTag("a").attr("href");
                    if (!NEWS_DATA.contains(new NewsSavior(head, null, link, null))) {
                        NEWS_DATA.add(0, new NewsSavior(head, body, link, null));
                        present.add(packNews(head, body, link, null), 0);
                        MComponent.ready(present);
                    }
                }
            }
            accessTime = MDate.now();
            accessLabel.setText(accessTime);
            if (userClicked) {
                App.promptPlain("News", "News refreshed successfully from " + NEWS_SITE);
            }
        } catch (IOException e) {
            if (userClicked) {
                App.signalError("Internet Error", "Feeds will be available when you're connected to the internet.");
            }
        } finally {
            refreshButton.setEnabled(true);
        }
    }

    /**
     * Organizes a news in a panel.
     */
    private KPanel packNews(String header, String body, String link, String allContent) {
        final KLabel hLabel = new KLabel(header, KFontFactory.createBoldFont(18), Color.BLUE);
        hLabel.setOpaque(false);
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(body.substring(0, body.length() - (header.length() + 13)));

        final NewsDialog newsDialog = new NewsDialog(header, body, link, allContent);

        final KButton extendedReader = new KButton();
        extendedReader.setFont(KFontFactory.createPlainFont(15));
        extendedReader.setCursor(MComponent.HAND_CURSOR);
        if (allContent == null) {
            extendedReader.setText("Get full news...");
            extendedReader.addActionListener(e-> newsDialog.primaryClick(extendedReader));
        } else {
            extendedReader.setText("Continue reading...");
            extendedReader.setForeground(Color.BLUE);
            extendedReader.addActionListener(e-> newsDialog.setVisible(true));
        }

        final KPanel readerWrap = new KPanel(new FlowLayout(FlowLayout.RIGHT), extendedReader);
        readerWrap.setBackground(Color.WHITE);

        final KPanel niceBox = new KPanel(new BorderLayout());
        niceBox.setBackground(Color.WHITE);
        niceBox.setPreferredSize(new Dimension(975, 150));
        niceBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        niceBox.add(hLabel, BorderLayout.NORTH);
        niceBox.add(textPane, BorderLayout.CENTER);
        niceBox.add(readerWrap, BorderLayout.SOUTH);
        return new KPanel(niceBox);
    }

//    push if any
    public void pushNews() {
        final ArrayList<NewsSavior> savedNews = (ArrayList<NewsSavior>) Serializer.fromDisk("news.ser");
        if (savedNews == null) {
            return;
        }
        NEWS_DATA.addAll(savedNews);
        if (!NEWS_DATA.isEmpty()) {
            for (NewsSavior news : NEWS_DATA) {
                present.addPenultimate(packNews(news.heading, news.body, news.link, news.content));
            }
            MComponent.ready(present);
        }

        final Object accessObj = Serializer.fromDisk("news-time.ser");
        if (accessObj != null) {
            accessTime = (String) accessObj;
            accessLabel.setText(accessTime);
        }
    }


    /**
     * Todo: push scrollBar to the top for a first-sight
     */
    private static class NewsDialog extends KDialog {
        private String keyContent;
        private String bodyContent;
        private String associateLink;
        private String allContent;
        private KTextPane textPane;

        private NewsDialog(String heading, String body, String link, String allNews){
            super(heading);
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            keyContent = heading;
            bodyContent = body;
            associateLink = link;
            allContent = allNews;
            textPane = KTextPane.wantHtmlFormattedPane(allContent);
            textPane.setPreferredSize(new Dimension(665, 465));
            KScrollPane newsScrollPane = new KScrollPane(textPane);
            newsScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));

            final KButton visitButton = new KButton("Visit site");
            visitButton.setMnemonic(KeyEvent.VK_V);
            visitButton.addActionListener(e-> new Thread(()-> {
                visitButton.setEnabled(false);
                dispose();
                try {
                    Desktop.getDesktop().browse(URI.create(NEWS_SITE));
                } catch (Exception e1) {
                    App.signalError(e1);
                }
                visitButton.setEnabled(true);
            }).start());

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e-> dispose());

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(newsScrollPane, new KPanel(new FlowLayout(FlowLayout.RIGHT), visitButton, closeButton));
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(closeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private void primaryClick(KButton primaryButton){
            new Thread(()-> {
                primaryButton.setEnabled(false);
                try {
                    final Document specificDocument = Jsoup.connect(associateLink).get();
                    allContent = specificDocument.select(".entry-content").outerHtml();
                    textPane.setText(allContent);
                    primaryButton.setText("Continue reading...");
                    primaryButton.setForeground(Color.BLUE);
                    primaryButton.removeActionListener(primaryButton.getActionListeners()[0]);
                    primaryButton.addActionListener(e-> setVisible(true));
                    final NewsSavior updatedNews = new NewsSavior(keyContent, bodyContent, associateLink, allContent);
                    int i = 0;
                    for (; i < NEWS_DATA.size(); i++) {
                        if (updatedNews.equals(NEWS_DATA.get(i))) {
                            break;
                        }
                    }
                    NEWS_DATA.set(i, updatedNews);
                    setVisible(true);
                } catch (IOException ioe) {
                    App.signalError("Error", "We are facing troubles getting the contents of the news '" + keyContent + "'\n" +
                            "Please check back later.");
                } catch (Exception e) {
                    App.silenceException(e);
                } finally {
                    primaryButton.setEnabled(true);
                }
            }).start();
        }
    }


    private static final class NewsSavior implements Serializable {
        private String heading;
        private String body;
        private String link;
        private String content;

        private NewsSavior(String heading, String body, String link, String content){
            this.heading = heading;
            this.body = body;
            this.link = link;
            this.content = content;
        }

        public boolean equals(NewsSavior s) {
            return this.heading.equals(s.heading);
        }
    }


    public static void serializeData() {
        Serializer.toDisk(NEWS_DATA, "news.ser");
        Serializer.toDisk(accessTime, "news-time.ser");
    }

}
