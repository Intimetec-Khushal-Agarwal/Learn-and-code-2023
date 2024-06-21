package finalproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 2709;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        Database.createTables();

        int count = 0;

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            count++;
            System.out.println("No of clients" + count);

            pool.execute(new ClientHandler(clientSocket));
        }
    }
}
