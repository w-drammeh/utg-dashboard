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

public class News {
    private KPanel present;
    private KButton refreshButton;
    private KLabel accessLabel;
    private static String accessTime;
    private static final ArrayList<NewsSavior> NEWS_DATA = new ArrayList<>() {
        @Override
        public boolean contains(Object o) {
            for (NewsSavior savior : NEWS_DATA) {
                if (savior.equals((NewsSavior) o)) {
                    return true;
                }
            }
            return false;
        }
    };//unlike many of its kind, this does not explicitly delete / remove.
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
        present.add(new KPanel(accessLabel));//then only add using addPenultimate(Component)

        final KPanel page = new KPanel(new BorderLayout());
        page.add(northPanel, BorderLayout.NORTH);
        page.add(new KScrollPane(present), BorderLayout.CENTER);

        Board.addCard(page, "News");

        if (Dashboard.isFirst()) {
            new Thread(()-> packAll(false)).start();
        } else {
            Board.postProcesses.add(this::pushNews);
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
            for (Element element : elements) {
                final String head = element.select("h2.entry-title").text();
                final String body = element.getElementsByTag("p").text();
                if (!NEWS_DATA.contains(new NewsSavior(head, null, null))) {
                    NEWS_DATA.add(new NewsSavior(head, body, null));
                    present.addPenultimate(packageNews(head, body, null));
                    MComponent.ready(present);
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
    private KPanel packageNews(String header, String body, String allContent) {
        final KLabel hLabel = new KLabel(header, KFontFactory.createBoldFont(18), Color.BLUE);
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(body.substring(0, body.length() - (header.length() + 13)));

        final KButton extendedReader = new KButton();
        extendedReader.setFont(KFontFactory.createPlainFont(14));
        extendedReader.setCursor(MComponent.HAND_CURSOR);
        if (allContent == null) {
            final AllReader dialogReader = new AllReader(header, body, null);
            extendedReader.setText("Get full news...");
            extendedReader.addActionListener(e-> dialogReader.primaryClick(extendedReader));
        } else {
            final AllReader dialogReader = new AllReader(header, body, allContent);
            extendedReader.setText("Continue reading...");
            extendedReader.setForeground(Color.BLUE);
            extendedReader.addActionListener(e-> dialogReader.setVisible(true));
        }

        final KPanel niceBox = new KPanel(new BorderLayout()) {
            @Override
            public Component add(Component comp) {
                comp.setBackground(Color.WHITE);
                return super.add(comp);
            }
        };
        niceBox.setBackground(Color.WHITE);
        niceBox.setPreferredSize(new Dimension(975, 150));
        niceBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        niceBox.add(hLabel, BorderLayout.NORTH);
        niceBox.add(textPane, BorderLayout.CENTER);

        final KPanel readerWrap = new KPanel(new FlowLayout(FlowLayout.RIGHT), extendedReader);
        readerWrap.setBackground(Color.WHITE);

        niceBox.add(readerWrap, BorderLayout.SOUTH);
        return new KPanel(niceBox);
    }

//    push if any
    public void pushNews() {
        final ArrayList<NewsSavior> savedNews = (ArrayList<NewsSavior>) Serializer.fromDisk("news.ser");
        if (savedNews == null) {
            return;
        }

        for (NewsSavior s : savedNews) {
            NEWS_DATA.add(s);
        }

        final Object accessObj = Serializer.fromDisk("news-time.ser");
        if (accessObj != null) {
            accessTime = (String) accessObj;
            accessLabel.setText(accessTime);
        }

        if (!NEWS_DATA.isEmpty()) {
            for (NewsSavior news : NEWS_DATA) {
                present.addPenultimate(packageNews(news.heading, news.body, news.content));
            }
            MComponent.ready(present);
        }
    }


    private static class AllReader extends KDialog {
    private String keyContent;//the header
    private String bodyContent;//the body packaged. it's only used here for a special purpose
    private String allContent;
    private KTextPane textPane;

    private AllReader(String heading, String body, String allNews){
        super(heading+" - News Feed");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setResizable(true);
        keyContent = heading;
        bodyContent = body;
        allContent = allNews;
        textPane = KTextPane.wantHtmlFormattedPane(allContent);
        textPane.setPreferredSize(new Dimension(665, 465));

        final KScrollPane newsKScrollPane = new KScrollPane(textPane);
        newsKScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        final KButton jumpButton = new KButton("Visit site");
        jumpButton.setMnemonic(KeyEvent.VK_V);
        jumpButton.addActionListener(e-> new Thread(()-> {
            jumpButton.setEnabled(false);
            dispose();
            try {
                Desktop.getDesktop().browse(URI.create(NEWS_SITE));
            } catch (Exception e1) {
                App.signalError(e1);
            }
            jumpButton.setEnabled(true);
        }).start());

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e-> dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(newsKScrollPane, new KPanel(new FlowLayout(FlowLayout.RIGHT), jumpButton, closeButton));
        setContentPane(contentPanel);
        getRootPane().setDefaultButton(closeButton);
        pack();
        setLocationRelativeTo(Board.getRoot());
    }

    /**
     * Actually, to decide whether to be visible, given it has all the news contents,
     * or to download it.
     */
    private void primaryClick(KButton primaryButton){
        new Thread(()-> {
            primaryButton.setEnabled(false);
            try {
                final String associatedLink = keyContent.toLowerCase().replace("’", "").replace(" ", "-");
                final Document specificDocument = Jsoup.connect(HOME_SITE + associatedLink).get();
                this.allContent = specificDocument.select(".entry-content").outerHtml();
                this.textPane.setText(allContent);
                primaryButton.setText("Continue Reading");
                primaryButton.setForeground(Color.BLUE);
                primaryButton.removeActionListener(primaryButton.getActionListeners()[0]);
                primaryButton.addActionListener(e-> setVisible(true));
                final NewsSavior updatedNews = new NewsSavior(keyContent, bodyContent, allContent);
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
        private String heading, body, content;

        private NewsSavior(String heading, String body, String content){
            this.heading = heading;
            this.body = body;
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
