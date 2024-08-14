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
    public void handleRequest(JSONObject request, PrintWriter socketWriter) {
        String action = (String) request.get("requestType");

        try {
            switch (action) {
                case "showMenu" ->
                    queryHandler.showMenuItems(socketWriter);
                case "addMenuItem" ->
                    menuActionHandler.addMenuItem(request, socketWriter);
                case "updateMenuItem" ->
                    menuActionHandler.updateMenuItem(request, socketWriter);
                case "deleteMenuItem" ->
                    menuActionHandler.deleteMenuItem(request, socketWriter);
                default ->
                    ErrorHandler.handleInvalidAction(socketWriter);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, socketWriter);
        }
    }
}
