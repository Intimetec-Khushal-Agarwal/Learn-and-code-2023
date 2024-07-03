package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AdminClientController implements RoleHandler {

    private static final int SHOW_MENU = 1;
    private static final int ADD_MENU_ITEM = 2;
    private static final int UPDATE_MENU_ITEM = 3;
    private static final int DELETE_MENU_ITEM = 4;
    private static final int DISCARD_MENU_ITEM = 5;
    private static final int EXIT = 6;

    private final List<String> operations;
    private final String employeeId;
    private final DiscardMenuItemController discardMenuItemController;
    private final UpdateUserProfile updateProfile;
    private final ServerRequestResponse jsonRequestResponse;
    private final ConsoleInputValidations inputValidations;
    private final JSONObject menuItem;

    public AdminClientController(DiscardMenuItemController discardMenuItemController, ConsoleInputValidations inputValidations, ServerRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.discardMenuItemController = discardMenuItemController;
        this.updateProfile = new UpdateUserProfile(inputValidations, jsonRequestResponse, employeeId);
        this.jsonRequestResponse = jsonRequestResponse;
        this.inputValidations = inputValidations;
        this.menuItem = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                System.out.println("Enter operation to perform:\n1. Show Menu\n2. Add Menu Item\n3. Update Menu Item\n4. Delete Menu Item\n5. Discard Menu Item\n6. Exit ");
                int command = inputValidations.getValidatedIntInput();

                if (command == EXIT) {
                    jsonRequestResponse.userLogs(employeeId, operations);
                    jsonRequestResponse.readResponse();
                    break;
                }

                switch (command) {
                    case SHOW_MENU -> {
                        operations.add("showMenu");
                        menuItem.put("requestType", "showMenu");
                        displayMenu(menuItem);
                    }
                    case ADD_MENU_ITEM -> {
                        operations.add("addMenu");
                        addMenuItem(menuItem);
                    }
                    case UPDATE_MENU_ITEM -> {
                        operations.add("updateMenu");
                        updateMenuItem(menuItem);
                    }
                    case DELETE_MENU_ITEM -> {
                        operations.add("deleteMenu");
                        deleteMenuItem(menuItem);
                    }
                    case DISCARD_MENU_ITEM -> {
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

    @SuppressWarnings("unchecked")
    private void addMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item name: ");
        String itemName = inputValidations.getValidatedStringInput();
        System.out.println("Enter item price: ");
        float itemPrice = inputValidations.getValidatedFloatInput();
        System.out.println("Enter item Rating (1-5): ");
        int rating = inputValidations.getValidatedOption(5);
        System.out.println("Enter item availability status (yes/no): ");
        String itemStatus = inputValidations.getValidatedBooleanInput();
        System.out.println("Enter meal type (1. Breakfast, 2. Lunch, 3. Dinner): ");
        int mealType = inputValidations.getValidatedOption(3);
        int dietaryPreference = updateProfile.getDietaryPreference();
        int spiceLevel = updateProfile.getSpiceLevel();
        int foodPreference = updateProfile.getFoodPreference();
        int sweetTooth = updateProfile.getSweetTooth();

        menuItem.put("requestType", "addMenuItem");
        menuItem.put("name", itemName);
        menuItem.put("price", itemPrice);
        menuItem.put("status", itemStatus);
        menuItem.put("rating", rating);
        menuItem.put("mealType", mealType);
        menuItem.put("foodType", dietaryPreference);
        menuItem.put("foodTaste", spiceLevel);
        menuItem.put("foodPreference", foodPreference);
        menuItem.put("sweetTooth", sweetTooth);

        displayMenu(menuItem);
    }

    @SuppressWarnings("unchecked")
    private void updateMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item id: ");
        int itemId = inputValidations.getValidatedIntInput();
        System.out.println("Enter item price: ");
        float itemPrice = inputValidations.getValidatedFloatInput();
        System.out.println("Enter item availability status (yes/no): ");
        String itemStatus = inputValidations.getValidatedBooleanInput();

        menuItem.put("requestType", "updateMenuItem");
        menuItem.put("id", itemId);
        menuItem.put("price", itemPrice);
        menuItem.put("status", itemStatus);

        displayMenu(menuItem);
    }

    @SuppressWarnings("unchecked")
    private void deleteMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item ID to delete: ");
        int itemId = inputValidations.getValidatedIntInput();
        menuItem.put("requestType", "deleteMenuItem");
        menuItem.put("id", itemId);

        displayMenu(menuItem);
    }

    private void displayMenu(JSONObject menuItem) throws IOException {
        System.out.println("Inside display menu");
        jsonRequestResponse.sendRequest(menuItem);
        jsonRequestResponse.readResponse();
    }
}
