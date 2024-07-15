package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.DiscardMenuItemService;

public class DiscardMenuItemController implements ClientRequestHandler {

    private final DiscardMenuItemService discardMenuItemDbService;

    public DiscardMenuItemController() {
        this.discardMenuItemDbService = new DiscardMenuItemService();
    }

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "discardMenuItem" ->
                discardMenuItemDbService.showDiscardMenuItemList(out);
            case "storeDiscardedItem" ->
                discardMenuItemDbService.storeDiscardMenuItem(request, out);
            case "showDiscardMenuItems" ->
                discardMenuItemDbService.showDiscardMenuItems(out);
            default ->
                ErrorHandler.handleInvalidAction(out);
        }
    }
}
