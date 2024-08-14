package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.EmployeeService;

public class EmployeeController implements ClientRequestHandler {

    private final EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "showRollOutMenuItems" ->
                    employeeService.showRollOutMenuItems(jsonData, socketWriter);
                case "processSelectedItems" ->
                    employeeService.processSelectedItems(jsonData, socketWriter);
                case "checkUserVote" ->
                    employeeService.checkUserVote(jsonData, socketWriter);
                default ->
                    ErrorHandler.handleInvalidAction(socketWriter);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, socketWriter);
        }
    }
}
