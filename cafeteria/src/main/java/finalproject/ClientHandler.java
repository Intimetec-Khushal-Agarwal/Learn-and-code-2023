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

class ClientHandler implements Runnable {

    private Socket clientSocket;
    private Map<String, ClientRequestHandler> requestHandlers;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        initializeRequestHandlers();
    }

    @SuppressWarnings("rawtypes")
    private void initializeRequestHandlers() {
        requestHandlers = new HashMap<>();
        requestHandlers.put("login", new AuthorizationService());
        requestHandlers.put("showMenu", new AdminService());
        requestHandlers.put("addMenuItem", new AdminService());
        requestHandlers.put("updateMenuItem", new AdminService());
        requestHandlers.put("deleteMenuItem", new AdminService());
        requestHandlers.put("showRollOutMenuItems", new EmployeeServer());
        requestHandlers.put("processSelectedItems", new EmployeeServer());
        requestHandlers.put("checkUserVote", new EmployeeServer());
        requestHandlers.put("createRecommendation", new ChefServer());
        requestHandlers.put("generateReport", new ChefServer());
        requestHandlers.put("selectMenuItem", new EmployeeServer());
        requestHandlers.put("chefSelectedMenuItem", new ChefServer());
        requestHandlers.put("giveFeedback", new FeedbackService());
        requestHandlers.put("showRolloutMenuByVote", new ChefServer());
        requestHandlers.put("insertRollOutMenuItem", new ChefServer());
        requestHandlers.put("viewRecommendations", new RecommendationService());
        requestHandlers.put("checkExistingFeedback", new FeedbackService());
    }

    @Override
    public void run() {
        System.out.println("Inside run");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            handleClientRequests(in, out);
        } catch (IOException ex) {
            System.out.println("Input output exception");
        } finally {
            System.out.println("Inside finally");
            closeSocket();
        }
    }

    private void handleClientRequests(BufferedReader in, PrintWriter out) throws IOException {
        String line;
        while (true) {
            StringBuilder requestBuilder = new StringBuilder();
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\n");
            }

            String requestData = requestBuilder.toString().trim();
            if (requestData.isEmpty()) {
                continue;
            }

            System.out.println("request Builder " + requestData);

            JSONObject jsonData = (JSONObject) JSONValue.parse(requestData);
            if (jsonData != null) {
                handleRequest(jsonData, out);
            } else {
                out.println("Invalid JSON data.");
            }

            System.out.println((String) jsonData.get("requestType"));
            if ("exit".equalsIgnoreCase((String) jsonData.get("requestType"))) {
                break;
            }
        }
    }

    private void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String requestType = (String) jsonData.get("requestType");//login
        ClientRequestHandler handler = requestHandlers.get(requestType);// = new AuthorziationService();

        if (handler != null) {
            handler.handleRequest(jsonData, out);
        } else {
            out.println("Invalid request");
        }
    }

    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }
}
