package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChefHandler implements RoleHandler {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2709;

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
                System.out.println(socket);
                System.out.println("Enter command (createRecommendation\ncreateRollOutMenu\ngenerateReport\nselectMenu\nexit): ");
                String command = consoleReader.readLine();

                if (command.equals("exit")) {
                    break;
                }
                switch (command) {
                    case "createRecommendation":
                        JSONObject chefDetails = new JSONObject();
                        chefDetails.put("requestType", "viewRecommendations");
                        String data = chefDetails.toJSONString();
                        out.println(data + "\n");
                        out.flush();
                        String line;
                        while ((line = in.readLine()) != null) {
                            System.out.println(line);
                        }
                        break;

                    case "createRollOutMenu":
                        try {
                            String[] mealTypes = {"breakfast", "lunch", "dinner"};
                            int cnt = 0;
                            for (String mealType : mealTypes) {
                                System.out.println("Enter number of items to enter for " + mealType + ": ");
                                int numberOfItems = Integer.parseInt(consoleReader.readLine());
                                cnt++;
                                for (int i = 0; i < numberOfItems; i++) {
                                    JSONObject itemData = new JSONObject();
                                    int menuId = Integer.parseInt(consoleReader.readLine());

                                    System.out.println("Enter menu item ID: ");
                                    itemData.put("menu_item_id", menuId);
                                    itemData.put("meal_type_id", cnt);
                                    itemData.put("requestType", "insertRollOutMenuItem");
                                    String details = itemData.toJSONString();
                                    out.println(details + "\n");
                                    out.flush();

                                }

                            }
                            while ((line = in.readLine()) != null) {
                                System.out.println(line);
                                break;
                            }

                        } catch (IOException | NumberFormatException e) {
                            System.out.println("Invalid input. Please try again.");
                            e.printStackTrace();
                        }
                        break;

                    case "generateReport":
                        // out.println("generateReport");
                        break;

                    case "selectMenu":
                        String[] mealTypes = {"breakfast", "lunch", "dinner"};
                        JSONArray mealSelections = new JSONArray();
                        int cnt = 0;
                        for (String mealType : mealTypes) {
                            cnt++;
                            JSONObject employeeData = new JSONObject();
                            System.out.println("Requesting menu for " + mealType + " from server...");
                            employeeData.put("requestType", "showRolloutMenuByVote");
                            employeeData.put("mealType", cnt);
                            String request = employeeData.toJSONString();
                            System.out.println("request " + request);
                            out.println(request + "\n");
                            out.flush();

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
                            }

                            System.out.println("Menu Items for " + mealType + ":\n" + responseBuilder.toString());

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
                            ((Map) mealSelections).put(mealType, itemIds);
                        }

                        JSONObject newData = new JSONObject();
                        newData.put("selectedItems", mealSelections);
                        newData.put("requestType", "chefSelectedMenuItem");

                        String selectedItemsJson = newData.toJSONString();
                        System.out.println("Sending JSON with selected items: " + selectedItemsJson);
                        out.println(selectedItemsJson + "\n");
                        out.flush();
                        break;
                    default:
                        System.out.println("Invalid command");
                }

                String response = in.readLine();
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
