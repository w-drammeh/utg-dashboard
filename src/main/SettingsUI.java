package main;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Objects;

public class SettingsUI implements Activity {
    private static KPanel aboutComponent;
    private static KLabel msLabel;
    private static KLabel pobLabel;
    private static KLabel fNameLabel;
    private static KLabel lNameLabel;
    private static KLabel natLabel;
    private static KLabel addressLabel;
    private static KCheckBox userChecking;
    private static KCheckBox exitChecking;
    private static KCheckBox instantToolTip;
    private static KCheckBox tipDismissible;
    private static KCheckBox syncChecking;
    private static JComboBox<String> nameFormatBox, bgBox, looksBox;
    private static final String changeHint = "Enter your Matriculation Number to effect this changes:";
    private static final Font H_FONT = KFontFactory.createBoldFont(16);//generally for the hints
    private static final Font V_FONT = KFontFactory.createPlainFont(17);//for the values
    public static final KTextField minorField = new KTextField(new Dimension(320,30));
    public static final KLabel minorLabel = new KLabel(Student.getMinor(), V_FONT);
    public static final KTextField majorCodeField = KTextField.rangeControlField(3);
    public static final KTextField minorCodeField = KTextField.rangeControlField(3);
    public static final KTextField studentMailField = new KTextField(Student.getStudentMail());
    public static final JPasswordField studentPsswdField = new JPasswordField(Student.getStudentPassword());
    public static final KTextArea descriptionArea = KTextArea.getLimitedEntryArea(1_000);


    public SettingsUI(){
        final JTabbedPane settingsTab = new JTabbedPane();
        settingsTab.add(aboutComponent());
        settingsTab.add(profileComponent());
        settingsTab.add(settingsComponent());

        final Font tabFont = KFontFactory.createPlainFont(17);
        final Color tabColor = Color.BLUE;
        settingsTab.setTabComponentAt(0, new KLabel("About Me", tabFont, tabColor));
        settingsTab.setTabComponentAt(1, new KLabel("Customize Profile", tabFont, tabColor));
        settingsTab.setTabComponentAt(2, new KLabel("Customize Dashboard", tabFont, tabColor));

        final KPanel settingsUI = new KPanel(new BorderLayout());
        settingsUI.add(new KPanel(new KLabel("Personalization", KFontFactory.BODY_HEAD_FONT, Color.BLUE)),
                BorderLayout.NORTH);
        settingsUI.add(settingsTab, BorderLayout.CENTER);

        Board.addCard(settingsUI, "Settings");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Settings");
    }

    private static JComponent aboutComponent(){
        final KButton changeButton = new KButton("Change");
        changeButton.setFont(KFontFactory.createPlainFont(14));
        changeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeButton.addActionListener(e-> Student.startSettingImage());

        final KButton resetButton = new KButton("Remove");
        resetButton.setForeground(Color.RED);
        resetButton.setFont(changeButton.getFont());
        resetButton.addActionListener(e-> {
            if (Student.isDefaultIconSet()) {
                App.reportInfo("No Image",
                        "No image is currently set. The default is already in use.");
            } else {
                Student.fireIconReset();
            }
        });

        final KPanel imagePanel = new KPanel(new BorderLayout());
        imagePanel.add(new KPanel(new KLabel("Image Options:", H_FONT)), BorderLayout.WEST);
        imagePanel.add(new KPanel(changeButton, resetButton), BorderLayout.CENTER);

        final KPanel fNamePanel = new KPanel(new BorderLayout());
        fNamePanel.add(new KPanel(new KLabel("First Name:", H_FONT)), BorderLayout.WEST);
        fNameLabel = new KLabel(Student.getFirstName(), V_FONT);
        fNamePanel.add(new KPanel(fNameLabel), BorderLayout.CENTER);

        final KPanel lNamePanel = new KPanel(new BorderLayout());
        lNamePanel.add(new KPanel(new KLabel("Last Name:", H_FONT)), BorderLayout.WEST);
        lNameLabel = new KLabel(Student.getLastName(), V_FONT);
        lNamePanel.add(new KPanel(lNameLabel), BorderLayout.CENTER);

        final KPanel natPanel = new KPanel(new BorderLayout());
        natPanel.add(new KPanel(new KLabel("Nationality:", H_FONT)), BorderLayout.WEST);
        natLabel = new KLabel(Student.getNationality(), V_FONT);
        natPanel.add(new KPanel(natLabel), BorderLayout.CENTER);

        final KPanel schoolPanel = new KPanel(new BorderLayout());
        schoolPanel.add(new KPanel(new KLabel("School:", H_FONT)), BorderLayout.WEST);
        schoolPanel.add(new KPanel(new KLabel(Student.getSchool(), V_FONT)), BorderLayout.CENTER);

        final KPanel depPanel = new KPanel(new BorderLayout());
        depPanel.add(new KPanel(new KLabel("Department / Faculty:", H_FONT)), BorderLayout.WEST);
        depPanel.add(new KPanel(new KLabel(Student.getDivision(), V_FONT)), BorderLayout.CENTER);

        final KPanel progPanel = new KPanel(new BorderLayout());
        progPanel.add(new KPanel(new KLabel("Program:", H_FONT)), BorderLayout.WEST);
        progPanel.add(new KPanel(new KLabel(Student.getMajor(), V_FONT)), BorderLayout.CENTER);

        final KPanel minPanel = new KPanel(new BorderLayout());
        minPanel.add(new KPanel(new KLabel("Minor:", H_FONT)), BorderLayout.WEST);
        minPanel.add(new KPanel(minorLabel), BorderLayout.CENTER);

        final KPanel yoaPanel = new KPanel(new BorderLayout());
        yoaPanel.add(new KPanel(new KLabel("Year of Admission:", H_FONT)), BorderLayout.WEST);
        yoaPanel.add(new KPanel(new KLabel(Integer.toString(Student.getYearOfAdmission()), V_FONT)),
                BorderLayout.CENTER);

        final KPanel moaPanel = new KPanel(new BorderLayout());
        moaPanel.add(new KPanel(new KLabel("Month of Admission:", H_FONT)), BorderLayout.WEST);
        moaPanel.add(new KPanel(new KLabel(Student.getMonthOfAdmissionName(), V_FONT)), BorderLayout.CENTER);

        final KPanel eygPanel = new KPanel(new BorderLayout());
        eygPanel.add(new KPanel(new KLabel("Expected Year of Graduation:", H_FONT)), BorderLayout.WEST);
        eygPanel.add(new KPanel(new KLabel(Integer.toString(Student.getExpectedYearOfGraduation()), V_FONT)),
                BorderLayout.CENTER);

        final KPanel levelPanel = new KPanel(new BorderLayout());
        levelPanel.add(new KPanel(new KLabel("Level:", H_FONT)), BorderLayout.WEST);
        levelPanel.add(new KPanel(new KLabel(Student.getLevel(), V_FONT)), BorderLayout.CENTER);

        final KPanel bdPanel = new KPanel(new BorderLayout());
        bdPanel.add(new KPanel(new KLabel("Birthday:", H_FONT)), BorderLayout.WEST);
        bdPanel.add(new KPanel(new KLabel(Student.getDateOfBirth(), V_FONT)), BorderLayout.CENTER);

        pobLabel = new KLabel(Student.getPlaceOfBirth(), V_FONT) {
            @Override
            public void setText(String text) {
                super.setText(text);
                Student.setPlaceOfBirth(text);
            }
        };
        final KPanel pobPanel = new KPanel(new BorderLayout());
        pobPanel.add(new KPanel(new KLabel("Place of Birth:", H_FONT)), BorderLayout.WEST);
        pobPanel.add(new KPanel(pobLabel), BorderLayout.CENTER);

        final KPanel addressPanel = new KPanel(new BorderLayout());
        addressPanel.add(new KPanel(new KLabel("Address:", H_FONT)), BorderLayout.WEST);
        addressLabel = new KLabel(Student.getAddress(), V_FONT);
        addressPanel.add(new KPanel(addressLabel), BorderLayout.CENTER);

        msLabel = new KLabel(Student.getMaritalStatue(), V_FONT){
            @Override
            public void setText(String text) {
                super.setText(text);
                Student.setMaritalStatue(text);
            }
        };
        final KPanel msPanel = new KPanel(new BorderLayout());
        msPanel.add(new KPanel(new KLabel("Marital Status:", H_FONT)), BorderLayout.WEST);
        msPanel.add(new KPanel(msLabel), BorderLayout.CENTER);

        final FlowLayout contactLabelsLayout = new FlowLayout();
        contactLabelsLayout.setHgap(15);
        final KPanel contactLabelsPanel = new KPanel(contactLabelsLayout);
        contactLabelsPanel.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                final KLabel label = (KLabel) e.getChild();//see to it that only such are added
                final String actualDial = label.getText();
                label.setStyle(V_FONT, Color.BLUE);
                label.underline(false);
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (App.showYesNoCancelDialog("Confirm",
                                "Are you sure you want to remove \""+actualDial+"\" from your list of contacts?")) {
                            contactLabelsPanel.remove(label);
                            MComponent.ready(contactLabelsPanel);
                        }
                    }
                });
                Student.addTelephone(actualDial);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                Student.removeTelephone(((KLabel) e.getChild()).getText());
            }
        });

        final KButton contactButton = KButton.createIconifiedButton("plus.png", 20, 20);
        contactButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contactButton.setToolTipText("Add Number");
        contactButton.addActionListener(e-> {
            if (Student.telephonesCount() >= 4) {
                App.reportError("Error", "Sorry, you cannot add more than four (4) contacts.\n" +
                        "You can remove a contact by clicking on it.");
                return;
            }
            final String incomingDial = App.requestInput("New Telephone", "Enter the new contact number:");
            if (incomingDial == null) {
                return;
            }
            if (Student.alreadyInContacts(incomingDial)) {
                App.reportError("Duplicate", incomingDial+" is already taken. Please enter a different number.");
                return;
            }

            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                contactLabelsPanel.add(new KLabel(incomingDial));
                MComponent.ready(contactLabelsPanel);
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        });
//        notice, at this point, telephones are initialized whether from Portal, or Serial
        if (Globals.hasText(Student.getTelephones())) {
            final String[] dials = Student.getTelephones().split("/");
            for (String dial : dials) {
                contactLabelsPanel.add(new KLabel(dial));
            }
        }
        final KPanel telPanel = new KPanel(new BorderLayout());
        telPanel.add(new KPanel(new KLabel("Telephones:", H_FONT)), BorderLayout.WEST);
        telPanel.add(new KPanel(contactLabelsPanel), BorderLayout.CENTER);
        telPanel.add(new KPanel(contactButton), BorderLayout.EAST);

//        postpone the extras
        Board.POST_PROCESSES.add(()-> {
            final LinkedHashMap<String, String> extra = Student.getAdditional();
            for (String key : extra.keySet()) {
                acceptUserDetail(key, extra.get(key));
            }
        });

        aboutComponent = new KPanel();
        aboutComponent.setLayout(new BoxLayout(aboutComponent, BoxLayout.Y_AXIS));
        aboutComponent.addAll(imagePanel, fNamePanel, lNamePanel, natPanel, schoolPanel, depPanel, progPanel, minPanel,
                yoaPanel, moaPanel, eygPanel, levelPanel, bdPanel, pobPanel, addressPanel, msPanel, telPanel,
                new KPanel(new KSeparator(new Dimension(750, 1))));
//        notice the last child... the custom details are added by index and over this
//        now
        if (Student.isTrial()) {
            aboutComponent.removeAll(schoolPanel, depPanel, progPanel, minPanel, yoaPanel, moaPanel, eygPanel,
                    levelPanel);
        }
        aboutComponent.add(MComponent.contentBottomGap());
        return new KScrollPane(aboutComponent);
    }

    private static void acceptUserDetail(String key, String value) {
        final KLabel valueLabel = new KLabel(value, V_FONT);

        final KButton editButton = KButton.createIconifiedButton("edit.png",25,25);
        editButton.setPreferredSize(new Dimension(25, 25));
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.setToolTipText("Edit");
        editButton.addActionListener(e-> {
            final String update = App.requestInput(key, "Update "+key+". Enter new value:");
            if (update == null) {
                return;
            }
            final int vInt = App.verifyUser(changeHint);
            if (vInt == App.VERIFICATION_TRUE) {
                valueLabel.setText(update);
                Student.getAdditional().put(key, update);
            } else if (vInt == App.VERIFICATION_FALSE) {
                App.reportMatError();
            }
        });

        final KPanel panel = new KPanel(new BorderLayout());

        final KButton removeButton = KButton.createIconifiedButton("terminate.png",20,20);
        removeButton.setPreferredSize(new Dimension(25, 25));
        removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeButton.setToolTipText("Remove");
        removeButton.addActionListener(e-> {
            if (App.showYesNoCancelDialog("Confirm", "Are you sure you want to remove '"+key+"'?")) {
                aboutComponent.remove(panel);
                MComponent.ready(aboutComponent);
                Student.getAdditional().remove(key);
            }
        });

        panel.add(new KPanel(new KLabel(key+":", H_FONT)), BorderLayout.WEST);
        panel.add(new KPanel(new KLabel(value, V_FONT)), BorderLayout.CENTER);
        panel.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 15, 0), editButton, removeButton), BorderLayout.EAST);
        aboutComponent.add(panel,aboutComponent.getComponentCount() - 1);
        aboutComponent.add(Box.createVerticalStrut(10),aboutComponent.getComponentCount() - 1);//i.e second to last
    }

    private static JComponent profileComponent(){
        final KPanel profileUI = new KPanel();
        profileUI.setLayout(new BoxLayout(profileUI, BoxLayout.Y_AXIS));
        if (Student.isTrial()) {
            final KTextField fNameField = new KTextField(Student.getFirstName());
            fNameField.setPreferredSize(new Dimension(320, 30));
            fNameField.setEditable(false);
            final ActionListener fNameEditorListener = e-> {
                final String newFName = App.requestInput("First Name", "Enter your first name:");
                if (newFName != null) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setFirstName(newFName);
                        fNameField.setText(newFName);
                        Board.effectNameFormatChanges();
                        fNameLabel.setText(newFName);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel fNamePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            fNamePanel.addAll(new KLabel("First Name:", H_FONT),
                    new KPanel(fNameField, newIconifiedEditButton(fNameEditorListener)));

            final KTextField lNameField = new KTextField(Student.getLastName());
            lNameField.setPreferredSize(new Dimension(320, 30));
            lNameField.setEditable(false);
            final ActionListener lNameEditorListener = e-> {
                final String newLName = App.requestInput("Last Name", "Enter your last name:");
                if (newLName != null) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setLastName(newLName);
                        lNameField.setText(newLName);
                        Board.effectNameFormatChanges();
                        lNameLabel.setText(newLName);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel lNamePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            lNamePanel.addAll(new KLabel("Last Name:", H_FONT),
                    new KPanel(lNameField, newIconifiedEditButton(lNameEditorListener)));

            final KTextField natField = new KTextField(Student.getNationality());
            natField.setPreferredSize(new Dimension(320, 30));
            natField.setEditable(false);
            final ActionListener natEditorListener = e-> {
                final String newNat = App.requestInput("Nationality", "Enter your nationality:");
                if (newNat != null) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setNationality(newNat);
                        natField.setText(newNat);
                        natLabel.setText(newNat);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel natPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            natPanel.addAll(new KLabel("Nationality:", H_FONT),
                    new KPanel(natField, newIconifiedEditButton(natEditorListener)));

            final KTextField addressField = new KTextField(Student.getAddress());
            addressField.setPreferredSize(new Dimension(320, 30));
            addressField.setEditable(false);
            final ActionListener addressEditorListener = e-> {
                final String newAddress = App.requestInput("Address", "Enter your address:");
                if (newAddress != null) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setAddress(newAddress);
                        addressField.setText(newAddress);
                        addressLabel.setText(newAddress);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel addressPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            addressPanel.addAll(new KLabel("Address:", H_FONT),
                    new KPanel(addressField, newIconifiedEditButton(addressEditorListener)));

            profileUI.addAll(fNamePanel, lNamePanel, natPanel, addressPanel);
        } else {
            final KTextField portalMailField = new KTextField(Student.getVisiblePortalMail());
            portalMailField.setPreferredSize(new Dimension(325, 30));
            portalMailField.setEditable(false);
            final ActionListener portalMailEditorListener = e-> {
                App.reportInfo("Portal Email","This is the email address Dashboard uses to gain access to your portal.\n" +
                        "You should only change this provided you've changed your email address.");
                final String newPortalMail = App.requestInput("Portal Email","Enter your new Portal Email Address:");
                if (Globals.hasText(newPortalMail)) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setPortalMail(newPortalMail);
                        portalMailField.setText(Student.getVisiblePortalMail());
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel portalMailPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            portalMailPanel.addAll(new KLabel("Portal Email:", H_FONT),
                    new KPanel(portalMailField, newIconifiedEditButton(portalMailEditorListener)));

            final JPasswordField portalPsswdField = new JPasswordField(Student.getPortalPassword());
            portalPsswdField.setPreferredSize(new Dimension(325, 30));
            portalPsswdField.setHorizontalAlignment(SwingConstants.CENTER);
            portalPsswdField.setEditable(false);
            final ActionListener portalPsswdEditorListener = e -> {
                App.reportInfo("Portal Password","This is the password Dashboard uses, in addition to the email above,\n" +
                        "to gain access to your portal. Only change this if you've changed actually your password.");
                final String newPortalPassword = App.requestInput("Portal Password","Enter your new Portal Password:");
                if (Globals.hasText(newPortalPassword)) {
                    final int vInt = App.verifyUser(changeHint);
                    if (vInt == App.VERIFICATION_TRUE) {
                        Student.setPortalPassword(newPortalPassword);
                        portalPsswdField.setText(newPortalPassword);
                    } else if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                    }
                }
            };
            final KPanel portalPsswdPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            portalPsswdPanel.addAll(new KLabel("Portal Password:", H_FONT),
                    new KPanel(portalPsswdField, newIconifiedEditButton(portalPsswdEditorListener)));

            studentMailField.setPreferredSize(new Dimension(325, 30));
            studentMailField.setEditable(false);
            final ActionListener studentMailEditorListener = e-> {
                final String newStudentMail = App.requestInput("New Email","Enter your Student Mail Address:");
                if (newStudentMail == null) {
                    return;
                }

                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    Student.setStudentMail(newStudentMail);
                    studentMailField.setText(newStudentMail);
                    if (Globals.hasNoText(newStudentMail)) {
                        Student.setStudentPassword("");
                        studentPsswdField.setText("");
                    }
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            };
            final KPanel studentMailPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            studentMailPanel.addAll(new KLabel("Student Mail:", H_FONT),
                    new KPanel(studentMailField, newIconifiedEditButton(studentMailEditorListener)));

            studentPsswdField.setPreferredSize(new Dimension(325, 30));
            studentPsswdField.setHorizontalAlignment(SwingConstants.CENTER);
            studentPsswdField.setEditable(false);
            final ActionListener studentPsswdEditorListener = e-> {
                final String newStudentPassword = App.requestInput("New Password",
                        "Enter your Student Mail Password:");
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
            studentPsswdPanel.addAll(new KLabel("Student Mail Password:", H_FONT),
                    new KPanel(studentPsswdField, newIconifiedEditButton(studentPsswdEditorListener)));

            majorCodeField.setPreferredSize(new Dimension(150, 30));
            majorCodeField.setText(Student.getMajorCode());
            majorCodeField.setEditable(false);
            final ActionListener majorCodeEditorListener = e-> {
                final String newMajorCode = App.requestInput("Major Code", "Enter your major-code below.\n" +
                        "Major-code is the 3-letter prefix to the course-codes of your major courses.\n" +
                        "Dashboard uses the major-code for auto-indexing of your program courses.");
                if (newMajorCode == null) {
                    return;
                } else if (Globals.hasNoText(newMajorCode)) {
                    if (!App.showYesNoCancelDialog("Reset Major-Code", "Do you want to reset your major-code?\n" +
                            "Dashboard will no longer be able to detect your program courses.")) {
                        return;
                    }
                } else if (newMajorCode.length() != 3) {
                    App.reportError("Error", "Sorry, that's not a valid program code. Please try again.");
                    return;
                }

                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    Student.setMajorCode(newMajorCode.toUpperCase());
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            };
            final KPanel majorCodePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            majorCodePanel.addAll(new KLabel("Major Code:", H_FONT),
                    new KPanel(majorCodeField, newIconifiedEditButton(majorCodeEditorListener)));

            minorField.setText(Student.getMinor());
            minorField.setEditable(false);
            final ActionListener minorEditorListener = e-> {
                final String newMinor = App.requestInput("Minor",
                        "Add or change your minor program here if you're doing a minor.");
                if (newMinor == null) {
                    return;
                }
                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    Student.setMinor(newMinor);
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            };
            final KPanel minorPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            minorPanel.addAll(new KLabel("Minor:", H_FONT),
                    new KPanel(minorField, newIconifiedEditButton(minorEditorListener)));

            minorCodeField.setPreferredSize(new Dimension(150, 30));
            minorCodeField.setText(Student.getMinorCode());
            minorCodeField.setEditable(false);
            final ActionListener minorCodeEditorListener = e-> {
                if (!Student.isDoingMinor()) {
                    App.reportInfo("No Minor",
                            "To set the minor-code, you first have to set your minor program above.");
                    return;
                }

                final String newMinorCode = App.requestInput("Minor Code", "Enter your minor-code below.\n" +
                        "Minor-code is the 3-letter prefix to the course-codes of your minor courses.\n" +
                        "Dashboard uses the minor-code for auto-indexing of your minor courses.");
                if (newMinorCode == null) {
                    return;
                } else if (Globals.hasNoText(newMinorCode)) {
                    if (!App.showYesNoCancelDialog("Reset Minor-Code", "Do you want to reset your minor-code?\n" +
                            "Dashboard will no longer be able to detect your minor courses.")) {
                        return;
                    }
                } else if (newMinorCode.length() != 3) {
                    App.reportError("Error", "Sorry, that's not a valid program code. Please try again.");
                    return;
                }

                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    Student.setMinorCode(newMinorCode.toUpperCase());
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            };
            final KPanel minorCodePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
            minorCodePanel.addAll(new KLabel("Minor Code:", H_FONT),
                    new KPanel(minorCodeField, newIconifiedEditButton(minorCodeEditorListener)));

            profileUI.addAll(portalMailPanel, portalPsswdPanel, studentMailPanel, studentPsswdPanel, majorCodePanel,
                    minorPanel, minorCodePanel);
        }

        final KTextField msField = new KTextField(Student.getMaritalStatue());
        msField.setPreferredSize(new Dimension(320, 30));
        msField.setEditable(false);
        final ActionListener maritalStatusEditorListener = e-> {
            final String newMaritalStatus = App.requestInput("Marital Status", "Enter your marital status:");
            if (newMaritalStatus != null) {
                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    msLabel.setText(newMaritalStatus);
                    msField.setText(Student.getMaritalStatue());
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            }
        };
        final KPanel msPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        msPanel.addAll(new KLabel("Marital Status:", H_FONT),
                new KPanel(msField, newIconifiedEditButton(maritalStatusEditorListener)));

        final KTextField pobField = new KTextField(Student.getPlaceOfBirth());
        pobField.setPreferredSize(new Dimension(320, 30));
        pobField.setEditable(false);
        final ActionListener pobEditorListener = e-> {
            final String newPlaceOfBirth = App.requestInput("Place of Birth", "Enter your place of birth:");
            if (newPlaceOfBirth != null) {
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
        pobPanel.addAll(new KLabel("Place of Birth:", H_FONT),
                new KPanel(pobField, newIconifiedEditButton(pobEditorListener)));

        final KTextField valueField = new KTextField();
        valueField.setPreferredSize(new Dimension(330, 30));
        final KTextField keyField = new KTextField();
        keyField.setPreferredSize(new Dimension(200, 30));
        keyField.setFont(KFontFactory.createBoldFont(15));
        keyField.addActionListener(e -> {
            if (Globals.hasNoText(valueField.getText())) {
                valueField.requestFocusInWindow();
            } else if (Globals.hasNoText(keyField.getText())) {
                keyField.requestFocusInWindow();
            } else {
                final String key = keyField.getText();
                final String value = valueField.getText();
                if (Student.getAdditional().containsKey(key)) {
                    App.reportInfo(key, key+" is already in your custom details.\n" +
                            "You can update or remove it at the 'About Me' tab.");
                    return;
                }

                final int vInt = App.verifyUser(changeHint);
                if (vInt == App.VERIFICATION_TRUE) {
                    acceptUserDetail(key, value);
                    Student.getAdditional().put(key, value);
                    App.reportInfo("Successful", "Customize detail "+key+" is been added successfully.");
                    keyField.setText(null);
                    valueField.setText(null);
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            }
        });
        valueField.addActionListener(keyField.getActionListeners()[0]);
        final KPanel craftPanel = new KPanel(new BorderLayout());
        craftPanel.add(new KPanel(new KLabel("Add a Custom Detail:", H_FONT)), BorderLayout.WEST);
        craftPanel.add(new KPanel(new KLabel("Key:", V_FONT), keyField,
                Box.createRigidArea(new Dimension(30,25)),
                new KLabel("Value:", V_FONT), valueField), BorderLayout.CENTER);

        descriptionArea.setText(Student.getAbout());
        descriptionArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                Student.setAbout(descriptionArea.getText());
            }

        });
        final KScrollPane descriptionScroll = descriptionArea.outerScrollPane(new Dimension(865,150));
        final KPanel aboutPanel = new KPanel(new KLabel("About Me:", V_FONT), descriptionScroll);

        profileUI.addAll(msPanel, pobPanel, craftPanel, aboutPanel, MComponent.contentBottomGap());
        return new KScrollPane(profileUI);
    }

    private static KButton newIconifiedEditButton(ActionListener actionListener){
        final KButton button = KButton.createIconifiedButton("edit.png",25, 25);
        button.addActionListener(actionListener);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Edit");
        return button;
    }

//    Todo: add re-index resources functionality for students
    private static JComponent settingsComponent() {
        final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        final int hereGap = 10;

        userChecking = new KCheckBox("Two-Step Verification", !Settings.noVerifyNeeded);
        userChecking.setIconTextGap(hereGap);
        userChecking.setFont(H_FONT);
        userChecking.setCursor(handCursor);
        userChecking.addActionListener(e-> {
            if (userChecking.isSelected()) {
                Settings.noVerifyNeeded = false;
            } else {
                final String matString = App.requestInput("Confirm",
                        "Dashboard will not ask for your verification in making sensitive changes.\n" +
                        "Enter your mat number if you wish to continue with this changes:");
                if (!Globals.hasText(matString)) {
                    userChecking.setSelected(true);
                    return;
                }

                if (Objects.equals(matString, Student.getMatNumber())) {
                    Settings.noVerifyNeeded = true;
                } else {
                    App.reportMatError();
                    userChecking.setSelected(true);
                }
            }
        });
        userChecking.setEnabled(!Student.isTrial());

        exitChecking = new KCheckBox("Confirm on Exit", Settings.confirmExit);
        exitChecking.setIconTextGap(hereGap);
        exitChecking.setFont(H_FONT);
        exitChecking.setCursor(handCursor);
        exitChecking.addItemListener(e-> Settings.confirmExit = (e.getStateChange() == ItemEvent.SELECTED));

        instantToolTip = new KCheckBox("Instantly Show Tooltips",
                ToolTipManager.sharedInstance().getInitialDelay() == 0);
        instantToolTip.setIconTextGap(hereGap);
        instantToolTip.setFont(H_FONT);
        instantToolTip.setCursor(handCursor);
        instantToolTip.addItemListener(e-> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ToolTipManager.sharedInstance().setInitialDelay(0);
            } else {
                ToolTipManager.sharedInstance().setInitialDelay(750);
            }
        });

        tipDismissible = new KCheckBox("Allow Tooltip Dismiss",
                ToolTipManager.sharedInstance().getDismissDelay() == 4_000);
        tipDismissible.setIconTextGap(hereGap);
        tipDismissible.setFont(H_FONT);
        tipDismissible.setCursor(handCursor);
        tipDismissible.addItemListener(e-> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ToolTipManager.sharedInstance().setDismissDelay(4_000);
            } else {
                ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
            }
        });

        syncChecking = new KCheckBox("Automatic syncing of Portal Resources", Portal.isAutoSynced());
        syncChecking.setIconTextGap(hereGap);
        syncChecking.setFont(H_FONT);
        syncChecking.setCursor(handCursor);
        syncChecking.addItemListener(e-> Portal.setAutoSync((e.getStateChange() == ItemEvent.SELECTED)));

        nameFormatBox = new JComboBox<String>(new String[]{"First Name first", "Last Name first"}) {
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        nameFormatBox.setFont(KFontFactory.createPlainFont(15));
        nameFormatBox.setSelectedItem(Student.currentNameFormat());
        nameFormatBox.setCursor(handCursor);
        nameFormatBox.addActionListener(e-> {
            final String selectedFormat = String.valueOf(nameFormatBox.getSelectedItem());
            Student.setNameFormat(selectedFormat);
        });
        final KPanel nameFormatPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        nameFormatPanel.addAll(new KLabel("Change Name Format:", H_FONT),
                Box.createRigidArea(new Dimension(30,25)), nameFormatBox);

        bgBox = new JComboBox<String>(Settings.backgroundNames()) {
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        bgBox.setFont(KFontFactory.createPlainFont(15));
        bgBox.setSelectedItem(Settings.currentBackgroundName());
        bgBox.setCursor(handCursor);
        bgBox.addActionListener(e-> SwingUtilities.invokeLater(()-> {
            bgBox.setEnabled(false);
            Settings.backgroundName = String.valueOf(bgBox.getSelectedItem());
            effectBackgroundChanges();
            bgBox.setEnabled(true);
        }));
        final KPanel bgPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        bgPanel.addAll(new KLabel("Change Background:", H_FONT),
                Box.createRigidArea(new Dimension(30,25)), bgBox);

        looksBox = new JComboBox<String>(Settings.getLookNames()) {
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        looksBox.setFont(KFontFactory.createPlainFont(15));
        looksBox.setFocusable(false);
        looksBox.setSelectedItem(Settings.currentLookName());
        looksBox.setCursor(handCursor);
        looksBox.addActionListener(e -> SwingUtilities.invokeLater(()-> {
            looksBox.setEnabled(false);
            final String selectedName = String.valueOf(looksBox.getSelectedItem());
            setLookTo(selectedName);
            Settings.lookName = selectedName;
            looksBox.setEnabled(true);
        }));
        final KPanel lafPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        lafPanel.addAll(new KLabel("Change Look & Feel:", H_FONT),
                Box.createRigidArea(new Dimension(30,25)), looksBox);

        final KLabel signOutLabel = newSignLabel("Sign out");
        signOutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (App.showYesNoCancelDialog("Sign out", "Are are sure you want to sign out?\n" +
                        "By signing out, all your data will be lost.")) {
                    final String matNumber = App.requestInput("Confirm","Enter your matriculation number to sign out:");
                    if (Globals.hasText(matNumber)) {
                        if (matNumber.equals(Student.getMatNumber())) {
                            if (Serializer.unMountUserData()) {
                                Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_THREAD);
                                Board.getInstance().dispose();
                                System.exit(0);
                            } else {
                                App.reportError("Error",
                                        "Unusual error encountered un-mounting the serializable files.\n" +
                                        "If there is any process using the Dashboard directory, stop it, and try signing out again.");
                            }
                        } else {
                            App.reportMatError();
                        }
                    }
                }
            }
        });

        final KLabel signInLabel = newSignLabel("Sign in");
        signInLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login.loginAction(signInLabel);
            }
        });

        final KPanel layoutPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        layoutPanel.addAll(new KLabel("You may wish to:", H_FONT),
                Box.createRigidArea(new Dimension(30,25)),
                Student.isTrial() ? signInLabel : signOutLabel);

        final KPanel homeOfNice = new KPanel();
        homeOfNice.setLayout(new BoxLayout(homeOfNice, BoxLayout.Y_AXIS));
        homeOfNice.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), userChecking),
                new KPanel(new FlowLayout(FlowLayout.LEFT), exitChecking),
                new KPanel(new FlowLayout(FlowLayout.LEFT), instantToolTip),
                new KPanel(new FlowLayout(FlowLayout.LEFT), tipDismissible),
                new KPanel(new FlowLayout(FlowLayout.LEFT), syncChecking),
                nameFormatPanel, bgPanel, lafPanel, layoutPanel);

        final KButton resetButton = new KButton("Reset Settings");
        resetButton.setFont(KFontFactory.createPlainFont(15));
        resetButton.setCursor(handCursor);
        resetButton.addActionListener(e-> {
            if (App.showYesNoCancelDialog("Reset",
                    "This action will restore the default developer settings. Continue?")) {
                loadDefaults();
            }
        });

        final KPanel dashUI = new KPanel(new BorderLayout());
        dashUI.add(new KScrollPane(homeOfNice), BorderLayout.CENTER);
        dashUI.add(new KPanel(new FlowLayout(FlowLayout.RIGHT), resetButton), BorderLayout.SOUTH);
        return dashUI;
    }

    private static KLabel newSignLabel(String text){
        final KLabel label = new KLabel(text, KFontFactory.createPlainFont(16), Color.BLUE);
        label.underline(false);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    public static void setLookTo(String lookName) {
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : Settings.allLooksInfo) {
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
                    App.reportError(e1.getClass().getSimpleName(),
                            "Unexpected error occurred setting the the UI to "+lookName+"\n" +
                            "Error Message: "+e1.getMessage());
                }
                break;
            }
        }
    }

    private static void effectBackgroundChanges() {
        final Color background = Settings.currentBackground();
        for (KPanel panel : KPanel.ALL_PANELS) {
            if (panel.getReflectTheme()) {
                panel.setBackground(background);
            }
        }
    }

    public static void loadDefaults() {
        userChecking.setSelected(true);
        Settings.noVerifyNeeded = false;
        exitChecking.setSelected(true);
        Settings.confirmExit = true;
        instantToolTip.setSelected(true);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        tipDismissible.setSelected(true);
        ToolTipManager.sharedInstance().setDismissDelay(4_000);
        syncChecking.setSelected(false);
        Portal.setAutoSync(false);
        SwingUtilities.invokeLater(()-> {
            nameFormatBox.setSelectedIndex(0);
            looksBox.setSelectedIndex(0);
            bgBox.setSelectedIndex(0);
        });
    }

}
