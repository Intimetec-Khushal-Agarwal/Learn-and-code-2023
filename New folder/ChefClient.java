package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChefClient implements RoleHandler {

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;
    private final BufferedReader consoleReader;
    private final List<String> operations;
    private final String employeeId;
    private final DiscardMenuItemController discardMenuItemController;
    LocalDateTime loginTime;

    public ChefClient(BufferedReader socketReader, PrintWriter socketWriter, BufferedReader consoleReader, String employeeId) {
        this.socketReader = socketReader;
        this.socketWriter = socketWriter;
        this.consoleReader = consoleReader;
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.loginTime = LocalDateTime.now();
        this.discardMenuItemController = new DiscardMenuItemController(socketReader, socketWriter, consoleReader);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            System.out.println("Connected to the server");

            while (true) {
                displayMenu();
                String command = consoleReader.readLine();

                if (command.equals("5")) {
                    JSONObject userActivity = new JSONObject();
                    userActivity.put("requestType", "userLogs");
                    userActivity.put("userId", this.employeeId);
                    userActivity.put("operations", operations);
                    userActivity.put("loginTime", loginTime.toString());
                    String sendRequest = userActivity.toJSONString();
                    socketWriter.println(sendRequest + "\n");
                    System.out.println("Chef logged out successfully");
                    break;
                }

                switch (command) {
                    case "1" -> {
                        operations.add("viewRecommendation");
                        handleCreateRecommendation();
                    }
                    case "2" -> {
                        operations.add("rolloutMenu");
                        handleCreateRolloutMenu();
                    }
                    case "3" -> {
                        operations.add("selectMenu");
                        handleSelectMenu();
                    }
                    case "4" -> {
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
        System.out.println("Enter command:\n1. Create Recommendation\n2. Create Rollout Menu\n3. Select Menu\n4.discardMenuItem\n5. Exit");
    }

    @SuppressWarnings("unchecked")
    private void handleCreateRecommendation() throws IOException {
        JSONObject chefDetails = new JSONObject();
        chefDetails.put("requestType", "viewRecommendations");
        sendRequest(chefDetails);
        readResponse();
    }

    @SuppressWarnings("unchecked")
    private void handleCreateRolloutMenu() throws IOException {
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            int numberOfItems = getNumberOfItems(mealType);

            for (int j = 0; j < numberOfItems; j++) {
                JSONObject itemData = new JSONObject();
                int menuId = getMenuItemId();

                itemData.put("menu_item_id", menuId);
                itemData.put("meal_type_id", i + 1);
                itemData.put("requestType", "insertRollOutMenuItem");
                sendRequest(itemData);
            }
        }
        System.out.println("Menu Item added successfully");
    }

    private int getNumberOfItems(String mealType) throws IOException {
        return getNumericInput("Enter number of items to enter for " + mealType + ": ");
    }

    private int getMenuItemId() throws IOException {
        return getNumericInput("Enter menu item ID: ");
    }

    private int getNumericInput(String prompt) throws IOException {
        int input;
        while (true) {
            try {
                System.out.println(prompt);
                input = Integer.parseInt(consoleReader.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return input;
    }

    @SuppressWarnings("unchecked")
    private void handleSelectMenu() throws IOException {
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        JSONArray mealSelections = new JSONArray();

        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            requestMenuFromServer(i);

            String response = readMenuResponse();
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
        readResponse();
    }

    @SuppressWarnings("unchecked")
    private void requestMenuFromServer(int mealTypeIndex) {
        JSONObject menuRequest = new JSONObject();
        menuRequest.put("requestType", "showRolloutMenuByVote");
        menuRequest.put("mealType", mealTypeIndex + 1);
        sendRequest(menuRequest);
    }

    private String readMenuResponse() throws IOException {
        return readResponseUntilEnd();
    }

    private List<String> getSelectedItems(String mealType) throws IOException {
        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + " (comma separated):");
        String[] inputItems = consoleReader.readLine().split(",");
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
        sendRequest(selectedItemsData);
    }

    private void sendRequest(JSONObject requestData) {
        String request = requestData.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    private void readResponse() throws IOException {
        System.out.println("Server response: ");
        String response = readResponseUntilEnd();
        System.out.println(response);
    }

    private String readResponseUntilEnd() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String responseLine;
        while ((responseLine = socketReader.readLine()) != null) {
            if (responseLine.equals("END_OF_RESPONSE")) {
                break;
            }
            responseBuilder.append(responseLine).append("\n");
        }
        return responseBuilder.toString();
    }
}
