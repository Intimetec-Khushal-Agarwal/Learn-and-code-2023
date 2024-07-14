package clientservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class EmployeeService {

    private final String employeeId;
    private final ConsoleInputValidator inputValidations;
    private final ServerResponseReader serverResponse;
    private final ServerRequestSender serverRequest;
    private final UpdateUserProfile updateProfile;
    private final JSONObject jsonRequest;

    public EmployeeService(ConsoleInputValidator inputValidations, ServerRequestSender serverRequest, ServerResponseReader serverResponse, String employeeId) {
        this.employeeId = employeeId;
        this.inputValidations = inputValidations;
        this.serverResponse = serverResponse;
        this.serverRequest = serverRequest;
        this.updateProfile = new UpdateUserProfile(inputValidations, serverRequest, serverResponse, employeeId);
        this.jsonRequest = new JSONObject();
    }

    public void handleSelectMenuItem() throws IOException, ParseException {
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

        serverRequest.sendRequest(jsonRequest);

        String response = serverResponse.readResponseUntilEnd();
        return Boolean.parseBoolean(response.trim());
    }

    private void requestMenuFromServer() throws IOException {
        System.out.println("Requesting menu from server...");
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        Map<String, List<String>> mealSelections = new HashMap<>();
        boolean result = false;

        for (int mealTypeId = 0; mealTypeId < mealTypes.length; mealTypeId++) {
            result = requestMenuForMealType(mealSelections, mealTypes[mealTypeId], mealTypeId + 1);
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
        serverRequest.sendRequest(jsonRequest);

        String response = serverResponse.readResponseUntilEnd();

        if (response.contains("Currently, the menu item list is not prepared")) {
            System.out.println(response + " " + mealType);
            return false;
        }
        System.out.println("Menu for " + mealType);
        System.out.println(response);
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
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }

    public void handleGiveFeedback() throws IOException, ParseException {
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
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    private boolean checkIfUserAlreadyReviewed(String itemId) throws IOException {
        jsonRequest.put("requestType", "checkExistingFeedback");
        jsonRequest.put("userId", this.employeeId);
        jsonRequest.put("itemId", itemId);

        serverRequest.sendRequest(jsonRequest);

        String response = serverResponse.readResponseUntilEnd();
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

        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    public void viewNotification() throws IOException {
        jsonRequest.put("requestType", "viewNotification");
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();
    }

    @SuppressWarnings("unchecked")
    public void showDiscardMenuItems() throws IOException, ParseException {
        jsonRequest.put("requestType", "showDiscardMenuItems");
        serverRequest.sendRequest(jsonRequest);
        serverResponse.printResponse();

        String response = serverResponse.readJsonResponse();

        if (response != null && !response.isBlank()) {
            processDiscardMenuResponse(response);
        }
    }

    private void processDiscardMenuResponse(String response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(response);
        String status = (String) responseJson.get("status");

        if ("false".equals(status)) {
            System.out.println("Currently no item has been added to the discard list");
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

    public void updateUserProfile() throws IOException {
        try {
            updateProfile.updateUserProfile();
        } catch (ParseException ex) {
        }
    }
}
