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
        public boolean add(NewsSavior newsSavior) {
            if (NEWS_DATA.contains(newsSavior)) {
                super.remove(newsSavior);
            }
            return super.add(newsSavior);
        }

        @Override
        public boolean contains(Object o) {
            for (NewsSavior savior : NEWS_DATA) {
                if (savior.heading.equals(((NewsSavior) o).heading)) {
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
        accessLabel.setText(accessTime = "Never");

        refreshButton = new KButton("Refresh Feeds");
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
            extendedReader.setText("Get Full News");
            extendedReader.addActionListener(e-> dialogReader.primaryClick(extendedReader));
        } else {
            final AllReader dialogReader = new AllReader(header, body, allContent);
            extendedReader.setText("Continue Reading...");
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
        niceBox.setBorder(BorderFactory.createLineBorder(Color.BLUE,2,true));
        niceBox.add(hLabel, BorderLayout.NORTH);
        niceBox.add(textPane, BorderLayout.CENTER);

        final KPanel readerWrap = new KPanel(new FlowLayout(FlowLayout.RIGHT), extendedReader);
        readerWrap.setBackground(Color.WHITE);

        niceBox.add(readerWrap, BorderLayout.SOUTH);
        return new KPanel(niceBox);
    }

//    push if any
    public void pushNews() {
        if (!NEWS_DATA.isEmpty()) {
            for (NewsSavior saved : NEWS_DATA) {
                present.addPenultimate(packageNews(saved.heading, saved.body, saved.content));
            }
            accessLabel.setText(accessTime);//which should be determined by now
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
        textPane.setPreferredSize(new Dimension(530, 375));

        final KButton jumpButton = new KButton("Visit site");
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
        contentPanel.addAll(new KScrollPane(textPane), MComponent.contentBottomGap(),
                new KPanel(new FlowLayout(FlowLayout.RIGHT)), jumpButton, closeButton);
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
                NEWS_DATA.add(new NewsSavior(keyContent, bodyContent, allContent));
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
    }


    public static void serializeData() {
        System.out.print("Serializing News updates... ");
        Serializer.toDisk(NEWS_DATA, "news.ser");
        Serializer.toDisk(accessTime, "news-time.ser");
        System.out.println("Completed.");
    }

    public static void deSerializeData(){
        System.out.print("Deserializing News updates... ");
        final ArrayList<NewsSavior> savedNews = (ArrayList<NewsSavior>) Serializer.fromDisk("news.ser");
        if (savedNews == null) {
            System.err.println("Unsuccessful.");
            return;
        }

        NEWS_DATA.addAll(savedNews);

        if (!savedNews.isEmpty()) {
            accessTime = "Unknown";
        }

        final Object accessObj = Serializer.fromDisk("news-time.ser");
        if (!(accessObj == null || "Never".equals(accessObj))) {
            accessTime = (String) accessObj;
        }

        System.out.println("Completed successfully.");
    }

}
