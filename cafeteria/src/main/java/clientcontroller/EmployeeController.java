package clientcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import clientservice.ConsoleInputValidator;
import clientservice.EmployeeService;
import clientservice.RoleHandler;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class EmployeeController implements RoleHandler {

    private static final int SELECT_MENU_ITEM = 1;
    private static final int GIVE_FEEDBACK = 2;
    private static final int VIEW_NOTIFICATION = 3;
    private static final int VIEW_DISCARD_MENU_ITEM = 4;
    private static final int UPDATE_PROFILE = 5;
    private static final int EXIT = 6;

    private final String employeeId;
    private final List<String> operations;
    private final EmployeeService employeeOperations;
    private final ConsoleInputValidator inputValidations;
    private final ServerResponseReader serverResponse;
    private final ServerRequestSender serverRequest;

    public EmployeeController(ConsoleInputValidator inputValidations, ServerRequestSender serverRequest, ServerResponseReader serverResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.employeeOperations = new EmployeeService(inputValidations, serverRequest, serverResponse, employeeId);
        this.serverResponse = serverResponse;
        this.serverRequest = serverRequest;
        this.inputValidations = inputValidations;
    }

    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                displayMenu();
                int command = inputValidations.getValidatedIntInput();

                if (command == EXIT) {
                    serverRequest.logUserOperations(employeeId, operations);
                    serverResponse.printResponse();
                    break;
                }

                switch (command) {
                    case SELECT_MENU_ITEM -> {
                        operations.add("Select Menu Item");
                        employeeOperations.handleSelectMenuItem();
                    }
                    case GIVE_FEEDBACK -> {
                        operations.add("Give Feedback");
                        employeeOperations.handleGiveFeedback();
                    }
                    case VIEW_NOTIFICATION -> {
                        operations.add("View Notification");
                        employeeOperations.viewNotification();
                    }
                    case VIEW_DISCARD_MENU_ITEM -> {
                        operations.add("View Discard Menu");
                        employeeOperations.showDiscardMenuItems();
                    }
                    case UPDATE_PROFILE -> {
                        operations.add("Update Profile");
                        employeeOperations.updateUserProfile();
                    }
                    default ->
                        System.out.println("Invalid command");
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error in employee client: " + e.getMessage());
        }
    }

    private void displayMenu() {
        System.out.println("Enter command\n1. Select Menu Item\n2. Give Feedback\n3. View Notification\n4. View Discard Menu Item\n5. Update Profile\n6. Exit ");
    }
}
