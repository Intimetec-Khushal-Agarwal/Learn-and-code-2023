package clientcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clientconstant.DisplayMenuConstants;
import clientservice.AdminMenuService;
import clientservice.ConsoleInputValidator;
import clientservice.RoleHandler;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class AdminController implements RoleHandler {

    private final List<String> operations;
    private final String employeeId;
    private final DiscardMenuItemController discardMenuItemController;
    private final ServerResponseReader jsonResponse;
    private final ServerRequestSender jsonRequest;
    private final ConsoleInputValidator inputValidations;
    private final AdminMenuService menuItemOperations;

    public AdminController(DiscardMenuItemController discardMenuItemController, ConsoleInputValidator inputValidations, ServerRequestSender jsonRequest, ServerResponseReader jsonResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.discardMenuItemController = discardMenuItemController;
        this.jsonResponse = jsonResponse;
        this.jsonRequest = jsonRequest;
        this.inputValidations = inputValidations;
        this.menuItemOperations = new AdminMenuService(inputValidations, jsonRequest, jsonResponse, employeeId);
    }

    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                System.out.println("Enter operation to perform:\n1. Show Menu\n2. Add Menu Item\n3. Update Menu Item\n4. Delete Menu Item\n5. Discard Menu Item\n6. Exit ");
                int command = inputValidations.getValidatedIntInput();

                if (command == DisplayMenuConstants.EXIT) {
                    jsonRequest.logUserOperations(employeeId, operations);
                    jsonResponse.printResponse();
                    break;
                }

                switch (command) {
                    case DisplayMenuConstants.SHOW_MENU -> {
                        operations.add("showMenu");
                        menuItemOperations.showMenu();
                    }
                    case DisplayMenuConstants.ADD_MENU_ITEM -> {
                        operations.add("addMenu");
                        menuItemOperations.addMenuItem();
                    }
                    case DisplayMenuConstants.UPDATE_MENU_ITEM -> {
                        operations.add("updateMenu");
                        menuItemOperations.updateMenuItem();
                    }
                    case DisplayMenuConstants.DELETE_MENU_ITEM -> {
                        operations.add("deleteMenu");
                        menuItemOperations.deleteMenuItem();
                    }
                    case DisplayMenuConstants.DISCARD_MENU_ITEM -> {
                        operations.add("discardMenuItem");
                        discardMenuItemController.discardMenuItems();
                    }
                    default ->
                        System.out.println("Invalid command");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling role operations: " + e.getMessage());
        }
    }
}
