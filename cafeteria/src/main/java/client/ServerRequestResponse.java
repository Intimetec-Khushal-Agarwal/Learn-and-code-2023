package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import org.json.simple.JSONObject;

public class ServerRequestResponse {

    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public ServerRequestResponse(BufferedReader socketReader, PrintWriter socketWriter) {
        this.socketReader = socketReader;
        this.socketWriter = socketWriter;
    }

    public void sendRequest(JSONObject requestData) {
        String request = requestData.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    public void readResponse() throws IOException {
        String response = readResponseUntilEnd();
        System.out.println(response);
    }

    public String readResponseUntilEnd() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String responseLine;
        while ((responseLine = socketReader.readLine()) != null) {
            if (responseLine.equals("END_OF_RESPONSE")) {
                break;
            }
            responseBuilder.append(responseLine).append("\n");
        }
        return responseBuilder.toString();
    }

    public String readJSONresponse() throws IOException {
        String response = socketReader.readLine();
        return response;
    }

    @SuppressWarnings("unchecked")
    public void userLogs(String employeeId, List<String> operations) {
        JSONObject logRequest = new JSONObject();
        LocalDateTime loginTime = LocalDateTime.now();
        logRequest.put("requestType", "userLogs");
        logRequest.put("userId", employeeId);
        logRequest.put("operations", operations);
        logRequest.put("loginTime", loginTime.toString());
        sendRequest(logRequest);
    }
}
