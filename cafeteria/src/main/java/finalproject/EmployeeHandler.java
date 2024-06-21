package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class EmployeeHandler implements RoleHandler {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2709;

    private String employeeId;

    public EmployeeHandler(String employeeId) {
        this.employeeId = employeeId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleRoleOperations() {
        try {

            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected to the server");
            while (true) {
                System.out.println("Enter command (selectMenuItem, giveFeedback, exit): ");
                String command = consoleReader.readLine();

                if (command.equals("exit")) {
                    System.out.println("Employee logout successfully");
                    break;
                }

                switch (command) {
                    case "selectMenuItem":
                        handleSelectMenuItem(out, in, consoleReader);
                        break;
                    case "giveFeedback":
                        handleGiveFeedback(out, in, consoleReader);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleSelectMenuItem(PrintWriter out, BufferedReader in, BufferedReader consoleReader) throws IOException, ParseException {
        if (hasUserVoted(out, in)) {
            System.out.println("You have already voted for the day");
        } else {
            requestMenuFromServer(out, in, consoleReader);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean hasUserVoted(PrintWriter out, BufferedReader in) throws IOException, ParseException {
        JSONObject employeeData = new JSONObject();
        employeeData.put("requestType", "checkUserVote");
        employeeData.put("userId", this.employeeId);
        String checkVoteJson = employeeData.toJSONString();

        System.out.println("Checking if user has already voted: " + checkVoteJson);
        out.println(checkVoteJson + "\n");
        out.flush();

        String voteCheckResponse;
        String result = "";
        while ((voteCheckResponse = in.readLine()) != null && !voteCheckResponse.isEmpty()) {
            System.out.println("Result" + voteCheckResponse);
            result = voteCheckResponse;
        }
        System.out.println("voteCheckResponse " + voteCheckResponse);
        return Boolean.parseBoolean(result);
    }

    @SuppressWarnings("unchecked")
    private void requestMenuFromServer(PrintWriter out, BufferedReader in, BufferedReader consoleReader) throws IOException {
        System.out.println("Requesting menu from server...");
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        Map<String, List<String>> mealSelections = new HashMap<>();
        int cnt = 0;

        for (String mealType : mealTypes) {
            cnt++;
            requestMenuForMealType(out, in, consoleReader, mealSelections, mealType, cnt);
        }

        JSONObject newData = new JSONObject();
        newData.put("selectedItems", mealSelections);
        newData.put("requestType", "processSelectedItems");
        newData.put("userId", this.employeeId);

        String selectedItemsJson = newData.toJSONString();
        System.out.println("Sending JSON with selected items: " + selectedItemsJson);
        out.println(selectedItemsJson + "\n");
        out.flush();
    }

    @SuppressWarnings("unchecked")
    private void requestMenuForMealType(PrintWriter out, BufferedReader in, BufferedReader consoleReader,
            Map<String, List<String>> mealSelections, String mealType, int mealTypeId) throws IOException {
        JSONObject employeeData = new JSONObject();
        employeeData.put("requestType", "showRollOutMenuItems");
        employeeData.put("mealType", mealTypeId);//breakfaset
        String showMenu = employeeData.toJSONString();

        System.out.println("Sending JSON: " + showMenu);
        out.println(showMenu + "\n");
        out.flush();

        System.out.println("Menu for " + mealType + ":");
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();

        try {
            while ((responseLine = in.readLine()) != null) {
                if (responseLine.equals("---END OF MENU---")) {
                    break;
                }
                responseBuilder.append(responseLine).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading from server: " + e.getMessage());
            // Handle or log the exception as needed
        }

        System.out.println(responseBuilder.toString());

        List<String> itemIds = new ArrayList<>();
        System.out.println("Select items for " + mealType + ":");
        String itemId = consoleReader.readLine();
        itemIds.add(itemId);
        mealSelections.put(mealType, itemIds);
    }

    @SuppressWarnings("unchecked")
    private void handleGiveFeedback(PrintWriter out, BufferedReader in, BufferedReader consoleReader) throws IOException, ParseException {
        JSONObject employeeData = new JSONObject();
        employeeData.put("requestType", "showMenu");
        String menu = employeeData.toJSONString();
        out.println(menu + "\n");
        out.flush();

        // Read and print the server response for the menu
        String response;
        System.out.println("Server response: ");
        while ((response = in.readLine()) != null && !response.isEmpty()) {
            System.out.println(response);
        }

        System.out.println("Enter item ID: ");
        String itemId = consoleReader.readLine();
        if (checkIfUserAlreadyReviewed(out, in, itemId)) {
            System.out.println("user already submitted feedback");
            return;
        } else {
            System.out.println("Enter comment: ");
            String comment = consoleReader.readLine();
            System.out.println("Enter rating (1-5): ");
            String rating = consoleReader.readLine();

            employeeData.put("requestType", "giveFeedback");
            employeeData.put("itemId", itemId);
            employeeData.put("userId", this.employeeId);
            employeeData.put("comment", comment);
            employeeData.put("rating", rating);

            String data = employeeData.toJSONString();
            out.println(data + "\n");
            out.flush();

            String feedbackResponse;
            while ((feedbackResponse = in.readLine()) != null && !feedbackResponse.isEmpty()) {
                System.out.println(feedbackResponse);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean checkIfUserAlreadyReviewed(PrintWriter out, BufferedReader in, String itemId) {
        System.out.println("Inside check class");
        JSONObject obj = new JSONObject();
        obj.put("itemId", itemId);
        obj.put("requestType", "checkExistingFeedback");
        String request = obj.toJSONString();
        out.println(request + "\n");
        out.flush();

        String response = null;
        try {
            while ((response = in.readLine()) != null && !response.isEmpty()) {
                System.out.println("Result: " + response);
                return Boolean.parseBoolean(response);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Default to false if no response or an error occurred
        System.out.println("voteCheckResponse: " + response);
        return false;
    }

}
