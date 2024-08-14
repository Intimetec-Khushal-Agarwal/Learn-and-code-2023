package clientservice;

import java.io.IOException;

import org.json.simple.JSONObject;

import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class AdminMenuService {

    private final ConsoleInputValidator inputValidations;
    private final ServerResponseReader jsonResponse;
    private final ServerRequestSender jsonRequest;
    private final UpdateUserProfile updateProfile;
    private final JSONObject menuItem;

    public AdminMenuService(ConsoleInputValidator inputValidations, ServerRequestSender jsonRequest, ServerResponseReader jsonResponse, String employeeId) {
        this.inputValidations = inputValidations;
        this.jsonResponse = jsonResponse;
        this.jsonRequest = jsonRequest;
        this.updateProfile = new UpdateUserProfile(inputValidations, jsonRequest, jsonResponse, employeeId);
        this.menuItem = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    public void showMenu() throws IOException {
        menuItem.put("requestType", "showMenu");
        displayMenu(menuItem);
    }

    @SuppressWarnings("unchecked")
    public void addMenuItem() throws IOException {
        System.out.println("Enter item name: ");
        String itemName = inputValidations.getValidatedStringInput();
        System.out.println("Enter item price: ");
        float itemPrice = inputValidations.getValidatedFloatInput();
        System.out.println("Enter item Rating (1-5): ");
        int rating = inputValidations.getValidatedOption(5);
        System.out.println("Enter meal type (1. Breakfast, 2. Lunch, 3. Dinner): ");
        int mealType = inputValidations.getValidatedOption(3);
        int dietaryPreference = updateProfile.getDietaryPreference();
        int spiceLevel = updateProfile.getSpiceLevel();
        int foodPreference = updateProfile.getFoodPreference();
        int sweetTooth = updateProfile.getSweetTooth();

        menuItem.put("requestType", "addMenuItem");
        menuItem.put("name", itemName);
        menuItem.put("price", itemPrice);
        menuItem.put("rating", rating);
        menuItem.put("mealType", mealType);
        menuItem.put("foodType", dietaryPreference);
        menuItem.put("foodTaste", spiceLevel);
        menuItem.put("foodPreference", foodPreference);
        menuItem.put("sweetTooth", sweetTooth);

        displayMenu(menuItem);
    }

    @SuppressWarnings("unchecked")
    public void updateMenuItem() throws IOException {
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
    public void deleteMenuItem() throws IOException {
        System.out.println("Enter item ID to delete: ");
        int itemId = inputValidations.getValidatedIntInput();
        menuItem.put("requestType", "deleteMenuItem");
        menuItem.put("id", itemId);

        displayMenu(menuItem);
    }

    private void displayMenu(JSONObject menuItem) throws IOException {
        jsonRequest.sendRequest(menuItem);
        jsonResponse.printResponse();
    }
}
