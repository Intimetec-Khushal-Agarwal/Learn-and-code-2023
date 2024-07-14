package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import server.ErrorHandler;
import service.LoginService;
import service.UserLogService;

public class AuthorizeController implements ClientRequestHandler {

    private final LoginService loginService;
    private final UserLogService userLogService;

    public AuthorizeController() {
        this.loginService = new LoginService();
        this.userLogService = new UserLogService();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");
        System.out.println("Handling request: " + action);
        try {
            switch (action) {
                case "login" ->
                    loginService.login(jsonData, out);
                case "userLogs" ->
                    userLogService.insertUserLogs(jsonData, out);
                case "showUserLogs" ->
                    userLogService.showUserLogs(out);
                default ->
                    ErrorHandler.handleInvalidAction(out);
            }
        } catch (Exception e) {
            ErrorHandler.handleJSONException(e, out, "Error handling request");
        }
    }
}
