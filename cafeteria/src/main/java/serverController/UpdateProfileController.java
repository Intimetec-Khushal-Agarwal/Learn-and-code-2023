package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.UpdateProfileService;

public class UpdateProfileController implements ClientRequestHandler {

    private final UpdateProfileService profileUpdater = new UpdateProfileService();

    @Override
    public void handleRequest(JSONObject request, PrintWriter socketWriter) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "updateUserProfile" -> {
                profileUpdater.updateUserProfile(request, socketWriter);
            }
            default -> {
                ErrorHandler.handleInvalidAction(socketWriter);

            }
        }
    }
}
