package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import server.ErrorHandler;
import service.EmployeeService;

public class EmployeeController implements ClientRequestHandler {

    private final EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "showRollOutMenuItems" ->
                    employeeService.showRollOutMenuItems(jsonData, out);
                case "processSelectedItems" ->
                    employeeService.processSelectedItems(jsonData, out);
                case "checkUserVote" ->
                    employeeService.checkUserVote(jsonData, out);
                default ->
                    ErrorHandler.handleInvalidAction(out);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(out, e);
        }
    }
}
