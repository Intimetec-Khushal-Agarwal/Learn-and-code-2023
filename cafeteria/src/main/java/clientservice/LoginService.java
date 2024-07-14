package clientservice;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import client.ClientConnectionManager;
import clientcontroller.AdminController;
import clientcontroller.ChefController;
import clientcontroller.DiscardMenuItemController;
import clientcontroller.EmployeeController;

public class LoginService {

    private final ClientConnectionManager connectionManager;
    private final DiscardMenuItemController discardMenuItemController;
    private int employeeId;

    public LoginService(ClientConnectionManager connectionManager, DiscardMenuItemController discardMenuItemController) {
        this.connectionManager = connectionManager;
        this.discardMenuItemController = discardMenuItemController;
    }

    @SuppressWarnings("unchecked")
    public void handleLogin() throws IOException, ParseException {
        System.out.println("Enter employee ID: ");
        employeeId = connectionManager.getInputValidations().getValidatedIntInput();
        System.out.println("Enter name: ");
        String name = connectionManager.getConsoleReader().readLine();

        JSONObject requestData = new JSONObject();
        requestData.put("requestType", "login");
        requestData.put("userId", String.valueOf(employeeId));
        requestData.put("name", name);

        connectionManager.getServerRequest().sendRequest(requestData);
        processLoginResponse();
    }

    private void processLoginResponse() throws IOException, ParseException {
        String response = connectionManager.getSocketReader().readLine();

        JSONObject responseJson = (JSONObject) new JSONParser().parse(response);
        String status = (String) responseJson.get("status");

        if ("success".equals(status)) {
            int roleId = ((Long) responseJson.get("role")).intValue();
            RoleHandler roleHandler = createRoleHandler(roleId, String.valueOf(employeeId));
            roleHandler.handleRoleOperations();
        } else {
            System.out.println("Login failed");
        }
    }

    private RoleHandler createRoleHandler(int roleId, String employeeId) {
        switch (roleId) {
            case 1 -> {
                System.out.println("Admin Login Successfully");
                return new AdminController(discardMenuItemController, connectionManager.getInputValidations(), connectionManager.getServerRequest(), connectionManager.getServerResponse(), employeeId);
            }
            case 2 -> {
                System.out.println("Chef Login Successfully");
                return new ChefController(discardMenuItemController, connectionManager.getInputValidations(), connectionManager.getServerRequest(), connectionManager.getServerResponse(), employeeId);
            }
            case 3 -> {
                System.out.println("Employee Login Successfully");
                return new EmployeeController(connectionManager.getInputValidations(), connectionManager.getServerRequest(), connectionManager.getServerResponse(), employeeId);
            }
            default ->
                throw new IllegalArgumentException("Unknown role ID: " + roleId);
        }
    }
}
