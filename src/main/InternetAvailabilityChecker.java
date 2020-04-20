package main;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <h1>class InternetAvailabilityChecker</h1>
 * <p><i>A class which is lucky that its functionality is not integrated to other types.</i></p>
 */
public class InternetAvailabilityChecker implements Serializable {


    /**
     * <p>Calling this on the main thread may cause Dashboard to be waiting!</p>
     */
    public static boolean isInternetAvailable(){
        try {
            return isHostAvailable("utg.gm") || isHostAvailable("google.com") || isHostAvailable("facebook.com");
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
