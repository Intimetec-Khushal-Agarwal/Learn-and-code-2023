package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Map<String, ClientRequestHandler> requestHandlers;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.requestHandlers = initializeRequestHandlers();
    }

    private Map<String, ClientRequestHandler> initializeRequestHandlers() {
        Map<String, ClientRequestHandler> handlers = new HashMap<>();
        handlers.put("login", new AuthorizationService());
        handlers.put("userLogs", new AuthorizationService());
        handlers.put("discardMenuItem", new DiscardMenuItemService());
        handlers.put("storeDiscardedItem", new DiscardMenuItemService());
        handlers.put("showDiscardMenuItems", new DiscardMenuItemService());
        handlers.put("showUserLogs", new AuthorizationService());
        handlers.put("showMenu", new AdminServerController());
        handlers.put("addMenuItem", new AdminServerController());
        handlers.put("updateMenuItem", new AdminServerController());
        handlers.put("deleteMenuItem", new AdminServerController());
        handlers.put("showRollOutMenuItems", new EmployeeServer());
        handlers.put("processSelectedItems", new EmployeeServer());
        handlers.put("checkUserVote", new EmployeeServer());
        handlers.put("createRecommendation", new ChefServer());
        handlers.put("generateReport", new ChefServer());
        handlers.put("selectMenuItem", new EmployeeServer());
        handlers.put("storeSelectedItemsInPreparedMenu", new ChefServer());
        handlers.put("giveFeedback", new FeedbackService());
        handlers.put("showRolloutMenuByVote", new ChefServer());
        handlers.put("insertRollOutMenuItem", new ChefServer());
        handlers.put("viewRecommendations", new RecommendationService());
        handlers.put("checkExistingFeedback", new FeedbackService());
        handlers.put("viewNotification", new NotificationService());
        handlers.put("updateUserProfile", new updateProfileServer());

        return handlers;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            handleClientRequests(in, out);
        } catch (IOException ex) {
            System.out.println("Error handling client: " + ex.getMessage());
        } finally {
            closeSocket();
        }
    }

    private void handleClientRequests(BufferedReader in, PrintWriter out) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append(line);

            if (!line.isEmpty()) {
                while (in.ready() && (line = in.readLine()) != null) {
                    requestBuilder.append("\n").append(line);
                }
            }

            String requestData = requestBuilder.toString().trim();
            if (requestData.isEmpty()) {
                continue;
            }

            System.out.println("Request received:\n" + requestData);

            JSONObject jsonData = (JSONObject) JSONValue.parse(requestData);
            if (jsonData != null) {
                handleRequest(jsonData, out);
                String requestType = (String) jsonData.get("requestType");
                if ("exit".equalsIgnoreCase(requestType)) {
                    break;
                }
            } else {
                out.println("Invalid JSON data.");
            }
        }
    }

    private void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String requestType = (String) jsonData.get("requestType");
        ClientRequestHandler handler = requestHandlers.get(requestType);

        if (handler != null) {
            handler.handleRequest(jsonData, out);
        } else {
            out.println("Invalid request type: " + requestType);
        }
    }

    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Client connection closed.");
            }
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }
}
