package finalproject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChefClient implements RoleHandler {

    private final List<String> operations;
    private final String employeeId;
    private final DiscardMenuItemController discardMenuItemController;
    LocalDateTime loginTime;
    private final JsonRequestResponse jsonRequestResponse;
    private final InputValidations inputValidations;

    public ChefClient(DiscardMenuItemController discardMenuItemController, InputValidations inputValidations, JsonRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.loginTime = LocalDateTime.now();
        this.discardMenuItemController = discardMenuItemController;
        this.jsonRequestResponse = jsonRequestResponse;
        this.inputValidations = inputValidations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            System.out.println("Connected to the server");

            while (true) {
                displayMenu();
                int command = inputValidations.getValidatedIntInput();

                if (command == 5) {
                    JSONObject userActivity = new JSONObject();
                    userActivity.put("requestType", "userLogs");
                    userActivity.put("userId", this.employeeId);
                    userActivity.put("operations", operations);
                    userActivity.put("loginTime", loginTime.toString());
                    jsonRequestResponse.sendRequest(userActivity);
                    System.out.println("Chef logged out successfully");
                    break;
                }

                switch (command) {
                    case 1 -> {
                        operations.add("viewRecommendation");
                        handleCreateRecommendation();
                    }
                    case 2 -> {
                        operations.add("rolloutMenu");
                        handleCreateRolloutMenu();
                    }
                    case 3 -> {
                        operations.add("selectMenu");
                        handleSelectMenu();
                    }
                    case 4 -> {
                        operations.add("discardMenuItem");
                        discardMenuItemController.discardMenuItems();
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
        System.out.println("Enter command:\n1. Create Recommendation\n2. Create Rollout Menu\n3. Select Menu\n4. Discard Menu Item\n5. Exit");
    }

    @SuppressWarnings("unchecked")
    private void handleCreateRecommendation() throws IOException {
        JSONObject chefDetails = new JSONObject();
        chefDetails.put("requestType", "viewRecommendations");
        jsonRequestResponse.sendRequest(chefDetails);
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private void handleCreateRolloutMenu() throws IOException {
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
                jsonRequestResponse.sendRequest(itemData);

            }
        }
        System.out.println("Menu Item added successfully");
    }

    @SuppressWarnings("unchecked")
    private void handleSelectMenu() throws IOException {
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        JSONArray mealSelections = new JSONArray();

        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            requestMenuFromServer(i);

            String response = jsonRequestResponse.readResponseUntilEnd();
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
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private void requestMenuFromServer(int mealTypeIndex) {
        JSONObject menuRequest = new JSONObject();
        menuRequest.put("requestType", "showRolloutMenuByVote");
        menuRequest.put("mealType", mealTypeIndex + 1);
        jsonRequestResponse.sendRequest(menuRequest);
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
        jsonRequestResponse.sendRequest(selectedItemsData);
    }
}
