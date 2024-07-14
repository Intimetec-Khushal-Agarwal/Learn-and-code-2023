package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.DatabaseEventScheduler;
import database.DatabaseInitializer;
import serverController.ClientHandler;

public class Server {

    private static final int PORT = 2709;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        DatabaseInitializer.createTables();
        DatabaseEventScheduler.enableEventScheduler();
        DatabaseEventScheduler.createScheduledEvent();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            pool.execute(new ClientHandler(clientSocket));
        }
    }
}
