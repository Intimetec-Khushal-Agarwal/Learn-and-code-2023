package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.FeedbackService;

public class FeedbackController implements ClientRequestHandler {

    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "giveFeedback" ->
                    feedbackService.giveFeedback(jsonData, socketWriter);
                case "checkExistingFeedback" ->
                    feedbackService.checkExistingFeedback(jsonData, socketWriter);
                default ->
                    ErrorHandler.handleInvalidAction(socketWriter);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, socketWriter);
        }
    }
}
