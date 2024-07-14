package clientcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clientservice.ChefService;
import clientservice.ConsoleInputValidator;
import clientservice.RoleHandler;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class ChefController implements RoleHandler {

    private static final int SHOW_MENU = 1;
    private static final int CREATE_RECOMMENDATION = 2;
    private static final int CREATE_ROLLOUT_MENU = 3;
    private static final int SELECT_MENU = 4;
    private static final int DISCARD_MENU_ITEM = 5;
    private static final int GENERATE_REPORT = 6;
    private static final int EXIT = 7;

    private final List<String> operations;
    private final String employeeId;
    private final DiscardMenuItemController discardMenuItemController;
    private final ServerResponseReader jsonResponse;
    private final ServerRequestSender jsonRequest;
    private final ConsoleInputValidator inputValidations;
    private final ChefService menuOperations;

    public ChefController(DiscardMenuItemController discardMenuItemController, ConsoleInputValidator inputValidations, ServerRequestSender jsonRequest, ServerResponseReader jsonResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.discardMenuItemController = discardMenuItemController;
        this.jsonResponse = jsonResponse;
        this.jsonRequest = jsonRequest;
        this.inputValidations = inputValidations;
        this.menuOperations = new ChefService(inputValidations, jsonRequest, jsonResponse);
    }

    @Override
    public void handleRoleOperations() {
        try {
            System.out.println("Connected to the server");

            while (true) {
                displayMenu();
                int command = inputValidations.getValidatedIntInput();

                if (command == EXIT) {
                    jsonRequest.logUserOperations(employeeId, operations);
                    jsonResponse.printResponse();
                    break;
                }

                switch (command) {
                    case SHOW_MENU -> {
                        operations.add("showMenu");
                        menuOperations.showMenu();
                    }
                    case CREATE_RECOMMENDATION -> {
                        operations.add("viewRecommendation");
                        menuOperations.createRecommendation();
                    }
                    case CREATE_ROLLOUT_MENU -> {
                        operations.add("rolloutMenu");
                        menuOperations.createRolloutMenu();
                    }
                    case SELECT_MENU -> {
                        operations.add("selectMenu");
                        menuOperations.selectMenu();
                    }
                    case DISCARD_MENU_ITEM -> {
                        operations.add("discardMenuItem");
                        discardMenuItemController.discardMenuItems();
                    }
                    case GENERATE_REPORT -> {
                        operations.add("generateReport");
                        menuOperations.generateReport();
                    }
                    default ->
                        System.out.println("Invalid command");
                }
            }
        } catch (IOException e) {
            System.out.println("Error in chef client: " + e.getMessage());
        }
    }

    private void displayMenu() {
        System.out.println("Enter command:\n1. Show Menu\n2. Create Recommendation\n3. Create Rollout Menu\n4. Select Menu\n5. Discard Menu Item\n6. Generate Report\n7. Exit");
    }
}
