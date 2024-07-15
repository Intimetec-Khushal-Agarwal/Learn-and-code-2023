package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.ChefService;

public class ChefController implements ClientRequestHandler {

    private final ChefService chefService = new ChefService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException {
        String action = (String) jsonData.get("requestType");
        try {
            switch (action) {
                case "showRolloutMenuByVote" ->
                    chefService.showRolloutMenuByVote(jsonData, socketWriter);
                case "insertRollOutMenuItem" ->
                    chefService.insertRolloutMenuItem(jsonData, socketWriter);
                case "storeSelectedItemsInPreparedMenu" ->
                    chefService.storeSelectedItemsInPreparedMenu(jsonData, socketWriter);
                default -> {
                    ErrorHandler.handleInvalidAction(socketWriter);
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, socketWriter);
        }
    }
}
