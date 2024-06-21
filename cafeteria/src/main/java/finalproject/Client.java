package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2709;

    @SuppressWarnings({ "unchecked", "unchecked" })
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server");
            String employeeId;

            while (true) {
                System.out.println("Enter operation to perform: login or exit ");
                String command = consoleReader.readLine();

                if (command.equals("exit")) {
                    break;
                }

                JSONObject requestData = new JSONObject();
                requestData.put("requestType", command);
                String request;

                switch (command) {
                    case "login":
                        System.out.println("Enter employee ID: ");
                        employeeId = consoleReader.readLine();
                        System.out.println("Enter name: ");
                        String name = consoleReader.readLine();
                        requestData.put("userId", employeeId);
                        requestData.put("name", name);
                        break;
                    default:
                        System.out.println("Invalid command");
                        continue;
                }

                request = requestData.toJSONString();
                out.println(request + "\n");
                out.flush();

                String response = in.readLine();
                System.out.println("Response: " + response);

                JSONObject responseJson = (JSONObject) new JSONParser().parse(response);
                String status = (String) responseJson.get("status");

                if ("success".equals(status)) {
                    int roleId = ((Long) responseJson.get("role")).intValue();
                    System.out.println("Login successful. Role ID: " + roleId);
                    RoleHandler roleHandler = createRoleHandler(roleId, employeeId);
                    roleHandler.handleRoleOperations();
                } else {
                    System.out.println("Login failed");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static RoleHandler createRoleHandler(int roleId, String employeeId) {
        switch (roleId) {
            case 1:
                System.out.println("Admin Login Successfully");
                return new AdminHandler();
            case 2:
                System.out.println("Chef Login Successfully");
                return new ChefHandler();
            case 3:
                System.out.println("Employee Login Successfully");
                return new EmployeeHandler(employeeId);
            default:
                throw new IllegalArgumentException("Unknown role ID: " + roleId);
        }
    }
}
