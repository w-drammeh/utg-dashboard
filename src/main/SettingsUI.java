package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Objects;

public class SettingsUI implements ActivityAnswerer{
    public static final KTextField majorCodeField = KTextField.rangeControlField(3);
    public static final KTextField minorField = new KTextField(new Dimension(320,30));
    public static final KTextField minorCodeField = KTextField.rangeControlField(3);
    public static final KTextField studentMailField = new KTextField(Student.getStudentMail());
    public static final JPasswordField studentPsswdField = new JPasswordField(Student.getStudentPassword());
    public static final KTextArea descriptionArea = KTextArea.limitedEntry(1_000);
    private static final String changeHint = "To effect this changes, you need to enter your matriculation number:\n \n";
    private static final LinkedHashMap<String, String> ADDITIONAL_DATA = new LinkedHashMap<>();
    private static final Font hFont = KFontFactory.createBoldFont(16);//generally for the hints
    private static final Font dFont = KFontFactory.createPlainFont(16);//values
    //
    public static final KLabel minorLabel = new KLabel(Student.getMinor(),dFont){
        @Override
        public void setText(String newText) {
            super.setText(newText);
            Student.setMinor(newText);
        }
    };
    private static KPanel detailsWrapper;
    /**
     * For technical harboring of the contacts as labels. An attempt to add a component to this panel other than a KLabel
     * will throw an IllegalArgumentException!
     */
    private static KPanel telsPlate;
    private static KLabel msLabel, pobLabel;
    //
    private static KCheckBox userChecking, exitChecking, instantToolTip, tipDismissible, syncChecking;
    private static JComboBox<String> nameFormatBox, bgBox, looksBox;


    public SettingsUI(){
        final KTabbedPane settingsTab = new KTabbedPane();
        settingsTab.add(new KScrollPane(userTabComponent(),false));
        settingsTab.add(new KScrollPane(profileTabComponent(),false));
        settingsTab.add(dashboardTabComponent());

        final Font tabFont = KFontFactory.createPlainFont(16);
        settingsTab.setTabComponentAt(0, new KLabel("About Me", tabFont));
        settingsTab.setTabComponentAt(1, new KLabel("Customize Profile", tabFont));
        settingsTab.setTabComponentAt(2, new KLabel("Customize Dashboard", tabFont));

        final KPanel settingsUI = new KPanel(new BorderLayout());
        settingsUI.add(KPanel.wantDirectAddition(new KLabel("Personalization",
                KFontFactory.createPlainFont(20), Color.BLUE)), BorderLayout.NORTH);
        settingsUI.add(settingsTab,BorderLayout.CENTER);

        Board.addCard(settingsUI, "Settings");
    }

    private static JComponent userTabComponent(){
        final KButton changeButton = new KButton("Change");
        changeButton.setFont(KFontFactory.createPlainFont(14));
        changeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeButton.addActionListener(e -> {
            Student.startSettingImage();
        });
        final KButton resetButton = new KButton("Remove");
        resetButton.setForeground(Color.RED);
        resetButton.setFont(changeButton.getFont());
        resetButton.addActionListener(e -> {
            if (Student.isNoIconSet()) {
                App.promptPlain("No Image","No image is currently set. The default  is in use.");
            } else {
                Student.fireIconReset();
            }
        });
        final KPanel buttonsPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.addAll(changeButton, resetButton);
        final KPanel imgOptoinsPanel = new KPanel(new BorderLayout());
        imgOptoinsPanel.add(KPanel.wantDirectAddition(new KLabel("Image Options", hFont)), BorderLayout.WEST);
        imgOptoinsPanel.add(buttonsPanel, BorderLayout.CENTER);

        final KPanel fNamePanel = new KPanel(new BorderLayout());
        fNamePanel.add(KPanel.wantDirectAddition(new KLabel("First Name:",hFont)),BorderLayout.WEST);
        fNamePanel.add(KPanel.wantDirectAddition(new KLabel(Student.getFirstName(),dFont)),BorderLayout.CENTER);

        final KPanel lNamePanel = new KPanel(new BorderLayout());
        lNamePanel.add(KPanel.wantDirectAddition(new KLabel("Last Name:",hFont)),BorderLayout.WEST);
        lNamePanel.add(KPanel.wantDirectAddition(new KLabel(Student.getLastName(),dFont)),BorderLayout.CENTER);

        final KPanel natPanel = new KPanel(new BorderLayout());
        natPanel.add(KPanel.wantDirectAddition(new KLabel("Nationality:",hFont)),BorderLayout.WEST);
        natPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getNationality(),dFont)),BorderLayout.CENTER);

        final KPanel schoolPanel = new KPanel(new BorderLayout());
        schoolPanel.add(KPanel.wantDirectAddition(new KLabel("School:",hFont)),BorderLayout.WEST);
        schoolPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getSchool(),dFont)),BorderLayout.CENTER);

        final KPanel depPanel = new KPanel(new BorderLayout());
        depPanel.add(KPanel.wantDirectAddition(new KLabel("Department / Faculty:",hFont)),BorderLayout.WEST);
        depPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getDepartment(),dFont)),BorderLayout.CENTER);

        final KPanel progPanel = new KPanel(new BorderLayout());
        progPanel.add(KPanel.wantDirectAddition(new KLabel("Program:",hFont)),BorderLayout.WEST);
        progPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getMajor(),dFont)),BorderLayout.CENTER);

        final KPanel minPanel = new KPanel(new BorderLayout());
        minPanel.add(KPanel.wantDirectAddition(new KLabel("Minor:",hFont)),BorderLayout.WEST);
        minPanel.add(KPanel.wantDirectAddition(minorLabel),BorderLayout.CENTER);

        final KPanel yoaPanel = new KPanel(new BorderLayout());
        yoaPanel.add(KPanel.wantDirectAddition(new KLabel("Year of Admission:",hFont)),BorderLayout.WEST);
        yoaPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getYearOfAdmission()+"",dFont)),BorderLayout.CENTER);

        final KPanel moaPanel = new KPanel(new BorderLayout());
        moaPanel.add(KPanel.wantDirectAddition(new KLabel("Month of Admission:",hFont)),BorderLayout.WEST);
        moaPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getMonthOfAdmission_Extended(),dFont)),BorderLayout.CENTER);

        final KPanel eygPanel = new KPanel(new BorderLayout());
        eygPanel.add(KPanel.wantDirectAddition(new KLabel("Expected Year of Graduation:",hFont)),BorderLayout.WEST);
        eygPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getExpectedYearOfGraduation()+"",dFont)),BorderLayout.CENTER);

        final KPanel levelPanel = new KPanel(new BorderLayout());
        levelPanel.add(KPanel.wantDirectAddition(new KLabel("Level:",hFont)),BorderLayout.WEST);
        levelPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getLevel()+"",dFont)),BorderLayout.CENTER);

        final KPanel bdPanel = new KPanel(new BorderLayout());
        bdPanel.add(KPanel.wantDirectAddition(new KLabel("Birthday:",hFont)),BorderLayout.WEST);
        bdPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getDateOfBirth(),dFont)),BorderLayout.CENTER);

        pobLabel = new KLabel(Student.getPlaceOfBirth(),dFont){
            @Override
            public void setText(String text) {
                super.setText(text);
                Student.setPlaceOfBirth(text);
            }
        };
        final KPanel bpPanel = new KPanel(new BorderLayout());
        bpPanel.add(KPanel.wantDirectAddition(new KLabel("Place of Birth:",hFont)),BorderLayout.WEST);
        bpPanel.add(KPanel.wantDirectAddition(pobLabel),BorderLayout.CENTER);

        final KPanel addressPanel = new KPanel(new BorderLayout());
        addressPanel.add(KPanel.wantDirectAddition(new KLabel("Address:",hFont)),BorderLayout.WEST);
        addressPanel.add(KPanel.wantDirectAddition(new KLabel(Student.getAddress(),dFont)),BorderLayout.CENTER);

        msLabel = new KLabel(Student.getMaritalStatue(),dFont){
            @Override
            public void setText(String text) {
                super.setText(text);
                Student.setMaritalStatue(text);
            }
        };
        final KPanel msPanel = new KPanel(new BorderLayout());
        msPanel.add(KPanel.wantDirectAddition(new KLabel("Marital Status:",hFont)),BorderLayout.WEST);
        msPanel.add(KPanel.wantDirectAddition(msLabel),BorderLayout.CENTER);

        telsPlate = new KPanel(new FlowLayout(FlowLayout.CENTER)){
            @Override
            public Component add(Component comp) {
                if (comp instanceof KLabel) {
                    final String t = ((KLabel) comp).getText();
                    final KLabel label = new KLabel(" "+t+" ");
                    label.setFont(dFont);
                    label.setForeground(Color.BLUE);
                    label.underline(null,false);
                    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (App.showYesNoDialog("Confirm","Do you want to remove "+t+" from your contacts?")) {
                                Student.removeTelephone(t);
                                telsPlate.remove(label);
                                ComponentAssistant.ready(telsPlate);
                            }
                        }
                    });
                    Student.addTelephone(t);
                    return super.add(label);
                } else {
                    throw new IllegalArgumentException("Only labels are to be added here");
                }
            }
        };
        //Notice, at this point, telephones are initialized whether from Portal, or Serial
        if (!Globals.isBlank(Student.getTelephones())) {
            final String[] dials = Student.getTelephones().split("/");
            for (String t : dials) {
                telsPlate.add(new KLabel(t));
            }
        }
        final KPanel telPanel = new KPanel(new BorderLayout());
        telPanel.add(KPanel.wantDirectAddition(new KLabel("Telephones:",hFont)),BorderLayout.WEST);
        telPanel.add(KPanel.wantDirectAddition(telsPlate),BorderLayout.CENTER);

        detailsWrapper = new KPanel();
        detailsWrapper.setLayout(new BoxLayout(detailsWrapper, BoxLayout.Y_AXIS));
        detailsWrapper.addAll(imgOptoinsPanel,fNamePanel,lNamePanel,natPanel,schoolPanel,depPanel,progPanel,minPanel,yoaPanel,moaPanel,eygPanel,
                levelPanel,bdPanel,bpPanel,addressPanel,msPanel,telPanel);
        //notice the last child
        detailsWrapper.add(ComponentAssistant.contentBottomGap());

        return detailsWrapper;
    }

    private static void acceptUserDetail(String detailKey, String detailValue){
        final KPanel newPanel = new KPanel(new BorderLayout());
        final KButton removeButton = KButton.getIconifiedButton("terminate.png",10,10);
        removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeButton.setToolTipText("Remove");//+detailKey
        removeButton.addActionListener(e->{
            if (App.showYesNoCancelDialog("Confirm","Are you sure you want to remove the custom detail "+detailKey+"?")) {
                detailsWrapper.remove(newPanel);
                ComponentAssistant.ready(detailsWrapper);
                ADDITIONAL_DATA.remove(detailKey);
            }
        });
        newPanel.add(KPanel.wantDirectAddition(new KLabel(detailKey,hFont)), BorderLayout.WEST);
        newPanel.add(KPanel.wantDirectAddition(new KLabel(detailValue,dFont)), BorderLayout.CENTER);
        newPanel.add(KPanel.wantDirectAddition(removeButton), BorderLayout.EAST);

        detailsWrapper.removeLastChild();
        detailsWrapper.add(newPanel);
        detailsWrapper.add(ComponentAssistant.contentBottomGap());
        ComponentAssistant.ready(detailsWrapper);
        ADDITIONAL_DATA.put(detailKey, detailValue);
    }

    private static JComponent profileTabComponent(){
        final KTextField portalMailField = new KTextField(Student.getPortalMail());
        portalMailField.setPreferredSize(new Dimension(325, 30));
        portalMailField.setEditable(false);
        final ActionListener portalMailEditorListener = e -> {
            if (App.showOkCancelDialog("Portal Email","This is the email address Dashboard uses to gain access to your portal.\n" +
                    "You should only change this provided you've changed your email address.")) {
                final String newPortalMail = App.requestInput("New Email","Enter new Portal E-mail Address:\n \n");
                if (Globals.hasText(newPortalMail)) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setPortalMail(newPortalMail);
                        portalMailField.setText(newPortalMail);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            }
        };
        final KPanel portalMailPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        portalMailPanel.addAll(new KLabel("Portal Email:", hFont),
                KPanel.wantDirectAddition(portalMailField, newIconifiedEditButton(portalMailEditorListener,null)));

        final JPasswordField portalPsswdField = new JPasswordField(Student.getPortalPassword());
        portalPsswdField.setPreferredSize(new Dimension(325, 30));
        portalPsswdField.setHorizontalAlignment(SwingConstants.CENTER);
        portalPsswdField.setEditable(false);
        final ActionListener portalPsswdEditorListener = e -> {
            if (App.showOkCancelDialog("Portal Password","This is the password Dashboard uses, in addition to the email above,\n" +
                    "to gain access to your portal. It's your responsibility to make sure these information are correct, otherwise\n" +
                    "Dashboard won't be able to reach your portal, leaving resources un-synced - not even alerts will be updated.")) {
                final String newPortalPassword = App.requestInput("New Password","Enter new Portal Password:\n \n");
                if (Globals.hasText(newPortalPassword)) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setPortalPassword(newPortalPassword);
                        portalPsswdField.setText(newPortalPassword);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            }
        };
        final KPanel portalPsswdPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        portalPsswdPanel.addAll(new KLabel("Portal Password:", hFont),
                KPanel.wantDirectAddition(portalPsswdField, newIconifiedEditButton(portalPsswdEditorListener, null)));

        studentMailField.setPreferredSize(new Dimension(325, 30));
        studentMailField.setEditable(false);
        final ActionListener studentMailEditorListener = e -> {
            final String newStudentMail = App.requestInput("New Email","Enter new Student Mail Address:\n \n");
            if (newStudentMail == null) {
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                Student.setStudentMail(newStudentMail);
                studentMailField.setText(newStudentMail);
                if (Globals.isBlank(newStudentMail)) {
                    Student.setStudentPassword("");
                    studentPsswdField.setText("");
                }
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel studentMailPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        studentMailPanel.addAll(new KLabel("Student Mail:", hFont),
                KPanel.wantDirectAddition(studentMailField, newIconifiedEditButton(studentMailEditorListener,null)));

        studentPsswdField.setPreferredSize(new Dimension(325, 30));
        studentPsswdField.setHorizontalAlignment(SwingConstants.CENTER);
        studentPsswdField.setEditable(false);
        final ActionListener studentPsswdEditorListener = e -> {
            final String newStudentPassword = App.requestInput("New Password","Enter new Student Mail Password:\n \n");
            if (newStudentPassword == null) {
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                Student.setStudentPassword(newStudentPassword);
                studentPsswdField.setText(newStudentPassword);
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel studentPsswdPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        studentPsswdPanel.addAll(new KLabel("Student Mail Password:", hFont),
                KPanel.wantDirectAddition(studentPsswdField, newIconifiedEditButton(studentPsswdEditorListener, null)));

        majorCodeField.setPreferredSize(new Dimension(150, 30));
        majorCodeField.setText(Student.getMajorCode());
        majorCodeField.setEditable(false);
        final ActionListener majorCodeEditorListener = e->{
            final String newMajorCode = App.requestInput("Major Code", "Enter your major-code below:\n" +
                    "Major-code is the 3-letter prefix to the course-codes of your major courses.\n" +
                    "Dashboard uses the major code for auto-indexing of your program courses.\n" +
                    "For accurate analysis sake, make sure this information is right.\n \n");
            if (newMajorCode == null) {
                return;
            }
            if (newMajorCode.isEmpty()) {
                if (!App.showYesNoCancelDialog("Confirm Reset", "Do you want to reset your major-code?\n" +
                        "Dashboard will no longer be able to detect your program courses.")) {
                    return;
                }
            } else if (newMajorCode.length() != 3) {
                App.signalError("Invalid Code", "Sorry, that's not a valid program code. Please try again.");
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                Student.setMajorCode(newMajorCode.toUpperCase(), true);
                majorCodeField.setText(Student.getMajorCode());
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel majorCodePanel = new KPanel(new FlowLayout(FlowLayout.LEFT), null);
        majorCodePanel.addAll(new KLabel("Major Code:", hFont),
                KPanel.wantDirectAddition(majorCodeField, newIconifiedEditButton(majorCodeEditorListener, null)));

        minorField.setText(Student.getMinor());
        minorField.setEditable(false);
        final ActionListener minorEditorListener = e->{
            final String newMinor = App.requestInput("Minor", "Add or change your minor program here provided you're doing a minor.\n \n");
            if (newMinor == null) {
                return;
            }
            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                minorLabel.setText(newMinor);
                minorField.setText(Student.getMinor());
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel minorPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        minorPanel.addAll(new KLabel("Minor:", hFont),
                KPanel.wantDirectAddition(minorField, newIconifiedEditButton(minorEditorListener, null)));

        minorCodeField.setPreferredSize(new Dimension(150, 30));
        minorCodeField.setText(Student.getMinorCode());
        minorCodeField.setEditable(false);
        final ActionListener minorCodeEditorListener = e->{
            if (!Student.isDoingMinor()) {
                App.promptPlain("No Minor","To set the minor-code, you first have to set your minor program above.");
                return;
            }

            final String newMinorCode = App.requestInput("Minor Code", "Enter your minor-code below:\n" +
                    "Minor-code is the 3-letter prefix to the course-codes of your minor courses.\n" +
                    "Dashboard uses the minor code for auto-indexing of your minor courses.\n" +
                    "For accurate analysis sake, make sure this information is right.\n \n");
            if (newMinorCode == null) {
                return;
            }
            if (newMinorCode.isEmpty()) {
                if (!App.showYesNoCancelDialog("Confirm Reset", "Do you want to reset your minor-code?\n" +
                        "Dashboard will no longer be able to detect your minor courses.")) {
                    return;
                }
            } else if (newMinorCode.length() != 3) {
                App.signalError("Invalid Code", "Sorry, that's not a valid program code. Please try again.");
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                Student.setMinorCode(newMinorCode.toUpperCase(), true);
                ModulesHandler.effectMinorCodeChanges(Student.getMinorCode(), newMinorCode);
                minorCodeField.setText(Student.getMinorCode());
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel minorCodePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        minorCodePanel.addAll(new KLabel("Minor Code:", hFont),
                KPanel.wantDirectAddition(minorCodeField, newIconifiedEditButton(minorCodeEditorListener, null)));

        final KTextField msField = new KTextField(Student.getMaritalStatue());
        msField.setPreferredSize(new Dimension(320, 30));
        msField.setEditable(false);
        msField.addActionListener(e -> {

        });
        final ActionListener maritalStatusEditorListener = e->{
            final String newMaritalStatus = App.requestInput("Marital Status", "Enter your marital status:\n \n");
            if (newMaritalStatus == null) {
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                msLabel.setText(newMaritalStatus);
                msField.setText(Student.getMaritalStatue());
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel msPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        msPanel.addAll(new KLabel("Marital Status:", hFont),
                KPanel.wantDirectAddition(msField, newIconifiedEditButton(maritalStatusEditorListener, null)));

        final KTextField pobField = new KTextField(Student.getPlaceOfBirth());
        pobField.setPreferredSize(new Dimension(320, 30));
        pobField.setEditable(false);
        final ActionListener pobEditorListener = e->{
            final String newPlaceOfBirth = App.requestInput("Place of Birth", "Enter your place of birth:\n \n");
            if (newPlaceOfBirth == null) {
                return;
            }

            if (pobField.getText() != null) {
                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    pobLabel.setText(newPlaceOfBirth);
                    pobField.setText(Student.getPlaceOfBirth());
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            }
        };
        final KPanel pobPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        pobPanel.addAll(new KLabel("Place of Birth:", hFont),
                KPanel.wantDirectAddition(pobField, newIconifiedEditButton(pobEditorListener, null)));

        final KTextField dialField = KTextField.digitPlusRangeControlField(15);
        dialField.setPreferredSize(new Dimension(320, 30));
        dialField.setEditable(false);
        final ActionListener contactAdditionListener = e->{
            if (Student.telephonesCount() >= 4) {
                App.signalError("Error", "Sorry, the maximum number of contacts addible is 4.\n" +
                        "You can remove a contact by clicking on it under the 'About Me' tab,\n" +
                        "then you can retry this action.");
                return;
            }

            final String incomingDial = App.requestInput("New Telephone", "Enter the contact number\n \n");
            if (incomingDial == null) {
                return;
            }
            if (Student.alreadyInContacts(incomingDial)) {
                App.signalError("Duplicate Error", "Sorry, "+incomingDial+" is already taken. Please try with a different number.");
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                telsPlate.add(new KLabel(incomingDial));
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        };
        final KPanel dialPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        dialPanel.addAll(new KLabel("Add Contact:",hFont),
                KPanel.wantDirectAddition(dialField,newIconifiedEditButton(contactAdditionListener, "Add a new Dial")));

        final KTextField valueField = new KTextField();
        valueField.setPreferredSize(new Dimension(330, 30));
        final KTextField keyField = new KTextField();
        keyField.setPreferredSize(new Dimension(200, 30));
        keyField.setFont(KFontFactory.createBoldFont(15));
        keyField.addActionListener(e -> {
            if (Globals.isBlank(valueField.getText())) {
                valueField.requestFocusInWindow();
            } else if (Globals.isBlank(keyField.getText())) {
                keyField.requestFocusInWindow();
            } else {
                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    acceptUserDetail(keyField.getText(),valueField.getText());
                    App.promptPlain("Successful","Customize detail "+keyField.getText()+" added successfully");
                    keyField.setText(null);
                    valueField.setText(null);
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            }
        });
        valueField.addActionListener(keyField.getActionListeners()[0]);
        final KPanel craftPanel = new KPanel(new BorderLayout());
        craftPanel.add(KPanel.wantDirectAddition(new KLabel("Add a Custom Detail:",hFont)), BorderLayout.WEST);
        craftPanel.add(KPanel.wantDirectAddition(new KLabel("Key", dFont), keyField, Box.createRigidArea(new Dimension(30,25)),
                new KLabel("Value", dFont), valueField), BorderLayout.CENTER);

        descriptionArea.setText(Student.getAbout());
        descriptionArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                Student.setAbout(descriptionArea.getText());
            }

        });
        final KScrollPane dWrapper = KScrollPane.getTextAreaScroller(descriptionArea,new Dimension(865,150));
        dWrapper.setBorder(BorderFactory.createLineBorder(Color.BLUE,1,true));

        final KPanel aboutPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
        aboutPanel.addAll(new KLabel("About Me:",KFontFactory.createPlainFont(16), Color.BLUE),dWrapper);

        final KPanel profileUI = new KPanel();
        profileUI.setLayout(new BoxLayout(profileUI, BoxLayout.Y_AXIS));
        profileUI.addAll(portalMailPanel, portalPsswdPanel, studentMailPanel, studentPsswdPanel, majorCodePanel,
                minorPanel, minorCodePanel, msPanel, pobPanel,
                dialPanel, KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER),null,
                        new KSeparator(new Dimension(875,1))),craftPanel,aboutPanel);
        profileUI.add(ComponentAssistant.contentBottomGap());

        return profileUI;
    }

    private static KButton newIconifiedEditButton(ActionListener actionListener, String optionalToolTip){
        final KButton edButton = KButton.getIconifiedButton("edit.png",25,25);
        edButton.addActionListener(actionListener);
        edButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        edButton.setToolTipText(optionalToolTip);

        return edButton;
    }

    private static JComponent dashboardTabComponent(){
        final Cursor hereCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        final int hereGap = 10;

        userChecking = new KCheckBox("Two-Step Verification",!SettingsCore.noVerifyNeeded);
        userChecking.setIconTextGap(hereGap);
        userChecking.setFont(hFont);
        userChecking.setCursor(hereCursor);
        userChecking.addActionListener(e->{
            if (userChecking.isSelected()) {
                SettingsCore.noVerifyNeeded = false;
            } else {
                final String matString = App.requestInput("Confirm","Dashboard will not ask for your verification in making sensitive changes.\n" +
                        "Enter your mat number if you wish to continue with this changes:");
                if (!Globals.hasText(matString)) {
                    userChecking.setSelected(true);
                    return;
                }

                if (Objects.equals(matString, String.valueOf(Student.getMatNumber()))) {
                    SettingsCore.noVerifyNeeded = true;
                } else {
                    App.reportMatError();
                    userChecking.setSelected(true);
                }
            }
        });

        exitChecking = new KCheckBox("Confirm on Exit",SettingsCore.confirmExit);
        exitChecking.setIconTextGap(hereGap);
        exitChecking.setFont(hFont);
        exitChecking.setCursor(hereCursor);
        exitChecking.addItemListener(e -> {
            SettingsCore.confirmExit = (e.getStateChange() == ItemEvent.SELECTED);
        });

        instantToolTip = new KCheckBox("Instantly Show Tooltips",ToolTipManager.sharedInstance().getInitialDelay() == 0);
        instantToolTip.setIconTextGap(hereGap);
        instantToolTip.setFont(hFont);
        instantToolTip.setCursor(hereCursor);
        instantToolTip.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ToolTipManager.sharedInstance().setInitialDelay(0);
            } else {
                ToolTipManager.sharedInstance().setInitialDelay(750);
            }
        });

        tipDismissible = new KCheckBox("Allow Tooltip Dismiss",ToolTipManager.sharedInstance().getDismissDelay() == 4_000);
        tipDismissible.setIconTextGap(hereGap);
        tipDismissible.setFont(hFont);
        tipDismissible.setCursor(hereCursor);
        tipDismissible.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ToolTipManager.sharedInstance().setDismissDelay(4_000);
            } else {
                ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
            }
        });

        syncChecking = new KCheckBox("Allow auto-syncing of Portal Resources",Portal.isAutoSynced());
        syncChecking.setIconTextGap(hereGap);
        syncChecking.setFont(hFont);
        syncChecking.setCursor(hereCursor);
        syncChecking.addItemListener(e -> {
            Portal.setAutoSync((e.getStateChange() == ItemEvent.SELECTED));
        });

        nameFormatBox = new JComboBox<String>(new String[] {"First Name first", "Last Name first"}){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        nameFormatBox.setFont(KFontFactory.createPlainFont(15));
        nameFormatBox.setSelectedItem(Student.currentNameFormat());
        nameFormatBox.setCursor(hereCursor);
        nameFormatBox.addActionListener(e->{
            final String selectedFormat = String.valueOf(nameFormatBox.getSelectedItem());
            Student.setNameFormat(selectedFormat);
        });
        final KPanel nameFormatPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        nameFormatPanel.addAll(new KLabel("Change Name Format:",hFont),Box.createRigidArea(new Dimension(30,25)),nameFormatBox);

        bgBox = new JComboBox<String>(SettingsCore.allBackgroundNames()){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        bgBox.setFont(KFontFactory.createPlainFont(15));
        bgBox.setSelectedItem(SettingsCore.currentBackgroundName());
        bgBox.setCursor(hereCursor);
        bgBox.addActionListener(e->{
            SwingUtilities.invokeLater(()->{
                bgBox.setEnabled(false);
                SettingsCore.backgroundName = String.valueOf(bgBox.getSelectedItem());
                effectBackgroundChanges();
                bgBox.setEnabled(true);
            });
        });
        final KPanel bgPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        bgPanel.addAll(new KLabel("Change Background:",hFont),Box.createRigidArea(new Dimension(30,25)),bgBox);

        looksBox = new JComboBox<String>(SettingsCore.getLookNames()){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        looksBox.setFont(KFontFactory.createPlainFont(15));
        looksBox.setFocusable(false);
        looksBox.setSelectedItem(SettingsCore.currentLookName());
        looksBox.setCursor(hereCursor);
        looksBox.addActionListener(e -> {
            SwingUtilities.invokeLater(()->{
                looksBox.setEnabled(false);
                final String selectedName = String.valueOf(looksBox.getSelectedItem());
                setLookTo(selectedName);
                SettingsCore.lookName = selectedName;
                looksBox.setEnabled(true);
            });
        });
        final KPanel lafPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        lafPanel.addAll(new KLabel("Change Look & Feel:",hFont),Box.createRigidArea(new Dimension(30,25)),looksBox);

        final KButton outButton = new KButton("Sign out");
        outButton.setPreferredSize(new Dimension(125, 30));
        outButton.setStyle(KFontFactory.createBoldFont(15), Color.BLUE);
        outButton.undress();
        outButton.underline(null, true);
        outButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        outButton.addActionListener(e-> {
            if (App.showOkCancelDialog("Sign out?", "By signing out, all your data will be lost, continue?")) {
                final int vInt = App.verifyUser("Please enter your matriculation number below\n \n" +
                        "A successful Sign-out will take you back to the Welcome activity\n" +
                        "Every subsequent launch will take you there until there be a login again\n \n" +
                        "We hope this tool has been of great help to you\n \n");

                if (vInt == App.VERIFICATION_TRUE) {
                    if (MyClass.unMountUserData()) {
                        Board.getInstance().dispose();
                        if (Runtime.getRuntime().removeShutdownHook(Board.shutDownThread)) {
                            Runtime.getRuntime().exit(0);
                        }
                    } else {
                        App.signalError("Error", "Unusual error encountered un-mounting the serializable files.\n" +
                                "If there is any process using the Dashboard directory, stop it, and try signing out again.");
                    }
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            }
        });
        final KPanel outPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        outPanel.addAll(new KLabel("You may wish to:",hFont),Box.createRigidArea(new Dimension(30,25)),outButton);

        final KPanel homeOfNice = new KPanel();
        homeOfNice.setBackground(Color.WHITE);
        homeOfNice.setLayout(new BoxLayout(homeOfNice, BoxLayout.Y_AXIS));
        homeOfNice.addAll(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,userChecking),
                KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,exitChecking),
                KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,instantToolTip),
                KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,tipDismissible),
                KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT),null,syncChecking),
                nameFormatPanel, bgPanel, lafPanel, outPanel);

        final KButton resetButton = new KButton("Restore Settings");
        resetButton.setFont(KFontFactory.createPlainFont(15));
        resetButton.setCursor(hereCursor);
        resetButton.addActionListener(e -> {
            if (App.showYesNoCancelDialog("Confirm Reset","This action will restore the default Developer Settings. Continue?")) {
                loadDefaults();
            }
        });

        final KPanel restPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        restPanel.add(resetButton);

        final KPanel dashUI = new KPanel(new BorderLayout());
        dashUI.add(new KScrollPane(homeOfNice),BorderLayout.CENTER);
        dashUI.add(restPanel,BorderLayout.SOUTH);

        return dashUI;
    }

    public static void serialize(){
        System.out.print("Serializing settingsUI... ");
        MyClass.serialize(ADDITIONAL_DATA, "settingsUI.ser");
        System.out.println("Completed.");
    }

    public static void deSerialize(){
        System.out.print("Deserializing settingsUI... ");
        final LinkedHashMap<String, String> uglyMap = (LinkedHashMap<String, String>) MyClass.deserialize("settingsUI.ser");
        for (String s : uglyMap.keySet()) {
            acceptUserDetail(s, uglyMap.get(s));
        }
        System.out.println("Completed.");
    }

    /**
     * This should be called by only Board, after components are just ready building to effect the
     * former preferences on them
     */
    public static void rememberPreferences(){
        setLookTo(SettingsCore.currentLookName());
        effectBackgroundChanges();
    }

    private static void setLookTo(String lookName){
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : SettingsCore.allLooksInfo) {
            if (lookAndFeelInfo.getName().equals(lookName)) {
                try {
                    for (KFrame frame : KFrame.ALL_FRAMES) {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                        SwingUtilities.updateComponentTreeUI(frame);
                        frame.pack();
                    }
                    for (KDialog dialog : KDialog.ALL_DIALOGS) {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                        SwingUtilities.updateComponentTreeUI(dialog);
                        dialog.pack();
                    }
                } catch (Exception e1) {
                    App.signalError(e1.getClass().getSimpleName(), "Unexpected error occurred setting the the UI to "+lookName+"\n" +
                            "Error Message = "+e1.getMessage());
                }

                break;
            }
        }

        NotificationGenerator.awareLookShift();
    }

    private static void effectBackgroundChanges(){
        final Color bg = SettingsCore.currentBackground();
        for (KPanel panel : KPanel.ALL_PANELS) {
            panel.setBackground(bg);
        }
    }

    public static boolean isDefaultLookSet(){
        return SettingsCore.currentLookName().equals("Metal");
    }

    public static void loadDefaults(){
        userChecking.setSelected(true);
        SettingsCore.noVerifyNeeded = false;
        exitChecking.setSelected(true);
        SettingsCore.confirmExit = true;
        instantToolTip.setSelected(true);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        tipDismissible.setSelected(true);
        ToolTipManager.sharedInstance().setDismissDelay(4_000);
        syncChecking.setSelected(false);
        Portal.setAutoSync(false);
        SwingUtilities.invokeLater(()->{
            nameFormatBox.setSelectedIndex(0);
            looksBox.setSelectedIndex(0);
            bgBox.setSelectedIndex(0);
        });
    }

    @Override
    public void answerActivity() {
        Board.showCard("Settings");
    }

}
