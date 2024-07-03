package server;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONObject;

public class DiscardMenuItemService implements ClientRequestHandler {

    private final MenuService menuService;

    public DiscardMenuItemService() {
        this.menuService = new MenuService();
    }

    private static final String SHOW_ELIGIBLE_DISCARD_ITEMS_QUERY = "SELECT mi.menu_item_id, mi.name, mi.rating, mi.sentiments, mi.sentiment_score, mt.meal_type AS meal_type_name "
            + "FROM menu_items mi JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
            + "WHERE mi.rating <= 2 AND mi.sentiment_score <= 50";

    private static final String STORE_DISCARD_MENU_QUERY = "INSERT INTO discard_items (menu_item_id, discard_date, message_id, name) VALUES (?, CURRENT_DATE, ?, (SELECT name FROM menu_items WHERE menu_item_id = ?))";

    private static final String GET_DISCARD_MENU_ITEM_LIST = "SELECT discard_date, menu_item_id, message_id FROM discard_items ORDER BY discard_date DESC";
    private static final String GET_LATEST_DISCARDED_ITEM_QUERY = "SELECT di.menu_item_id, discard_item_name, di.discard_date, di.message_id, nm.message "
            + "FROM discard_items di "
            + "LEFT JOIN menu_items mi ON di.menu_item_id = mi.menu_item_id "
            + "LEFT JOIN notifiedmessages nm ON di.message_id = nm.id "
            + "ORDER BY di.discard_date DESC "
            + "LIMIT 1";

    @Override
    public void handleRequest(JSONObject request, PrintWriter out) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "discardMenuItem" ->
                showDiscardMenuItemList(out);
            case "storeDiscardedItem" ->
                storeDiscardMenuItem(request, out);
            case "showDiscardMenuItems" ->
                showDiscardMenuItems(out);
            default ->
                out.println("Invalid menu action");
        }
    }

    @SuppressWarnings("unchecked")
    private void showDiscardMenuItemList(PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(SHOW_ELIGIBLE_DISCARD_ITEMS_QUERY); ResultSet rs = stmt.executeQuery()) {
            JSONObject jsonResponse = new JSONObject();
            if (rs.isBeforeFirst()) {
                out.printf("%-15s%-35s%-10s%-15s%-20s%-20s\n", "MenuItemID", "Name", "Rating", "Sentiments", "Sentiment Score", "MealType");
                out.println("-----------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    int menuItemId = rs.getInt("menu_item_id");
                    String name = rs.getString("name");
                    double rating = rs.getDouble("rating");
                    String sentiments = rs.getString("sentiments");
                    double sentimentScore = rs.getDouble("sentiment_score");
                    String mealTypeName = rs.getString("meal_type_name");

                    out.printf("%-15d%-35s%-10.2f%-15s%-20.2f%-20s\n", menuItemId, name, rating, sentiments, sentimentScore, mealTypeName);
                }
                out.println("END_OF_RESPONSE");
                jsonResponse.put("status", "success");
                jsonResponse.put("date", getDiscardMenuItemList());

            } else {
                jsonResponse.put("status", "fail");
                out.println("No discarded menu items found.");
                out.println("END_OF_RESPONSE");
            }
            String jsonData = jsonResponse.toJSONString();
            out.println(jsonData + "\n");
            out.flush();

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    private void storeDiscardMenuItem(JSONObject request, PrintWriter out) {
        int menuItemId = ((Long) request.get("id")).intValue();
        int messageId = ((Long) request.get("messageId")).intValue();

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(STORE_DISCARD_MENU_QUERY)) {

            stmt.setInt(1, menuItemId);
            stmt.setInt(2, messageId);
            stmt.setInt(3, menuItemId);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0 && messageId == 3) {
                menuService.deleteMenuItem(request, out);
            } else if (rowsInserted > 0 && messageId == 4) {
                out.println("Item added for feedback response");
            } else {
                out.println("Failed to add discard menu item");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public Date getDiscardMenuItemList() {
        Date currentDate = null;
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_DISCARD_MENU_ITEM_LIST); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                currentDate = rs.getDate("discard_date");
                return currentDate;
            }

        } catch (SQLException e) {
            e.getMessage();
        }

        return currentDate;
    }

    @SuppressWarnings("unchecked")
    private void showDiscardMenuItems(PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_LATEST_DISCARDED_ITEM_QUERY); ResultSet rs = stmt.executeQuery()) {

            JSONObject jsonResponse = new JSONObject();
            if (rs.isBeforeFirst()) {
                out.printf("%-15s%-20s%-18s%-70s\n", "MenuItemID", "Name", "discard_date", "message");
                out.println("-----------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    int menuItemId = rs.getInt("menu_item_id");
                    String name = rs.getString("discard_item_name");
                    String date = rs.getDate("discard_date").toString();
                    String message = rs.getString("message");

                    String[] parts = message.split("\\?");
                    String text = message;
                    if (parts.length > 1) {
                        text = "Answer the below question";
                    }

                    out.printf("%-15s%-20s%-18s%-70s\n", menuItemId, name, date, text);
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", message);
                }
            } else {
                jsonResponse.put("status", "fail");
            }

            out.println("END_OF_RESPONSE");
            out.println(jsonResponse.toJSONString());
            out.flush();
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
