package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import server.ErrorHandler;
import service.ChefService;

public class ChefController implements ClientRequestHandler {

    private final ChefService chefService;

    public ChefController() {
        chefService = new ChefService();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "showRolloutMenuByVote" ->
                    chefService.showRolloutMenuByVote(jsonData, out);
                case "insertRolloutMenuItem" ->
                    chefService.insertRolloutMenuItem(jsonData, out);
                case "storeSelectedItemsInPreparedMenu" ->
                    chefService.storeSelectedItemsInPreparedMenu(jsonData, out);
                default -> {
                    ErrorHandler.handleInvalidAction(out);
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(out, e);
        }
    }
}
