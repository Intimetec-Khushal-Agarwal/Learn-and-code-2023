package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.ChefService;

public class ChefController implements ClientRequestHandler {

    private final ChefService chefService = new ChefService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");
        System.out.println("request type" + action);
        try {
            switch (action) {
                case "showRolloutMenuByVote" ->
                    chefService.showRolloutMenuByVote(jsonData, out);
                case "insertRollOutMenuItem" ->
                    chefService.insertRolloutMenuItem(jsonData, out);
                case "storeSelectedItemsInPreparedMenu" ->
                    chefService.storeSelectedItemsInPreparedMenu(jsonData, out);
                default -> {
                    ErrorHandler.handleInvalidAction(out);
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, out);
        }
    }
}
