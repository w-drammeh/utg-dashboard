package main;

import customs.*;
import utg.Dashboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class About extends KDialog {
    private CardLayout midCard;
    private KPanel midLayer;


    public About(){
        super("About");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        midCard = new CardLayout();
        midLayer = new KPanel(midCard);
        midCard.addLayoutComponent(midLayer.add(getAboutCard()), "about");
        midCard.addLayoutComponent(midLayer.add(getCreditsCard()), "credits");
        midCard.addLayoutComponent(midLayer.add(new KScrollPane(getFeedbackCard(),
                new Dimension(680,550))), "feedback");
        midCard.addLayoutComponent(midLayer.add(getDonateCard()), "donate");
        midCard.addLayoutComponent(midLayer.add(getTermsCard()), "terms");

        final KPanel hindLayer = new KPanel();
        hindLayer.addAll(newCardButton("About", "About", "about"),
                newCardButton("Credits", "Credits", "credits"),
                newCardButton("Feedback", "Feedback", "feedback"),
                newCardButton("Donate", "Donate", "donate"),
                newCardButton("Terms", "Terms & Conditions", "terms"));

        final KPanel panel = new KPanel(new BorderLayout());
        panel.add(midLayer, BorderLayout.CENTER);
        panel.add(hindLayer, BorderLayout.SOUTH);
        setContentPane(panel);
        pack();
        setLocationRelativeTo(Board.getRoot());
    }

    @Override
    public void setTitle(String title) {
        super.setTitle("Dashboard - "+title);
    }

    private KButton newCardButton(String buttonText, String activityTitle, String component){
        final KButton cardButton = new KButton(buttonText);
        cardButton.setFont(KFontFactory.createPlainFont(15));
        cardButton.addActionListener(e-> {
            midCard.show(midLayer, component);
            setTitle(activityTitle);//which is hereby overridden
        });
        return cardButton;
    }

    private KPanel getAboutCard(){
        final KPanel dashboardLayer = new KPanel(new BorderLayout());
        dashboardLayer.add(KLabel.wantIconLabel("dashboard.png", 150, 135), BorderLayout.CENTER);
        dashboardLayer.add(new KPanel(new KLabel("A flexible and elegant student management system of the UTG",
                KFontFactory.createPlainFont(16))), BorderLayout.SOUTH);

        final KPanel javaLayer = new KPanel(new BorderLayout());
        javaLayer.add(new KLabel(new ImageIcon(App.getIconURL("splash.gif"))), BorderLayout.CENTER);
        javaLayer.add(new KPanel(new KLabel("Dashboard is 100% Java and only Java!",
                        KFontFactory.createPlainFont(16))), BorderLayout.SOUTH);

        final KPanel iconsLayer = new KPanel();
        iconsLayer.setLayout(new BoxLayout(iconsLayer, BoxLayout.Y_AXIS));
        iconsLayer.addAll(dashboardLayer, javaLayer, Box.createVerticalStrut(50));

        final KPanel bottomLayer = new KPanel();
        bottomLayer.setLayout(new BoxLayout(bottomLayer, BoxLayout.Y_AXIS));
        bottomLayer.addAll(new KLabel("Version: "+ Dashboard.VERSION, KFontFactory.createPlainFont(15)),
                new KLabel("Email: "+Mailer.DEVELOPERS_MAIL, KFontFactory.createPlainFont(15)),
                new KLabel("Contact: +220 3413910", KFontFactory.createPlainFont(15)));

        final KPanel aboutCard = new KPanel(new BorderLayout());
        aboutCard.add(new KPanel(new KLabel("University Student Dashboard", KFontFactory.createBoldFont(18))),
                BorderLayout.NORTH);
        aboutCard.add(iconsLayer, BorderLayout.CENTER);
        aboutCard.add(bottomLayer, BorderLayout.SOUTH);
        return aboutCard;
    }

    private KPanel getCreditsCard(){
        final KLabel iconLabel = KLabel.wantIconLabel("muhammed.jpg", 140, 150);
        final ActionListener exhibitAction = e-> new SpecificExhibitor().setVisible(true);

        final Font specialFont = KFontFactory.createPlainFont(15);
        final KPanel specialNamesLayer = new KPanel();
        specialNamesLayer.setLayout(new BoxLayout(specialNamesLayer, BoxLayout.Y_AXIS));
        specialNamesLayer.addAll(new KLabel("Mr. Fred Sangol Uche : Lecturer, UTG", specialFont),
                new KLabel("Mahmud S Jallow : Student, UTG", specialFont),
                new KLabel("Alieu Ceesay : Student, UTG", specialFont));

        final KPanel respectLayer = new KPanel(new BorderLayout());
        respectLayer.add(new KPanel(new FlowLayout(FlowLayout.LEFT),
                new KLabel("Special thanks to:", KFontFactory.createBoldFont(16))), BorderLayout.NORTH);
        respectLayer.add(new KPanel(specialNamesLayer), BorderLayout.CENTER);
        respectLayer.add(new KPanel(new KLabel("Plus all the students whose details were used during the \"Testing\"",
                KFontFactory.createPlainFont(14), Color.GRAY)), BorderLayout.SOUTH);

        final KPanel creditsPanel = new KPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.addAll(forgeCredits(iconLabel, exhibitAction), respectLayer);

        final KPanel creditsCard = new KPanel(new BorderLayout());
        creditsCard.add(new KPanel(new KLabel("In the name of Allah - The Entirely Merciful, Especially  Merciful",
                KFontFactory.createBoldFont(18))), BorderLayout.NORTH);
        creditsCard.add(new KScrollPane(creditsPanel, new Dimension(getPreferredSize().width,530)), BorderLayout.CENTER);
        return creditsCard;
    }

    private KPanel forgeCredits(KLabel label, ActionListener listener){
        final KPanel nameLayer = new KPanel();
        nameLayer.setLayout(new BoxLayout(nameLayer,BoxLayout.Y_AXIS));
        nameLayer.addAll(new KLabel("MUHAMMED W. DRAMMEH", KFontFactory.createPlainFont(17)),
                new KLabel("Author", KFontFactory.createPlainFont(14)));

        final KPanel headLayer = new KPanel(new FlowLayout(FlowLayout.LEFT));
        headLayer.addAll(label, nameLayer);

        final KButton moreButton = new KButton("Read More...");
        moreButton.undress();
        moreButton.underline(false);
        moreButton.setPreferredSize(new Dimension(130, 30));
        moreButton.setStyle(KFontFactory.createPlainFont(14), Color.BLUE);
        moreButton.setCursor(MComponent.HAND_CURSOR);
        moreButton.addActionListener(listener);

        final KTextPane finePane = newNotePane("<p><b>Muhammed</b> was admitted at UTG in September 2016 to pursue " +
                "a <i>BSc in mathematics</i>. However, after his programming 1 course with Sangol Fred, " +
                "Muhammed undoubtedly face a turning-point in his academic journey.</p>" +
                "<p>In an adventure to embark on self-development, Muhammed initiated the <b>Dashboard Project</b> " +
                "which he initially refer to as the <i>Student Analyzation Tool</i>. " +
                "So-called because it was his handy-work of a simple .jar file which he use to analyse himself. " +
                "With this .jar file, he would pass the courses he has done for a semester or year, " +
                "and the simple program will perform analysis based on his scores " +
                "(like highest score, lowest score, highest-major score, etc), grades (best and worst); " +
                "and finally, it will be able to tell if he has done better in this semester compared to the last " +
                "by evaluating his CGPA.</p>" +
                "<p>With the help of Allah, Muhammed then advanced his adventure, universalize the personal simple tool " +
                "to a more complex system usable by every UTG student.",325);

        final KPanel authorLayer = new KPanel(new BorderLayout());
        authorLayer.add(headLayer, BorderLayout.NORTH);
        authorLayer.add(finePane, BorderLayout.CENTER);
        authorLayer.add(new KPanel(new FlowLayout(FlowLayout.RIGHT), moreButton), BorderLayout.SOUTH);
        return authorLayer;
    }

    private KPanel getFeedbackCard(){
        final String bettermentText = "<b>Help make Dashboard better by giving the developers a Review</b>." +
                "<p>You may <b>Report a Bug</b> to be fixed, make a <b>Suggestion</b> to be implemented in a future release, " +
                "or provide an <b>Answer</b> to a Frequently Asked Question. By clicking Send, your review shall be delivered " +
                "to the developers' mail address: "+Mailer.DEVELOPERS_MAIL+". Your student-mail might be is used for this purpose.</p>";

        final Border lineBorder = BorderFactory.createLineBorder(Color.BLUE, 1,true);
        final Border spaceBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        final Font feedHeadFont = KFontFactory.createBoldFont(16);

        final KTextArea reviewTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane reviewTextAreaScroll = reviewTextArea.outerScrollPane(new Dimension(500, 100));
        reviewTextAreaScroll.setBorder(spaceBorder);

        final String reviewString = "If you have any review, you can send write it to the developers in the text-area below. " +
                "The review must exclude any kind of greetings, or introductions.";

        final KButton reviewSender = newReviewSender();
        reviewSender.addActionListener(e-> new Thread(()-> {
            if (Globals.isBlank(reviewTextArea.getText())) {
                reportBlankReview(reviewTextArea);
            } else {
                MComponent.toggle(reviewTextArea, reviewSender);
                reviewSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | Review | "+Student.getFullNamePostOrder(),
                            reviewTextArea.getText());
                    if (gMailer.send()) {
                        reviewTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggle(reviewTextArea, reviewSender);
                reviewSender.setText("Send");
            }
        }).start());

        final KPanel reviewLayer = new KPanel();
        reviewLayer.setBorder(lineBorder);
        reviewLayer.setLayout(new BoxLayout(reviewLayer, BoxLayout.Y_AXIS));
        reviewLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Give a Review", feedHeadFont)),
                newNotePane(reviewString,50), reviewTextAreaScroll,
                new KPanel(new FlowLayout(FlowLayout.RIGHT), reviewSender));

        final KTextArea suggestionTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane suggestionTextAreaScroll = suggestionTextArea.outerScrollPane(new Dimension(500, 100));
        suggestionTextAreaScroll.setBorder(spaceBorder);

        final String suggestionString = "In no more than 500 characters, briefly state, in the text-area below, a feature " +
                "you'd like to use in a future release of Dashboard.";

        final KButton suggestionSender = newReviewSender();
        suggestionSender.addActionListener(e-> new Thread(()-> {
            if (Globals.isBlank(suggestionTextArea.getText())) {
                reportBlankReview(suggestionTextArea);
            } else {
                MComponent.toggle(suggestionTextArea,suggestionSender);
                suggestionSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | Suggestion |"+Student.getFullNamePostOrder(),
                            suggestionTextArea.getText());
                    if (gMailer.send()) {
                        suggestionTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggle(suggestionTextArea,suggestionSender);
                suggestionSender.setText("Send");
            }
        }).start());

        final KPanel suggestionLayer = new KPanel();
        suggestionLayer.setBorder(lineBorder);
        suggestionLayer.setLayout(new BoxLayout(suggestionLayer, BoxLayout.Y_AXIS));
        suggestionLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Suggest a Feature", feedHeadFont)),
                newNotePane(suggestionString,50), suggestionTextAreaScroll,
                new KPanel(new FlowLayout(FlowLayout.RIGHT), suggestionSender));

        final KTextField answerTitleField = KTextField.rangeControlField(100);
        answerTitleField.setPreferredSize(new Dimension(550, 30));

        final KTextArea answerTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane answerTextAreaScroll = answerTextArea.outerScrollPane(new Dimension(500, 100));
        answerTextAreaScroll.setBorder(spaceBorder);
        final String answerString = "Answer a problem faced by students at UTG and benefit your brothers and sisters! " +
                "Please refer to 'Home | FAQs & Help | UTG FAQs' to ensure your question is not already answered.";

        final KButton answerSender = newReviewSender();
        answerSender.addActionListener(e-> {
            if (Globals.isBlank(answerTitleField.getText())) {
                App.signalError(getRootPane(),"No Question", "Please provide the question by filling out the Text Field.");
                answerTitleField.requestFocusInWindow();
                return;
            }

            if (Globals.isBlank(answerTextArea.getText())) {
                reportBlankReview(answerTextArea);
                return;
            }

            new Thread(()-> {
                MComponent.toggle(answerTitleField, answerTextArea, answerSender);
                answerSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | FAQ & Answer"+Student.getFullNamePostOrder(),
                            "Question: "+answerTitleField.getText()+"\nAnswer: "+answerTextArea.getText());
                    if (gMailer.send()) {
                        answerTitleField.setText(null);
                        answerTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggle(answerTitleField,answerTextArea,answerSender);
                answerSender.setText("Send");
            }).start();
        });

        final KPanel titleSubstance = new KPanel(new BorderLayout());
        titleSubstance.add(new KPanel(new KLabel("Question:", KFontFactory.createPlainFont(15))), BorderLayout.WEST);
        titleSubstance.add(new KPanel(answerTitleField), BorderLayout.CENTER);

        final KPanel bodySubstance = new KPanel(new BorderLayout());
        bodySubstance.add(new KPanel(new KLabel("Answer:", KFontFactory.createPlainFont(15))), BorderLayout.WEST);
        bodySubstance.add(answerTextAreaScroll, BorderLayout.CENTER);

        final KPanel answerSubstance = new KPanel();
        answerSubstance.setLayout(new BoxLayout(answerSubstance, BoxLayout.Y_AXIS));
        answerSubstance.addAll(titleSubstance, Box.createVerticalStrut(10), bodySubstance);

        final KPanel answerLayer = new KPanel();
        answerLayer.setBorder(lineBorder);
        answerLayer.setLayout(new BoxLayout(answerLayer, BoxLayout.Y_AXIS));
        answerLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Answer a FAQ", feedHeadFont)),
                newNotePane(answerString,75), answerSubstance, new KPanel(new FlowLayout(FlowLayout.RIGHT), answerSender));

        final KTextArea bugTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane bugTextAreaScroll = bugTextArea.outerScrollPane(new Dimension(500,100));
        bugTextAreaScroll.setBorder(spaceBorder);
        final String bugString = "In no more than 500 characters, " +
                "kindly describe a problem (if there is any) you encountered while using Dashboard.";

        final KButton bugSender = newReviewSender();
        bugSender.addActionListener(e-> new Thread(()-> {
            if (Globals.isBlank(bugTextArea.getText())) {
                reportBlankReview(bugTextArea);
            } else {
                MComponent.toggle(bugTextArea,bugSender);
                bugSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | A Bug Report | "+
                            Student.getFullNamePostOrder(), bugTextArea.getText());
                    if (gMailer.send()) {
                        bugTextArea.setText(null);
                    }
                    bugTextArea.setEditable(true);
                    bugSender.setEnabled(true);
                    bugSender.setText("Send");
                } else {
                    reportNoConnection();
                }
                MComponent.toggle(bugTextArea,bugSender);
                bugSender.setText("Send");
            }
        }).start());

        final KPanel bugLayer = new KPanel();
        bugLayer.setBorder(lineBorder);
        bugLayer.setLayout(new BoxLayout(bugLayer, BoxLayout.Y_AXIS));
        bugLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Report a Problem", feedHeadFont)),
                newNotePane(bugString,50), bugTextAreaScroll, new KPanel(new FlowLayout(FlowLayout.RIGHT), bugSender));

        final KPanel feedbackCard = new KPanel();
        feedbackCard.setBorder(spaceBorder);
        feedbackCard.setLayout(new BoxLayout(feedbackCard, BoxLayout.Y_AXIS));
        feedbackCard.addAll(newNotePane(bettermentText, 130), Box.createVerticalStrut(25),
                reviewLayer, Box.createVerticalStrut(20), suggestionLayer, Box.createVerticalStrut(20),
                answerLayer, Box.createVerticalStrut(20), bugLayer, Box.createVerticalStrut(10));
        return feedbackCard;
    }

//    creates a sender button
    private KButton newReviewSender(){
        final KButton button = new KButton("Send");
        button.setStyle(KFontFactory.createPlainFont(14), Color.BLUE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void reportBlankReview(KTextArea textArea){
        App.signalError(getRootPane(), "Blank", "Fill out the Text Area first.");
        textArea.requestFocusInWindow();
    }

    private void reportNoConnection(){
        App.signalError(getRootPane(), "No Internet", "Internet connection is required to send a Review.\n" +
                "Please connect and try again.");
    }

    private KPanel getDonateCard(){
        final String donationText = "Dashboard is not currently backed, funded, or sponsored by any institution or organization. " +
                "Therefore, to guarantee our long term of service, and continuous update, we humbly welcome all kinds of " +
                "donations. (Every dalasi shall count!)";

        final KPanel donationCard = new KPanel();
        donationCard.setLayout(new BoxLayout(donationCard, BoxLayout.Y_AXIS));
        donationCard.addAll(newNotePane(donationText, 100));
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
        termsCard.addAll(newNotePane(termsString, 200));
        return termsCard;
    }

    private KTextPane newNotePane(String text, int height){
        final KTextPane notesPane = KTextPane.wantHtmlFormattedPane(text);
        notesPane.setPreferredSize(new Dimension(getPreferredSize().width, height));
        return notesPane;
    }


    private class SpecificExhibitor extends KDialog {

        private SpecificExhibitor(){
            super("Dashboard Author");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final Font hintFont = KFontFactory.createBoldFont(15);
            final Font valueFont = KFontFactory.createPlainFont(16);

            final KPanel firstNamePanel = new KPanel(new BorderLayout());
            firstNamePanel.add(new KPanel(new KLabel("First Name:", hintFont)), BorderLayout.WEST);
            firstNamePanel.add(new KPanel(new KLabel("Drammeh", valueFont)), BorderLayout.CENTER);

            final KPanel lastNamePanel = new KPanel(new BorderLayout());
            lastNamePanel.add(new KPanel(new KLabel("Last Name:", hintFont)), BorderLayout.WEST);
            lastNamePanel.add(new KPanel(new KLabel("Muhammed W", valueFont)), BorderLayout.CENTER);

            final KPanel dobPanel = new KPanel(new BorderLayout());
            dobPanel.add(new KPanel(new KLabel("Date of Birth:", hintFont)), BorderLayout.WEST);
            dobPanel.add(new KPanel(new KLabel("Jan 01, 1999", valueFont)), BorderLayout.CENTER);

            final KPanel pobPanel = new KPanel(new BorderLayout());
            pobPanel.add(new KPanel(new KLabel("Place of Birth:", hintFont)), BorderLayout.WEST);
            pobPanel.add(new KPanel(new KLabel("Diabugu Batapa, Sandu District, URR", valueFont)), BorderLayout.CENTER);

            final KPanel addressPanel = new KPanel(new BorderLayout());
            addressPanel.add(new KPanel(new KLabel("Address:", hintFont)), BorderLayout.WEST);
            addressPanel.add(new KPanel(new KLabel("Sukuta, Kombo North", valueFont)), BorderLayout.CENTER);

            final KPanel telephonePanel = new KPanel(new BorderLayout());
            telephonePanel.add(new KPanel(new KLabel("Telephone:", hintFont)), BorderLayout.WEST);
            telephonePanel.add(new KPanel(new KLabel("+220 3413910", valueFont)), BorderLayout.CENTER);

            final KPanel emailPanel = new KPanel(new BorderLayout());
            emailPanel.add(new KPanel(new KLabel("Email Address:", hintFont)), BorderLayout.WEST);
            emailPanel.add(new KPanel(new KLabel("wakadrammeh@gmail.com", valueFont)), BorderLayout.CENTER);

            final KPanel nationalityPanel = new KPanel(new BorderLayout());
            nationalityPanel.add(new KPanel(new KLabel("Nationality:", hintFont)), BorderLayout.WEST);
            nationalityPanel.add(new KPanel(new KLabel("The Gambia", valueFont)), BorderLayout.CENTER);

            final KButton closeButton = new KButton("Ok");
            closeButton.addActionListener(e-> dispose());

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(firstNamePanel, lastNamePanel, dobPanel, pobPanel, addressPanel, telephonePanel,
                    emailPanel, nationalityPanel, MComponent.contentBottomGap(),
                    new KPanel(new FlowLayout(FlowLayout.RIGHT), closeButton));

            getRootPane().setDefaultButton(closeButton);
            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(getRootPane());
        }

    }

}
