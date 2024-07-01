package finalproject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AdminClientController implements RoleHandler {

    private final List<String> operations;
    private final String employeeId;
    LocalDateTime loginTime;
    private final DiscardMenuItemController discardMenuItemController;
    private final UpdateProfile updateProfile;
    private final JsonRequestResponse jsonRequestResponse;
    private final InputValidations inputValidations;
    private final JSONObject menuItem;

    public AdminClientController(DiscardMenuItemController discardMenuItemController, InputValidations inputValidations, JsonRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.loginTime = LocalDateTime.now();
        this.discardMenuItemController = discardMenuItemController;
        this.updateProfile = new UpdateProfile(inputValidations, jsonRequestResponse, employeeId);
        this.jsonRequestResponse = jsonRequestResponse;
        this.inputValidations = inputValidations;
        this.menuItem = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                System.out.println("Enter operation to perform:\n1.showMenu\n2.addMenuItem\n3.updateMenuItem\n4.deleteMenuItem\n5.discardMenuItem\n6.exit ");
                int command = inputValidations.getValidatedIntInput();
                if (command == 6) {

                    menuItem.put("requestType", "userLogs");
                    menuItem.put("userId", this.employeeId);
                    menuItem.put("operations", operations);
                    menuItem.put("loginTime", loginTime.toString());
                    jsonRequestResponse.sendRequest(menuItem);
                    System.out.println("Admin logged out successfully");
                    break;
                }

                switch (command) {
                    case 1 -> {
                        operations.add("showMenu");
                        menuItem.put("requestType", "showMenu");
                        displayMenu(menuItem);
                    }
                    case 2 -> {
                        operations.add("addMenu");
                        addMenuItem(menuItem);
                    }
                    case 3 -> {
                        operations.add("updateMenu");
                        updateMenuItem(menuItem);
                    }
                    case 4 -> {
                        operations.add("deleteMenu");
                        deleteMenuItem(menuItem);
                    }
                    case 5 -> {
                        operations.add("discardMenuItem");
                        discardMenuItemController.discardMenuItems();
                    }
                    default -> {
                        System.out.println("Invalid command");
                        continue;
                    }
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
        System.out.println("Enter item Rating(1-5): ");
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
        System.out.println("Enter item availability status(yes/no): ");
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
        System.out.println("inside display menu");
        jsonRequestResponse.sendRequest(menuItem);
        jsonRequestResponse.readResponse();
    }
}
