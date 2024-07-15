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
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "viewRecommendations" -> {
                try {
                    recommendationProcessor.viewRecommendations(out);
                } catch (SQLException ex) {
                    out.println("Error fetching recommendations.");
                    out.println("END_OF_RESPONSE");
                    out.flush();
                }
            }
            default -> {
                ErrorHandler.handleInvalidAction(out);
            }
        }
    }
}
