package finalproject;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

public class AdminServerController implements ClientRequestHandler {

    private final MenuService menuService;

    public AdminServerController() {
        this.menuService = new MenuService();
    }

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) throws IOException {
        String action = (String) request.get("requestType");

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
                out.println("Invalid menu action");
        }
    }
}
