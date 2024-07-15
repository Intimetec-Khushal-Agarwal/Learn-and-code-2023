package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;

public class ChefService {

    public void showRolloutMenuByVote(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.SELECT_ROLLOUT_MENU_ITEMS)) {

            stmt.setInt(1, mealType);
            stmt.setDate(2, currentDate);

            try (ResultSet rs = stmt.executeQuery()) {
                List<String> menuItems = formatMenuItems(rs);
                sendResponse(out, menuItems, getRolloutMenuHeaders());
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error showing rollout menu item by vote");
        }
    }

    public void insertRolloutMenuItem(JSONObject jsonData, PrintWriter out) {
        int itemId = ((Long) jsonData.get("menu_item_id")).intValue();
        int mealId = ((Long) jsonData.get("meal_type_id")).intValue();
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.INSERT_ROLLOUT_MENU_ITEM)) {

            stmt.setInt(1, itemId);
            stmt.setDate(2, currentDate);
            stmt.setInt(3, mealId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error occurred in adding menu item");

        }
    }

    public void storeSelectedItemsInPreparedMenu(JSONObject jsonData, PrintWriter out) {
        JSONArray selectedItems = (JSONArray) jsonData.get("selectedItems");

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.INSERT_PREPARED_MENU_ITEM)) {

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
            ErrorHandler.handleSQLException(e, out, "Error In storing next day menu item to prepare");
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

            String formattedItem = String.format("%-8d| %-25s| %-8.2f| %-8.2f| %-26s| %-2d",
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

    private void sendBatchInsertionResponse(PrintWriter out, int rowsInserted) {
        out.println("Menu items added successfully: " + rowsInserted);
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private String getRolloutMenuHeaders() {
        return String.format("%-8s| %-25s| %-8s| %-8s| %-26s| %-2s",
                "Item Id", "Name", "Price", "Rating", "Sentiments", "Vote");
    }
}
