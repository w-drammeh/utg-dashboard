package main;

import customs.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NewsGenerator {
    public static final String HOME_SITE = "https://www.utg.edu.gm/";
    private static final String NEWS_SITE = "https://www.utg.edu.gm/category/news/";
    private static KLabel accessLabel;
    private static KPanel accessPanel;
    private static String accessTime = "Never";
    private static final ArrayList<NewsSavior> NEWS_DATA = new ArrayList<NewsSavior>(){
        @Override
        public boolean add(NewsSavior incomingSavior) {
            for (NewsSavior s : NEWS_DATA) {
                if (s.heading.equals(incomingSavior.heading)) {
                    super.remove(s);
                    return super.add(incomingSavior);
                }
            }
            return super.add(incomingSavior);
        }

        @Override
        public boolean contains(Object o) {
            for (NewsSavior s : NEWS_DATA) {
                if (s.heading.equals(((NewsSavior)o).heading)) {
                    return true;
                }
            }
            return false;
        }
    };//Unlike many of its kind, this does not explicitly delete / remove.


    public NewsGenerator(){
        accessLabel = KLabel.getPredefinedLabel("Last accessed: ", SwingConstants.LEFT);
        accessLabel.setStyle(KFontFactory.createPlainFont(15), Color.RED);
        accessPanel = KPanel.wantDirectAddition(accessLabel);
    }

    /**
     * Places all the separately organized newsPackages in the resident.
     */
    public void packAllIn(JComponent resident, boolean userClicked) {
        try {
            resident.remove(accessPanel);
            final Document doc = Jsoup.connect(NEWS_SITE).get();
            final List<Element> elements = doc.getElementsByTag("article");
            for (Element e : elements) {
                final String head = e.select("h2.entry-title").text();
                final String body = e.getElementsByTag("p").text();
                if (!NEWS_DATA.contains(new NewsSavior(head, null, null))) {
                    resident.add(packageNews(head, body, null));
                    ComponentAssistant.ready(resident);
                    NEWS_DATA.add(new NewsSavior(head, body, null));
                }
            }
            accessTime = MDate.now();
            accessLabel.setText(accessTime);
            if (userClicked) {
                App.promptPlain("News", "News refreshed successfully from " + NEWS_SITE + ".");
            }
        } catch (IOException e) {
            if (userClicked) {
                App.signalError("Internet Error", "Feeds will be available when you're connected to the internet.");
            }
        } catch (NullPointerException ignored) {
        } finally {
            resident.add(accessPanel);
            ComponentAssistant.ready(resident);
        }
    }

    /**
     * Organizes a news in a panel.
     */
    private KPanel packageNews(String header, String body, String allContent){
        final KLabel hLabel = new KLabel(header, KFontFactory.createBoldFont(18), Color.BLUE);
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(body.substring(0, body.length() - (header.length() + 13)));
        final KButton extendedReader = new KButton();
        extendedReader.setFont(KFontFactory.createPlainFont(14));
        extendedReader.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (allContent == null) {
            final AllReader dialogReader = new AllReader(header, body, null);
            extendedReader.setText("Get Full News");
            extendedReader.addActionListener(e -> dialogReader.primaryClick(extendedReader));
        } else {
            final AllReader dialogReader = new AllReader(header, body, allContent);
            extendedReader.setText("Continue Reading");
            extendedReader.setForeground(Color.BLUE);
            extendedReader.addActionListener(e-> dialogReader.setVisible(true));
        }

        final KPanel niceBox = new KPanel(new BorderLayout()){
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
        final KPanel readerWrap = KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT), null, extendedReader);
        readerWrap.setBackground(Color.WHITE);
        niceBox.add(readerWrap, BorderLayout.SOUTH);
        return KPanel.wantDirectAddition(niceBox);
    }

    public void pushNewsIfAny(JComponent c){
        if (!NEWS_DATA.isEmpty()) {
            for (NewsSavior saved : NEWS_DATA) {
                c.add(packageNews(saved.heading, saved.body, saved.content));
                ComponentAssistant.ready(c);
            }
            accessLabel.setText(accessTime);
            c.add(accessPanel);
            ComponentAssistant.ready(c);
        }
    }


    private static class AllReader extends KDialog {
    private String keyContent;//The header
    private String bodyContent;//The body packaged. It's only used here for a special purpose
    private String allContent;
    private KTextPane textPane;

    private AllReader(String heading, String body, String allNews){
        super(heading+" - News Feed");
        this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        this.setResizable(true);
        this.keyContent = heading;
        this.bodyContent = body;
        this.allContent = allNews;

        textPane = KTextPane.wantHtmlFormattedPane(this.allContent);
        textPane.setPreferredSize(new Dimension(530, 375));

        final KButton jumpButton = new KButton("Visit site");
        jumpButton.addActionListener(e -> new Thread(() -> {
            jumpButton.setEnabled(false);
            this.dispose();
            try {
                Desktop.getDesktop().browse(URI.create(NEWS_SITE));
            } catch (Exception e1) {
                App.signalError(e1);
            }
            jumpButton.setEnabled(true);
        }).start());

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> AllReader.this.dispose());

        final KPanel buttonPane = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.addAll(jumpButton, closeButton);

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(new KScrollPane(textPane, false), ComponentAssistant.contentBottomGap(),
                buttonPane);
        this.setContentPane(contentPanel);
        this.getRootPane().setDefaultButton(closeButton);
        this.pack();
        this.setLocationRelativeTo(Board.getRoot());
    }

    /**
     * Actually, to decide whether to be visible, given it has all the news contents,
     * or to download it.
     */
    private void primaryClick(KButton primaryButton){
        new Thread(()->{
            primaryButton.setEnabled(false);
            try {
                final String associatedLink = this.keyContent.toLowerCase().replace("’", "").replace(" ", "-");
                final Document specificDocument = Jsoup.connect(HOME_SITE + associatedLink).get();
                this.allContent = specificDocument.select(".entry-content").outerHtml();
                this.textPane.setText(this.allContent);
                primaryButton.setText("Continue Reading");
                primaryButton.setForeground(Color.BLUE);
                primaryButton.removeActionListener(primaryButton.getActionListeners()[0]);
                primaryButton.addActionListener(e -> this.setVisible(true));
                NEWS_DATA.add(new NewsSavior(this.keyContent, this.bodyContent, this.allContent));
                this.setVisible(true);
            } catch (IOException ioe) {
                App.signalError("Download Error", "We are facing troubles getting the contents of the news '" + this.keyContent + "'\n" +
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

    public static void serializeData(){
        System.out.print("Serializing news... ");
        MyClass.serialize(NEWS_DATA, "news.ser");
        MyClass.serialize(accessTime, "newsAccessTime.ser");
        System.out.println("Completed.");
    }

    public static void deSerializeData(){
        System.out.print("Deserializing news...");
        final ArrayList<NewsSavior> savedNews = (ArrayList<NewsSavior>) MyClass.deserialize("news.ser");
        if (!(savedNews == null)) {
            for (NewsSavior savior : savedNews) {
                NEWS_DATA.add(savior);
            }
            if (!savedNews.isEmpty()) {
                accessTime = "Undefined";
            }
        }
        final Object accessObj = MyClass.deserialize("newsAccessTime.ser");
        if (!(accessObj == null || "Never".equals(accessObj))) {
            accessTime = accessObj.toString();
        }
        System.out.println("Completed.");
    }

}
