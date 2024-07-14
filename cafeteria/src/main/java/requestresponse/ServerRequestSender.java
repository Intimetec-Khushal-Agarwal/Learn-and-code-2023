package requestresponse;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import org.json.simple.JSONObject;

public class ServerRequestSender {

    private final PrintWriter socketWriter;

    public ServerRequestSender(PrintWriter socketWriter) {
        this.socketWriter = socketWriter;
    }

    public void sendRequest(JSONObject requestData) {
        String request = requestData.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    @SuppressWarnings("unchecked")
    public void logUserOperations(String userId, List<String> operations) {
        JSONObject logRequest = new JSONObject();
        logRequest.put("requestType", "userLogs");
        logRequest.put("userId", userId);
        logRequest.put("operations", operations);
        logRequest.put("loginTime", LocalDateTime.now().toString());
        sendRequest(logRequest);
    }
}
