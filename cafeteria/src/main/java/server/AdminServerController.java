package server;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

public class AdminServerController implements ClientRequestHandler {

    private final MenuService menuService;

    public AdminServerController() {
        this.menuService = new MenuService();
    }

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) {
        String action = (String) request.get("requestType");

        try {
            switch (action) {
                case "showMenu" ->
                    menuService.showMenuItems(out);
                case "addMenuItem" ->
                    menuService.addMenuItem(request, out);
                case "updateMenuItem" ->
                    menuService.updateMenuItem(request, out);
                case "deleteMenuItem" ->
                    menuService.deleteMenuItem(request, out);
                default ->
                    handleInvalidAction(out);
            }
        } catch (Exception e) {
            handleUnexpectedError(e, out);
        }
    }

    private void handleInvalidAction(PrintWriter out) {
        out.println("Invalid menu action");
        out.println("END_OF_RESPONSE");
        out.flush();
        System.err.println("Received an invalid menu action request");
    }

    private void handleUnexpectedError(Exception e, PrintWriter out) {
        out.println("An unexpected error occurred: " + e.getMessage());
        out.println("END_OF_RESPONSE");
        out.flush();
        System.err.println("Unexpected error: " + e.getMessage());
    }
}
