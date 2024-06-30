package finalproject;

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

public class EmployeeServer implements ClientRequestHandler {

    private static final String STORE_SELECTED_ITEMS_SQL = "UPDATE rollout_menu_items SET vote = vote + 1 WHERE menu_item_id = ? AND rollout_date = ? ";
    private static final String INSERT_USER_SELECTION_SQL = "INSERT INTO user_vote (user_id, vote_date) VALUES (?, ?)";
    private static final String CHECK_USER_VOTE_SQL = "SELECT COUNT(*) FROM user_vote WHERE user_id = ? AND vote_date = ?";
    private static final String NEXT_DAY_MENU_ITEM_SQL
            = "SELECT rmi.rollout_item_id, mi.menu_item_id, mi.name, mi.price, mi.rating, mi.sentiments, rmi.rollout_date "
            + "FROM rollout_menu_items rmi "
            + "JOIN menu_items mi ON rmi.menu_item_id = mi.menu_item_id "
            + "WHERE rmi.meal_type_id = ? AND rmi.rollout_date = ?";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");
        System.out.println("Handling request: " + action);

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
    }

    private void showRollOutMenuItems(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(NEXT_DAY_MENU_ITEM_SQL)) {

            stmt.setInt(1, mealType);
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            formatAndSendMenuItems(out, rs);

        } catch (SQLException e) {
            handleError(out, "Error retrieving menu items", e);
        }
    }

    private void formatAndSendMenuItems(PrintWriter out, ResultSet rs) throws SQLException {
        out.printf("%-10s%-20s%-15s%-10s%-20s\n", "ID", "Meal Item", "Price", "Rating", "Sentiments");

        while (rs.next()) {
            int itemId = rs.getInt("menu_item_id");
            String name = rs.getString("name");
            float price = rs.getFloat("price");
            int rating = rs.getInt("rating");
            String sentiments = rs.getString("sentiments");

            out.printf("%-10d%-20s%-15.2f%-10d%-20s\n", itemId, name, price, rating, sentiments);
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
            stmt.setDate(2,voteDate);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Error incrementing vote for item", e);
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

        } catch (SQLException e) {
            throw new SQLException("Error inserting user vote", e);
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
            out.println(hasVoted + "\n");
            out.println("END_OF_RESPONSE");

            out.flush();

        } catch (SQLException e) {
            handleError(out, "Error checking user vote", e);
        }
    }

    private void handleError(PrintWriter out, String errorMessage, SQLException e) {
        out.println(errorMessage);
        out.println("END_OF_RESPONSE");
        out.flush();
        e.getMessage();
    }
}
