package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

class AdminService<JsonData> implements ClientRequestHandler {

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {

        String action = (String) jsonData.get("requestType");
        System.out.println("Inside handle request" + action);
        switch (action) {
            case "showMenu" ->
                showMenuItems(jsonData, out);
            case "addMenuItem" ->
                addMenuItem(jsonData, out);
            case "updateMenuItem" ->
                updateMenuItem(jsonData, out);
            case "deleteMenuItem" ->
                deleteMenuItem(jsonData, out);
            default ->
                out.println("Invalid menu action");
        }
    }

    @SuppressWarnings("hiding")
    public <JsonData> void showMenuItems(JsonData jsonData, PrintWriter out) {

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT menu_item_id, name, price, availability_status, menu_types.meal_type, sentiments "
                    + "FROM menu_items "
                    + "RIGHT JOIN menu_types ON menu_items.meal_type_id = menu_types.meal_type_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                out.println("Menu Items:");
                out.printf("%-10s%-35s%-20s%-10s%-15s%-15s\n", "itemId", "name", "price", "status", "mealType", "sentiments");
                out.println("---------------------------------------------------------------------------------");

                while (rs.next()) {
                    int itemId = rs.getInt("menu_item_id");
                    String name = rs.getString("name");
                    float price = rs.getFloat("price");
                    String status = rs.getString("availability_status");
                    String mealType = rs.getString("meal_type");
                    String sentiments = rs.getString("sentiments");

                    out.printf("%-10d%-35s%-20.2f%-10s%-15s%-15s\n", itemId, name, price, status, mealType, sentiments);
                }
                out.println("\n");
                out.flush();
            } catch (SQLException e) {
                out.println("Error showing menu item: " + e.getMessage());
            }
        } catch (SQLException e) {
            out.println("Database connection error: " + e.getMessage());
        }

    }

    private void addMenuItem(JSONObject jsonData, PrintWriter out) throws IOException {
        String itemName = (String) jsonData.get("name");
        float itemPrice = ((Double) jsonData.get("price")).floatValue();
        String itemStatus = (String) jsonData.get("status");
        int rating = ((Long) jsonData.get("rating")).intValue();
        int mealType = ((Long) jsonData.get("mealType")).intValue();
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO menu_items (name, price, availability_status,rating,meal_type_id) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, itemName);
            stmt.setFloat(2, itemPrice);
            stmt.setString(3, itemStatus);
            stmt.setInt(4, rating);
            stmt.setInt(5, mealType);

            int rowsInserted = stmt.executeUpdate();
            System.out.println("rowIs inserted " + rowsInserted);
            if (rowsInserted > 0) {
                out.println("Menu item added successfully\n");
            } else {
                out.println("Failed to add menu item\n");
            }
        } catch (SQLException e) {
            out.println("Error adding menu item");
        }
    }

    private void updateMenuItem(JSONObject jsonData, PrintWriter out) throws IOException {
        int itemId = ((Long) jsonData.get("id")).intValue();
        float itemPrice = ((Double) jsonData.get("price")).floatValue();
        String itemStatus = (String) jsonData.get("status");

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE menu_items SET price = ?, availability_status = ? WHERE menu_item_id = ?");
            stmt.setFloat(1, itemPrice);
            stmt.setString(2, itemStatus);
            stmt.setInt(3, itemId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("Menu item updated successfully");
            } else {
                out.println("Failed to update menu item");
            }
        } catch (SQLException e) {
            out.println("Error updating menu item");
        }
    }

    private void deleteMenuItem(JSONObject jsonData, PrintWriter out) throws IOException {
        int itemId = ((Long) jsonData.get("id")).intValue();
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM menu_items WHERE menu_item_id = ?");
            stmt.setInt(1, itemId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                out.println("Menu item deleted successfully");
            } else {
                out.println("Failed to delete menu item");
            }
        } catch (SQLException e) {
            out.println("Error deleting menu item");
        }
    }

    // @SuppressWarnings("unchecked")
    // public void showRollOutMenuItems(JSONObject jsonData, PrintWriter out) {
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
    //         // Add a delimiter to indicate the end of the response
    //         out.println("---END OF MENU---");
    //         out.flush();
    //     } catch (SQLException e) {
    //         out.println("Error retrieving menu items");
    //         out.println("---END OF MENU---");
    //         out.flush();
    //         e.printStackTrace(); // For server-side logging
    //     }
    // }
    //@SuppressWarnings("unchecked")
    /*  private void selectMenuItem(JSONObject jsonData, PrintWriter out) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            out.println("Error checking user vote");
        }
    }

    @SuppressWarnings("unchecked")
    public void showRollOutMenuByVote(JSONObject jsonData, PrintWriter out) {
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

            out.println("Menu Items:");
            out.println("ID\tName\t\tPrice\t\tRating\t\tSentiments");
            out.println("---------------------------------------------------------");
            while (rs.next()) {
                int itemId = rs.getInt("rollout_item_id");
                String name = rs.getString("name");
                float price = rs.getFloat("price");
                int rating = rs.getInt("rating");
                String sentiments = rs.getString("sentiments");
                out.println(itemId + "\t" + name + "\t\t" + price + "\t" + rating + "\t" + sentiments);
            }
            out.println("---END OF MENU---");
            out.flush();
        } catch (SQLException e) {
            out.println("Error retrieving menu items");
            out.println("---END OF MENU---");
            out.flush();
            e.printStackTrace(); // For server-side logging
        }
    }

    @SuppressWarnings("unchecked")
    private void selectedMenuItem(JSONObject jsonData, PrintWriter out) {
        JSONObject selectedItems = (JSONObject) jsonData.get("selectedItems");

        try (Connection conn = Database.getConnection()) {
            for (Object key : selectedItems.keySet()) {
                String mealType = (String) key;
                List<String> itemIds = (List<String>) selectedItems.get(mealType);
                List<Integer> intItemIds = itemIds.stream().map(Integer::parseInt).collect(Collectors.toList());
                System.out.println(intItemIds);
                for (int itemId : intItemIds) {
                    storeIntoSelectionTable(conn, itemId, out);
                }
            }
        } catch (SQLException e) {
            out.println("Error selecting menu items");
            e.printStackTrace(out);
        }
    }

    private void storeIntoSelectionTable(Connection conn, int itemId, PrintWriter out) {
        String updateVoteSQL = "INSERT INTO selected_items(itemid,recommended_date) values";
        try (PreparedStatement stmt = conn.prepareStatement(updateVoteSQL)) {
            stmt.setInt(1, itemId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
            } else {
                out.println("Failed to vote menu item");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
