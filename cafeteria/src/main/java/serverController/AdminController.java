package serverController;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.AdminService;
import service.ShowMenuService;

public class AdminController implements ClientRequestHandler {

    private final AdminService menuActionHandler;
    private final ShowMenuService queryHandler;

    public AdminController() {
        this.menuActionHandler = new AdminService();
        this.queryHandler = new ShowMenuService();
    }

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) {
        String action = (String) request.get("requestType");

        try {
            switch (action) {
                case "showMenu" ->
                    queryHandler.showMenuItems(out);
                case "addMenuItem" ->
                    menuActionHandler.addMenuItem(request, out);
                case "updateMenuItem" ->
                    menuActionHandler.updateMenuItem(request, out);
                case "deleteMenuItem" ->
                    menuActionHandler.deleteMenuItem(request, out);
                default ->
                    ErrorHandler.handleInvalidAction(out);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, out);
        }
    }
}
