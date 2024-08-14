package clientcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clientconstant.DisplayMenuConstants;
import clientservice.ChefService;
import clientservice.ConsoleInputValidator;
import clientservice.RoleHandler;
import error.ErrorHandler;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class ChefController implements RoleHandler {

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
            while (true) {
                displayMenu();
                int command = inputValidations.getValidatedIntInput();

                if (command == DisplayMenuConstants.EXIT) {
                    jsonRequest.logUserOperations(employeeId, operations);
                    jsonResponse.printResponse();
                    break;
                }

                switch (command) {
                    case DisplayMenuConstants.SHOW_MENU -> {
                        operations.add("showMenu");
                        menuOperations.showMenu();
                    }
                    case DisplayMenuConstants.CREATE_RECOMMENDATION -> {
                        operations.add("viewRecommendation");
                        menuOperations.createRecommendation();
                    }
                    case DisplayMenuConstants.CREATE_ROLLOUT_MENU -> {
                        operations.add("rolloutMenu");
                        menuOperations.createRolloutMenu();
                    }
                    case DisplayMenuConstants.SELECT_MENU -> {
                        operations.add("selectMenu");
                        menuOperations.selectMenu();
                    }
                    case DisplayMenuConstants.DISCARD_MENU_ITEM -> {
                        operations.add("discardMenuItem");
                        discardMenuItemController.discardMenuItems();
                    }
                    case DisplayMenuConstants.GENERATE_REPORT -> {
                        operations.add("generateReport");
                        menuOperations.generateReport();
                    }
                    default ->
                        System.out.println("Invalid command");
                }
            }
        } catch (IOException e) {
            ErrorHandler.handleIOException(e);
        }
    }

    private void displayMenu() {
        System.out.println("Enter command:\n1. Show Menu\n2. Create Recommendation\n3. Create Rollout Menu\n4. Select Menu\n5. Discard Menu Item\n6. Exit\n7. Genrate Report");
    }
}
