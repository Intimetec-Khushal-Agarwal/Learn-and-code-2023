package clientservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class ChefService {

    private final ConsoleInputValidator inputValidations;
    private final ServerResponseReader serverResponse;
    private final ServerRequestSender serverRequest;

    public ChefService(ConsoleInputValidator inputValidations, ServerRequestSender jsonRequest, ServerResponseReader jsonResponse) {
        this.inputValidations = inputValidations;
        this.serverResponse = jsonResponse;
        this.serverRequest = jsonRequest;
    }

    @SuppressWarnings("unchecked")
    public void showMenu() throws IOException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("requestType", "showMenu");
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    public void createRecommendation() throws IOException {
        JSONObject chefDetails = new JSONObject();
        chefDetails.put("requestType", "viewRecommendations");
        serverRequest.sendRequest(chefDetails);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    public void createRolloutMenu() throws IOException {
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            System.out.println("Enter number of items to enter for " + mealType + ": ");
            int numberOfItems = inputValidations.getValidatedIntInput();

            for (int j = 0; j < numberOfItems; j++) {
                JSONObject itemData = new JSONObject();
                System.out.println("Enter itemId to add: ");
                int menuId = inputValidations.getValidatedIntInput();

                itemData.put("menu_item_id", menuId);
                itemData.put("meal_type_id", i + 1);
                itemData.put("requestType", "insertRollOutMenuItem");
                serverRequest.sendRequest(itemData);
            }
        }
        System.out.println("Menu Item added successfully");
    }

    @SuppressWarnings("unchecked")
    public void selectMenu() throws IOException {
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        JSONArray mealSelections = new JSONArray();

        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            requestMenuFromServer(i);

            String response = serverResponse.readResponseUntilEnd();
            System.out.println("Menu Items for " + mealType + ":\n" + response);

            List<String> itemIds = getSelectedItems(mealType);

            if (!itemIds.isEmpty()) {
                JSONObject mealSelection = new JSONObject();
                mealSelection.put("mealType", mealType);
                mealSelection.put("menu_item_id", itemIds);
                mealSelections.add(mealSelection);
            }
        }

        storeSelectedItems(mealSelections);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    private void requestMenuFromServer(int mealTypeIndex) {
        JSONObject menuRequest = new JSONObject();
        menuRequest.put("requestType", "showRolloutMenuByVote");
        menuRequest.put("mealType", mealTypeIndex + 1);
        serverRequest.sendRequest(menuRequest);
    }

    private List<String> getSelectedItems(String mealType) throws IOException {
        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + " (comma separated):");
        String[] inputItems = inputValidations.getValidatedStringInput().split(",");
        for (String itemId : inputItems) {
            itemId = itemId.trim();
            if (!itemId.isEmpty()) {
                try {
                    Integer.valueOf(itemId);
                    itemIds.add(itemId);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid menu item ID: " + itemId + ". Skipping it.");
                }
            }
        }
        return itemIds;
    }

    @SuppressWarnings("unchecked")
    private void storeSelectedItems(JSONArray mealSelections) {
        JSONObject selectedItemsData = new JSONObject();
        selectedItemsData.put("selectedItems", mealSelections);
        selectedItemsData.put("requestType", "storeSelectedItemsInPreparedMenu");
        serverRequest.sendRequest(selectedItemsData);
    }

    @SuppressWarnings("unchecked")
    public void generateReport() throws IOException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("requestType", "generateReport");
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }
}
