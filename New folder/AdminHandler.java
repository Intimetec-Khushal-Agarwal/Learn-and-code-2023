package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;

public class AdminHandler implements RoleHandler {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2709;

    @SuppressWarnings({"unchecked", "resource"})
    @Override
    public void handleRoleOperations() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Enter operation to perform:\nshowMenu\naddMenuItem\nupdateMenuItem\ndeleteMenuItem\nexit ");
                String command = consoleReader.readLine();
                if (command.equals("exit")) {
                    break;
                }
                JSONObject menuItem = new JSONObject();
                menuItem.put("requestType", command);
                String request;
                switch (command) {
                    case "showMenu":
                        break;
                    case "addMenuItem":
                        System.out.println("Enter item name: ");
                        String itemName = consoleReader.readLine();
                        System.out.println("Enter item price: ");
                        float itemPrice = Float.parseFloat(consoleReader.readLine());
                        System.out.println("Enter item rating(1-5): ");
                        int rating = Integer.parseInt(consoleReader.readLine());
                        while (rating < 1 || rating > 5) {
                            System.out.println("Enter the rating in range 1-5:");
                            rating = Integer.parseInt(consoleReader.readLine());
                        }
                        System.out.println("Enter item availability status: ");
                        String itemStatus = consoleReader.readLine();
                        System.out.println("Enter meal type(1-3):\n1.Breakfast\n2.Lunch\n3.Dinner");
                        int mealType = Integer.parseInt(consoleReader.readLine());
                        menuItem.put("name", itemName);
                        menuItem.put("price", itemPrice);
                        menuItem.put("status", itemStatus);
                        menuItem.put("rating", rating);
                        menuItem.put("mealType", mealType);
                        break;
                    case "updateMenuItem":
                        System.out.println("Enter item id: ");
                        int itemId = Integer.parseInt(consoleReader.readLine());
                        System.out.println("Enter item price: ");
                        itemPrice = Float.parseFloat(consoleReader.readLine());
                        System.out.println("Enter item availability status: ");
                        itemStatus = consoleReader.readLine();
                        menuItem.put("id", itemId);
                        menuItem.put("price", itemPrice);
                        menuItem.put("status", itemStatus);
                        break;
                    case "deleteMenuItem":
                        System.out.println("Enter item ID to delete: ");
                        itemId = Integer.parseInt(consoleReader.readLine());
                        menuItem = new JSONObject();
                        menuItem.put("requestType", command);
                        menuItem.put("id", itemId);
                        break;
                    default:
                        System.out.println("Invalid command");
                }

                request = menuItem.toJSONString();
                out.println(request + "\n");
                out.flush();
                String response;
                System.out.println("Server response: ");
                while ((response = in.readLine()) != null && !response.isEmpty()) {
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
        }
    }
}
