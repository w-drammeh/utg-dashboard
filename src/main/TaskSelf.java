package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * It specifies the task itself - provides description, dates, etc.
 * A TaskSelf must be serializable.
 */
public class TaskSelf {


    public static class TodoSelf implements Serializable {
        private String description;
        private String startDate;
        private int specifiedDuration;//In days !<5, !>30
        private int totalTimeConsumed;//This has only 2 variations: 1. An ongoing task is marked done 2. A task completes normally
        private boolean isActive;
        private String dateExpectedToComplete;//Shown for running tasks only - set with the constructor
        private String dateCompleted;//Shown for completed #s only - set with ending, whether voluntary or time-due
        private Timer timer;
        private boolean eveIsAlerted, doneIsAlerted;
        private transient KLabel togoLabel;
        private transient TaskExhibition.TodoExhibition exhibition;
        private transient KPanel layerPanel;

        public TodoSelf(String desc, int duration){
            this.setCoreProperties(desc, duration);
            this.setUpUI();
        }

        private void setCoreProperties(String desc, int duration){
            this.startDate = MDate.now();
            this.description = desc;
            this.specifiedDuration = duration;
            this.dateExpectedToComplete = MDate.daysAfter(new Date(), duration);
            this.setActive(true);
            this.initializeTimer(Globals.DAY_IN_MILLI);
        }

        private void initializeTimer(int firstDelay){
            this.timer = new Timer(Globals.DAY_IN_MILLI,null);
            this.timer.setInitialDelay(firstDelay);
            this.timer.addActionListener(e -> {
                this.togoLabel.setText(Globals.checkPlurality(this.getDaysLeft(), "days")+" to go");
                if (getDaysLeft() == 1) {//Fire eve-day notification if was not fired already
                    this.togoLabel.setText("Less than a day to go");
                    this.signalEveNotice();
                } else if (this.getDaysLeft() <= 0) {
                    if (this.exhibition != null && this.exhibition.isShowing()) {
                        this.exhibition.dispose();
                    }
                    TasksGenerator.TodoHandler.transferTask(this, null, true);
                    this.signalDoneNotice();
                }
            });
            this.timer.start();
        }

        private void setUpUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(this.description, KFontFactory.createBoldFont(16), Color.BLUE), BorderLayout.SOUTH);

            final KButton moreOptions = KButton.getIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Task");
            moreOptions.addActionListener(e -> this.exhibition = new TaskExhibition.TodoExhibition(this));

            this.togoLabel = new KLabel(Globals.checkPlurality(this.getDaysLeft(), "days") + " to complete",
                    KFontFactory.createPlainFont(16));
            this.togoLabel.setOpaque(false);
            if (this.isActive) {
                this.togoLabel.setForeground(Color.RED);
            } else {
                this.togoLabel.setText("Completed "+this.dateCompleted);
                this.togoLabel.setForeground(Color.BLUE);
            }

            final KPanel quantaPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quantaPanel.addAll(new KLabel(this.specifiedDuration+" days task", KFontFactory.createPlainFont(16)),
                    Box.createRigidArea(new Dimension(10, 10)), this.togoLabel,
                    Box.createRigidArea(new Dimension(15, 10)), moreOptions);

            if (layerPanel == null) {
                this.layerPanel = new KPanel(1_000, 35);
            } else {
                ComponentAssistant.repair(layerPanel);
            }
            this.layerPanel.setLayout(new BoxLayout(this.layerPanel, BoxLayout.X_AXIS));
            this.layerPanel.addAll(namePanel, quantaPanel);
            ComponentAssistant.ready(this.layerPanel);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the Task you created on "+this.getStartDate()+", "+
                        this.getDescription()+", is to be completed in less than a day.";
                Notification.create("Task Reminder","Task "+this.getDescription()+" is due tomorrow", text);
                eveIsAlerted = true;
            }
        }

        private void signalDoneNotice(){
            if (!doneIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the Task you created on "+this.getStartDate()+", "+
                        this.getDescription()+", is now due. This Task is now considered done as per the given date limit.";
                Notification.create("Task Completed","Task "+this.getDescription()+" is now completed", text);
                doneIsAlerted = true;
            }
        }

        public KLabel getTogoLabel(){
            return togoLabel;
        }

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public int getDaysTaken() {
            return MDate.actualDayDifference(Objects.requireNonNull(MDate.parse(this.startDate)), new Date());
        }

        public int getTotalTimeConsumed(){
            return totalTimeConsumed;
        }

        public void setTotalTimeConsumed(int totalTimeConsumed){
            this.totalTimeConsumed = totalTimeConsumed;
        }

        public int getSpecifiedDuration() {
            return specifiedDuration;
        }

        public int getDaysLeft() {
            return specifiedDuration - getDaysTaken();
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
            if (!active) {
                this.timer.stop();
            }
        }

        public String getDateCompleted() {
            return dateCompleted;
        }

        public void setDateCompleted(String dateCompleted) {
            this.dateCompleted = dateCompleted;
        }

        public String getDateExpectedToComplete() {
            return dateExpectedToComplete;
        }

        public KPanel getLayer(){
            return layerPanel;
        }

        private void wakeAlive(){
            this.setUpUI();
            if (this.getDaysLeft() == 1) {
                this.togoLabel.setText("Less than a day to complete");
                this.signalEveNotice();
            }
            int residue = MDate.getTimeValue(MDate.parse(this.dateExpectedToComplete)) - MDate.getTimeValue(new Date());
            if (residue < 0) {//reverse it then...
                residue = Globals.DAY_IN_MILLI - Math.abs(residue);
            }
            this.initializeTimer(residue);
        }

        private void wakeDead(){
            this.dateCompleted = this.dateExpectedToComplete;
            this.setActive(false);
            this.signalDoneNotice();
        }
    }


    public static class ProjectSelf implements Serializable {
        private String projectName;
        private String type;
        private String startDate;
        private int specifiedDuration;
        private int totalTimeConsumed;
        private boolean isLive;
        private Timer timer;
        private String dateExpectedToComplete;
        private String dateCompleted;
        private boolean eveIsAlerted, completionIsAlerted;
        private transient TaskExhibition.ProjectExhibition exhibition;
        private transient KButton terminationButton, completionButton, moreOptions;
        private transient JProgressBar projectProgression;
        private transient KLabel progressLabelPercentage;
        private transient KPanel projectLayer;

        public ProjectSelf(String name, String type, int duration){
            this.setCoreProperties(name, type, duration);
            this.initializeUI();
        }

        private void setCoreProperties(String name, String type, int duration){
            this.startDate = MDate.now();
            this.projectName = name;
            this.type = type;
            this.specifiedDuration = duration;
            this.setLive(true);
            this.dateExpectedToComplete = MDate.daysAfter(new Date(), duration);
            this.initializeTimer(Globals.DAY_IN_MILLI);
        }

        private void initializeTimer(int firstDelay){
            this.timer = new Timer(Globals.DAY_IN_MILLI,null);
            this.timer.setInitialDelay(firstDelay);
            this.timer.addActionListener(e -> {
                this.projectProgression.setValue(this.getDaysTaken());
                if (getDaysLeft() == 1) {
                    signalEveNotice();
                } else if (getDaysLeft() <= 0) {
                    if (exhibition != null && exhibition.isShowing()) {
                        exhibition.dispose();
                    }
                    TasksGenerator.ProjectsHandler.performIComplete(this,true);
                    signalCompletionNotice();
                }
            });
            this.timer.start();
        }

        private void initializeUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(this.projectName, KFontFactory.createBoldFont(16), Color.BLUE), BorderLayout.CENTER);

            final Dimension optionsDim = new Dimension(30, 30);//the small-buttons actually

            progressLabelPercentage = new KLabel("", KFontFactory.createPlainFont(18), Color.BLUE);
            progressLabelPercentage.setOpaque(false);

            projectProgression = new JProgressBar(0, specifiedDuration){
                @Override
                public void setValue(int n) {
                    super.setValue(n);
                    progressLabelPercentage.setText(projectProgression.getString());
                }
            };
            projectProgression.setValue(this.getDaysTaken());
            projectProgression.setPreferredSize(new Dimension(150, 20));
            projectProgression.setForeground(Color.BLUE);

            terminationButton = KButton.getIconifiedButton("terminate.png", 15, 15);
            terminationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            terminationButton.setPreferredSize(optionsDim);
            terminationButton.setToolTipText("Remove this Project");
            terminationButton.addActionListener(TasksGenerator.ProjectsHandler.removalListener(this));

            completionButton = KButton.getIconifiedButton("mark.png", 20, 20);
            completionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            completionButton.setPreferredSize(optionsDim);
            completionButton.setToolTipText("Mark as Complete");
            completionButton.addActionListener(e -> TasksGenerator.ProjectsHandler.performIComplete(this, false));

            moreOptions = KButton.getIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Project");
            moreOptions.addActionListener(e -> exhibition = new TaskExhibition.ProjectExhibition(this));

            final KPanel quanterLayer = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quanterLayer.addAll(new KLabel(this.getType()+" Project", KFontFactory.createPlainFont(16)), projectProgression,
                    progressLabelPercentage, terminationButton, completionButton, Box.createRigidArea(new Dimension(15, 10)), moreOptions);

            this.projectLayer = new KPanel(1_000, 35);
            this.projectLayer.setLayout(new BoxLayout(this.projectLayer, BoxLayout.X_AXIS));
            this.projectLayer.addAll(namePanel, quanterLayer);
        }

        public void setUpDoneUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(this.projectName, KFontFactory.createBoldFont(16), Color.BLUE), BorderLayout.CENTER);

            projectProgression = new JProgressBar(0, specifiedDuration);
            projectProgression.setValue(this.specifiedDuration);
            projectProgression.setPreferredSize(new Dimension(150, 20));
            projectProgression.setForeground(Color.BLUE);

            progressLabelPercentage = new KLabel(projectProgression.getString(), KFontFactory.createPlainFont(18), Color.BLUE);
            progressLabelPercentage.setOpaque(false);

            terminationButton = KButton.getIconifiedButton("trash.png", 20, 20);
            terminationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            terminationButton.setPreferredSize(new Dimension(30, 30));
            terminationButton.setToolTipText("Remove this Project");
            terminationButton.addActionListener(TasksGenerator.ProjectsHandler.removalListener(this));

            moreOptions = KButton.getIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Project");
            moreOptions.addActionListener(e -> exhibition = new TaskExhibition.ProjectExhibition(this));

            final KPanel quantaLayer = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quantaLayer.addAll(new KLabel(this.getType()+" Project", KFontFactory.createPlainFont(16)),
                    projectProgression, progressLabelPercentage, terminationButton, Box.createRigidArea(new Dimension(10, 10)), moreOptions);

            if (projectLayer == null) {
                projectLayer = new KPanel(1_000, 35);
                this.projectLayer.setLayout(new BoxLayout(this.projectLayer, BoxLayout.X_AXIS));
            } else {
                ComponentAssistant.repair(projectLayer);
            }
            this.projectLayer.addAll(namePanel, quantaLayer);
            ComponentAssistant.ready(projectLayer);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the "+this.getSpecifiedDuration()+" days "+
                        this.getType()+" Project you created, "+this.getProjectName()+", since "+this.getStartDate()+" is to be completed by tomorrow.";
                Notification.create("Project Reminder","Specified duration for the "+this.getType()+" Project "+
                        this.getProjectName()+" is running out",text);
                eveIsAlerted = true;
            }
        }

        private void signalCompletionNotice(){
            if (!completionIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the specified period of the "+
                        this.getType()+" Project you created, "+this.getProjectName()+", since "+this.getStartDate()+" is now attained.";
                Notification.create("Project Completed","Specified duration for the "+this.getType()+" Project "+
                        this.getProjectName()+" is reached",text);
                completionIsAlerted = true;
            }
        }

        public String getProjectName() {
            return projectName;
        }

        public String getType() {
            return type;
        }

        public String getStartDate() {
            return startDate;
        }

        public int getSpecifiedDuration() {
            return specifiedDuration;
        }

        public boolean isLive() {
            return isLive;
        }

        public void setLive(boolean live) {
            this.isLive = live;
            if (!live) {
                this.timer.stop();
            }
        }

        public String getDateCompleted() {
            return dateCompleted;
        }

        public void setDateCompleted(String dateCompleted) {
            this.dateCompleted = dateCompleted;
        }

        public int getDaysLeft() {
            return specifiedDuration - getDaysTaken();
        }

        public int getDaysTaken() {
            return MDate.actualDayDifference(Objects.requireNonNull(MDate.parse(this.startDate)), new Date());
        }

        public int getTotalTimeConsumed() {
            return totalTimeConsumed;
        }

        public void setTotalTimeConsumed(int totalTimeConsumed) {
            this.totalTimeConsumed = totalTimeConsumed;
        }

        public String getDateExpectedToComplete() {
            return dateExpectedToComplete;
        }

        public KPanel getLayer(){
            return projectLayer;
        }

        private void wakeLive(){
            if (getDaysLeft() == 1) {
                signalEveNotice();
            }
            int residue = MDate.getTimeValue(MDate.parse(this.dateExpectedToComplete)) - MDate.getTimeValue(new Date());
            if (residue < 0) {
                residue = Globals.DAY_IN_MILLI - Math.abs(residue);
            }
            this.initializeTimer(residue);
        }

        private void wakeDead(){
            this.dateCompleted = this.dateExpectedToComplete;
            this.setLive(false);
            this.signalCompletionNotice();
        }
    }


    /**
     * Please note that the assignments-task type does not consider the time differences
     * among date values as other types do.
     * In fact, it assumes all time values to be the beginning of day.
     * Computation with this is easier.
     */
    public static class AssignmentSelf implements Serializable {
        private String courseName;
        private String question;
        private boolean isGroup;
        private boolean isOn;
        private String modeOfSubmission;
        private String startDate;
        private String deadLine;
        private String dateSubmitted;
        private Timer timer;//the only purpose of its timer is to compare the dates after every day... comparison returns 0 implies deadline is met
        private int memberCount;
        private ArrayList<String> members = new ArrayList<>();
        private boolean eveIsAlerted, submissionIsAlerted;
        private transient KLabel deadlineIndicator;
        private transient KLabel groupLabel;
        private transient DeadLineEditor deadlineEditor;
        private transient TaskExhibition.AssignmentExhibition assignmentExhibitor;
        private transient KPanel assignmentPanel;

        public AssignmentSelf(String subject, String dueDate, String query, boolean groupWork, String submissionMode){
            setCoreProperties(subject, dueDate, query, groupWork, submissionMode);
            setUpUI();
        }

        private void setCoreProperties(String subject, String dueDate, String query, boolean groupWork, String submissionMode){
            this.startDate = MDate.formatDateOnly(new Date());
            this.courseName = subject;
            this.deadLine = dueDate;
            this.question = query;
            this.isGroup = groupWork;
            this.modeOfSubmission = submissionMode;
            this.setOn(true);
            this.initializeTimer(Globals.DAY_IN_MILLI);
        }

        private void initializeTimer(int firstDelay){
            this.timer = new Timer(Globals.DAY_IN_MILLI,null);
            this.timer.setInitialDelay(firstDelay);
            timer.addActionListener(e -> {
                if (getTimeRemaining() == 1) {
                    signalEveNotice();
                } else if (getTimeRemaining() <= 0) {
                    if (assignmentExhibitor != null && assignmentExhibitor.isShowing()) {
                        assignmentExhibitor.dispose();
                    }
                    if (deadlineEditor != null && deadlineEditor.isShowing()) {
                        deadlineEditor.dispose();
                    }
                    TasksGenerator.AssignmentsHandler.transferAssignment(this, null, true);
                    this.signalSubmissionNotice();
                }
            });
            timer.start();
        }

        private void setUpUI(){
        	final KPanel namePanel = new KPanel(new BorderLayout());
        	namePanel.add(new KLabel(this.getCourseName(), KFontFactory.createBoldFont(16),
                    Color.BLUE), BorderLayout.SOUTH);

        	deadlineIndicator = new KLabel();
            if (this.isOn) {
                deadlineIndicator.setText("Deadline: "+deadLine);
                deadlineIndicator.setStyle(KFontFactory.createItalicFont(16), Color.RED);
                deadlineIndicator.underline(null,false);
                deadlineIndicator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                deadlineIndicator.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        deadlineEditor = new DeadLineEditor(AssignmentSelf.this);
                    }
                });
            } else {
                this.deadlineIndicator.setText("Submitted: "+this.dateSubmitted);
                this.deadlineIndicator.setStyle(KFontFactory.createPlainFont(16), Color.BLUE);
                this.deadlineIndicator.setCursor(null);
                for (MouseListener l : this.deadlineIndicator.getMouseListeners()) {
                    this.deadlineIndicator.removeMouseListener(l);
                }
            }
            this.deadlineIndicator.setOpaque(false);

            if (this.isGroup()) {
                groupLabel = KLabel.wantIconLabel("group.png",20,20);
                groupLabel.setToolTipText("View Participants");
                groupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                groupLabel.setText(Globals.checkPlurality(this.memberCount, "Members"));
                groupLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        new MemberExhibitor(AssignmentSelf.this).setVisible(true);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        groupLabel.setForeground(Color.BLUE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        groupLabel.setForeground(null);
                    }
                });
            } else {
                groupLabel = KLabel.wantIconLabel("personal.png", 20, 20);
                groupLabel.setText("Personal");
            }
            groupLabel.setFont(KFontFactory.createPlainFont(16));

            final KButton showButton = KButton.getIconifiedButton("options.png", 20, 20);
            showButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            showButton.setToolTipText("About this Assignment");
            showButton.addActionListener(e -> assignmentExhibitor = new TaskExhibition.AssignmentExhibition(AssignmentSelf.this));

        	final KPanel quantaPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        	quantaPanel.addAll(deadlineIndicator, Box.createRigidArea(new Dimension(10, 10)), groupLabel,
                    Box.createRigidArea(new Dimension(10, 15)), showButton);

        	this.assignmentPanel = new KPanel(1_000, 35);
        	this.assignmentPanel.setLayout(new BoxLayout(this.assignmentPanel, BoxLayout.X_AXIS));
        	this.assignmentPanel.addAll(namePanel, quantaPanel);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear, "+Student.getLastName()+", the "+this.courseName+
                        (this.isGroup ? " Group Assignment" : " Assignment")+" is to be submitted in 24 hours. Submission Mode is "+this.modeOfSubmission+". " +
                        "If you have already submitted this assignment, mark it as 'submitted' to prevent further-notifications.";
                Notification.create("Assignment Reminder",this.courseName+" Assignment is due tomorrow!", text);
                eveIsAlerted = true;
            }
        }

        private void signalSubmissionNotice(){
            if (!submissionIsAlerted) {
                final String text = "Dear, "+Student.getLastName()+", the submission date of the "+
                        this.courseName+(this.isGroup ? " Group Assignment" : " Assignment")+" is past. Submission Mode was "+this.modeOfSubmission+".";
                Notification.create("Assignment Completed",this.courseName+" Assignment has reached submission date.", text);
                submissionIsAlerted = true;
            }
        }

        public KLabel getDeadlineIndicator(){
            return deadlineIndicator;
        }

        public boolean isGroup() {
            return isGroup;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean on) {
            isOn = on;
            if (!on) {
                this.timer.stop();
            }
        }

        public String getCourseName() {
            return courseName;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getDeadLine() {
            return deadLine;
        }

        public void setDeadLine(String deadLine) {
            this.deadLine = deadLine;
            deadlineIndicator.setText("Deadline: "+deadLine);
        }

        public String getStartDate() {
        	return startDate;
        }

        public String getSubmissionDate() {
            return dateSubmitted;
        }

        public void setSubmissionDate(String submissionDate) {
            this.dateSubmitted = submissionDate;
        }

        public String getModeOfSubmission() {
            return modeOfSubmission;
        }

        public void setModeOfSubmission(String modeOfSubmission) {
            this.modeOfSubmission = modeOfSubmission;
        }

        public int getTimeRemaining(){
            return MDate.actualDayDifference(Objects.requireNonNull(MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0")),
                    Objects.requireNonNull(MDate.parse(this.deadLine + " 0:0:0")));
        }

        private void effectMembersCount(int effectValue){
            memberCount += effectValue;
            groupLabel.setText(Globals.checkPlurality(memberCount, "Members"));
        }

        public KPanel getLayer(){
            return assignmentPanel;
        }

        private void wakeAlive(){
            if (getTimeRemaining() == 1) {
                signalEveNotice();
            }
            final int residue = Globals.DAY_IN_MILLI - MDate.getTimeValue(new Date());
            this.initializeTimer(residue);
        }

        private void wakeDead(){
            this.setOn(false);
            this.setSubmissionDate(this.deadLine);
            this.signalSubmissionNotice();
        }

        //Inner-class of an inner-class...
        private static class DeadLineEditor extends KDialog {

            private DeadLineEditor(AssignmentSelf assignmentSelf){
                super("Edit Deadline");
                this.setResizable(true);
                this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

                final Font valsFont = KFontFactory.createPlainFont(16);
                final Date assignmentDeadline = MDate.parse(assignmentSelf.deadLine+" 0:0:0");

                final KTextField dField = KTextField.newDayField();
                dField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.DATE));
                final KTextField mField = KTextField.newMonthField();
                mField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.MONTH));
                final KTextField yField = KTextField.newYearField();
                yField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.YEAR));
                final KPanel datesPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
                datesPanel.addAll(new KLabel("D", valsFont), dField, Box.createRigidArea(new Dimension(20, 30)),
                        new KLabel("M", valsFont), mField, Box.createRigidArea(new Dimension(20, 30)),
                        new KLabel("Y", valsFont), yField);
                final KPanel deadLinePanel = new KPanel(new BorderLayout(), new Dimension(465, 35));
                deadLinePanel.add(KPanel.wantDirectAddition(new KLabel("New Deadline", KFontFactory.createBoldFont(15))), BorderLayout.WEST);
                deadLinePanel.add(datesPanel,BorderLayout.EAST);

                final KButton setButton = new KButton("Set");
                setButton.addActionListener(e1 -> {
                    if (Globals.isBlank(dField.getText())) {
                        App.signalError(this.getRootPane(),"Error", "Please specify the day");
                        dField.requestFocusInWindow();
                    } else if (Globals.isBlank(mField.getText())) {
                        App.signalError(this.getRootPane(),"Error", "Please specify the month");
                        mField.requestFocusInWindow();
                    } else if (Globals.isBlank(yField.getText())) {
                        App.signalError(this.getRootPane(),"Error", "Please specify the year");
                        yField.requestFocusInWindow();
                    } else {
                        final Date newDeadline = MDate.parse(dField.getText()+"/"+mField.getText()+"/"+yField.getText()+" 0:0:0");
                        if (newDeadline == null) {
                            return;
                        }
                        if (newDeadline.before(new Date())) {
                            App.signalError(this.getRootPane(), "Invalid Deadline", "Sorry, the deadline cannot be at most today.");
                        } else {
                            assignmentSelf.setDeadLine(MDate.formatDateOnly(newDeadline));
                            this.dispose();
                        }
                    }
                });
                this.getRootPane().setDefaultButton(setButton);
                final KButton cancelButton = new KButton("Cancel");
                cancelButton.addActionListener(e2 -> this.dispose());
                final KPanel bottomPlate = new KPanel(new FlowLayout(FlowLayout.RIGHT));
                bottomPlate.addAll(cancelButton,setButton);

                final KPanel allPlate = new KPanel();
                allPlate.setLayout(new BoxLayout(allPlate, BoxLayout.Y_AXIS));
                allPlate.addAll(deadLinePanel,Box.createVerticalStrut(25),bottomPlate);
                this.setContentPane(allPlate);
                this.pack();
                this.setMinimumSize(this.getPreferredSize());
                this.setLocationRelativeTo(Board.getRoot());
                SwingUtilities.invokeLater(()-> this.setVisible(true));
            }
        }

        //Hmm, here comes another one
        private class MemberExhibitor extends KDialog {
            int pX, pY;
            private KLabel titleLabel;
            private KPanel membersPanel;
            private KButton memberAdder;

            private MemberExhibitor(AssignmentSelf assignmentSelf){
                this.setUndecorated(true);
                this.setSize(500, 500);
                this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
                titleLabel = new KLabel("", KFontFactory.createPlainFont(15), Color.BLUE);
                final KPanel upperBar = new KPanel(new FlowLayout(FlowLayout.CENTER));
                upperBar.add(titleLabel);
                upperBar.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        pX = e.getX();
                        pY = e.getY();
                        upperBar.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        MemberExhibitor.this.setLocation(MemberExhibitor.this.getLocation().x + e.getX() - pX,
                                MemberExhibitor.this.getLocation().y + e.getY() - pY);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        upperBar.setCursor(null);
                    }
                });
                upperBar.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        MemberExhibitor.this.setLocation(MemberExhibitor.this.getLocation().x + e.getX() - pX,
                                MemberExhibitor.this.getLocation().y + e.getY() - pY);
                    }
                });

                membersPanel = new KPanel(){
                    @Override
                    public Component add(Component comp) {
                        assignmentSelf.effectMembersCount(1);
                        titleLabel.setText(assignmentSelf.getCourseName()+" Assignment : "+assignmentSelf.groupLabel.getText());
                        membersPanel.setPreferredSize(new Dimension(membersPanel.getPreferredSize().width,membersPanel.getPreferredSize().height+35));
                        return super.add(comp);
                    }

                    @Override
                    public void remove(Component comp) {
                        super.remove(comp);
                        membersPanel.setPreferredSize(new Dimension(membersPanel.getPreferredSize().width,membersPanel.getPreferredSize().height-35));
                        assignmentSelf.effectMembersCount(-1);
                        titleLabel.setText(assignmentSelf.getCourseName()+" Assignment : "+assignmentSelf.groupLabel.getText());
                    }
                };
                membersPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                membersPanel.setBackground(Color.WHITE);
                final KScrollPane midScroll = new KScrollPane(membersPanel, false);

                final KButton closeButton = new KButton("Close");
                closeButton.addActionListener(e -> this.dispose());

                memberAdder = new KButton("Add Member");
                memberAdder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                memberAdder.addActionListener(e -> {
                    final String newMemberName = App.requestInput(this.getRootPane(),"New Member","Enter member's name below:\n \n");
                    if (newMemberName != null && !Globals.isBlank(newMemberName)) {
                        if (newMemberName.length() > 30) {
                            App.signalError("Error", "Sorry, a member's name cannot exceed 30 characters.");
                        } else {
                            appendNewMember(newMemberName, false);
                            assignmentSelf.members.add(newMemberName);
                        }
                    }
                });
                memberAdder.setEnabled(assignmentSelf.isOn);

                this.getRootPane().setDefaultButton(closeButton);
                final KPanel contentPanel = new KPanel(new BorderLayout());
                contentPanel.setBorder(BorderFactory.createLineBorder(null, 1, false));
                contentPanel.add(upperBar, BorderLayout.NORTH);
                contentPanel.add(midScroll, BorderLayout.CENTER);
                contentPanel.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT), null, memberAdder, closeButton),
                        BorderLayout.SOUTH);
                this.setContentPane(contentPanel);
                this.setLocationRelativeTo(Board.getRoot());

                effectMembersCount(-assignmentSelf.memberCount);//This is useful for those triggered from serialization
                if (assignmentSelf.members.isEmpty()) {
                    appendNewMember(Student.getFullNamePostOrder()+" (me)", true);
                    assignmentSelf.members.add(Student.getFullNamePostOrder());
                } else {
                    appendNewMember(Student.getFullNamePostOrder()+" (me)", true);
                    for (int i = 1; i < assignmentSelf.members.size(); i++) {
                        appendNewMember(assignmentSelf.members.get(i), false);
                    }
                }
                titleLabel.setText(assignmentSelf.getCourseName()+" Assignment : "+assignmentSelf.groupLabel.getText());
            }

            private void appendNewMember(String name, boolean myself){
                final KLabel nameLabel = new KLabel(name,KFontFactory.createPlainFont(18));

                final KButton removeButton = new KButton("X");
                removeButton.setStyle(KFontFactory.createPlainFont(15), Color.RED);
                removeButton.undress();

                final KPanel namePanel = new KPanel(new BorderLayout(),new Dimension(480,30));
                namePanel.add(KPanel.wantDirectAddition(nameLabel),BorderLayout.WEST);
                namePanel.add(removeButton, BorderLayout.EAST);
                namePanel.setBackground(Color.WHITE);//except head and toe, the dialog is to be white
                namePanel.getComponent(0).setBackground(Color.WHITE);

                membersPanel.add(namePanel);
                ComponentAssistant.ready(membersPanel);

                removeButton.setToolTipText("Remove "+name.split(" ")[0]);
                removeButton.addActionListener(e -> {
                    if (App.showYesNoCancelDialog(this.getRootPane(), "Confirm Removal",
                            "This action will remove "+name+" as a group participant for this assignment.\nContinue?")) {
                        membersPanel.remove(namePanel);
                        ComponentAssistant.ready(membersPanel);
                        AssignmentSelf.this.members.remove(name);
                    }
                });
                removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                removeButton.setEnabled(!myself);
            }
        }
    }


    public static class EventSelf implements Serializable {
        private String title;
        private String dateDue;
        private Timer timer;
        private boolean isPending;
        private boolean eveIsAlerted, timeupIsAlerted;
        private transient KLabel stateIndicator;
        private transient KButton canceller;
        private transient KPanel eventLayer;

        public EventSelf(String eName, String eDate){
            setCoreProperties(eName, eDate);
            setUpUI();
        }

        private void setCoreProperties(String eName, String eDate){
            this.isPending = true;
            this.title = eName;
            this.dateDue = eDate;
            this.initializeTimer(Globals.DAY_IN_MILLI);
        }

        private void initializeTimer(int iDelay){
            timer = new Timer(Globals.DAY_IN_MILLI,null);
            timer.setInitialDelay(iDelay);
            timer.addActionListener(e -> {
                final Calendar eveCalendar = Calendar.getInstance();
                eveCalendar.setTime(MDate.parse(this.dateDue+" 0:0:0"));
                eveCalendar.add(Calendar.DATE, -1);
                if (MDate.sameDay(eveCalendar.getTime(), new Date())) {
                    signalEveNotice();
                } else if(MDate.sameDay(MDate.parse(this.dateDue+" 0:0:0"), new Date())) {
                    endState();
                    setUpUI();
                    ComponentAssistant.ready(this.eventLayer);
                    TasksGenerator.EventsHandler.renewCount(-1);
                }
            });
            timer.start();
        }

        private void setUpUI(){
            if (isPending) {
                canceller = KButton.getIconifiedButton("terminate.png", 15, 15);
                canceller.setToolTipText("Terminate this Event");
                canceller.addActionListener(e -> {
                    if (App.showYesNoCancelDialog("Confirm Termination",
                            "Do you really wish to cancel this " + (this.isTest() ? "Test?" : this.isExam() ? "Exam?" : "Event?"))) {
                        TasksGenerator.EventsHandler.deleteEvent(this);
                        this.isPending = false;
                        this.timer.stop();
                    }
                });
            } else {
                canceller = KButton.getIconifiedButton("trash.png", 20, 20);
                canceller.setToolTipText("Remove this Event");
                canceller.addActionListener(e -> {
                    if (App.showYesNoCancelDialog("Confirm Removal","Do you wish to remove this "+(isTest() ? "Test?" : isExam() ? "Exam?" : "Event?"))) {
                        TasksGenerator.EventsHandler.deleteEvent(this);
                    }
                });
            }
            canceller.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            this.stateIndicator = new KLabel("Pending : "+dateDue, KFontFactory.createBoldFont(16));
            this.stateIndicator.setOpaque(false);

            this.eventLayer = new KPanel(new Dimension(1_000,30));//this is 30
            this.eventLayer.setLayout(new BorderLayout());
            this.eventLayer.add(KPanel.wantDirectAddition(new KLabel(this.getTitle(), KFontFactory.createBoldFont(16), Color.BLUE)),
                    BorderLayout.WEST);
            this.eventLayer.add(KPanel.wantDirectAddition(stateIndicator), BorderLayout.CENTER);
            this.eventLayer.add(canceller, BorderLayout.EAST);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String eveText = "Dear "+Student.getLastName()+", "+this.title+" is just one day away from now.";
                Notification.create("Event Reminder",Student.getLastName()+", "+this.getTitle()+" is at your door-step!", eveText);
                eveIsAlerted = true;
            }
        }

        private void signalTimeupNotice(){
            if (!timeupIsAlerted) {
                final String timeupText = "Dear "+Student.getLastName()+", time is up for the event "+this.title+".";
                Notification.create("Event Time-Up",Student.getLastName()+", "+this.getTitle()+" is due now!", timeupText);
                timeupIsAlerted = true;
            }
        }

        public String getTitle(){
            return title;
        }

        public boolean isTest(){
            return this.title.contains("Test");
        }

        public boolean isExam(){
            return this.title.contains("Exam");
        }

        public boolean isPending(){
            return isPending;
        }

        private KButton getCanceller(){
            return canceller;
        }

        public KPanel getEventLayer(){
            return eventLayer;
        }

        public void endState(){
            this.stateIndicator.setText("Past : "+this.dateDue);
            this.stateIndicator.setFont(KFontFactory.createPlainFont(16));
            this.isPending = false;
            this.timer.stop();
            signalTimeupNotice();
        }

        private void wakeAlive(){
            final Calendar eveCalendar = Calendar.getInstance();
            eveCalendar.setTime(MDate.parse(this.dateDue+" 0:0:0"));
            eveCalendar.add(Calendar.DATE, -1);
            if (MDate.sameDay(eveCalendar.getTime(), new Date())) {
                signalEveNotice();
            }
            final int residue = Globals.DAY_IN_MILLI - MDate.getTimeValue(new Date());
            this.initializeTimer(residue);
        }
    }


    public static void serializeAll(){
        System.out.print("Serializing tasks... ");
        MyClass.serialize(TasksGenerator.TodoHandler.TODOS, "todos.ser");
        MyClass.serialize(TasksGenerator.ProjectsHandler.PROJECTS, "projects.ser");
        MyClass.serialize(TasksGenerator.AssignmentsHandler.ASSIGNMENTS, "assignments.ser");
        MyClass.serialize(TasksGenerator.EventsHandler.EVENTS, "events.ser");
        System.out.println("Completed.");
    }

    public static void deSerializeAll(){
        System.out.print("Deserializing tasks... ");
        final ArrayList<TodoSelf> savedTasks = (ArrayList) MyClass.deserialize("todos.ser");
        if (savedTasks != null) {
            for (TodoSelf todoSelf : savedTasks) {
                if (todoSelf.isActive) {//This only means it slept alive - we're to check if it's to wake alive or not
                    if (new Date().before(MDate.parse(todoSelf.dateExpectedToComplete))) {
                        todoSelf.wakeAlive();
                    } else {
                        todoSelf.wakeDead();
                    }
                }
                todoSelf.setUpUI();
                TasksGenerator.TodoHandler.receiveFromSerials(todoSelf);
            }
        }

        final ArrayList<ProjectSelf> savedProjects = (ArrayList) MyClass.deserialize("projects.ser");
        if (savedProjects != null) {
            for (ProjectSelf projectSelf : savedProjects) {
                if (projectSelf.isLive) {
                    if (new Date().before(MDate.parse(projectSelf.dateExpectedToComplete))) {
                        projectSelf.wakeLive();
                        projectSelf.initializeUI();
                    } else {
                        projectSelf.wakeDead();
                        projectSelf.setUpDoneUI();
                    }
                } else {
                    projectSelf.setUpDoneUI();
                }
                TasksGenerator.ProjectsHandler.receiveFromSerials(projectSelf);
            }
        }

        final ArrayList<AssignmentSelf> savedAssignments = (ArrayList) MyClass.deserialize("assignments.ser");
        if (savedAssignments != null) {
            for (AssignmentSelf assignmentSelf : savedAssignments) {
                if (assignmentSelf.isOn) {
                    if (MDate.parse(MDate.formatDateOnly(new Date())+" 0:0:0").before(MDate.parse(assignmentSelf.deadLine+" 0:0:0"))) {
                        assignmentSelf.wakeAlive();
                    } else {
                        assignmentSelf.wakeDead();
                    }
                }
                assignmentSelf.setUpUI();
                TasksGenerator.AssignmentsHandler.receiveFromSerials(assignmentSelf);
            }
        }

        final ArrayList<EventSelf> savedEvents = (ArrayList) MyClass.deserialize("events.ser");
        if (savedEvents != null) {
            for (EventSelf eventSelf : savedEvents) {
                if (eventSelf.isPending) {
                    if (MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0").before(MDate.parse(eventSelf.dateDue + " 0:0:0"))) {
                        eventSelf.wakeAlive();
                    } else {
                        eventSelf.endState();
                    }
                }
                eventSelf.setUpUI();
                TasksGenerator.EventsHandler.receiveFromSerials(eventSelf);
            }
        }
        System.out.println("Completed.");
    }

}
