package main;

import customs.*;
import utg.Dashboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class MyDashboard extends KDialog {
    private final CardLayout midCard = new CardLayout();
    private KPanel midLayer;


    public MyDashboard(){
        super("About");
        this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        midLayer = new KPanel(midCard);
        midCard.addLayoutComponent(midLayer.add(getAboutCard()),"about");
        midCard.addLayoutComponent(midLayer.add(getCreditsCard()),"credits");
        midCard.addLayoutComponent(midLayer.add(new KScrollPane(getFeedbackCard(),new Dimension(680,550),false)),"feedback");
        midCard.addLayoutComponent(midLayer.add(getDonateCard()),"donate");
        midCard.addLayoutComponent(midLayer.add(getTermsCard()),"terms");

        final KPanel hindLayer = new KPanel();
        hindLayer.addAll(newCardButton("About","About","about"),
                newCardButton("Credits","Credits","credits"),
                newCardButton("Feedback","Feedback","feedback"),
                newCardButton("Donate","Donate","donate"),
                newCardButton("Terms","Terms & Conditions","terms"));

        final KPanel panel = new KPanel(new BorderLayout());
        panel.add(midLayer,BorderLayout.CENTER);
        panel.add(hindLayer,BorderLayout.SOUTH);
        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(Board.getRoot());
    }

    /**
     * Provides the look of the bottom buttons.
     */
    private KButton newCardButton(String buttonText, String showingTitle, String showingComponent){
        final KButton cardButton = new KButton(buttonText);
        cardButton.setFont(KFontFactory.createPlainFont(15));
        cardButton.addActionListener(e -> {
            midCard.show(midLayer,showingComponent);
            this.setTitle(showingTitle);//which is hereby overridden
        });
        return cardButton;
    }

    @Override
    public void setTitle(String newTitle) {
        super.setTitle("Dashboard - "+newTitle);
    }

    private KPanel getAboutCard(){
        final KPanel dashboardLayer = new KPanel(new BorderLayout());
        dashboardLayer.add(KLabel.wantIconLabel("dashboard.png",150,135),BorderLayout.CENTER);
        dashboardLayer.add(new KPanel(new KLabel("A flexible and elegant student management system of the UTG",KFontFactory.createPlainFont(16))),BorderLayout.SOUTH);

        final KPanel javaLayer = new KPanel(new BorderLayout());
        javaLayer.add(new KLabel(new ImageIcon(App.getIconURL("splash.gif"))),BorderLayout.CENTER);
        javaLayer.add(new KPanel(new KLabel("Dashboard is 100% Java and only Java!",KFontFactory.createPlainFont(16))),BorderLayout.SOUTH);

        final KPanel iconsLayer = new KPanel();
        iconsLayer.setLayout(new BoxLayout(iconsLayer, BoxLayout.Y_AXIS));
        iconsLayer.addAll(dashboardLayer,javaLayer,Box.createVerticalStrut(50));

        final KPanel contactLayer = new KPanel();
        contactLayer.setLayout(new BoxLayout(contactLayer, BoxLayout.Y_AXIS));
        contactLayer.addAll(new KLabel("Version: "+ Dashboard.VERSION,KFontFactory.createPlainFont(15)),
                new KLabel("Email: "+Mailer.DEVELOPERS_MAIL,KFontFactory.createPlainFont(15)),
                new KLabel("Contact: +220 3413910", KFontFactory.createPlainFont(15)));

        final KPanel aboutCard = new KPanel(new BorderLayout());
        aboutCard.add(new KPanel(new KLabel("University Student Dashboard",KFontFactory.createBoldFont(18))),BorderLayout.NORTH);
        aboutCard.add(iconsLayer,BorderLayout.CENTER);
        aboutCard.add(contactLayer,BorderLayout.SOUTH);
        return aboutCard;
    }

    private KPanel getCreditsCard(){
        final KLabel waksLabel = KLabel.wantIconLabel("muhammed.jpg",140,150);
        final ActionListener moreOfWask = e -> new SpecificExhibitor().exhibitAuthor();

        final KPanel specialNamesLayer = new KPanel(){
            @Override
            public Component add(Component comp) {
                comp.setFont(KFontFactory.createPlainFont(15));
                return super.add(comp);
            }
        };
        specialNamesLayer.setLayout(new BoxLayout(specialNamesLayer, BoxLayout.Y_AXIS));
        specialNamesLayer.addAll(new KLabel("Mr. Fred Sangol Uche : Lecturer, UTG"),new KLabel("Mahmud S Jallow : Student, UTG"),new KLabel("Alieu Ceesay : Student, UTG"));

        final KPanel respectLayer = new KPanel(new BorderLayout());
        respectLayer.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT), null, new KLabel("Special thanks to:",KFontFactory.createBoldFont(16))),BorderLayout.NORTH);
        respectLayer.add(new KPanel(specialNamesLayer),BorderLayout.CENTER);
        respectLayer.add(new KPanel(new KLabel("*Plus all the students whose details were used during the 'Testing'",KFontFactory.createPlainFont(13),Color.GRAY)),BorderLayout.SOUTH);

        final KPanel creditsPanel = new KPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.addAll(forgeCredits(waksLabel, moreOfWask), respectLayer);

        final KPanel creditsCard = new KPanel(new BorderLayout());
        creditsCard.add(new KPanel(new KLabel("In the name of Allah - The Entirely Merciful, Especially  Merciful",KFontFactory.createBoldFont(17))),BorderLayout.NORTH);
        creditsCard.add(new KScrollPane(creditsPanel, new Dimension(this.getPreferredSize().width,530),false),BorderLayout.CENTER);
        return creditsCard;
    }

    private KPanel forgeCredits(KLabel iLabel, ActionListener moreListener){
        final KPanel namesLayer = new KPanel();
        namesLayer.setLayout(new BoxLayout(namesLayer,BoxLayout.Y_AXIS));
        namesLayer.addAll(new KLabel("MUHAMMED W. DRAMMEH",KFontFactory.createPlainFont(16)),
                new KLabel("Author",KFontFactory.createPlainFont(14)));

        final KPanel headLayer = new KPanel(new FlowLayout(FlowLayout.LEFT));
        headLayer.addAll(iLabel,namesLayer);

        final KButton moreButton = new KButton("Read More...");
        moreButton.setPreferredSize(new Dimension(130, 30));
        moreButton.setStyle(KFontFactory.createPlainFont(14),Color.BLUE);
        moreButton.undress();
        moreButton.underline(false);
        moreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moreButton.addActionListener(moreListener);

        final KTextPane finePane = getANotePane("<b>Muhammed</b> was admitted at UTG in September 2016 to pursue a <i>BSc in mathematics</i>. " +
                "However, after his programming 1 course with Sangol Fred, Muhammed undoubtedly face a turning-point in his academic journey." +
                "<p>In an adventure to embark on self-development, Muhammed initiated the <b>Dashboard Project</b> which he initially refer to as the <i>Student Analyzation Tool</i>. " +
                "So-called because it was his handy-work of a simple .jar file which he use to analyse himself. With this .jar file, he would pass the courses he has done for a semester or year, " +
                "and the simple program will perform analysis based on his scores (like highest score, lowest score, highest-major score, etc), grades (best and worst); and finally, it will be able to tell " +
                "if he has done better in this semester compared to the last by evaluating his CGPA.</p>" +
                "<p>With the help of Allah, Muhammed then advance his adventure, universalize the personal simple tool " +
                "to a more complex system usable by every UTG student.<br>Currently - 2019/2020 - he is a final year student in the above-mentioned disciplines.</p>",335);

        final KPanel fineLayer = new KPanel(new BorderLayout());
        fineLayer.add(headLayer,BorderLayout.NORTH);
        fineLayer.add(finePane, BorderLayout.CENTER);
        fineLayer.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,moreButton),BorderLayout.SOUTH);
        return fineLayer;
    }

    private KTextPane getANotePane(String text, int height){
        final KTextPane notesPane = KTextPane.wantHtmlFormattedPane(text);
        notesPane.setPreferredSize(new Dimension(this.getPreferredSize().width, height));
        return notesPane;
    }

    private KPanel getFeedbackCard(){
        final String bettermentText = "<b>Help make Dashboard better by giving the developers a Review</b>." +
                "<p>You may <b>Report a Bug</b> to be fixed, make a <b>Suggestion</b> to be implemented in a future release, or " +
                "provide an <b>Answer</b> to a Frequently Asked Question. By clicking Send, your review shall be delivered to " +
                "the developer's mail - "+Mailer.DEVELOPERS_MAIL+". Your student-mail is used for this purpose.</p>";

        final Border lineBorder = BorderFactory.createLineBorder(Color.BLUE,1,true);
        final Border spaceBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        final Font hFedFont = KFontFactory.createBoldFont(16);

        final KTextArea reviewTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane reviewTextAreaScroll = KScrollPane.getTextAreaScroller(reviewTextArea,new Dimension(500,100));
        reviewTextAreaScroll.setBorder(spaceBorder);
        final String reviewString = "If you have any review, you can send write it to the developers in the text-area below. " +
                "The review must exclude any kind of greetings, or introductions.";
        final KButton reviewSender = newReviewSender();
        reviewSender.addActionListener(e -> {
            if (!Mailer.canSend()) {
                Mailer.reportQuickFeedbackDenial();
                return;
            }
            new Thread(()->{
                if (Globals.isBlank(reviewTextArea.getText())) {
                    App.signalError(MyDashboard.this.getRootPane(), "Error", "Cannot send blank review. Fill out the Text Area first.");
                    reviewTextArea.requestFocusInWindow();
                } else {
                    changeState(false, reviewTextArea,reviewSender);
                    reviewSender.setText("Sending Review...");
                    if (InternetAvailabilityChecker.isInternetAvailable()) {
                        final Mailer gMailer = new Mailer("Dashboard Feedback | Review | "+Student.getFullNamePostOrder(),reviewTextArea.getText());
                        if (gMailer.sendAs(Mailer.FEEDBACK)) {
                            reviewTextArea.setText(null);
                        }
                    } else {
                        App.signalError(MyDashboard.this.getRootPane(),"No Internet","Sorry, internet connection is required to send the review.\n" +
                                "Please connect and try again.");
                    }
                    changeState(true, reviewTextArea, reviewSender);
                    reviewSender.setText("Send");
                }
            }).start();
        });
        final KPanel reviewLayer = new KPanel();
        reviewLayer.setLayout(new BoxLayout(reviewLayer, BoxLayout.Y_AXIS));
        reviewLayer.setBorder(lineBorder);
        reviewLayer.addAll(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,new KLabel("Give a Review",hFedFont)),
                getANotePane(reviewString,50),reviewTextAreaScroll,KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,reviewSender));

        final KTextArea suggestionTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane suggestionTextAreaScroll = KScrollPane.getTextAreaScroller(suggestionTextArea,new Dimension(500,100));
        suggestionTextAreaScroll.setBorder(spaceBorder);
        final String suggestionString = "In no more than 500 characters, briefly state, in the text-area below, a feature " +
                "you'd like to use in a future release of Dashboard.";
        final KButton suggestionSender = newReviewSender();
        suggestionSender.addActionListener(e -> {
            if (!Mailer.canSend()) {
                Mailer.reportQuickFeedbackDenial();
                return;
            }
            new Thread(()->{
                if (Globals.isBlank(suggestionTextArea.getText())) {
                    App.signalError(MyDashboard.this.getRootPane(),"Error", "Cannot send blank review. Fill out the Text Area first.");
                    suggestionTextArea.requestFocusInWindow();
                } else {
                    changeState(false, suggestionTextArea,suggestionSender);
                    suggestionSender.setText("Sending Suggestion...");
                    if (InternetAvailabilityChecker.isInternetAvailable()) {
                        final Mailer gMailer = new Mailer("Dashboard Feedback | Suggestion |"+Student.getFullNamePostOrder(),suggestionTextArea.getText());
                        if (gMailer.sendAs(Mailer.FEEDBACK)) {
                            suggestionTextArea.setText(null);
                        }
                    } else {
                        App.signalError(MyDashboard.this.getRootPane(), "No Internet","Sorry, internet connection is required to send the review.\n" +
                                "Please connect and try again.");
                    }
                    changeState(true, suggestionTextArea,suggestionSender);
                    suggestionSender.setText("Send");
                }
            }).start();
        });
        final KPanel suggestionLayer = new KPanel();
        suggestionLayer.setLayout(new BoxLayout(suggestionLayer, BoxLayout.Y_AXIS));
        suggestionLayer.setBorder(lineBorder);
        suggestionLayer.addAll(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,new KLabel("Suggest a Feature",hFedFont)),
                getANotePane(suggestionString,50),suggestionTextAreaScroll,KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,suggestionSender));

        final KTextField answerTitleField = KTextField.rangeControlField(100);
        answerTitleField.setPreferredSize(new Dimension(550, 30));
        final KTextArea answerTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane answerTextAreaScroll = KScrollPane.getTextAreaScroller(answerTextArea,new Dimension(500,100));
        answerTextAreaScroll.setBorder(spaceBorder);
        final String answerString = "Answer a problem faced by students at UTG and benefit your brothers and sisters! Please refer to 'Home | FAQs & Help | UTG FAQs' to " +
                "ensure your question is not already answered.";
        final KButton answerSender = newReviewSender();
        answerSender.addActionListener(e -> {
            if (!Mailer.canSend()) {
                Mailer.reportQuickFeedbackDenial();
                return;
            }
            if (Globals.isBlank(answerTitleField.getText())) {
                App.signalError(MyDashboard.this.getRootPane(),"Error", "Please provide the question by filling out the text-field.");
                answerTitleField.requestFocusInWindow();
                return;
            }
            if (Globals.isBlank(answerTextArea.getText())) {
                App.signalError(MyDashboard.this.getRootPane(),"Error", "Please provide the answer by filling out the text-area.");
                answerTextArea.requestFocusInWindow();
                return;
            }
            new Thread(()->{
                changeState(false, answerTitleField,answerTextArea,answerSender);
                answerSender.setText("Sending FAQ...");
                if (InternetAvailabilityChecker.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | FAQ & Answer"+Student.getFullNamePostOrder(),"Question: "+answerTitleField.getText()+"\nAnswer: "+answerTextArea.getText());
                    if (gMailer.sendAs(Mailer.FEEDBACK)) {
                        answerTitleField.setText(null);
                        answerTextArea.setText(null);
                    }
                } else {
                    App.signalError(MyDashboard.this.getRootPane(), "No Internet","Sorry, internet connection is required to send the review.\n" +
                            "Please connect and try again.");
                }
                changeState(true, answerTitleField,answerTextArea,answerSender);
                answerSender.setText("Send");
            }).start();
        });
        final KPanel titleSubstance = new KPanel(new BorderLayout());
        titleSubstance.add(new KLabel(" Question:",KFontFactory.createPlainFont(15)),BorderLayout.WEST);
        titleSubstance.add(new KPanel(answerTitleField),BorderLayout.CENTER);
        final KPanel bodySubstance = new KPanel(new BorderLayout());
        bodySubstance.add(new KLabel(" Answer: ",KFontFactory.createPlainFont(15)),BorderLayout.WEST);
        bodySubstance.add(answerTextAreaScroll,BorderLayout.CENTER);
        final KPanel answerSubstance = new KPanel();
        answerSubstance.setLayout(new BoxLayout(answerSubstance, BoxLayout.Y_AXIS));
        answerSubstance.addAll(titleSubstance,Box.createVerticalStrut(10),bodySubstance);
        final KPanel answerLayer = new KPanel();
        answerLayer.setLayout(new BoxLayout(answerLayer, BoxLayout.Y_AXIS));
        answerLayer.setBorder(lineBorder);
        answerLayer.addAll(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,new KLabel("Answer a FAQ",hFedFont)),
                getANotePane(answerString,75),answerSubstance,KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,answerSender));

        final KTextArea bugTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane bugTextAreaScroll = KScrollPane.getTextAreaScroller(bugTextArea,new Dimension(500,100));
        bugTextAreaScroll.setBorder(spaceBorder);
        final String bugString = "In no more than 500 characters, kindly describe a problem (if there is any) you encountered while using Dashboard.";
        final KButton bugSender = newReviewSender();
        bugSender.addActionListener(e -> new Thread(()->{
            if (!Mailer.canSend()) {
                Mailer.reportQuickFeedbackDenial();
                return;
            }
            if (Globals.isBlank(bugTextArea.getText())) {
                App.signalError(MyDashboard.this.getRootPane(),"Error", "Cannot send blank review. Fill out the text-area first");
                bugTextArea.requestFocusInWindow();
            } else {
                changeState(false, bugTextArea,bugSender);
                bugSender.setText("Reporting Bug...");
                if (InternetAvailabilityChecker.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | A Bug Report | "+Student.getFullNamePostOrder(),bugTextArea.getText());
                    if (gMailer.sendAs(Mailer.FEEDBACK)) {
                        bugTextArea.setText(null);
                    }
                    bugTextArea.setEditable(true);
                    bugSender.setEnabled(true);
                    bugSender.setText("Send");
                } else {
                    App.signalError(MyDashboard.this.getRootPane(), "No Internet","Sorry, internet connection is required to send the review.\n" +
                            "Please connect and try again.");
                }
                changeState(true, bugTextArea,bugSender);
                bugSender.setText("Send");
            }
        }).start());
        final KPanel bugLayer = new KPanel();
        bugLayer.setLayout(new BoxLayout(bugLayer, BoxLayout.Y_AXIS));
        bugLayer.setBorder(lineBorder);
        bugLayer.addAll(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,new KLabel("Report a Problem",hFedFont)),
                getANotePane(bugString,50),bugTextAreaScroll,KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,bugSender));

        final KPanel feedbackCard = new KPanel();
        feedbackCard.setBorder(spaceBorder);
        feedbackCard.setLayout(new BoxLayout(feedbackCard, BoxLayout.Y_AXIS));
        feedbackCard.addAll(getANotePane(bettermentText,130),Box.createVerticalStrut(30),
                reviewLayer, Box.createVerticalStrut(20), suggestionLayer, Box.createVerticalStrut(20), answerLayer,
                Box.createVerticalStrut(20), bugLayer, Box.createVerticalStrut(15));
        return feedbackCard;
    }

    private KButton newReviewSender(){
        final KButton rButton = new KButton("Send");
        rButton.setStyle(KFontFactory.createPlainFont(14),Color.BLUE);
        rButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return rButton;
    }

    private void changeState(boolean enable, Component ... components){
        for (Component c : components) {
            c.setEnabled(enable);
        }
    }

    private KPanel getDonateCard(){
        final String donationText = "Dashboard is not currently backed, funded, or sponsored by any institution or organization. " +
                "Therefore, to guarantee our long term of service, and continuous update, we humbly welcome all kinds of " +
                "donations. (Every dalasi shall count!)";

        final KPanel donationCard = new KPanel();
        donationCard.setLayout(new BoxLayout(donationCard, BoxLayout.Y_AXIS));
        donationCard.addAll(getANotePane(donationText,100));
        return donationCard;
    }

    private KPanel getTermsCard(){
        final String termsString = "Your usage of the <b>Personal Dashboard</b> is subject to the following terms and conditions:" +
                "<p>It is strictly out of bounds that any part of this tool be used, directly or indirectly, " +
                "in your course work-projects like Junior Project, Senior Project, etc. Such an act will lead to a serious " +
                "academic penalty. In a nutshell, no part of this project may be modified or reproduced without the prior " +
                "written permission of the authors. This product contains sensitive mechanisms, " +
                "thus tampering with any can be detrimental to the students, or even burden the servers of the portal.</p>" +
                "<p style='text-align: right;'><b>__Muhammed W Drammeh</b></p>";

        final KPanel termsCard = new KPanel();
        termsCard.setLayout(new BoxLayout(termsCard, BoxLayout.Y_AXIS));
        termsCard.addAll(getANotePane(termsString,200));
        return termsCard;
    }


    private class SpecificExhibitor extends KDialog {
        private final Font font = KFontFactory.createPlainFont(16);

        private SpecificExhibitor(){
            super("Dashboard Author");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final Font hintFont = KFontFactory.createBoldFont(15);

            final KPanel firstNamePanel = new KPanel(new BorderLayout());
            firstNamePanel.add(new KPanel(new KLabel("First Name:",hintFont)),BorderLayout.WEST);
            firstNamePanel.add(new KPanel(new KLabel("Drammeh",font)),BorderLayout.CENTER);

            final KPanel lastNamePanel = new KPanel(new BorderLayout());
            lastNamePanel.add(new KPanel(new KLabel("Last Name:",hintFont)),BorderLayout.WEST);
            lastNamePanel.add(new KPanel(new KLabel("Muhammed W",font)),BorderLayout.CENTER);

            final KPanel dobPanel = new KPanel(new BorderLayout());
            dobPanel.add(new KPanel(new KLabel("Date of Birth:",hintFont)),BorderLayout.WEST);
            dobPanel.add(new KPanel(new KLabel("Jan 01, 1999",font)),BorderLayout.CENTER);

            final KPanel pobPanel = new KPanel(new BorderLayout());
            pobPanel.add(new KPanel(new KLabel("Place of Birth:",hintFont)),BorderLayout.WEST);
            pobPanel.add(new KPanel(new KLabel("Diabugu Batapa, Sandu District, URR",font)),BorderLayout.CENTER);

            final KPanel addressPanel = new KPanel(new BorderLayout());
            addressPanel.add(new KPanel(new KLabel("Address:",hintFont)),BorderLayout.WEST);
            addressPanel.add(new KPanel(new KLabel("Sukuta, Kombo North",font)),BorderLayout.CENTER);

            final KPanel telephonePanel = new KPanel(new BorderLayout());
            telephonePanel.add(new KPanel(new KLabel("Telephone:",hintFont)),BorderLayout.WEST);
            telephonePanel.add(new KPanel(new KLabel("+220 3413910",font)),BorderLayout.CENTER);

            final KPanel emailPanel = new KPanel(new BorderLayout());
            emailPanel.add(new KPanel(new KLabel("Email Address:",hintFont)),BorderLayout.WEST);
            emailPanel.add(new KPanel(new KLabel("wakadrammeh@gmail.com",font)),BorderLayout.CENTER);

            final KPanel nationalityPanel = new KPanel(new BorderLayout());
            nationalityPanel.add(new KPanel(new KLabel("Nationality:",hintFont)),BorderLayout.WEST);
            nationalityPanel.add(new KPanel(new KLabel("The Gambia",font)),BorderLayout.CENTER);

            final KButton closeButton = new KButton("Ok");
            closeButton.addActionListener(e -> dispose());

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(firstNamePanel,lastNamePanel,dobPanel,pobPanel,addressPanel,telephonePanel,emailPanel,nationalityPanel,
                    ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,closeButton));

            getRootPane().setDefaultButton(closeButton);
            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(MyDashboard.this.getRootPane());
        }

        private void exhibitAuthor(){
            SwingUtilities.invokeLater(()-> setVisible(true));
        }

    }

}
