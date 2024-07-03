package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EmployeeClientController implements RoleHandler {

    private static final int SELECT_MENU_ITEM = 1;
    private static final int GIVE_FEEDBACK = 2;
    private static final int VIEW_NOTIFICATION = 3;
    private static final int VIEW_DISCARD_MENU_ITEM = 4;
    private static final int UPDATE_PROFILE = 5;
    private static final int EXIT = 6;

    private final String employeeId;
    private final List<String> operations;
    private final UpdateUserProfile updateProfile;
    private final ServerRequestResponse jsonRequestResponse;
    private final ConsoleInputValidations inputValidations;
    private final JSONObject jsonRequest;

    public EmployeeClientController(ConsoleInputValidations inputValidations, ServerRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.updateProfile = new UpdateUserProfile(inputValidations, jsonRequestResponse, employeeId);
        this.jsonRequestResponse = jsonRequestResponse;
        this.inputValidations = inputValidations;
        this.jsonRequest = new JSONObject();
    }

    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                displayMenu();
                int command = inputValidations.getValidatedIntInput();

                if (command == EXIT) {
                    jsonRequestResponse.userLogs(employeeId, operations);
                    jsonRequestResponse.readResponse();
                    break;
                }

                switch (command) {
                    case SELECT_MENU_ITEM -> {
                        operations.add("Select Menu Item");
                        handleSelectMenuItem();
                    }
                    case GIVE_FEEDBACK -> {
                        operations.add("Five Feedback");
                        handleGiveFeedback();
                    }
                    case VIEW_NOTIFICATION -> {
                        operations.add("View Notification");
                        viewNotification();
                    }
                    case VIEW_DISCARD_MENU_ITEM -> {
                        operations.add("View Discard Menu");
                        showDiscardMenuItems();
                    }
                    case UPDATE_PROFILE -> {
                        operations.add("Update Profile");
                                                updateProfile.updateUserProfile();
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
        System.out.println("Enter command\n1. Select Menu Item\n2. Give Feedback \n3. View Notification\n4. View Discard Menu Item\n5. Update Profile \n6. Exit ");
    }

    private void handleSelectMenuItem() throws IOException, ParseException {
        if (hasUserVoted()) {
            System.out.println("You have already voted for the day");
        } else {
            requestMenuFromServer();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean hasUserVoted() throws IOException, ParseException {
        jsonRequest.put("requestType", "checkUserVote");
        jsonRequest.put("userId", this.employeeId);

        jsonRequestResponse.sendRequest(jsonRequest);

        String response = jsonRequestResponse.readResponseUntilEnd();
        return Boolean.parseBoolean(response.trim());
    }

    private void requestMenuFromServer() throws IOException {
        System.out.println("Requesting menu from server...");
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        Map<String, List<String>> mealSelections = new HashMap<>();
        boolean result = false;

        for (int i = 0; i < mealTypes.length; i++) {
            result = requestMenuForMealType(mealSelections, mealTypes[i], i + 1);
        }
        if (result) {
            processSelectedItems(mealSelections);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean requestMenuForMealType(Map<String, List<String>> mealSelections, String mealType, int mealTypeId) throws IOException {
        jsonRequest.put("requestType", "showRollOutMenuItems");
        jsonRequest.put("mealType", mealTypeId);
        jsonRequest.put("userId", this.employeeId);
        jsonRequestResponse.sendRequest(jsonRequest);

        String response = jsonRequestResponse.readResponseUntilEnd();
        if (response.contains("Currently menu item list is not prepared")) {
            System.out.println(response + " " + mealType);
            return false;
        } else {
            System.out.println("Menu for " + mealType);
            System.out.println(response);
        }

        List<String> itemIds = getSelectedItemIds(mealType);
        mealSelections.put(mealType, itemIds);
        return true;
    }

    private List<String> getSelectedItemIds(String mealType) throws IOException {
        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + ":");
        int itemId = inputValidations.getValidatedIntInput();
        itemIds.add(String.valueOf(itemId));
        return itemIds;
    }

    @SuppressWarnings("unchecked")
    private void processSelectedItems(Map<String, List<String>> mealSelections) throws IOException {
        jsonRequest.put("requestType", "processSelectedItems");
        jsonRequest.put("userId", this.employeeId);
        jsonRequest.put("selectedItems", mealSelections);
        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();
    }

    private void handleGiveFeedback() throws IOException, ParseException {
        requestAndPrintMenu();

        System.out.println("Enter item ID: ");
        int itemId = inputValidations.getValidatedIntInput();

        if (checkIfUserAlreadyReviewed(String.valueOf(itemId))) {
            System.out.println("User already submitted feedback");
        } else {
            collectAndSendFeedback(String.valueOf(itemId));
        }
    }

    @SuppressWarnings("unchecked")
    private void requestAndPrintMenu() throws IOException {
        jsonRequest.put("requestType", "showMenu");
        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private boolean checkIfUserAlreadyReviewed(String itemId) throws IOException {
        jsonRequest.put("requestType", "checkExistingFeedback");
        jsonRequest.put("userId", this.employeeId);
        jsonRequest.put("itemId", itemId);

        jsonRequestResponse.sendRequest(jsonRequest);

        String response = jsonRequestResponse.readResponseUntilEnd();
        return Boolean.parseBoolean(response.trim());
    }

    @SuppressWarnings("unchecked")
    private void collectAndSendFeedback(String itemId) throws IOException {
        System.out.println("Enter comment: ");
        String comment = inputValidations.getValidatedStringInput();
        System.out.println("Enter rating (1-5): ");
        int rating = inputValidations.getValidatedOption(5);

        jsonRequest.put("requestType", "giveFeedback");
        jsonRequest.put("userId", this.employeeId);
        jsonRequest.put("itemId", itemId);
        jsonRequest.put("comment", comment);
        jsonRequest.put("rating", String.valueOf(rating));

        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private void viewNotification() throws IOException {
        jsonRequest.put("requestType", "viewNotification");
        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private void showDiscardMenuItems() throws IOException, ParseException {
        jsonRequest.put("requestType", "showDiscardMenuItems");
        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();

        String response = jsonRequestResponse.readJSONresponse();

        if (response != null && !response.isBlank()) {
            processDiscardMenuResponse(response);
        }
    }

    private void processDiscardMenuResponse(String response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(response);
        String status = (String) responseJson.get("status");

        if ("false".equals(status)) {
            System.out.println("Currently no item has been added discard list ");
        } else if ("success".equals(status)) {
            processFeedbackQuestions((String) responseJson.get("message"));
        }
    }

    private void processFeedbackQuestions(String message) throws IOException {
        String[] parts = message.split("\\?");
        if (parts.length > 1) {
            System.out.println("Provide the feedback for below questions\n\n");
            for (String question : parts) {
                System.out.println(question);
                inputValidations.getValidatedStringInput();
            }
            System.out.println("Thank you for submitting feedback");
        }
    }
}
