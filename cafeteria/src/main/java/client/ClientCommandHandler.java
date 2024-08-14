package client;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import clientcontroller.DiscardMenuItemController;
import clientservice.LoginService;
import clientservice.UserLogService;

public class ClientCommandHandler {

    private final ClientConnectionManager connectionManager;
    private final DiscardMenuItemController discardMenuItemController;
    private final LoginService loginHandler;
    private final UserLogService userLogHandler;

    public ClientCommandHandler(ClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.discardMenuItemController = new DiscardMenuItemController(connectionManager.getInputValidations(), connectionManager.getServerRequest(), connectionManager.getServerResponse());
        this.loginHandler = new LoginService(connectionManager, discardMenuItemController);
        this.userLogHandler = new UserLogService(connectionManager);
    }

    public void handleUserCommands() throws IOException, ParseException {
        while (true) {
            System.out.println("Enter operation to perform\n1. login\n2. showUserLogs\n3. Exit");
            int command = connectionManager.getInputValidations().getValidatedIntInput();

            if (command == 3) {
                break;
            }

            switch (command) {
                case 1 ->
                    loginHandler.handleLogin();
                case 2 ->
                    userLogHandler.showUserLogs();
                default ->
                    System.out.println("Invalid command");
            }
        }
    }
}
