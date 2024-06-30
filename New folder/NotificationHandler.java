package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

class NotificationHandler implements Runnable {

    private Socket clientSocket = null;

    public NotificationHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            while (true) {
                out.println("Notification: " + new Date());
                Thread.sleep(5000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
