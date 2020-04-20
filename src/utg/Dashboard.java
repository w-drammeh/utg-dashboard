package utg;

import main.*;

import javax.swing.*;

/**
 * <h1>class Dashboard</h1>
 *
 * @author Muhammed W. Drammeh
 *
 * This is the actual runner type of the project.
 * In a nutshell, it may read from a serializable state, if existed, or triggers a new instance if not,
 * or otherwise found inconsistent.
 *
 * This class defines the normal process-flow of the Dashboard. Please read the logic.txt file.
 */
public class Dashboard {
    public static final String VERSION = "1.0.0 - LTS";
    public static boolean isTest;
    private static boolean isFirst;
    private static Preview preview;


    public static void main(String[] args) {
        preview  = new Preview(null);
        preview.setVisible(true);
        final Object object = MyClass.deserialize("userName.ser");
        if (object == null) {
            System.out.println("Triggering a new instance for a potential first launch... Please wait");
            forgeNew();
            return;
        }

        final String recentUser = object.toString();
        final String immediateUser = System.getProperty("user.name");
        if (immediateUser.equals(recentUser)) {
            try {
                System.out.println("Reading from your serializable state... Please wait");
                Student.resetFields();
                Student.deserializeData();
            } catch (Exception e) {
                System.out.println("Serializable file - core - not found, or otherwise could not be read properly; now triggering a new instance...");
                forgeNew();
                return;
            }
            launchRebuildSequences();
        } else {
            System.out.println("Different user detected; now triggering a new instance...");
            forgeNew();
        }
    }

    private static void forgeNew(){
        isFirst = true;
        new Thread(()-> {
            Student.resetFields();
            PrePortal.startFixingDriver();
        }).start();
        final Welcome welcome = new Welcome();
        SwingUtilities.invokeLater(() -> {
            preview.dispose();
            welcome.setVisible(true);
        });
    }

    private static void launchRebuildSequences(){
        try {
            SettingsCore.deSerialize();
        } catch (Exception e) {
            App.silenceException(e);
        }
        try {
            Portal.deSerialize();
        } catch (Exception e) {
            App.silenceException(e);
        }
        try {
            NewsGenerator.deSerializeData();
        } catch (Exception e) {
            App.silenceException(e);
        }

        SwingUtilities.invokeLater(()->{
            final Board lastBoard = new Board();
            try {
                SettingsUI.deSerialize();
            } catch (Exception e) {
                App.silenceException(e);
            }
            try {
                RunningCoursesGenerator.deserializeModules();
            } catch (Exception e) {
                App.silenceException(e);
            }
            try {
                ModulesHandler.deserializeData();
            } catch (Exception e) {
                App.silenceException(e);
            }
            try {
                TaskSelf.deSerializeAll();
            } catch (Exception e) {
                App.silenceException(e);
            }
            try {
                Notification.deSerializeAll();
            } catch (Exception e) {
                App.silenceException(e);
            }
            preview.dispose();
            lastBoard.setVisible(true);
        });
    }

    public static boolean isFirst() {
        return isFirst;
    }

}
