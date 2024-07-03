package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

public class EmployeeServerController implements ClientRequestHandler {

    private static final String STORE_SELECTED_ITEMS_SQL = "UPDATE rollout_menu_items SET vote = vote + 1 WHERE menu_item_id = ? AND rollout_date = ?";
    private static final String INSERT_USER_SELECTION_SQL = "INSERT INTO user_vote (user_id, vote_date) VALUES (?, ?)";
    private static final String CHECK_USER_VOTE_SQL = "SELECT COUNT(*) FROM user_vote WHERE user_id = ? AND vote_date = ?";
    private static final String GET_USER_PREFERENCE_SQL = "SELECT food_type_id, sweetTooth, food_preference_id, food_taste_id FROM users WHERE user_id = ?";
    private static final String NEXT_DAY_MENU_ITEM_SQL = "SELECT rmi.rollout_item_id, mi.menu_item_id, mi.name, mi.price, mi.rating, mi.sentiments, rmi.rollout_date, "
            + "ft.food_type_name AS food_type_name, fp.food_preference_name AS food_preference_name, ft1.spice_level AS spice_level, mi.sweetTooth "
            + "FROM rollout_menu_items rmi "
            + "JOIN menu_items mi ON rmi.menu_item_id = mi.menu_item_id "
            + "JOIN food_type ft ON mi.food_type_id = ft.id "
            + "JOIN food_preference fp ON mi.food_preference_id = fp.id "
            + "JOIN food_taste ft1 ON mi.food_taste_id = ft1.id "
            + "WHERE rmi.meal_type_id = ? AND rmi.rollout_date = ? "
            + "ORDER BY "
            + "CASE WHEN mi.food_type_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.food_preference_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.food_taste_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.sweetTooth = ? THEN 1 ELSE 0 END DESC";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        try {
            switch (action) {
                case "showRollOutMenuItems" ->
                    showRollOutMenuItems(jsonData, out);
                case "processSelectedItems" ->
                    processSelectedItems(jsonData, out);
                case "checkUserVote" ->
                    checkUserVote(jsonData, out);
                default ->
                    out.println("Invalid menu action");
            }
        } catch (Exception e) {
            handleError(out, "Unexpected error occurred", e);
        }
    }

    private void showRollOutMenuItems(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        String userId = (String) jsonData.get("userId");
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); PreparedStatement userPrefStmt = conn.prepareStatement(GET_USER_PREFERENCE_SQL); PreparedStatement menuStmt = conn.prepareStatement(NEXT_DAY_MENU_ITEM_SQL)) {

            userPrefStmt.setInt(1, Integer.parseInt(userId));
            ResultSet userPrefRs = userPrefStmt.executeQuery();

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
                    formatAndSendMenuItems(out, rs);
                }
            } else {
                out.println("User preferences not found");
                out.println("END_OF_RESPONSE");
                out.flush();
            }

        } catch (SQLException e) {
            handleError(out, "Error retrieving menu items", e);
        }
    }

    private void formatAndSendMenuItems(PrintWriter out, ResultSet rs) throws SQLException {
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
            }
        } else {
            out.println("Currently, the menu item list is not prepared");
        }

        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private void processSelectedItems(JSONObject jsonData, PrintWriter out) {
        JSONObject selectedItems = (JSONObject) jsonData.get("selectedItems");
        String userId = (String) jsonData.get("userId");

        try (Connection conn = Database.getConnection()) {
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
            handleError(out, "Error processing selected items", e);
        }
    }

    private void incrementVoteForItem(Connection conn, int itemId) throws SQLException {
        Date voteDate = new Date(System.currentTimeMillis());
        try (PreparedStatement stmt = conn.prepareStatement(STORE_SELECTED_ITEMS_SQL)) {
            stmt.setInt(1, itemId);
            stmt.setDate(2, voteDate);
            stmt.executeUpdate();
        }
    }

    private void insertUserVote(Connection conn, String userId, PrintWriter out) throws SQLException {
        Date voteDate = new Date(System.currentTimeMillis());

        try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_SELECTION_SQL)) {
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

    private void checkUserVote(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(CHECK_USER_VOTE_SQL)) {

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            boolean hasVoted = rs.next() && rs.getInt(1) > 0;
            out.println(hasVoted);
            out.println("END_OF_RESPONSE");
            out.flush();

        } catch (SQLException e) {
            handleError(out, "Error checking user vote", e);
        }
    }

    private void handleError(PrintWriter out, String errorMessage, Exception e) {
        out.println(errorMessage + " : " + e.getMessage());
        out.println("END_OF_RESPONSE");
        out.flush();
    }
}
