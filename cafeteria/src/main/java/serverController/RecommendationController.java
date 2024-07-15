package serverController;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.RecommendationService;

public class RecommendationController implements ClientRequestHandler {

    private final RecommendationService recommendationProcessor = new RecommendationService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "viewRecommendations" -> {
                try {
                    recommendationProcessor.viewRecommendations(socketWriter);
                } catch (SQLException ex) {
                    socketWriter.println("Error fetching recommendations.");
                    socketWriter.println("END_OF_RESPONSE");
                    socketWriter.flush();
                }
            }
            default -> {
                ErrorHandler.handleInvalidAction(socketWriter);
            }
        }
    }
}
