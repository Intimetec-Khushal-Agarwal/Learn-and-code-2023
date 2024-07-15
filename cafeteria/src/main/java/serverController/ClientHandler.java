package serverController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import error.ErrorHandler;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Map<String, ClientRequestHandler> requestHandlers;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.requestHandlers = initializeRequestHandlers();
    }

    private Map<String, ClientRequestHandler> initializeRequestHandlers() {
        Map<String, ClientRequestHandler> handlers = new HashMap<>();
        handlers.put("login", new AuthorizeController());
        handlers.put("userLogs", new AuthorizeController());
        handlers.put("discardMenuItem", new DiscardMenuItemController());
        handlers.put("storeDiscardedItem", new DiscardMenuItemController());
        handlers.put("showDiscardMenuItems", new DiscardMenuItemController());
        handlers.put("showUserLogs", new AuthorizeController());
        handlers.put("showMenu", new AdminController());
        handlers.put("addMenuItem", new AdminController());
        handlers.put("updateMenuItem", new AdminController());
        handlers.put("deleteMenuItem", new AdminController());
        handlers.put("showRollOutMenuItems", new EmployeeController());
        handlers.put("processSelectedItems", new EmployeeController());
        handlers.put("checkUserVote", new EmployeeController());
        handlers.put("createRecommendation", new ChefController());
        handlers.put("generateReport", new ReportController());
        handlers.put("selectMenuItem", new EmployeeController());
        handlers.put("storeSelectedItemsInPreparedMenu", new ChefController());
        handlers.put("giveFeedback", new FeedbackController());
        handlers.put("showRolloutMenuByVote", new ChefController());
        handlers.put("insertRollOutMenuItem", new ChefController());
        handlers.put("viewRecommendations", new RecommendationController());
        handlers.put("checkExistingFeedback", new FeedbackController());
        handlers.put("viewNotification", new NotificationController());
        handlers.put("updateUserProfile", new UpdateProfileController());

        return handlers;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            handleClientRequests(in, out);
        } catch (IOException ex) {
            ErrorHandler.handleIOException(ex);
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
            }
        } catch (IOException e) {
            ErrorHandler.handleIOException(e);
        }
    }
}
