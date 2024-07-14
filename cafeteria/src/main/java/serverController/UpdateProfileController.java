package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import server.ErrorHandler;
import service.UpdateProfileService;

public class UpdateProfileController implements ClientRequestHandler {

    private final UpdateProfileService profileUpdater = new UpdateProfileService();

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "updateUserProfile" -> {
                profileUpdater.updateUserProfile(request, out);
            }
            default -> {
                ErrorHandler.handleInvalidAction(out);

            }
        }
    }
}
