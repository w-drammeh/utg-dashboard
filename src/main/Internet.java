package main;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

public class Internet {


    public static void visit(String site) throws Exception {
        Desktop.getDesktop().browse(URI.create(site));
    }

    /**
     * Calling this on the main thread can put Dashboard in a serious waiting state!
     */
    public static boolean isInternetAvailable(){
        try {
            final boolean available = isHostAvailable("google.com") || isHostAvailable("github.com") ||
                    isHostAvailable("facebook.com");
            if (available && Board.isReady()) {
                new Thread(Board::online).start();//as it should not delay the return
            }
            return available;
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
