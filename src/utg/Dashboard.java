package utg;

import main.*;

import javax.swing.*;
import java.io.File;

/**
 * @author Muhammed W. Drammeh <wakadrammeh@gmail.com>
 *
 * This is the actual runner type.
 * In a nutshell, it reads from a serializable state if existed,
 * or triggers a new instance if not - or otherwise found inconsistent.
 * This class defines the normal process-flow of the Dashboard. Please read the logic.txt file.
 */
public class Dashboard {
    private static final Preview PREVIEW = new Preview(null);
    public static final String VERSION = "1.6.2";
    private static boolean isFirst;


    public static void main(String[] args) {
        PREVIEW.setVisible(true);
        final Object recentUser = Serializer.fromDisk("user-name.ser");
        if (recentUser == null) {
            final File coreFile = new File(Serializer.SERIALS_DIR + File.separator + "core.ser");
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
        PREVIEW.dispose();
        welcome.setVisible(true);
        welcome.getScrollPane().toTop();
    }

    private static void verifyUser(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
            } catch (NullPointerException e) {
                App.silenceException("Error reading user data.");
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
            final String userName = Student.getFullNamePostOrder();
            App.signalError(PREVIEW, "Error", "Incorrect Matriculation Number for "+userName+". Please try again.");
            verifyUser(false);
        }
    }

//    constantly requests the input, actually
    private static String requestInput(){
        final String userName = Student.getFullNamePostOrder();
        final String input = App.requestInput(PREVIEW, "UTG Student Dashboard", "This Dashboard belongs to "+userName+".\n" +
                "Please enter your Matriculation Number to confirm:");
        if (input == null) {
            System.exit(0);
        }
        return Globals.hasText(input) ? input : requestInput();
    }

    private static void rebuildNow(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
            } catch (NullPointerException e) {
                App.silenceException("Error reading user data.");
                freshStart();
                return;
            }
        }

        SwingUtilities.invokeLater(()-> {
            final Board lastBoard = new Board();
            PREVIEW.dispose();
            lastBoard.setVisible(true);
        });
    }

    public static boolean isFirst() {
        return isFirst;
    }

}
