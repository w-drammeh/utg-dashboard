package main;

import proto.*;

import javax.swing.*;
import java.awt.*;

public class ModuleActivity implements Activity {
    private KButton onButton;
    private KLabel indicator;
    private CardLayout residentLayout;//there's a 'residentPanel'
    private ModuleHandler handlerInstance;
    private KButton refreshButton;


    public ModuleActivity() {
        indicator = new KLabel("First Year: "+Student.firstAcademicYear(), KFontFactory.bodyHeaderFont());

        final KButton tipShower = KButton.getIconifiedButton("warn.png", 25, 25);
        tipShower.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tipShower.addActionListener(e-> App.promptPlain("Module Collection", "For more options, simply right-click on the tables.\n" +
                "To Edit, Remove, Verify, or Show the Details of a course, just right-click on it and choose the corresponding\n" +
                "option from the Popup Menu.\n \n" +
                "You can navigate through your collection by using the buttons outlined in the left-most panel.\n" +
                "If Dashboard cannot determine the academic year & semester of a course relative to your level,\n" +
                "it will be pushed to the Miscellaneous Table.\n \n" +
                "It should be noted that only the courses that are Confirmed will be included in your Analysis and Transcript.\n \n" +
                "For more information about this activity, refer to "+ Tips.reference("Module Collection")));

        refreshButton = new KButton("Sync");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Synchronize Courses");
        refreshButton.addActionListener(e-> ModuleHandler.startThoroughSync(true, refreshButton));

        final KPanel headerPanel = new KPanel(new BorderLayout());
        headerPanel.add(new KPanel(indicator), BorderLayout.WEST);
        headerPanel.add(new KPanel(tipShower), BorderLayout.CENTER);
        headerPanel.add(new KPanel(refreshButton), BorderLayout.EAST);

        handlerInstance = new ModuleHandler();
        residentLayout = new CardLayout();
        final KPanel residentPanel = new KPanel(residentLayout) {
            @Override
            public Component add(Component comp) {
                return super.add(new KScrollPane(comp));
            }
        };
        final KButton y1Button = getControlButton("Year One");
        y1Button.redress();
        onButton = y1Button;
        y1Button.addActionListener(e-> {
            setOnButton(y1Button);
            indicator.setText("First Year: "+Student.firstAcademicYear());
            residentLayout.show(residentPanel,"Year1");
        });

        final KButton y2Button = getControlButton("Year Two");
        y2Button.addActionListener(e-> {
            setOnButton(y2Button);
            indicator.setText("Second Year: " + Student.secondAcademicYear());
            residentLayout.show(residentPanel, "Year2");
        });

        final KButton y3Button = getControlButton("Year Three");
        y3Button.addActionListener(e-> {
            setOnButton(y3Button);
            indicator.setText("Third Year: " + Student.thirdAcademicYear());
            residentLayout.show(residentPanel, "Year3");
        });

        final KButton y4Button = getControlButton("Year Four");
        y4Button.addActionListener(e-> {
            setOnButton(y4Button);
            indicator.setText("Final Year: " + Student.fourthAcademicYear());
            residentLayout.show(residentPanel, "Year4");
        });

        final KButton summerButton = getControlButton("Summer");
        summerButton.addActionListener(e-> {
            setOnButton(summerButton);
            indicator.setText("Summer");
            residentLayout.show(residentPanel,"Summer");
        });

        final KButton miscButton = getControlButton("Misc.");
        miscButton.addActionListener(e-> {
            setOnButton(miscButton);
            indicator.setText("Miscellaneous");
            residentLayout.show(residentPanel,"misc");
        });

        final KPanel controlPanel = new KPanel(150, 400);
        controlPanel.add(Box.createRigidArea(new Dimension(150, 50)));
        controlPanel.addAll(y1Button, y2Button, y3Button, y4Button, new KSeparator(new Dimension(125,1)),
                summerButton, miscButton);

        residentLayout.addLayoutComponent(residentPanel.add(handlerInstance.yearOnePresent()),"Year1");
        residentLayout.addLayoutComponent(residentPanel.add(handlerInstance.yearTwoPresent()),"Year2");
        residentLayout.addLayoutComponent(residentPanel.add(handlerInstance.yearThreePresent()),"Year3");
        residentLayout.addLayoutComponent(residentPanel.add(handlerInstance.yearFourPresent()),"Year4");
        residentLayout.addLayoutComponent(residentPanel.add(new SummerModule().getPresent()),"Summer");
        residentLayout.addLayoutComponent(residentPanel.add(new MiscellaneousModule().getPresent()),"misc");

        final KPanel modulesActivity = new KPanel(new BorderLayout());
        modulesActivity.add(headerPanel, BorderLayout.NORTH);
        modulesActivity.add(controlPanel, BorderLayout.WEST);
        modulesActivity.add(residentPanel, BorderLayout.CENTER);

        Board.addCard(modulesActivity, "Modules Collection");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Modules Collection");
    }

    private KButton getControlButton(String text) {
        final KButton button = new KButton(text);
        button.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        button.setPreferredSize(new Dimension(150,30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.undress();
        return button;
    }

    private void setOnButton(KButton button){
        if (onButton != button) {
            button.redress();
            onButton.undress();
            onButton = button;
        }
    }

}

