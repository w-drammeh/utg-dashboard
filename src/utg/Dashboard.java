package utg;

import main.*;

import javax.swing.*;
import java.io.File;

/**
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

        final Object recentUser = MyClass.deserialize("userName.ser");
        if (recentUser == null) {
            final File coreFile = new File(MyClass.serialsDir + MyClass.fileSeparator + "core.ser");
            if (coreFile.exists()) {
                verifyUser(true);
            } else {
                forgeNew();
            }
        } else {
            final String immediateUser = System.getProperty("user.name");
            if (immediateUser.equals(recentUser)) {
                rebuildNow(true);
            } else {
                verifyUser(true);
            }
        }
    }

    private static void forgeNew(){
        isFirst = true;
        new Thread(()-> {
            Student.reset();
            PrePortal.startFixingDriver();
        }).start();
        final Welcome welcome = new Welcome();
        SwingUtilities.invokeLater(() -> {
            preview.dispose();
            welcome.setVisible(true);
        });
    }

    private static void verifyUser(boolean deserialize){
        if (deserialize) {
            try {
                Student.reset();
                Student.deserializeData();
            } catch (Exception e) {
                App.silenceException(e);
                forgeNew();
                return;
            }
        }

        preview.setVisible(false);
        final String matNumber = constantlyRequestInput();
        if (matNumber.equals(String.valueOf(Student.getMatNumber()))) {
            rebuildNow(false);
        } else {
            App.signalError(preview, "Error", "That Mat. Number does not match "+Student.getFullNamePostOrder()+".\n" +
                    "Try again.");
            verifyUser(false);
        }
    }

    private static String constantlyRequestInput(){
        final String input = App.requestInput(preview, "UTG Dashboard", "This Dashboard belongs to "+Student.getFullNamePostOrder()+".\n" +
                "Please enter your Matriculation Number:");
        if (input == null) {
            System.exit(0);
        }
        return Globals.hasText(input) ? input : constantlyRequestInput();
    }

    private static void rebuildNow(boolean deserialize){
        if (deserialize) {
            try {
                Student.reset();
                Student.deserializeData();
            } catch (Exception e) {
                App.silenceException(e);
                forgeNew();
                return;
            }
        } else {
            preview.setVisible(true);
        }

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
        SwingUtilities.invokeLater(()-> {
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
