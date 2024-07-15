package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.NotificationService;

public class NotificationController implements ClientRequestHandler {

    private final NotificationService notificationService = new NotificationService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "viewNotification" ->
                notificationService.viewNotifications(out);
            default ->
                ErrorHandler.handleInvalidAction(out);
        }
    }
}
