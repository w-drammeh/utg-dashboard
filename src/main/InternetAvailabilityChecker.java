package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A class which is lucky its functionality is not integrated in other types.
 * It is purposely meant for checking the availability of the internet.
 * This algorithm is "rough"!
 */
public class InternetAvailabilityChecker {


    /**
     * Calling this on the main thread can put Dashboard in a serious waiting state!
     */
    public static boolean isInternetAvailable(){
        try {
            return isHostAvailable("google.com") || isHostAvailable("utg.gm") ||
                    isHostAvailable("facebook.com");
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isHostAvailable(String hostName) throws IOException {
        try {
            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(hostName, 80), 10_000);
            return true;
        } catch (UnknownHostException unknownHost) {
            return false;
        }
    }

}
