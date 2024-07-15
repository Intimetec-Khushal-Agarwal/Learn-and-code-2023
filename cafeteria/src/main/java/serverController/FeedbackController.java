package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.FeedbackService;

public class FeedbackController implements ClientRequestHandler {

    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "giveFeedback" ->
                    feedbackService.giveFeedback(jsonData, out);
                case "checkExistingFeedback" ->
                    feedbackService.checkExistingFeedback(jsonData, out);
                default ->
                    ErrorHandler.handleInvalidAction(out);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, out);
        }
    }
}
