package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * This class packages all its data onto a panel, which is also added to the cardBoard Layout
 * in Board to make it come to sight at the corresponding big-button click.
 * It serves as the intermediary between all the so-called TaskSelf, Helpers, etc. and the Board.
 */
public class TasksGenerator {
    private TodoHandler todoGuy = new TodoHandler();
    private ProjectsHandler projectsGuy = new ProjectsHandler();
    private AssignmentsHandler assignmentsGuy = new AssignmentsHandler();
    private EventsHandler eventsGuy = new EventsHandler();
    private CardLayout cardLayout = new CardLayout();
    private KPanel inPanel = new KPanel(cardLayout);
    private static KButton todoBigButton, projectBigButton, assignmentBigButton, eventBigButton;
    private static KLabel hintLabel;
    private static final Font TASK_BUTTONS_FONT = KFontFactory.createPlainFont(14);
    private static final Font TASK_HEADERS_FONT = KFontFactory.createPlainFont(18);
    private static final int CONTENTS_POSITION = FlowLayout.LEFT;


    public TasksGenerator(){//'Home' is added with it
        hintLabel = KLabel.getPredefinedLabel("My Tasks",SwingConstants.LEFT);
        hintLabel.setStyle(KFontFactory.bodyHeaderFont(), Color.BLUE);

        final KPanel home = new KPanel(new FlowLayout(FlowLayout.LEFT));
        home.addAll(giveTodoButton(),Box.createRigidArea(new Dimension(25, 150)),
                giveProjectsButton(),Box.createRigidArea(new Dimension(25, 150)),
                giveAssignmentsButton(),Box.createRigidArea(new Dimension(25, 150)),
                giveEventsButton());

        cardLayout.addLayoutComponent(inPanel.add(home),"Home");
    }

    /**
     * Basically, returns a big-button that'll be added to the home. And also configures
     * its on-click action by adding its succeeding component to the card.
     * The rest perform the same.
     */
    private KButton giveTodoButton(){
        todoBigButton = provideGeneralLook("TODO", TodoHandler.activeCount);
        todoBigButton.addActionListener(e ->  {
            cardLayout.show(inPanel,"TODO");
            hintLabel.setText(" > TODO List");
        });
        cardLayout.addLayoutComponent(inPanel.add(todoGuy.todoComponent()),"TODO");
        return todoBigButton;
    }

    private KButton giveProjectsButton(){
        projectBigButton = provideGeneralLook("Projects", ProjectsHandler.liveCount);
        projectBigButton.addActionListener(e -> {
            cardLayout.show(inPanel,"Projects");
            hintLabel.setText(" > Projects");
        });
        cardLayout.addLayoutComponent(inPanel.add(projectsGuy.projectComponent()),"Projects");
        return projectBigButton;
    }

    private KButton giveAssignmentsButton(){
        assignmentBigButton = provideGeneralLook("Assignments", AssignmentsHandler.getDoingCount());
        assignmentBigButton.addActionListener(e -> {
            cardLayout.show(inPanel,"Assignments");
            hintLabel.setText(" > Assignments");
        });
        cardLayout.addLayoutComponent(inPanel.add(assignmentsGuy.assignmentsComponent()),"Assignments");
        return assignmentBigButton;
    }

    private KButton giveEventsButton(){
        eventBigButton = provideGeneralLook("Upcoming", EventsHandler.upcomingCount);
        eventBigButton.addActionListener(e -> {
            cardLayout.show(inPanel,"Events");
            hintLabel.setText(" > Upcoming Events");
        });
        cardLayout.addLayoutComponent(inPanel.add(eventsGuy.eventsComponent()),"Events");
        return eventBigButton;
    }

    /**
     * Provides the shared look of these big-buttons.
     * Note that the setText function of these buttons are forwarded to their inner-label
     * numberText, which indicates the number of tasks running on each.
     */
    private KButton provideGeneralLook(String label, int number){
        final KLabel lText = new KLabel(label, KFontFactory.createBoldFont(17));
        lText.setPreferredSize(new Dimension(175, 30));
        lText.setHorizontalAlignment(KLabel.CENTER);

        final KLabel numberText = new KLabel(Integer.toString(number), KFontFactory.createBoldFont(50));
        numberText.setHorizontalAlignment(KLabel.CENTER);

        final KButton lookButton = new KButton(){
            @Override
            public void setText(String text) {
                numberText.setText(text);
                ComponentAssistant.ready(numberText);
            }
        };
        lookButton.setPreferredSize(new Dimension(175, 150));
        lookButton.setLayout(new BorderLayout(0, 0));
        lookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lookButton.add((lText), BorderLayout.NORTH);
        lookButton.add(numberText, BorderLayout.CENTER);
        return lookButton;
    }

    public KPanel presentedContainer(){
        final KButton returnButton = new KButton("< Back");
        returnButton.setFont(TASK_BUTTONS_FONT);
        returnButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        returnButton.addActionListener(e -> {
            cardLayout.show(inPanel,"Home");
            hintLabel.setText("");
        });
        returnButton.setMnemonic(KeyEvent.VK_BACK_SPACE);
        returnButton.setToolTipText("Return to Task Menu (Alt+Back_Space)");
        returnButton.doClick(0);

        final KPanel upPanel = new KPanel(new BorderLayout());
        upPanel.add(hintLabel, BorderLayout.WEST);
        upPanel.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT), null, returnButton), BorderLayout.EAST);

        final KPanel present = new KPanel(new BorderLayout());
        present.add(upPanel, BorderLayout.NORTH);
        present.add(inPanel);
        return present;
    }


    /*
    What do these helper-classes do?
    Like their immediate mother-class, they serve as the intermediary between their respective task-types
    and the Board. They provide UI and behavior as well.
     */
    public static class TodoHandler {
        public static final ArrayList<TaskSelf.TodoSelf> TODOS = new ArrayList<>();
        private static int activeCount, dormantCount;
        private static KPanel activeContainer, dormantContainer;
        private static TaskCreator.TodoCreator todoCreator;

        public TodoHandler(){
            activeContainer = new KPanel(){
                @Override
                public Component add(Component comp) {
                    TODOS.add((TaskSelf.TodoSelf) comp);
                    activeContainer.setPreferredSize(new Dimension(activeContainer.getPreferredSize().width,activeContainer.getPreferredSize().height+40));
                    renewCount(1);
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    TODOS.remove(comp);
                    activeContainer.setPreferredSize(new Dimension(activeContainer.getPreferredSize().width,
                            activeContainer.getPreferredSize().height-40));
                    renewCount(-1);
                }
            };
            activeContainer.setLayout(new FlowLayout(CONTENTS_POSITION));

            dormantContainer = new KPanel(){
                @Override
                public Component add(Component comp) {
                    dormantContainer.setPreferredSize(new Dimension(dormantContainer.getPreferredSize().width,
                            dormantContainer.getPreferredSize().height+40));
                    TODOS.add((TaskSelf.TodoSelf) comp);
                    dormantCount++;
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    TODOS.remove(comp);
                    dormantContainer.setPreferredSize(new Dimension(dormantContainer.getPreferredSize().width,
                            dormantContainer.getPreferredSize().height-40));
                    dormantCount--;
                }
            };
            dormantContainer.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionWaiter(){
            return e -> {
                final String name = todoCreator.getDescriptionField().getText();
                int givenDays = 0;
                if (Globals.isBlank(name)) {
                    App.signalError(todoCreator.getRootPane(), "Blank Name", "Please specify a name for the task");
                    todoCreator.getDescriptionField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.signalError("Error", "Sorry, description of a task must be at most "+TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters only.");
                } else {
                    final String span = todoCreator.getDuration();
                    if (Objects.equals(span, "Five Days")) {
                        givenDays = 5;
                    } else if (Objects.equals(span, "One Week")) {
                        givenDays = 7;
                    } else if (Objects.equals(span, "Two Weeks")) {
                        givenDays = 14;
                    } else if (Objects.equals(span, "Three Weeks")) {
                        givenDays = 21;
                    } else if (Objects.equals(span, "One Month")) {
                        givenDays = 30;
                    }

                    if (App.showYesNoCancelDialog(todoCreator.getRootPane(), "Confirm", "Do you wish to add the following task?\n-\n" +
                            "Name:  " + name + "\n" +
                            "To be completed in:  " + span)) {
                        activeContainer.add(new TaskSelf.TodoSelf(name, givenDays));
                        ComponentAssistant.ready(activeContainer);
                        todoCreator.dispose();
                    }
                }
            };
        }

        public static void transferTask(TaskSelf.TodoSelf oldSelf, KDialog dialog, boolean timeDue){
            if (timeDue) {
                oldSelf.setTotalTimeConsumed(oldSelf.getSpecifiedDuration());
            	finalizeTransfer(oldSelf);
            } else {
                if (App.showYesNoDialog(dialog.getRootPane(), "Confirm Completion", "Are you sure you've completed this task?")) {
                    oldSelf.setTotalTimeConsumed(oldSelf.getDaysTaken());
                	finalizeTransfer(oldSelf);
                    dialog.dispose();
                }
            }
        }

        private static void finalizeTransfer(TaskSelf.TodoSelf oldSelf) {
        	oldSelf.setDateCompleted(MDate.now());
            oldSelf.getTogoLabel().setText("Completed "+oldSelf.getDateCompleted());//Which is that
            oldSelf.getTogoLabel().setForeground(Color.BLUE);
            oldSelf.setActive(false);
            activeContainer.remove(oldSelf);
            dormantContainer.add(oldSelf);
            ComponentAssistant.ready(activeContainer, dormantContainer);
        }

        public static ActionListener removalWaiter(TaskSelf.TodoSelf task, KDialog dialog){
            return e -> {
                if (App.showYesNoCancelDialog(dialog.getRootPane(),"Confirm", "Do you really want to remove this task?")) {
                    if (task.isActive()) {
                        activeContainer.remove(task);
                        ComponentAssistant.ready(activeContainer);
                    } else {
                        dormantContainer.remove(task);
                        ComponentAssistant.ready(dormantContainer);
                    }
                    task.setActive(false);
                    dialog.dispose();
                }
            };
        }

        private static void renewCount(int valueEffected){
            activeCount += valueEffected;
            todoBigButton.setText(activeCount);
        }
        
        public static void receiveFromSerials(TaskSelf.TodoSelf dTodo){
            if (dTodo.isActive()) {
                activeContainer.add(dTodo);
            } else {
                dormantContainer.add(dTodo);
            }
        }

        private JComponent todoComponent(){
            final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, setUpRunningTasks(), setUpCompletedTasks());
            splitPane.setContinuousLayout(true);
            splitPane.setBorder(null);
            splitPane.setDividerLocation(175);

            final KPanel todoComponent = new KPanel();
            todoComponent.setLayout(new BoxLayout(todoComponent,BoxLayout.Y_AXIS));
            todoComponent.add(splitPane);
            return todoComponent;
        }

        private JComponent setUpRunningTasks(){
            final KButton addButton = new KButton("New Task");
            addButton.setFont(TASK_BUTTONS_FONT);
            addButton.setMnemonic(KeyEvent.VK_T);
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addButton.setToolTipText("Create a Task (Alt+T)");
            addButton.addActionListener(e -> {
                todoCreator = new TaskCreator.TodoCreator();
                todoCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(addButton, BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("Active Tasks", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel runningTasksPanel = new KPanel(new BorderLayout());
            runningTasksPanel.add(labelPanelPlus, BorderLayout.NORTH);
            final KScrollPane aScroll = new KScrollPane(activeContainer, false);
            runningTasksPanel.add(aScroll, BorderLayout.CENTER);
            return runningTasksPanel;
        }

        private JComponent setUpCompletedTasks(){
            final KButton clearButton = new KButton("Clear List");
            clearButton.setFont(TASK_BUTTONS_FONT);
            clearButton.setToolTipText("Remove Completed Tasks (Alt+C)");
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearButton.addActionListener(e -> {
                if (dormantContainer.getComponentCount() > 0) {
                    if (App.showOkCancelDialog("Confirm", "This action will remove all the completed tasks.")) {
                        for (Component c : dormantContainer.getComponents()) {
                            dormantContainer.remove(c);
                        }
                        ComponentAssistant.ready(dormantContainer);
                    }
                }
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(clearButton, BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("Completed Tasks",TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel completedTasksPanel = new KPanel(new BorderLayout());
            completedTasksPanel.add(labelPanelPlus, BorderLayout.NORTH);
            final KScrollPane cScroll = new KScrollPane(dormantContainer, false);
            completedTasksPanel.add(cScroll, BorderLayout.CENTER);
            return completedTasksPanel;
        }

        private int getAllCount(){
            return activeCount + dormantCount;
        }
    }


    public static class ProjectsHandler {
        public static final ArrayList<TaskSelf.ProjectSelf> PROJECTS = new ArrayList<>();
        private static int liveCount, completeCount;//count - number of all, liveCount - number currently running
        private static KPanel projectsReside;
        private static TaskCreator.ProjectCreator projectCreator;

        public ProjectsHandler(){
            projectsReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    projectsReside.setPreferredSize(new Dimension(projectsReside.getPreferredSize().width,
                            projectsReside.getPreferredSize().height+40));
                    PROJECTS.add((TaskSelf.ProjectSelf) comp);
                    return super.add(comp);
                }
                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    PROJECTS.remove(comp);
                    projectsReside.setPreferredSize(new Dimension(projectsReside.getPreferredSize().width,
                            projectsReside.getPreferredSize().height-40));
                }
            };
            projectsReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionWaiter(){
            return e -> {
                final String name = projectCreator.getNameField().getText();
                int givenDays = 0;
                if (Globals.isBlank(name)) {
                    App.signalError("Blank Name","Please specify a name for the project");
                    projectCreator.getNameField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.signalError("Error", "Sorry, name of a project must be at most "+TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters only.");
                } else {
                    final String dDuration = projectCreator.getTheDuration();
                    if (Objects.equals(dDuration, "Five Days")) {
                        givenDays = 5;
                    } else if (Objects.equals(dDuration, "One Week")) {
                        givenDays = 7;
                    } else if (Objects.equals(dDuration, "Two Weeks")) {
                        givenDays = 14;
                    } else if (Objects.equals(dDuration, "Three Weeks")) {
                        givenDays = 21;
                    } else if (Objects.equals(dDuration, "One Month")) {
                        givenDays = 30;
                    } else if (Objects.equals(dDuration, "Two Months")) {
                        givenDays = 60;
                    } else if (Objects.equals(dDuration, "Three Months")) {
                        givenDays = 90;
                    } else if (Objects.equals(dDuration, "Six Months")) {
                        givenDays = 180;
                    }

                    if (App.showYesNoCancelDialog(projectCreator.getRootPane(), "Confirm Addition", "Do you wish to add the following project?\n-\n" +
                            "Name:  " + name + "\n" +
                            "Type:  " + projectCreator.getTheType() + " Project" + "\n" +
                            "Duration:  " + dDuration)) {
                        projectsReside.add(new TaskSelf.ProjectSelf(name, projectCreator.getTheType(), givenDays));
                        projectCreator.dispose();
                        ComponentAssistant.ready(projectsReside);
                        renewCount(1);
                    }
                }
            };
        }

        public static void performIComplete(TaskSelf.ProjectSelf project, boolean timeDue){
        	if (timeDue) {
        	    project.setTotalTimeConsumed(project.getSpecifiedDuration());
        		finalizeCompletion(project);
                renewCount(-1);
                completeCount++;
        	} else {
        		if (App.showYesNoDialog("Confirm Completion", "Are you sure you've completed this project before the specified time?")) {
                    project.setTotalTimeConsumed(project.getDaysTaken());
        			finalizeCompletion(project);
                    renewCount(-1);
                    completeCount++;
        		}
        	}

        }

        private static void finalizeCompletion(TaskSelf.ProjectSelf project){
            project.setDateCompleted(MDate.now());
            project.setLive(false);
            ComponentAssistant.repair(project);
            project.setUpDoneUI();
            ComponentAssistant.ready(project);
            //Respect that order of sorting... since the project generator does not use separator for arrangement
            projectsReside.remove(project);
            projectsReside.add(project);
            ComponentAssistant.ready(projectsReside);
        }

        public static ActionListener removalListener(TaskSelf.ProjectSelf project){
            return e -> {
                if (App.showOkCancelDialog("Confirm","This Project will be cleared from your list.")) {
                    if (project.isLive()) {
                        renewCount(-1);
                    } else {
                        completeCount--;
                    }
                    project.setLive(false);
                    projectsReside.remove(project);
                    ComponentAssistant.ready(projectsReside);
                }
            };
        }

        public static void renewCount(int value){
            liveCount += value;
            projectBigButton.setText(liveCount);
        }

        public static void receiveFromSerials(TaskSelf.ProjectSelf dProject){
            projectsReside.add(dProject);
            if (dProject.isLive()) {
                renewCount(1);
            } else {
                completeCount++;
            }
        }

        private JComponent projectComponent(){
            final KButton addButton = new KButton("New Project");
            addButton.setFont(TASK_BUTTONS_FONT);
            addButton.setMnemonic(KeyEvent.VK_P);
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addButton.setToolTipText("Create a Project (Alt+P)");
            addButton.addActionListener(e -> {
                projectCreator = new TaskCreator.ProjectCreator();
                projectCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(addButton, BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("My Projects", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel projectComponent = new KPanel(new BorderLayout());
            projectComponent.add(labelPanelPlus,BorderLayout.NORTH);
            projectComponent.add(new KScrollPane(projectsReside, false), BorderLayout.CENTER);
            return projectComponent;
        }

        private int getTotalCount(){
            return liveCount + completeCount;
        }
    }


    public static class AssignmentsHandler {
        public static final ArrayList<TaskSelf.AssignmentSelf> ASSIGNMENTS = new ArrayList<>();
		private static int doingCount, doneCount;
        private static KPanel activeReside, doneReside;
        private static TaskCreator.AssignmentCreator assignmentCreator;

        public AssignmentsHandler(){
        	activeReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    activeReside.setPreferredSize(new Dimension(activeReside.getPreferredSize().width, activeReside.getPreferredSize().height+40));
                    ASSIGNMENTS.add((TaskSelf.AssignmentSelf) comp);
                    renewCount(1);
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    activeReside.setPreferredSize(new Dimension(activeReside.getPreferredSize().width, activeReside.getPreferredSize().height-40));
                    renewCount(-1);
                    ASSIGNMENTS.remove(comp);
                }
            };
            activeReside.setLayout(new FlowLayout(CONTENTS_POSITION));

            doneReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    doneReside.setPreferredSize(new Dimension(doneReside.getPreferredSize().width, doneReside.getPreferredSize().height+40));
                    ASSIGNMENTS.add((TaskSelf.AssignmentSelf) comp);
                    doneCount++;
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    ASSIGNMENTS.remove(comp);
                    doneCount--;
                    doneReside.setPreferredSize(new Dimension(doneReside.getPreferredSize().width, doneReside.getPreferredSize().height-40));
                }
            };
            doneReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionListener(){
            return e -> {
                final String name = assignmentCreator.getNameField().getText();
                if (Globals.isBlank(name)) {
                    App.signalError(assignmentCreator.getRootPane(), "Blank Name", "Please provide the name of the course");
                    assignmentCreator.getNameField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.signalError(assignmentCreator.getRootPane(), "Error", "Sorry, the subject name cannot exceed "+TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters.");
                    assignmentCreator.getNameField().requestFocusInWindow();
                } else if (Globals.isBlank(assignmentCreator.getProvidedDeadLine())) {
                    App.signalError(assignmentCreator.getRootPane(), "Deadline Error", "Please fill out all the fields for the deadline. You can change them later.");
                } else {
                    final String type = assignmentCreator.isGroup() ? "Group Assignment" : "Individual Assignment";
                    final String question = assignmentCreator.getQuestion();
                    final Date givenDate = MDate.parse(assignmentCreator.getProvidedDeadLine()+" 0:0:0");
                    if (givenDate.before(new Date())) {
                        App.signalError(assignmentCreator.getRootPane(), "Invalid Deadline", "Sorry, the deadline cannot be at most today.");
                        return;
                    }
                    final String deadline = MDate.formatDateOnly(givenDate);
                    final String preMean = String.valueOf(assignmentCreator.getSelectedMode());
                    String mean;
                    if (preMean.contains("hard")) {
                        mean = "A Hard Copy";
                    } else if (preMean.contains("soft")) {
                        mean = "A Soft Copy";
                    } else if (preMean.contains("email")) {
                        mean = "An Email Address - " + assignmentCreator.getMeanValue();
                    } else if (preMean.contains("web")) {
                        mean = "A Webpage - " + assignmentCreator.getMeanValue();
                    } else {
                        mean = "Other Means";
                    }
                    if (App.showYesNoCancelDialog(assignmentCreator.getRootPane(), "Confirm", "Do you wish to add the following assignment?\n-\n" +
                            "Subject:  " + name + "\n" +
                            "Type:  " + type + "\n" +
                            "Submittion:  "+deadline+ "\n" +
                            "Through:  "+mean)) {
                        activeReside.add(new TaskSelf.AssignmentSelf(name, deadline, question, assignmentCreator.isGroup(), mean));
                        ComponentAssistant.ready(activeReside);
                        assignmentCreator.dispose();
                    }
                }
            };
        }

        public static void transferAssignment(TaskSelf.AssignmentSelf assignmentSelf, TaskExhibition.AssignmentExhibition assignmentExhibition, boolean isTime) {
            if (isTime) {
                assignmentSelf.setSubmissionDate(assignmentSelf.getDeadLine());
                completeTransfer(assignmentSelf);
            } else {
                if (App.showYesNoCancelDialog(assignmentExhibition.getRootPane(),"Confirm", "Are you sure you have submitted this assignment?")) {
                    assignmentSelf.setSubmissionDate(MDate.formatDateOnly(new Date()));
                    completeTransfer(assignmentSelf);
                    assignmentExhibition.dispose();
                }
            }
        }
        
        private static void completeTransfer(TaskSelf.AssignmentSelf aSelf){
            aSelf.getDeadlineIndicator().setText("Submitted: "+aSelf.getSubmissionDate());
            aSelf.getDeadlineIndicator().setFont(KFontFactory.createPlainFont(16));
            aSelf.getDeadlineIndicator().setForeground(Color.BLUE);
            aSelf.getDeadlineIndicator().setCursor(null);
            for (MouseListener l : aSelf.getDeadlineIndicator().getMouseListeners()) {
                aSelf.getDeadlineIndicator().removeMouseListener(l);
            }
            aSelf.setOn(false);
            activeReside.remove(aSelf);
            doneReside.add(aSelf);
            ComponentAssistant.ready(activeReside,doneReside);
        }
        
        public static ActionListener removalListener(TaskSelf.AssignmentSelf assignmentSelf, TaskExhibition.AssignmentExhibition eDialog){
            return e -> {
                if (App.showYesNoCancelDialog(eDialog.getRootPane(), "Confirm","Are you sure you want to remove this assignment?")) {
                    if (assignmentSelf.isOn()) {
                        activeReside.remove(assignmentSelf);
                        ComponentAssistant.ready(activeReside);
                    } else {
                        doneReside.remove(assignmentSelf);
                        ComponentAssistant.ready(doneReside);
                    }
                    assignmentSelf.setOn(false);
                    eDialog.dispose();
                }
            };
        }
        
        private static int getTotalAssignments(){
            return doingCount + doneCount;
        }

        private static int getDoingCount(){
            return doingCount;
        }

        private static void renewCount(int effect){
            doingCount += effect;
            assignmentBigButton.setText(getDoingCount());
        }

        public static void receiveFromSerials(TaskSelf.AssignmentSelf aSelf){
            if (aSelf.isOn()) {
                activeReside.add(aSelf);
            } else {
                doneReside.add(aSelf);
            }
        }

        private JComponent assignmentsComponent(){
        	final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, setUpActiveAssignments(), setUpDoneAssignments());
            splitPane.setContinuousLayout(true);
            splitPane.setDividerLocation(175);

            final KPanel assignmentComponent = new KPanel();
            assignmentComponent.setLayout(new BoxLayout(assignmentComponent, BoxLayout.Y_AXIS));
            assignmentComponent.add(splitPane);
            return assignmentComponent;
        }

        private JComponent setUpActiveAssignments() {
        	final KButton createButton = new KButton("New Assignment");
            createButton.setFont(TASK_BUTTONS_FONT);
            createButton.setMnemonic(KeyEvent.VK_A);
            createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            createButton.setToolTipText("Add an Assignment (Alt+A)");
            createButton.addActionListener(e -> {
                assignmentCreator = new TaskCreator.AssignmentCreator();
                assignmentCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(createButton,BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("Assignments", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel upperReside = new KPanel(new BorderLayout());
            upperReside.add(labelPanelPlus, BorderLayout.NORTH);
            final KScrollPane scrollPane = new KScrollPane(activeReside);
            scrollPane.setBorder(null);
            upperReside.add(scrollPane, BorderLayout.CENTER);
            return upperReside;
        }

        private JComponent setUpDoneAssignments() {
        	final KButton clearButton = new KButton("Remove All");
            clearButton.setFont(TASK_BUTTONS_FONT);
            clearButton.setToolTipText("Clear Submitted Assignments (Alt+R)");
            clearButton.setMnemonic(KeyEvent.VK_R);
            clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearButton.addActionListener(e -> {
                if (doneReside.getComponentCount() > 0) {
                    if (App.showYesNoCancelDialog("Confirm", "This action will remove all the assignments you're done with. Continue?")) {
                        for (Component c : doneReside.getComponents()) {
                            doneReside.remove(c);
                        }
                        ComponentAssistant.ready(doneReside);
                    }
                }
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(clearButton, BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("Completed Assignments", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel lowerReside = new KPanel(new BorderLayout());
            lowerReside.add(labelPanelPlus, BorderLayout.NORTH);
            lowerReside.add(new KScrollPane(doneReside, false), BorderLayout.CENTER);
            return lowerReside;
        }
    }


    public static class EventsHandler {
        public static final ArrayList<TaskSelf.EventSelf> EVENTS = new ArrayList<>();
        private static int upcomingCount;
        private static KPanel eventsReside;
        private static TaskCreator.EventCreator testCreator, examCreator, othersCreator;

        public EventsHandler(){
            eventsReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width, eventsReside.getPreferredSize().height+35));
                    EVENTS.add((TaskSelf.EventSelf) comp);
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width, eventsReside.getPreferredSize().height-35));
                    EVENTS.remove(comp);
                }
            };
            eventsReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener newListener(){
            return e -> {
                final TaskCreator.EventCreator requiredCreator = getShowingCreator();
                if (requiredCreator == null) {
                    return;
                }
                String tName = requiredCreator.getDescriptionField().getText();
                if (Globals.isBlank(tName)) {
                    App.signalError(requiredCreator.getRootPane(), "No Name", "Please specify a name for the event.");
                    requiredCreator.getDescriptionField().requestFocusInWindow();
                } else if (tName.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.signalError(requiredCreator.getRootPane(), "Error", "Sorry, the event's name should be at most "+TaskCreator.TASKS_DESCRIPTION_LIMIT+" characters.");
                    requiredCreator.getDescriptionField().requestFocusInWindow();
                } else if (Globals.isBlank(requiredCreator.getProvidedDate())) {
                    App.signalError(requiredCreator.getRootPane(), "Error", "Please provide all the fields for the date of the "+(requiredCreator.type()));
                } else {
                    final Date date = MDate.parse(requiredCreator.getProvidedDate() + " 0:0:0");
                    if (date == null) {
                        return;
                    }
                    if (date.before(new Date())) {
                        App.signalError(requiredCreator.getRootPane(),"Invalid Date", "Please consider the date - it cannot be at most today.");
                        return;
                    }
                    if (requiredCreator.getTitle().contains("Test")) {
                        tName = tName + " Test";
                    } else if (requiredCreator.getTitle().contains("Exam")) {
                        tName = tName + " Examination";
                    }
                    final String dateString = MDate.formatDateOnly(date);
                    if (App.showYesNoCancelDialog(requiredCreator.getRootPane(),"Confirm", "Do you wish to add the following event?\n-\n" +
                            "Title:  "+tName+"\n" +
                            "Date:  "+dateString)) {
                        eventsReside.add(new TaskSelf.EventSelf(tName, dateString));
                        ComponentAssistant.ready(eventsReside);
                        requiredCreator.dispose();
                        renewCount(1);
                    }
                }
            };
        }

        private static TaskCreator.EventCreator getShowingCreator(){
            if (testCreator != null && testCreator.isShowing()) {
                return testCreator;
            } else if (examCreator != null && examCreator.isShowing()) {
                return examCreator;
            } else if (othersCreator != null && othersCreator.isShowing()) {
                return othersCreator;
            } else {
                return null;
            }
        }

        public static void deleteEvent(TaskSelf.EventSelf event){
            eventsReside.remove(event);
            ComponentAssistant.ready(eventsReside);
            if (event.isPending()) {
                TasksGenerator.EventsHandler.renewCount(-1);
            }
        }

        public static void renewCount(int value){
            upcomingCount += value;
            eventBigButton.setText(upcomingCount);
        }

        public static void receiveFromSerials(TaskSelf.EventSelf eventSelf) {
            eventsReside.add(eventSelf);
            if (eventSelf.isPending()) {
                renewCount(1);
            }
        }

        private JComponent eventsComponent(){
            final KMenuItem testItem = new KMenuItem("Upcoming Test", e-> {
                testCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.TEST);
                testCreator.setVisible(true);
            });

            final KMenuItem examItem = new KMenuItem("Upcoming Exam", e->{
                examCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.EXAM);
                examCreator.setVisible(true);
            });

            final KMenuItem otherItem = new KMenuItem("Other", e->{
                othersCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.OTHER);
                othersCreator.setVisible(true);
            });

            final JPopupMenu jPopup = new JPopupMenu();
            jPopup.add(testItem);
            jPopup.add(examItem);
            jPopup.add(otherItem);

            final KButton popUpButton = new KButton("New Event");
            popUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            popUpButton.setFont(TASK_BUTTONS_FONT);
            popUpButton.addActionListener(e -> jPopup.show(popUpButton, popUpButton.getX(), popUpButton.getY() + (popUpButton.getPreferredSize().height)));

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(popUpButton, BorderLayout.WEST);
            labelPanelPlus.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.CENTER), null,
                    new KLabel("Events", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel eventsComponent = new KPanel(new BorderLayout());
            eventsComponent.add(labelPanelPlus, BorderLayout.NORTH);
            final KScrollPane aScroll = new KScrollPane(eventsReside);
            aScroll.setBorder(null);
            eventsComponent.add(aScroll, BorderLayout.CENTER);
            return eventsComponent;
        }
    }

}
