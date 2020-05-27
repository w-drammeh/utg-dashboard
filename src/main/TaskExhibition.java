package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
            nameLayer.add(KPanel.wantDirectAddition(giveLabel("Task name:")),BorderLayout.WEST);
            nameLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getDescription())),BorderLayout.CENTER);

            final KPanel stateLayer = new KPanel(new BorderLayout());
            stateLayer.add(KPanel.wantDirectAddition(giveLabel("Current state:")),BorderLayout.WEST);
            stateLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.isActive() ? "In progress" : "Completed")),BorderLayout.CENTER);

            final KPanel durationLayer = new KPanel(new BorderLayout());
            durationLayer.add(KPanel.wantDirectAddition(giveLabel("Specified duration:")),BorderLayout.WEST);
            durationLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getSpecifiedDuration()+" days")),BorderLayout.CENTER);

            final KPanel addDateLayer = new KPanel(new BorderLayout());
            addDateLayer.add(KPanel.wantDirectAddition(giveLabel("Date Initiated:")),BorderLayout.WEST);
            addDateLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getStartDate())),BorderLayout.CENTER);

            final KPanel completeDateLayer = new KPanel(new BorderLayout());
            completeDateLayer.add(KPanel.wantDirectAddition(giveLabel("Date expected to complete:")),BorderLayout.WEST);
            completeDateLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getDateExpectedToComplete())),BorderLayout.CENTER);

            final KPanel soFarLayer = new KPanel(new BorderLayout());
            soFarLayer.add(KPanel.wantDirectAddition(giveLabel("Time taken so far:")),BorderLayout.WEST);
            soFarLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getDaysTaken() == 0 ? "Less than a day" : Globals.checkPlurality(theTask.getDaysTaken(), "days"))), BorderLayout.CENTER);

            final KPanel remainingLayer = new KPanel(new BorderLayout());
            remainingLayer.add(KPanel.wantDirectAddition(giveLabel("Time remaining:")),BorderLayout.WEST);
            remainingLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getDaysLeft() == 1 ? "Less than a day" : theTask.getDaysLeft()+" days")),BorderLayout.CENTER);

            final KPanel completedLayer = new KPanel(new BorderLayout());
            completedLayer.add(KPanel.wantDirectAddition(giveLabel("Date completed:")),BorderLayout.WEST);
            completedLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getDateCompleted())),BorderLayout.CENTER);

            final KPanel consumedLayer = new KPanel(new BorderLayout());
            consumedLayer.add(KPanel.wantDirectAddition(giveLabel("Time consumed:")),BorderLayout.WEST);
            consumedLayer.add(KPanel.wantDirectAddition(giveValueLabel(theTask.getTotalTimeConsumed() == 0 ? "Less than a day" : theTask.getTotalTimeConsumed() == theTask.getSpecifiedDuration() ? " Period specified ("+theTask.getSpecifiedDuration()+" days)" : Globals.checkPlurality(theTask.getTotalTimeConsumed(), "days"))),BorderLayout.CENTER);

            if (theTask.isActive()) {
                contentPanel.addAll(nameLayer, stateLayer, addDateLayer, durationLayer, completeDateLayer, soFarLayer, remainingLayer);
            } else {
                contentPanel.addAll(nameLayer, stateLayer, durationLayer, addDateLayer, completedLayer, consumedLayer);
            }

            final KButton doneButton = new KButton("Mark as Done");
            doneButton.setForeground(Color.BLUE);
            doneButton.addActionListener(e -> TasksGenerator.TodoHandler.transferTask(theTask,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TasksGenerator.TodoHandler.removalWaiter(theTask,this));

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e -> this.dispose());

            final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            if (theTask.isActive()) {
                buttonsContainer.addAll(removeButton,doneButton,closeButton);
            } else {
                buttonsContainer.addAll(removeButton,closeButton);
            }

            contentPanel.addAll(ComponentAssistant.contentBottomGap(), buttonsContainer);
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
            namePanel.add(KPanel.wantDirectAddition(giveLabel("Project name:")),BorderLayout.WEST);
            namePanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getProjectName())),BorderLayout.CENTER);

            final KPanel typePanel = new KPanel(new BorderLayout());
            typePanel.add(KPanel.wantDirectAddition(giveLabel("Type:")),BorderLayout.WEST);
            typePanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getType())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(KPanel.wantDirectAddition(giveLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.isLive() ? "Running" : "Completed")),BorderLayout.CENTER);

            final KPanel startedPanel = new KPanel(new BorderLayout());
            startedPanel.add(KPanel.wantDirectAddition(giveLabel("Started:")),BorderLayout.WEST);
            startedPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getStartDate())),BorderLayout.CENTER);

            final KPanel expectedPanel = new KPanel(new BorderLayout());
            expectedPanel.add(KPanel.wantDirectAddition(giveLabel("Expected to complete:")),BorderLayout.WEST);
            expectedPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getDateExpectedToComplete())), BorderLayout.CENTER);

            final KPanel soFarPanel = new KPanel(new BorderLayout());
            soFarPanel.add(KPanel.wantDirectAddition(giveLabel("Time taken so far:")),BorderLayout.WEST);
            soFarPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getDaysTaken() == 0 ? "Less than a day" : Globals.checkPlurality(theProject.getDaysTaken(), "days"))),BorderLayout.CENTER);

            final KPanel remPanel = new KPanel(new BorderLayout());
            remPanel.add(KPanel.wantDirectAddition(giveLabel("Time remaining:")),BorderLayout.WEST);
            remPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getDaysLeft() == 1 ? "Less than a day" : theProject.getDaysLeft()+" days")),BorderLayout.CENTER);

            final KPanel completedPanel = new KPanel(new BorderLayout());
            completedPanel.add(KPanel.wantDirectAddition(giveLabel("Completed:")),BorderLayout.WEST);
            completedPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getDateCompleted())),BorderLayout.CENTER);

            final KPanel consumedPanel = new KPanel(new BorderLayout());
            consumedPanel.add(KPanel.wantDirectAddition(giveLabel("Time Consumed:")),BorderLayout.WEST);
            consumedPanel.add(KPanel.wantDirectAddition(giveValueLabel(theProject.getTotalTimeConsumed() == 0 ? "Less than a day" : theProject.getDaysTaken() == theProject.getSpecifiedDuration() ? "Specified period ("+theProject.getSpecifiedDuration()+" days)" : theProject.getDaysTaken() +" days")),BorderLayout.CENTER);

            if (theProject.isLive()) {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, expectedPanel, soFarPanel, remPanel);
            } else {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, completedPanel, consumedPanel);
            }

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e -> this.dispose());

            exhibitionPane.add(Box.createVerticalStrut(25));
            exhibitionPane.add(KPanel.wantDirectAddition(closeButton));

            this.getRootPane().setDefaultButton(closeButton);
            this.setContentPane(exhibitionPane);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(() -> this.setVisible(true));
        }
    }


    public static class AssignmentExhibition extends KDialog{

        public AssignmentExhibition(TaskSelf.AssignmentSelf assignment){
            this.setTitle(assignment.getCourseName()+" - "+(assignment.isGroup() ? "Group Assignment" : "Personal Assignment"));
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final KPanel assignmentPane = new KPanel();
            assignmentPane.setLayout(new BoxLayout(assignmentPane, BoxLayout.Y_AXIS));

            final KPanel subjectPanel = new KPanel(new BorderLayout());
            subjectPanel.add(KPanel.wantDirectAddition(giveLabel("Subject:")),BorderLayout.WEST);
            subjectPanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.getCourseName())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(KPanel.wantDirectAddition(giveLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.isOn() ? "In-progress / Un-submitted" : "Submitted")),BorderLayout.CENTER);

            final KPanel startPanel = new KPanel(new BorderLayout());
            startPanel.add(KPanel.wantDirectAddition(giveLabel("Date Initiated:")),BorderLayout.WEST);
            startPanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.getStartDate())),BorderLayout.CENTER);

            final KPanel submittedPanel = new KPanel(new BorderLayout());
            submittedPanel.add(KPanel.wantDirectAddition(giveLabel("Date Submitted:")),BorderLayout.WEST);
            submittedPanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.getSubmissionDate())),BorderLayout.CENTER);

            final KPanel deadlinePanel = new KPanel(new BorderLayout());
            deadlinePanel.add(KPanel.wantDirectAddition(giveLabel("Deadline Given:")),BorderLayout.WEST);
            deadlinePanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.getDeadLine())),BorderLayout.CENTER);

            final KPanel remainPanel = new KPanel(new BorderLayout());
            remainPanel.add(KPanel.wantDirectAddition(giveLabel("Time Remaining:")),BorderLayout.WEST);
            remainPanel.add(KPanel.wantDirectAddition(giveValueLabel((Globals.checkPlurality(assignment.getTimeRemaining(),"days")) + " to submit")),BorderLayout.CENTER);

            final KPanel modePanel = new KPanel(new BorderLayout());
            modePanel.add(KPanel.wantDirectAddition(giveLabel("Mode of submission:")),BorderLayout.WEST);
            modePanel.add(KPanel.wantDirectAddition(giveValueLabel(assignment.getModeOfSubmission())),BorderLayout.CENTER);

            final KPanel questionPanel = new KPanel(new BorderLayout());
            questionPanel.add(KPanel.wantDirectAddition(giveLabel("Question(s):")),BorderLayout.WEST);
            final KTextArea questionArea = new KTextArea();
            questionArea.setText(assignment.getQuestion());
            questionArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    assignment.setQuestion(questionArea.getText());
                }
            });
            questionArea.setEditable(assignment.isOn());
            final KScrollPane scrollPane = KScrollPane.getTextAreaScroller(questionArea, new Dimension(475,150));
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY,2,true));
            questionPanel.add(scrollPane,BorderLayout.CENTER);

            if (assignment.isOn()) {
                questionPanel.add(new KLabel("Question(s) wont be editable after assignments are submitted.",KFontFactory.createPlainFont(15),Color.RED), BorderLayout.SOUTH);
                assignmentPane.addAll(subjectPanel, statusPanel, startPanel, deadlinePanel, remainPanel, modePanel, questionPanel);
            } else {
                assignmentPane.addAll(subjectPanel, statusPanel, startPanel, submittedPanel, deadlinePanel, modePanel, questionPanel);
            }

            final KButton submitButton = new KButton("Mark as Submit");
            submitButton.setForeground(Color.BLUE);
            submitButton.addActionListener(e-> TasksGenerator.AssignmentsHandler.transferAssignment(assignment,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TasksGenerator.AssignmentsHandler.removalListener(assignment, this));

            final KButton closeButton = new KButton("Close");
            closeButton.setFocusable(true);
            closeButton.addActionListener(e -> this.dispose());

            final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            if (assignment.isOn()) {
                buttonsContainer.addAll(removeButton, submitButton, closeButton);
            } else {
                buttonsContainer.addAll(removeButton, closeButton);
            }

            assignmentPane.addAll(ComponentAssistant.contentBottomGap(), buttonsContainer);
            this.getRootPane().setDefaultButton(closeButton);
            this.setContentPane(assignmentPane);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(() -> this.setVisible(true));
        }
    }

}
