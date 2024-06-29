package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class EmployeeClient implements RoleHandler {

    private final String employeeId;
    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;
    private final BufferedReader consoleReader;
    private final List<String> operations;
    LocalDateTime loginTime;

    public EmployeeClient(BufferedReader socketReader, PrintWriter socketWriter, BufferedReader consoleReader, String employeeId) {
        this.socketReader = socketReader;
        this.socketWriter = socketWriter;
        this.consoleReader = consoleReader;
        this.employeeId = employeeId;
        this.operations = new ArrayList<>();
        this.loginTime = LocalDateTime.now();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {
            while (true) {
                System.out.println("Enter command\n1. selectMenuItem\n2. giveFeedback\n3. viewNotification\n4. Exit: ");
                String command = consoleReader.readLine();

                if ("4".equals(command)) {
                    JSONObject userActivity = new JSONObject();
                    userActivity.put("requestType", "userLogs");
                    userActivity.put("userId", this.employeeId);
                    userActivity.put("operations", operations);
                    userActivity.put("loginTime", loginTime.toString());
                    String sendRequest = userActivity.toJSONString();
                    socketWriter.println(sendRequest + "\n");
                    System.out.println("Employee logged out successfully");
                    break;
                }

                switch (command) {
                    case "1" -> {
                        operations.add("SelectMenuItem");
                        handleSelectMenuItem();
                    }
                    case "2" -> {
                        operations.add("GiveFeedback");
                        handleGiveFeedback();
                    }
                    case "3" -> {
                        operations.add("ViewNotification");
                        viewNotification();
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

    private boolean hasUserVoted() throws IOException, ParseException {
        String checkVoteJson = createRequest("checkUserVote", Map.of("userId", this.employeeId));
        sendRequest(checkVoteJson);

        String response = readResponse();
        return Boolean.parseBoolean(response.trim());
    }

    private void requestMenuFromServer() throws IOException {
        System.out.println("Requesting menu from server...");
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        Map<String, List<String>> mealSelections = new HashMap<>();

        for (int i = 0; i < mealTypes.length; i++) {
            requestMenuForMealType(mealSelections, mealTypes[i], i + 1);
        }

        String request = createRequest("processSelectedItems", Map.of(
                "selectedItems", mealSelections,
                "userId", this.employeeId
        ));
        sendRequest(request);
        printResponse();
    }

    private void requestMenuForMealType(Map<String, List<String>> mealSelections, String mealType, int mealTypeId) throws IOException {
        String request = createRequest("showRollOutMenuItems", Map.of("mealType", mealTypeId));
        sendRequest(request);

        System.out.println("Menu for " + mealType + ":");
        String response = readResponse();
        System.out.println(response);

        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + ":");
        String itemId = consoleReader.readLine();
        if (isValidItemId(itemId)) {
            itemIds.add(itemId);
            mealSelections.put(mealType, itemIds);
        } else {
            System.out.println("Invalid item ID. Please try again.");
        }
    }

    private void handleGiveFeedback() throws IOException, ParseException {
        requestAndPrintMenu();

        System.out.println("Enter item ID: ");
        String itemId = consoleReader.readLine();

        if (isValidItemId(itemId)) {
            if (checkIfUserAlreadyReviewed(itemId)) {
                System.out.println("User already submitted feedback");
            } else {
                collectAndSendFeedback(itemId);
            }
        } else {
            System.out.println("Invalid item ID. Please try again.");
        }
    }

    private void requestAndPrintMenu() throws IOException {
        String request = createRequest("showMenu", null);
        sendRequest(request);
        printResponse();
    }

    private boolean checkIfUserAlreadyReviewed(String itemId) throws IOException {
        String request = createRequest("checkExistingFeedback", Map.of(
                "userId", this.employeeId,
                "itemId", itemId
        ));
        sendRequest(request);

        String response = readResponse();
        return Boolean.parseBoolean(response.trim());
    }

    private void collectAndSendFeedback(String itemId) throws IOException {
        System.out.println("Enter comment: ");
        String comment = consoleReader.readLine();
        System.out.println("Enter rating (1-5): ");
        String rating = consoleReader.readLine();

        if (isValidRating(rating)) {
            String request = createRequest("giveFeedback", Map.of(
                    "itemId", itemId,
                    "userId", this.employeeId,
                    "comment", comment,
                    "rating", rating
            ));
            sendRequest(request);
            printResponse();
        } else {
            System.out.println("Invalid rating. Please enter a number between 1 and 5.");
        }
    }

    private void viewNotification() {
        String request = createRequest("viewNotification", null);
        sendRequest(request);
        printResponse();
    }

    @SuppressWarnings("unchecked")
    private String createRequest(String requestType, Map<String, Object> parameters) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", requestType);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        return jsonObject.toJSONString();
    }

    private void sendRequest(String request) {
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    private String readResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        String responseLine;
        try {
            while ((responseLine = socketReader.readLine()) != null) {
                if ("END_OF_RESPONSE".equals(responseLine)) {
                    break;
                }
                responseBuilder.append(responseLine).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading response: " + e.getMessage());
        }
        return responseBuilder.toString();
    }

    private void printResponse() {
        String response = readResponse();
        System.out.println(response);
    }

    private boolean isValidItemId(String itemId) {
        try {
            Integer.valueOf(itemId);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidRating(String rating) {
        try {
            int rate = Integer.parseInt(rating);
            return rate >= 1 && rate <= 5;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
