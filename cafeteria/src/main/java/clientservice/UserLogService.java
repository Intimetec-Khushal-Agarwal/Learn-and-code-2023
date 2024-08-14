package clientservice;

import java.io.IOException;

import org.json.simple.JSONObject;

import client.ClientConnectionManager;
import error.ErrorHandler;

public class UserLogService {

    private final ClientConnectionManager connectionManager;

    public UserLogService(ClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SuppressWarnings("unchecked")
    public void showUserLogs() {
        JSONObject requestData = new JSONObject();
        requestData.put("requestType", "showUserLogs");
        connectionManager.getServerRequest().sendRequest(requestData);

        try {
            String serverResponse;
            while ((serverResponse = connectionManager.getSocketReader().readLine()) != null) {
                if (serverResponse.equals("END_OF_RESPONSE")) {
                    break;
                }
            }
        } catch (IOException ex) {
            ErrorHandler.handleIOException(ex);
        }
    }
}
