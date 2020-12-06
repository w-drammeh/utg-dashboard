package main;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Like its co., TaskExhibition too has subclasses for all the separate task types.
 * for exhibiting them.
 * The event has no Exhibitor.
 * TaskExhibitions are made visible at time of creation.
 */
public class TaskExhibition {


    private static KLabel giveLabel(String text){
        return new KLabel(text, KFontFactory.createBoldFont(16));
    }

    private static KLabel giveValueLabel(String text){
        return new KLabel(text, KFontFactory.createPlainFont(16));
    }


    public static class TodoExhibition extends KDialog {

        public TodoExhibition(TaskSelf.TodoSelf theTask){
            super("Task");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final KPanel contentPanel = new KPanel();//To be used... contentPane!
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            final KPanel nameLayer = new KPanel(new BorderLayout());
            nameLayer.add(new KPanel(giveLabel("Task name:")),BorderLayout.WEST);
            nameLayer.add(new KPanel(giveValueLabel(theTask.getDescription())),BorderLayout.CENTER);

            final KPanel stateLayer = new KPanel(new BorderLayout());
            stateLayer.add(new KPanel(giveLabel("Current state:")),BorderLayout.WEST);
            stateLayer.add(new KPanel(giveValueLabel(theTask.isActive() ? "In progress" : "Completed")),BorderLayout.CENTER);

            final KPanel durationLayer = new KPanel(new BorderLayout());
            durationLayer.add(new KPanel(giveLabel("Specified duration:")),BorderLayout.WEST);
            durationLayer.add(new KPanel(giveValueLabel(theTask.getSpecifiedDuration()+" days")),BorderLayout.CENTER);

            final KPanel addDateLayer = new KPanel(new BorderLayout());
            addDateLayer.add(new KPanel(giveLabel("Date Initiated:")),BorderLayout.WEST);
            addDateLayer.add(new KPanel(giveValueLabel(theTask.getStartDate())),BorderLayout.CENTER);

            final KPanel completeDateLayer = new KPanel(new BorderLayout());
            completeDateLayer.add(new KPanel(giveLabel("Date expected to complete:")),BorderLayout.WEST);
            completeDateLayer.add(new KPanel(giveValueLabel(theTask.getDateExpectedToComplete())),BorderLayout.CENTER);

            final KPanel soFarLayer = new KPanel(new BorderLayout());
            soFarLayer.add(new KPanel(giveLabel("Time taken so far:")),BorderLayout.WEST);
            soFarLayer.add(new KPanel(giveValueLabel(theTask.getDaysTaken() == 0 ? "Less than a day" :
                    Globals.checkPlurality(theTask.getDaysTaken(), "days"))), BorderLayout.CENTER);

            final KPanel remainingLayer = new KPanel(new BorderLayout());
            remainingLayer.add(new KPanel(giveLabel("Time remaining:")),BorderLayout.WEST);
            remainingLayer.add(new KPanel(giveValueLabel(theTask.getDaysLeft() == 1 ? "Less than a day" :
                    theTask.getDaysLeft()+" days")),BorderLayout.CENTER);

            final KPanel completedLayer = new KPanel(new BorderLayout());
            completedLayer.add(new KPanel(giveLabel("Date completed:")),BorderLayout.WEST);
            completedLayer.add(new KPanel(giveValueLabel(theTask.getDateCompleted())),BorderLayout.CENTER);

            final KPanel consumedLayer = new KPanel(new BorderLayout());
            consumedLayer.add(new KPanel(giveLabel("Time consumed:")),BorderLayout.WEST);
            consumedLayer.add(new KPanel(giveValueLabel(theTask.getTotalTimeConsumed() == 0 ? "Less than a day" :
                    theTask.getTotalTimeConsumed() == theTask.getSpecifiedDuration() ? " Period specified ("+theTask.getSpecifiedDuration()+" days)" :
                            Globals.checkPlurality(theTask.getTotalTimeConsumed(), "days"))),BorderLayout.CENTER);

            if (theTask.isActive()) {
                contentPanel.addAll(nameLayer, stateLayer, addDateLayer, durationLayer, completeDateLayer, soFarLayer, remainingLayer);
            } else {
                contentPanel.addAll(nameLayer, stateLayer, durationLayer, addDateLayer, completedLayer, consumedLayer);
            }

            final KButton doneButton = new KButton("Mark as Done");
            doneButton.setForeground(Color.BLUE);
            doneButton.addActionListener(e -> TaskActivity.TodoHandler.transferTask(theTask,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TaskActivity.TodoHandler.removalWaiter(theTask,this));

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e -> this.dispose());

            final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            if (theTask.isActive()) {
                buttonsContainer.addAll(removeButton,doneButton,closeButton);
            } else {
                buttonsContainer.addAll(removeButton,closeButton);
            }

            contentPanel.addAll(MComponent.contentBottomGap(), buttonsContainer);
            this.getRootPane().setDefaultButton(closeButton);
            this.setContentPane(contentPanel);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(() -> this.setVisible(true));
        }
    }


    public static class ProjectExhibition extends KDialog{

        public ProjectExhibition(TaskSelf.ProjectSelf theProject){
            this.setTitle("Project");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final KPanel exhibitionPane = new KPanel();
            exhibitionPane.setLayout(new BoxLayout(exhibitionPane, BoxLayout.Y_AXIS));

            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KPanel(giveLabel("Project name:")),BorderLayout.WEST);
            namePanel.add(new KPanel(giveValueLabel(theProject.getProjectName())),BorderLayout.CENTER);

            final KPanel typePanel = new KPanel(new BorderLayout());
            typePanel.add(new KPanel(giveLabel("Type:")),BorderLayout.WEST);
            typePanel.add(new KPanel(giveValueLabel(theProject.getType())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(new KPanel(giveLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(new KPanel(giveValueLabel(theProject.isLive() ? "Running" : "Completed")),BorderLayout.CENTER);

            final KPanel startedPanel = new KPanel(new BorderLayout());
            startedPanel.add(new KPanel(giveLabel("Started:")),BorderLayout.WEST);
            startedPanel.add(new KPanel(giveValueLabel(theProject.getStartDate())),BorderLayout.CENTER);

            final KPanel expectedPanel = new KPanel(new BorderLayout());
            expectedPanel.add(new KPanel(giveLabel("Expected to complete:")),BorderLayout.WEST);
            expectedPanel.add(new KPanel(giveValueLabel(theProject.getDateExpectedToComplete())), BorderLayout.CENTER);

            final KPanel soFarPanel = new KPanel(new BorderLayout());
            soFarPanel.add(new KPanel(giveLabel("Time taken so far:")),BorderLayout.WEST);
            soFarPanel.add(new KPanel(giveValueLabel(theProject.getDaysTaken() == 0 ? "Less than a day" :
                    Globals.checkPlurality(theProject.getDaysTaken(), "days"))),BorderLayout.CENTER);

            final KPanel remPanel = new KPanel(new BorderLayout());
            remPanel.add(new KPanel(giveLabel("Time remaining:")),BorderLayout.WEST);
            remPanel.add(new KPanel(giveValueLabel(theProject.getDaysLeft() == 1 ? "Less than a day" :
                    theProject.getDaysLeft()+" days")), BorderLayout.CENTER);

            final KPanel completedPanel = new KPanel(new BorderLayout());
            completedPanel.add(new KPanel(giveLabel("Completed:")),BorderLayout.WEST);
            completedPanel.add(new KPanel(giveValueLabel(theProject.getDateCompleted())), BorderLayout.CENTER);

            final KPanel consumedPanel = new KPanel(new BorderLayout());
            consumedPanel.add(new KPanel(giveLabel("Time Consumed:")),BorderLayout.WEST);
            consumedPanel.add(new KPanel(giveValueLabel(theProject.getTotalTimeConsumed() == 0 ? "Less than a day" :
                    theProject.getDaysTaken() == theProject.getSpecifiedDuration() ? "Specified period ("+theProject.getSpecifiedDuration()+" days)" :
                            theProject.getDaysTaken() +" days")), BorderLayout.CENTER);

            if (theProject.isLive()) {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, expectedPanel, soFarPanel, remPanel);
            } else {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, completedPanel, consumedPanel);
            }

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e -> this.dispose());

            exhibitionPane.add(Box.createVerticalStrut(25));
            exhibitionPane.add(new KPanel(closeButton));

            this.getRootPane().setDefaultButton(closeButton);
            this.setContentPane(exhibitionPane);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(() -> this.setVisible(true));
        }
    }


    public static class AssignmentExhibition extends KDialog {
//        private KTextArea questionArea;


        public AssignmentExhibition(TaskSelf.AssignmentSelf assignment){
            this.setTitle(assignment.getCourseName()+" - "+(assignment.isGroup() ? "Group Assignment" : "Personal Assignment"));
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final KPanel subjectPanel = new KPanel(new BorderLayout());
            subjectPanel.add(new KPanel(giveLabel("Subject:")),BorderLayout.WEST);
            subjectPanel.add(new KPanel(giveValueLabel(assignment.getCourseName())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(new KPanel(giveLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(new KPanel(giveValueLabel(assignment.isOn() ? "In-progress / Un-submitted" : "Submitted")), BorderLayout.CENTER);

            final KPanel startPanel = new KPanel(new BorderLayout());
            startPanel.add(new KPanel(giveLabel("Date Initiated:")),BorderLayout.WEST);
            startPanel.add(new KPanel(giveValueLabel(assignment.getStartDate())),BorderLayout.CENTER);

            final KPanel submittedPanel = new KPanel(new BorderLayout());
            submittedPanel.add(new KPanel(giveLabel("Date Submitted:")),BorderLayout.WEST);
            submittedPanel.add(new KPanel(giveValueLabel(assignment.getSubmissionDate())),BorderLayout.CENTER);

            final KPanel deadlinePanel = new KPanel(new BorderLayout());
            deadlinePanel.add(new KPanel(giveLabel("Deadline Given:")),BorderLayout.WEST);
            deadlinePanel.add(new KPanel(giveValueLabel(assignment.getDeadLine())),BorderLayout.CENTER);

            final KPanel remainPanel = new KPanel(new BorderLayout());
            remainPanel.add(new KPanel(giveLabel("Time Remaining:")),BorderLayout.WEST);
            remainPanel.add(new KPanel(giveValueLabel((Globals.checkPlurality(assignment.getTimeRemaining(),"days")) +
                    " to submit")),BorderLayout.CENTER);

            final KPanel modePanel = new KPanel(new BorderLayout());
            modePanel.add(new KPanel(giveLabel("Mode of submission:")), BorderLayout.WEST);
            modePanel.add(new KPanel(giveValueLabel(assignment.getModeOfSubmission())), BorderLayout.CENTER);

            final KPanel questionPanel = new KPanel(new BorderLayout());
            questionPanel.add(new KPanel(giveLabel("Question(s):")), BorderLayout.WEST);
            final KTextArea questionArea = new KTextArea();
            questionArea.setText(assignment.getQuestion());
            final KScrollPane scrollPane = questionArea.outerScrollPane(new Dimension(475,150));
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY,1,false));
            questionPanel.add(scrollPane, BorderLayout.CENTER);

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            if (assignment.isOn()) {
                AssignmentExhibition.this.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        assignment.setQuestion(questionArea.getText());
                    }
                });
                questionPanel.add(new KLabel("Question(s) wont be editable after assignments are submitted.",
                        KFontFactory.createPlainFont(15), Color.RED), BorderLayout.SOUTH);
                contentPanel.addAll(subjectPanel, statusPanel, startPanel, deadlinePanel, remainPanel, modePanel, questionPanel);
            } else {
                questionArea.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume();
                    }
                });
                contentPanel.addAll(subjectPanel, statusPanel, startPanel, submittedPanel, deadlinePanel, modePanel, questionPanel);
            }

            final KButton submitButton = new KButton("Mark as Submit");
            submitButton.setForeground(Color.BLUE);
            submitButton.addActionListener(e-> TaskActivity.AssignmentsHandler.transferAssignment(assignment,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TaskActivity.AssignmentsHandler.removalListener(assignment, this));

            final KButton closeButton = new KButton("Close");
            closeButton.setFocusable(true);
            closeButton.addActionListener(e -> this.dispose());

            final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            if (assignment.isOn()) {
                buttonsContainer.addAll(removeButton, submitButton, closeButton);
            } else {
                buttonsContainer.addAll(removeButton, closeButton);
            }

            contentPanel.addAll(MComponent.contentBottomGap(), buttonsContainer);

            this.getRootPane().setDefaultButton(closeButton);
            this.setContentPane(contentPanel);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(() -> this.setVisible(true));
        }
    }

}
