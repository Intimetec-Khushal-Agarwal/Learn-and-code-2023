package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
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
    public void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException {
        String action = (String) jsonData.get("requestType");
        try {
            switch (action) {
                case "login" ->
                    loginService.login(jsonData, socketWriter);
                case "userLogs" ->
                    userLogService.insertUserLogs(jsonData, socketWriter);
                case "showUserLogs" ->
                    userLogService.showUserLogs(socketWriter);
                default ->
                    ErrorHandler.handleInvalidAction(socketWriter);
            }
        } catch (Exception e) {
            ErrorHandler.handleJSONException(e, socketWriter, "Error handling request");
        }
    }
}
