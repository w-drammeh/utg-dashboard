package utg;

import main.*;

import javax.swing.*;
import java.io.File;

/**
 * @author Muhammed W. Drammeh
 * This is the actual runner type.
 * In a nutshell, it reads from a serializable state if existed, or triggers a new instance if not -
 * or otherwise found inconsistent.
 * This class defines the normal process-flow of the Dashboard. Please read the logic.txt file.
 */
public class Dashboard {
    private static final Preview PREVIEW = new Preview(null);
    public static final String VERSION = "1.5.5";
    private static boolean isFirst;


    public static void main(String[] args) {
        PREVIEW.setVisible(true);
        final Object recentUser = Serializer.fromDisk("user-name.ser");
        if (recentUser == null) {
            final File coreFile = new File(Serializer.SERIALS_DIR + Serializer.FILE_SEPARATOR + "core.ser");
            if (coreFile.exists()) {
                verifyUser(true);
            } else {
                freshStart();
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

    private static void freshStart(){
        isFirst = true;
        new Thread(PrePortal::startFixingDriver).start();
        final Welcome welcome = new Welcome();
        SwingUtilities.invokeLater(()-> {
            PREVIEW.dispose();
            welcome.setVisible(true);
            welcome.getScrollPane().toTop();
        });
    }

    private static void verifyUser(boolean deserialize){
        if (deserialize) {
            try {
                Student.deserializeData();
            } catch (NullPointerException e) {
                App.silenceException(e);
                freshStart();
                return;
            }
        }

        PREVIEW.setVisible(false);
        final String matNumber = requestInput();
        if (matNumber.equals(Student.getMatNumber())) {
            PREVIEW.setVisible(true);
            rebuildNow(false);
        } else {
            App.signalError(PREVIEW, "Error", "Wrong Matriculation Number. Please try again.");
            verifyUser(false);
        }
    }

//    constantly requests the input, actually
    private static String requestInput(){
        final String userName = Student.getFullNamePostOrder();
        final String input = App.requestInput(PREVIEW, "UTG Dashboard", "This Dashboard belongs to "+userName+".\n" +
                "Please enter your Matriculation Number to confirm:");
        if (input == null) {
            System.exit(0);
        }
        return Globals.hasText(input) ? input : requestInput();
    }

    private static void rebuildNow(boolean deserialize){
        if (deserialize) {
            try {
                Student.deserializeData();
            } catch (NullPointerException e) {
                App.silenceException(e);
                freshStart();
                return;
            }
        }

        Settings.deSerialize();
        Portal.deSerialize();
        News.deSerializeData();
        SwingUtilities.invokeLater(()-> {
            final Board lastBoard = new Board();
            RunningCoursesGenerator.deserializeModules();
            ModulesHandler.deserializeData();
            TaskSelf.deSerializeAll();
            Notification.deSerializeAll();
            PREVIEW.dispose();
            lastBoard.setVisible(true);
        });
    }

    public static boolean isFirst() {
        return isFirst;
    }

}
