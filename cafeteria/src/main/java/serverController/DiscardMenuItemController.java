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
    public void handleRequest(JSONObject request, PrintWriter socketWriter) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "discardMenuItem" ->
                discardMenuItemDbService.showDiscardMenuItemList(socketWriter);
            case "storeDiscardedItem" ->
                discardMenuItemDbService.storeDiscardMenuItem(request, socketWriter);
            case "showDiscardMenuItems" ->
                discardMenuItemDbService.showDiscardMenuItems(socketWriter);
            default ->
                ErrorHandler.handleInvalidAction(socketWriter);
        }
    }
}
