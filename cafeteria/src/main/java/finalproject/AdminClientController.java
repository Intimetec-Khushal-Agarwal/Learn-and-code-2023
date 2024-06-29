package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AdminClientController implements RoleHandler {

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;
    private final BufferedReader consoleReader;
    private final List<String> operations;
    private final String employeeId;
    LocalDateTime loginTime;

    public AdminClientController(BufferedReader socketReader, PrintWriter socketWriter, BufferedReader consoleReader, String employeeId) {
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
                System.out.println("Enter operation to perform:\n1.showMenu\n2.addMenuItem\n3.updateMenuItem\n4.deleteMenuItem\n5.exit ");
                String command = consoleReader.readLine();
                if (command.equalsIgnoreCase("5")) {

                    JSONObject userActivity = new JSONObject();
                    userActivity.put("requestType", "userLogs");
                    userActivity.put("userId", this.employeeId);
                    userActivity.put("operations", operations);
                    userActivity.put("loginTime", loginTime.toString());
                    String sendRequest = userActivity.toJSONString();
                    socketWriter.println(sendRequest + "\n");
                    System.out.println("Admin logged out successfully");
                    break;
                }

                JSONObject menuItem = new JSONObject();

                switch (command) {
                    case "1" -> {
                        operations.add("showMenu");
                        menuItem.put("requestType", "showMenu");
                    }
                    case "2" -> {
                        operations.add("addMenu");
                        addMenuItem(menuItem);
                    }
                    case "3" -> {
                        operations.add("updateMenu");
                        updateMenuItem(menuItem);
                    }
                    case "4" -> {
                        operations.add("deleteMenu");
                        deleteMenuItem(menuItem);
                    }
                    default -> {
                        System.out.println("Invalid command");
                        continue;
                    }
                }
                sendRequest(menuItem);
                readResponse();
            }
        } catch (IOException e) {
            System.err.println("Error handling role operations: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void addMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item name: ");
        String itemName = consoleReader.readLine();
        System.out.println("Enter item price: ");
        float itemPrice = getValidatedFloatInput();
        System.out.println("Enter item Rating: ");
        int rating = getValidatedRating();
        System.out.println("Enter item availability status: ");
        String itemStatus = consoleReader.readLine();
        System.out.println("Enter meal type (1. Breakfast, 2. Lunch, 3. Dinner): ");
        int mealType = getValidatedIntInput();

        menuItem.put("requestType", "addMenuItem");
        menuItem.put("name", itemName);
        menuItem.put("price", itemPrice);
        menuItem.put("status", itemStatus);
        menuItem.put("rating", rating);
        menuItem.put("mealType", mealType);
    }

    @SuppressWarnings("unchecked")
    private void updateMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item id: ");
        int itemId = getValidatedIntInput();
        System.out.println("Enter item price: ");
        float itemPrice = getValidatedFloatInput();
        System.out.println("Enter item availability status: ");
        String itemStatus = consoleReader.readLine();

        menuItem.put("requestType", "updateMenuItem");
        menuItem.put("id", itemId);
        menuItem.put("price", itemPrice);
        menuItem.put("status", itemStatus);
    }

    @SuppressWarnings("unchecked")
    private void deleteMenuItem(JSONObject menuItem) throws IOException {
        System.out.println("Enter item ID to delete: ");
        int itemId = getValidatedIntInput();
        menuItem.put("requestType", "deleteMenuItem");
        menuItem.put("id", itemId);
    }

    private void sendRequest(JSONObject menuItem) {
        System.out.println("Sending Request");
        String request = menuItem.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    private void readResponse() throws IOException {
        String serverResponse;
        System.out.println("server Response: ");
        while ((serverResponse = socketReader.readLine()) != null) {
            if (serverResponse.equals("END_OF_RESPONSE")) {
                break;
            }
            System.out.println(serverResponse);
        }
    }

    private int getValidatedIntInput() throws IOException {
        while (true) {
            try {
                return Integer.parseInt(consoleReader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a valid number:");
            }
        }
    }

    private float getValidatedFloatInput() throws IOException {
        while (true) {
            try {
                return Float.parseFloat(consoleReader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a valid number:");
            }
        }
    }

    private int getValidatedRating() throws IOException {
        while (true) {
            try {
                int rating = Integer.parseInt(consoleReader.readLine());
                if (rating >= 1 && rating <= 5) {
                    return rating;
                } else {
                    System.out.println("Enter the rating in range 1-5:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number between 1 and 5:");
            }
        }
    }
}
