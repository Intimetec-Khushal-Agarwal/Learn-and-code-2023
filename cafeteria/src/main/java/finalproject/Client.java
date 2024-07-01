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
    private static Socket socket = null;
    private static BufferedReader socketReader = null;
    private static PrintWriter socketWriter = null;
    private static BufferedReader consoleReader = null;
    private static DiscardMenuItemController discardMenuItemController = null;
    private static int employeeId;
    private static InputValidations inputValidations;
    private static JsonRequestResponse jsonRequestResponse;

    public static void main(String[] args) {
        try {
            initializeConnection();
            handleUserCommands();
        } catch (IOException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private static void initializeConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        socketWriter = new PrintWriter(socket.getOutputStream(), true);
        inputValidations = new InputValidations(consoleReader);
        jsonRequestResponse = new JsonRequestResponse(socketReader, socketWriter);
        discardMenuItemController = new DiscardMenuItemController(inputValidations, jsonRequestResponse, consoleReader);

    }

    private static void handleUserCommands() throws IOException, ParseException {
        while (true) {
            System.out.println("Enter operation to perform\n1. login\n2. showUserLogs\n3. Exit");
            int command = inputValidations.getValidatedIntInput();

            if (command == 3) {
                break;
            }

            switch (command) {
                case 1 ->
                    handleLogin();
                case 2 ->
                    showUserLogs();
                default ->
                    System.out.println("Invalid command");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void handleLogin() throws IOException, ParseException {
        System.out.println("Enter employee ID: ");
        employeeId = inputValidations.getValidatedIntInput();
        System.out.println("Enter name: ");
        String name = consoleReader.readLine();

        JSONObject requestData = new JSONObject();
        requestData.put("requestType", "login");
        requestData.put("userId", String.valueOf(employeeId));
        requestData.put("name", name);

        jsonRequestResponse.sendRequest(requestData);
        processLoginResponse();
    }

    private static void processLoginResponse() throws IOException, ParseException {
        String response = socketReader.readLine();
        System.out.println("Response: " + response);

        JSONObject responseJson = (JSONObject) new JSONParser().parse(response);
        String status = (String) responseJson.get("status");

        if ("success".equals(status)) {
            int roleId = ((Long) responseJson.get("role")).intValue();
            System.out.println("Login successful. Role ID: " + roleId);
            RoleHandler roleHandler = createRoleHandler(roleId, String.valueOf(employeeId));
            roleHandler.handleRoleOperations();
        } else {
            System.out.println("Login failed");
        }
    }

    private static RoleHandler createRoleHandler(int roleId, String employeeId) {
        switch (roleId) {
            case 1 -> {
                System.out.println("Admin Login Successfully");
                return new AdminClientController(discardMenuItemController, inputValidations, jsonRequestResponse, String.valueOf(employeeId));
            }
            case 2 -> {
                System.out.println("Chef Login Successfully");
                return new ChefClient(discardMenuItemController, inputValidations, jsonRequestResponse, String.valueOf(employeeId));
            }
            case 3 -> {
                System.out.println("Employee Login Successfully");
                return new EmployeeClient(inputValidations, jsonRequestResponse, String.valueOf(employeeId));
            }
            default ->
                throw new IllegalArgumentException("Unknown role ID: " + roleId);
        }
    }

    @SuppressWarnings("unchecked")
    private static void showUserLogs() {
        JSONObject requestData = new JSONObject();
        requestData.put("requestType", "showUserLogs");
        jsonRequestResponse.sendRequest(requestData);

        System.out.println("Server Response:");
        try {
            String serverResponse;
            while ((serverResponse = socketReader.readLine()) != null) {
                if (serverResponse.equals("END_OF_RESPONSE")) {
                    break;
                }
                System.out.println(serverResponse);
            }
        } catch (IOException ex) {
            System.out.println("Error reading server response: " + ex.getMessage());
        }
    }

    private static void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (socketReader != null) {
                socketReader.close();
            }
            if (socketWriter != null) {
                socketWriter.close();
            }
            if (consoleReader != null) {
                consoleReader.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
