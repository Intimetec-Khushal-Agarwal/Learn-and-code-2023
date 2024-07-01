package finalproject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EmployeeClient implements RoleHandler {

    private final String employeeId;
    private final List<String> operations;
    private final UpdateProfile updateProfile;
    LocalDateTime loginTime;
    private final JsonRequestResponse jsonRequestResponse;
    private final InputValidations inputValidations;
    JSONObject jsonRequest;

    public EmployeeClient(InputValidations inputValidations, JsonRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.loginTime = LocalDateTime.now();
        this.updateProfile = new UpdateProfile(inputValidations, jsonRequestResponse, employeeId);
        this.jsonRequestResponse = jsonRequestResponse;
        this.inputValidations = inputValidations;

        this.jsonRequest = new JSONObject();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                System.out.println("Enter command\n1. Select Menu Item\n2. Give Feedback \n3. View Notification\n4. View Discard Menu Item\n5. Update Profile \n6. Exit ");
                int command = inputValidations.getValidatedIntInput();

                if (command == 6) {
                    jsonRequest.put("requestType", "userLogs");
                    jsonRequest.put("userId", this.employeeId);
                    jsonRequest.put("operations", operations);
                    jsonRequest.put("loginTime", loginTime.toString());
                    jsonRequestResponse.sendRequest(jsonRequest);
                    System.out.println("Employee logged out successfully");
                    break;
                }

                switch (command) {
                    case 1 -> {
                        operations.add("SelectMenuItem");
                        handleSelectMenuItem();
                    }
                    case 2 -> {
                        operations.add("GiveFeedback");
                        handleGiveFeedback();
                    }
                    case 3 -> {
                        operations.add("ViewNotification");
                        viewNotification();
                    }
                    case 4 -> {
                        operations.add("showDiscardMenuItems");
                        showDiscardMenuItems();
                    }

                    case 5 -> {
                        operations.add("updateUserProfile");
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

    @SuppressWarnings("unchecked")
    private void requestMenuFromServer() throws IOException {
        System.out.println("Requesting menu from server...");
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        Map<String, List<String>> mealSelections = new HashMap<>();

        for (int i = 0; i < mealTypes.length; i++) {
            requestMenuForMealType(mealSelections, mealTypes[i], i + 1);
        }

        jsonRequest.put("requestType", "processSelectedItems");
        jsonRequest.put("userId", this.employeeId);
        jsonRequest.put("selectedItems", mealSelections);
        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readResponse();
    }

    @SuppressWarnings("unchecked")
    private void requestMenuForMealType(Map<String, List<String>> mealSelections, String mealType, int mealTypeId) throws IOException {
        jsonRequest.put("requestType", "showRollOutMenuItems");
        jsonRequest.put("mealType", mealTypeId);
        jsonRequestResponse.sendRequest(jsonRequest);

        System.out.println("Menu for " + mealType + ":");
        jsonRequestResponse.readResponse();

        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + ":");
        int itemId = inputValidations.getValidatedIntInput();

        itemIds.add(String.valueOf(itemId));
        mealSelections.put(mealType, itemIds);
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

    @SuppressWarnings({"unchecked", "null"})
    private void showDiscardMenuItems() throws IOException, ParseException {
        jsonRequest.put("requestType", "showDiscardMenuItems");
        jsonRequestResponse.sendRequest(jsonRequest);
        System.out.println("Request send successfully");
        jsonRequestResponse.readResponse();
        System.out.println("Response read");

        String response = jsonRequestResponse.readJSONresponse();
        System.out.println("Response: " + response);

        if (response != null || response.isBlank()) {
            JSONParser parser = new JSONParser();
            JSONObject responseJson;
            responseJson = (JSONObject) parser.parse(response);
            String status = (String) responseJson.get("status");

            if ("false".equals(status)) {
                System.out.println("Currently no item has been added discard list ");
            } else if ("success".equals(status)) {
                String message = (String) responseJson.get("message");
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
    }
}
