package main;

import customs.*;

import javax.swing.*;
import java.awt.*;

public class ModulesGenerator implements ActivityAnswerer {
    private KButton onButton;
    private KLabel indicator;
    private CardLayout residentLayout;//there's a 'residentPanel'
    private ModulesHandler handlerInstance;
    private static KButton refreshButton;//to be used by sync


    public ModulesGenerator(){
        indicator = new KLabel("First Year - "+Student.firstAcademicYear(), KFontFactory.bodyHeaderFont());
        handlerInstance = new ModulesHandler();
        residentLayout = new CardLayout();
        final KPanel residentPanel = new KPanel(residentLayout){
            @Override
            public Component add(Component comp) {
                return super.add(new KScrollPane(comp, false));
            }
        };

        final KButton tipShower = KButton.getIconifiedButton("warn.png", 25, 25);
        tipShower.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tipShower.addActionListener(e-> App.promptPlain("Module Collection", "For more options, simply right-click on the tables.\n" +
                "To Edit, Remove, Verify, or Show the Details of a course, just right-click on it and choose the corresponding\n" +
                "option from the Popup Menu.\n \n" +
                "You can navigate through your collection by using the buttons outlined in the left-most panel.\n" +
                "If Dashboard cannot determine the academic year & semester of a course relative to your level,\n" +
                "it will be pushed to the Miscellaneous table.\n \n" +
                "It should be noted that only the courses that are Confirmed will be included in your 'Analysis' and Transcript.\n \n" +
                "For more information about this activity, refer to "+HelpGenerator.reference("Module Collection")));

        refreshButton = new KButton("Sync");
        refreshButton.setToolTipText("Synchronize Courses");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> triggerRefresh(true));

        final KPanel headerPanel = new KPanel(new BorderLayout());
        headerPanel.add(new KPanel(refreshButton), BorderLayout.EAST);
        headerPanel.add(new KPanel(tipShower), BorderLayout.CENTER);
        headerPanel.add(indicator, BorderLayout.WEST);

        final KButton y1Button = getControlButton("Year One");
        y1Button.setBorderPainted(true);
        y1Button.setContentAreaFilled(true);
        onButton = y1Button;
        y1Button.addActionListener(e -> {
            setOnButton(y1Button);
            indicator.setText("First Year - "+Student.firstAcademicYear());
            residentLayout.show(residentPanel,"Year1");
        });

        final KButton y2Button = getControlButton("Year Two");
        y2Button.addActionListener(e -> {
            setOnButton(y2Button);
            indicator.setText("Second Year - " + Student.secondAcademicYear());
            residentLayout.show(residentPanel, "Year2");
        });

        final KButton y3Button = getControlButton("Year Three");
        y3Button.addActionListener(e -> {
            setOnButton(y3Button);
            indicator.setText("Third Year - " + Student.thirdAcademicYear());
            residentLayout.show(residentPanel, "Year3");
        });

        final KButton y4Button = getControlButton("Year Four");
        y4Button.addActionListener(e -> {
            setOnButton(y4Button);
            indicator.setText("Final Year - " + Student.finalAcademicYear());
            residentLayout.show(residentPanel, "Year4");
        });

        final KButton summerButton = getControlButton("Summer");
        summerButton.addActionListener(e -> {
            setOnButton(summerButton);
            indicator.setText("Summer Courses");
            residentLayout.show(residentPanel,"Summer");
        });

        final KButton miscButton = getControlButton("Misc.");
        miscButton.addActionListener(e -> {
            setOnButton(miscButton);
            indicator.setText("Miscellaneous");
            residentLayout.show(residentPanel,"misc");
        });

        final KPanel controlPanel = new KPanel(150,400);
        controlPanel.add(Box.createRigidArea(new Dimension(150, 50)));
        controlPanel.addAll(y1Button,y2Button,y3Button,y4Button,new KSeparator(new Dimension(125,1)),
                summerButton, new KSeparator(new Dimension(125,1)),miscButton);

        residentLayout.addLayoutComponent(residentPanel.add(generateYear1()),"Year1");
        residentLayout.addLayoutComponent(residentPanel.add(generateYear2()),"Year2");
        residentLayout.addLayoutComponent(residentPanel.add(generateYear3()),"Year3");
        residentLayout.addLayoutComponent(residentPanel.add(generateYear4()),"Year4");
        residentLayout.addLayoutComponent(residentPanel.add(generateSummer()),"Summer");
        residentLayout.addLayoutComponent(residentPanel.add(generateMiscellaneous()),"misc");

        final KPanel allPanel = new KPanel(new BorderLayout());
        allPanel.add(headerPanel, BorderLayout.NORTH);
        allPanel.add(controlPanel, BorderLayout.WEST);
        allPanel.add(residentPanel, BorderLayout.CENTER);

        Board.addCard(allPanel, "Modules Collection");
    }

    private KButton getControlButton(String text){
        final KButton newButton = new KButton(text);
        newButton.setStyle(KFontFactory.createPlainFont(16),Color.BLUE);
        newButton.setPreferredSize(new Dimension(150,30));
        newButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newButton.setBorderPainted(false);
        newButton.setContentAreaFilled(false);

        return newButton;
    }

    /**
     * Sync will only be allowed to commence if one is not already on the way.
     */
    public static void triggerRefresh(boolean userRequested){
        new Thread(()-> {
            if (!userRequested || App.showOkCancelDialog("Synchronize Courses",
                    "This action is experimental. Dashboard will perform a thorough 'Re-indexing' of your modules.\n" +
                    "Please refer to "+HelpGenerator.reference("My Courses | Modules Synchronization")+"\n" +
                    "for details about the consequences of this action. Start now?")) {
                ModulesHandler.startThoroughSync(refreshButton, userRequested);
            } else {
                refreshButton.setEnabled(true);
            }
        }).start();
    }

    @Override
    public void answerActivity() {
        Board.showCard("Modules Collection");
    }

    private JComponent generateYear1(){
        return handlerInstance.presentForYearOne();
    }

    private JComponent generateYear2(){
        return handlerInstance.presentForYearTwo();
    }

    private JComponent generateYear3(){
        return handlerInstance.presentForYearThree();
    }

    private JComponent generateYear4(){
        return handlerInstance.presentForYearFour();
    }

    private JComponent generateSummer(){
        return new SummerModulesHandler().getSummerPresent();
    }

    private JComponent generateMiscellaneous(){
        return new MiscellaneousModulesHandler().getMiscPresent();
    }

    private KButton getOnButton(){
        return onButton;
    }

    private void setOnButton(KButton button){
        if (getOnButton() == button) {
            return;
        }
        button.redress();
        getOnButton().undress();
        onButton = button;
    }

}

