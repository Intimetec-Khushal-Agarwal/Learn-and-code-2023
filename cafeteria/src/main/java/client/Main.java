package client;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) {
        ClientConnectionManager connectionManager = new ClientConnectionManager();
        try {
            connectionManager.initializeConnection();
            ClientCommandHandler commandHandler = new ClientCommandHandler(connectionManager);
            commandHandler.handleUserCommands();
        } catch (IOException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            connectionManager.closeConnection();
        }
    }
}
