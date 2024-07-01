package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChefServer implements ClientRequestHandler {

    private static final String SELECT_ROLLOUT_MENU_ITEMS_QUERY
            = "SELECT rmi.rollout_item_id, mi.menu_item_id, mi.name, mi.price, mi.rating, mi.sentiments, rmi.rollout_date, rmi.vote "
            + "FROM rollout_menu_items rmi "
            + "JOIN menu_items mi ON rmi.menu_item_id = mi.menu_item_id "
            + "WHERE rmi.meal_type_id = ? AND rmi.rollout_date = ?";

    private static final String INSERT_ROLLOUT_MENU_ITEM_QUERY
            = "INSERT INTO rollout_menu_items (menu_item_id, rollout_date, meal_type_id) VALUES (?, ?, ?)";

    private static final String INSERT_PREPARED_MENU_QUERY
            = "INSERT INTO prepared_menu (menu_item_id, prepared_date) VALUES (?, CURRENT_DATE)";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");
        System.out.println("Handling request: " + action);

        switch (action) {
            case "showRolloutMenuByVote" ->
                showRollOutMenuByVote(jsonData, out);
            case "insertRollOutMenuItem" ->
                insertRollOutMenuItem(jsonData, out);
            case "storeSelectedItemsInPreparedMenu" ->
                storeSelectedItemsInPreparedMenu(jsonData, out);
            default -> {
                out.println("Invalid menu action");
                out.println("END_OF_RESPONSE");
                out.flush();
            }
        }
    }

    private void showRollOutMenuByVote(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(SELECT_ROLLOUT_MENU_ITEMS_QUERY);
            stmt.setInt(1, mealType);
            stmt.setDate(2, currentDate);

            ResultSet rs = stmt.executeQuery();
            List<String> menuItems = formatMenuItems(rs);
            sendResponse(out, menuItems, getRolloutMenuHeaders());

        } catch (SQLException e) {
            handleSQLException(out, e);
        }
    }

    private void insertRollOutMenuItem(JSONObject jsonData, PrintWriter out) {
        int itemId = ((Long) jsonData.get("menu_item_id")).intValue();
        int mealId = ((Long) jsonData.get("meal_type_id")).intValue();
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_ROLLOUT_MENU_ITEM_QUERY)) {
            stmt.setInt(1, itemId);
            stmt.setDate(2, currentDate);
            stmt.setInt(3, mealId);

            stmt.executeUpdate();
            out.flush();

        } catch (SQLException e) {
            out.println("Error occured in adding menu item");
            handleSQLException(out, e);
        }
    }

    private void storeSelectedItemsInPreparedMenu(JSONObject jsonData, PrintWriter out) {
        JSONArray selectedItems = (JSONArray) jsonData.get("selectedItems");

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_PREPARED_MENU_QUERY)) {
            for (Object itemObj : selectedItems) {
                JSONObject itemMap = (JSONObject) itemObj;
                JSONArray itemIdsArray = (JSONArray) itemMap.get("menu_item_id");

                for (Object itemIdObj : itemIdsArray) {
                    String itemId = (String) itemIdObj;
                    stmt.setInt(1, Integer.parseInt(itemId));
                    stmt.addBatch();
                }
            }
            int[] rowsInserted = stmt.executeBatch();
            sendBatchInsertionResponse(out, rowsInserted.length);
        } catch (SQLException e) {
            handleSQLException(out, e);
        }
    }

    private List<String> formatMenuItems(ResultSet rs) throws SQLException {
        List<String> menuItems = new ArrayList<>();
        while (rs.next()) {
            int menuItemId = rs.getInt("menu_item_id");
            String name = rs.getString("name");
            float price = rs.getFloat("price");
            float rating = rs.getFloat("rating");
            String sentiments = rs.getString("sentiments");
            int vote = rs.getInt("vote");

            String formattedItem = String.format("%-4d| %-25s| %-6.2f| %-6.2f| %-26s| %-2d",
                    menuItemId, name, price, rating, sentiments, vote);
            menuItems.add(formattedItem);
        }
        return menuItems;
    }

    private void sendResponse(PrintWriter out, List<String> items, String headers) {
        out.println(headers);
        items.forEach(out::println);
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    // private void sendInsertionResponse(PrintWriter out, int rowsInserted) {
    //     if (rowsInserted > 0) {
    //         out.println("Rollout menu item added successfully");
    //     } else {
    //         out.println("Failed to add rollout menu item");
    //     }
    //     out.println("END_OF_RESPONSE");
    //     out.flush();
    // }
    private void sendBatchInsertionResponse(PrintWriter out, int rowsInserted) {
        out.println("Prepared menu items added successfully: " + rowsInserted);
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private void handleSQLException(PrintWriter out, SQLException e) {
        out.println("Error processing request: " + e.getMessage());
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private String getRolloutMenuHeaders() {
        return String.format("%-4s| %-25s| %-6s| %-6s| %-26s| %-2s",
                "Item Id", "Name", "Price", "Rating", "Sentiments", "Vote");
    }
}
