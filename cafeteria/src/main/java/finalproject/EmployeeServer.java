package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

class EmployeeServer<JsonData> implements ClientRequestHandler {

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        System.out.println("Inside handle request");

        String action = (String) jsonData.get("requestType");
        System.out.println("Inside handle request" + action);
        switch (action) {
            case "selectMenuItems":
                selectMenuItem(jsonData, out);
                break;
            case "showRollOutMenuItems":
                showRollOutMenuItems(jsonData, out);
                break;
            case "processSelectedItems":
                processSelectedItems(jsonData, out);
                break;
            case "checkUserVote":
                checkUserVote(jsonData, out);
                break;
            default:
                out.println("Invalid menu action");
        }
    }

    @SuppressWarnings("unchecked")
    public void showRollOutMenuItems(JSONObject jsonData, PrintWriter out) {
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        System.out.println("mealType: " + mealType);

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT rollout_item_id, name, price, rating, sentiments,recommended_date FROM rollout_menu_items WHERE meal_type_id = ? and recommended_date = ?"
            );
            stmt.setInt(1, mealType);
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            out.printf("%-10s%-20s%-15s%-10s%-20s\n", "ID", "Meal Item", "Price", "Rating", "Sentiments");

            while (rs.next()) {
                int itemId = rs.getInt("rollout_item_id");
                String name = rs.getString("name");
                float price = rs.getFloat("price");
                int rating = rs.getInt("rating");
                String sentiments = rs.getString("sentiments");
                Date recommendedDate = rs.getDate("recommended_date");

                out.printf("%-10d%-20s%-15.2f%-10d%-20s%-15s\n", itemId, name, price, rating, sentiments, recommendedDate.toString());
            }
            // Add a delimiter to indicate the end of the response
            out.println("---END OF MENU---");
            out.flush();
        } catch (SQLException e) {
            out.println("Error retrieving menu items");
            out.println("---END OF MENU---");
            out.flush();
        }
    }

    @SuppressWarnings("unchecked")
    private void selectMenuItem(JSONObject jsonData, PrintWriter out) {
        List<String> itemIds = (List<String>) jsonData.get("itemIds");
        String employeeId = (String) jsonData.get("employeeId");

        try (Connection conn = Database.getConnection()) {
            for (String itemId : itemIds) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO selected_menu_items (menu_item_id, employee_id) VALUES (?, ?)");
                stmt.setInt(1, Integer.parseInt(itemId));
                stmt.setString(2, employeeId);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    out.println("Menu item " + itemId + " selected successfully");
                } else {
                    out.println("Failed to select menu item " + itemId);
                }
            }
        } catch (SQLException e) {
            out.println("Error selecting menu items");
            e.printStackTrace(out);
        }
    }

    @SuppressWarnings("unchecked")
    public void processSelectedItems(JSONObject jsonData, PrintWriter out) {
        JSONObject selectedItems = (JSONObject) jsonData.get("selectedItems");
        String userId = (String) jsonData.get("userId");
        try (Connection conn = Database.getConnection()) {
            for (Object key : selectedItems.keySet()) {
                String mealType = (String) key;
                List<String> itemIds = (List<String>) selectedItems.get(mealType);
                List<Integer> intItemIds = itemIds.stream().map(Integer::parseInt).collect(Collectors.toList());
                System.out.println(intItemIds);
                for (int itemId : intItemIds) {
                    incrementVoteForItem(conn, itemId, out);
                }
            }
            insertUserVote(conn, userId, out);
        } catch (SQLException e) {
            System.out.println("Error processing food items");
        }
    }

    private void incrementVoteForItem(Connection conn, int itemId, PrintWriter out) {
        String updateVoteSQL = "UPDATE rollout_menu_items SET vote = vote + 1 WHERE rollout_item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateVoteSQL)) {
            stmt.setInt(1, itemId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
            } else {
                out.println("Failed to vote menu item");
            }
        } catch (SQLException e) {
        }
    }

    private void insertUserVote(Connection conn, String userId, PrintWriter out) throws SQLException {
        java.sql.Date voteDate = new java.sql.Date(System.currentTimeMillis());
        String insertUserVoteSQL = "INSERT INTO user_vote (user_id, vote_date) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertUserVoteSQL)) {
            insertStmt.setInt(1, Integer.parseInt(userId));
            insertStmt.setDate(2, voteDate);
            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("Thankyou for voting");
            } else {

            }
        }

    }

    @SuppressWarnings("unchecked")
    private void checkUserVote(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        String checkVoteSQL = "SELECT * FROM user_vote WHERE user_id = ? AND vote_date = ?";

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(checkVoteSQL)) {

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            boolean hasVoted = false;
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    hasVoted = true;
                }
            }
            System.out.println(hasVoted);
            out.println(hasVoted + "\n");
            out.flush();
            // JSONObject response = new JSONObject();
            // response.put("hasVoted", hasVoted);
            // out.println(response.toJSONString() + "\n");
            // out.flush();
        } catch (SQLException e) {
            out.println("Error checking user vote");
        }
    }

    // @SuppressWarnings("unchecked")
    // public void showRollOutMenuByVote(JSONObject jsonData, PrintWriter out) {
    //     int mealType = ((Long) jsonData.get("mealType")).intValue();
    //     java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
    //     System.out.println("mealType: " + mealType);
    //     try (Connection conn = Database.getConnection()) {
    //         PreparedStatement stmt = conn.prepareStatement(
    //                 "SELECT rollout_item_id, name, price, rating, sentiments,recommended_date FROM rollout_menu_items WHERE meal_type_id = ? and recommended_date = ?"
    //         );
    //         stmt.setInt(1, mealType);
    //         stmt.setDate(2, currentDate);
    //         ResultSet rs = stmt.executeQuery();
    //         out.println("Menu Items:");
    //         out.println("ID\tName\t\tPrice\t\tRating\t\tSentiments");
    //         out.println("---------------------------------------------------------");
    //         while (rs.next()) {
    //             int itemId = rs.getInt("rollout_item_id");
    //             String name = rs.getString("name");
    //             float price = rs.getFloat("price");
    //             int rating = rs.getInt("rating");
    //             String sentiments = rs.getString("sentiments");
    //             out.println(itemId + "\t" + name + "\t\t" + price + "\t" + rating + "\t" + sentiments);
    //         }
    //         out.println("---END OF MENU---");
    //         out.flush();
    //     } catch (SQLException e) {
    //         out.println("Error retrieving menu items");
    //         out.println("---END OF MENU---");
    //         out.flush();
    //         e.printStackTrace(); // For server-side logging
    //     }
    // }
    // @SuppressWarnings("unchecked")
    // private void selectedMenuItem(JSONObject jsonData, PrintWriter out) {
    //     JSONObject selectedItems = (JSONObject) jsonData.get("selectedItems");
    //     try (Connection conn = Database.getConnection()) {
    //         for (Object key : selectedItems.keySet()) {
    //             String mealType = (String) key;
    //             List<String> itemIds = (List<String>) selectedItems.get(mealType);
    //             List<Integer> intItemIds = itemIds.stream().map(Integer::parseInt).collect(Collectors.toList());
    //             System.out.println(intItemIds);
    //             for (int itemId : intItemIds) {
    //                 storeIntoSelectionTable(conn, itemId, out);
    //             }
    //         }
    //     } catch (SQLException e) {
    //         out.println("Error selecting menu items");
    //         e.printStackTrace(out);
    //     }
    // }
    // private void storeIntoSelectionTable(Connection conn, int itemId, PrintWriter out) {
    //     String updateVoteSQL = "INSERT INTO selected_items(itemid,recommended_date) values";
    //     try (PreparedStatement stmt = conn.prepareStatement(updateVoteSQL)) {
    //         stmt.setInt(1, itemId);
    //         int rowsUpdated = stmt.executeUpdate();
    //         if (rowsUpdated > 0) {
    //         } else {
    //             out.println("Failed to vote menu item");
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
}
