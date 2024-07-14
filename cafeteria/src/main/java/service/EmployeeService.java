package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import server.ErrorHandler;

public class EmployeeService {

    public void showRollOutMenuItems(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        String userId = (String) jsonData.get("userId");
        Date currentDate = new Date(System.currentTimeMillis());

        System.out.println(mealType + "  " + userId + "  " + currentDate);

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement userPrefStmt = conn.prepareStatement(QueryConstants.GET_USER_PREFERENCES); PreparedStatement menuStmt = conn.prepareStatement(QueryConstants.GET_NEXT_DAY_MENU_ITEMS)) {

            userPrefStmt.setInt(1, Integer.parseInt(userId));
            ResultSet userPrefRs = userPrefStmt.executeQuery();

            System.out.println("Inside showRollOutMenuItems");
            if (userPrefRs.next()) {
                int foodTypeId = userPrefRs.getInt("food_type_id");
                int foodPreferenceId = userPrefRs.getInt("food_preference_id");
                int foodTasteId = userPrefRs.getInt("food_taste_id");
                boolean sweetTooth = userPrefRs.getBoolean("sweetTooth");

                menuStmt.setInt(1, mealType);
                menuStmt.setDate(2, currentDate);
                menuStmt.setInt(3, foodTypeId);
                menuStmt.setInt(4, foodPreferenceId);
                menuStmt.setInt(5, foodTasteId);
                menuStmt.setBoolean(6, sweetTooth);

                try (ResultSet rs = menuStmt.executeQuery()) {
                    System.out.println("Inside result set");
                    formatAndSendMenuItems(out, rs);
                }
            } else {
                out.println("User preferences not found");
                out.println("END_OF_RESPONSE");
                out.flush();
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error retrieving menu items");
        }
    }

    private void formatAndSendMenuItems(PrintWriter out, ResultSet rs) throws SQLException {
        System.out.println("Inside format method");
        if (rs.isBeforeFirst()) {
            out.printf("%-10s%-20s%-15s%-10s%-20s%-20s%-20s%-15s%-10s%n",
                    "ID", "Meal Item", "Price", "Rating", "Sentiments",
                    "Food Type", "Food Preference", "Spice Level", "Sweet Tooth");

            while (rs.next()) {
                int itemId = rs.getInt("menu_item_id");
                String name = rs.getString("name");
                float price = rs.getFloat("price");
                int rating = rs.getInt("rating");
                String sentiments = rs.getString("sentiments");
                String foodTypeName = rs.getString("food_type_name");
                String foodPreferenceName = rs.getString("food_preference_name");
                String spiceLevel = rs.getString("spice_level");
                boolean sweetTooth = rs.getBoolean("sweetTooth");

                out.printf("%-10d%-20s%-15.2f%-10d%-20s%-20s%-20s%-15s%-10b%n",
                        itemId, name, price, rating, sentiments,
                        foodTypeName, foodPreferenceName, spiceLevel, sweetTooth);

                System.out.printf("%-10d%-20s%-15.2f%-10d%-20s%-20s%-20s%-15s%-10b%n",
                        itemId, name, price, rating, sentiments,
                        foodTypeName, foodPreferenceName, spiceLevel, sweetTooth);
            }
        } else {
            out.println("Currently, the menu item list is not prepared");
        }

        out.println("END_OF_RESPONSE");
        out.flush();
    }

    public void processSelectedItems(JSONObject jsonData, PrintWriter out) {
        JSONObject selectedItems = (JSONObject) jsonData.get("selectedItems");
        String userId = (String) jsonData.get("userId");

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Object key : selectedItems.keySet()) {
                String mealType = (String) key;
                @SuppressWarnings("unchecked")
                List<String> itemIds = (List<String>) selectedItems.get(mealType);
                List<Integer> intItemIds = itemIds.stream().map(Integer::parseInt).collect(Collectors.toList());

                for (int itemId : intItemIds) {
                    incrementVoteForItem(conn, itemId);
                }
            }
            insertUserVote(conn, userId, out);

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error processing selected items");

        }
    }

    private void incrementVoteForItem(Connection conn, int itemId) throws SQLException {
        Date voteDate = new Date(System.currentTimeMillis());
        try (PreparedStatement stmt = conn.prepareStatement(QueryConstants.STORE_SELECTED_ITEMS)) {
            stmt.setInt(1, itemId);
            stmt.setDate(2, voteDate);
            stmt.executeUpdate();
        }
    }

    private void insertUserVote(Connection conn, String userId, PrintWriter out) throws SQLException {
        Date voteDate = new Date(System.currentTimeMillis());

        try (PreparedStatement stmt = conn.prepareStatement(QueryConstants.INSERT_USER_VOTE)) {
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, voteDate);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("Thank you for voting");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        }
    }

    public void checkUserVote(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.CHECK_USER_VOTE)) {

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            boolean hasVoted = rs.next() && rs.getInt(1) > 0;
            out.println(hasVoted);
            out.println("END_OF_RESPONSE");
            out.flush();

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error checking user vote");
        }
    }
}
