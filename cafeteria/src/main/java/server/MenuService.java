package server;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

public class MenuService {

    private static final String SHOW_MENU_QUERY
            = "SELECT DISTINCT menu_item_id, name, price, availability_status, menu_types.meal_type, sentiments "
            + "FROM menu_items "
            + "RIGHT JOIN menu_types ON menu_items.meal_type_id = menu_types.meal_type_id ORDER BY menu_item_id";

    private static final String ADD_MENU_ITEM_QUERY
            = "INSERT INTO menu_items (name, price, rating, meal_type_id,food_type_id,food_taste_id,food_preference_id,sweetTooth)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_MENU_ITEM_QUERY
            = "UPDATE menu_items SET price = ?, availability_status = ? WHERE menu_item_id = ?";

    private static final String DELETE_MENU_ITEM_QUERY
            = "DELETE FROM menu_items WHERE menu_item_id = ?";

    public void showMenuItems(PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(SHOW_MENU_QUERY); ResultSet rs = stmt.executeQuery()) {

            out.println("Menu Items:");
            out.printf("%-10s%-35s%-20s%-10s%-15s%-15s\n",
                    "itemId", "name", "price", "status", "mealType", "sentiments");
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
            out.println("END_OF_RESPONSE\n");
            out.flush();
        } catch (SQLException e) {
            handleSQLException(e, out, "Error showing menu items");
        }
    }

    public void addMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(ADD_MENU_ITEM_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            setMenuItemDetails(stmt, request);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                handleGeneratedKeys(stmt, out, "Menu item added successfully", "Menu item added, but failed to retrieve menu item ID");
            } else {
                out.println("Failed to add menu item");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            handleSQLException(e, out, "Error adding menu item");
        }
    }

    public void updateMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_MENU_ITEM_QUERY)) {

            stmt.setFloat(1, ((Double) request.get("price")).floatValue());
            stmt.setString(2, (String) request.get("status"));
            stmt.setInt(3, ((Long) request.get("id")).intValue());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                NotificationServiceController notificationService = new NotificationServiceController();
                notificationService.addNotification(((Long) request.get("id")).intValue(), 2, out);
                out.println("Menu item updated successfully");
            } else {
                out.println("Failed to update menu item");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            handleSQLException(e, out, "Error updating menu item");
        }
    }

    public void deleteMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_MENU_ITEM_QUERY)) {

            stmt.setInt(1, ((Long) request.get("id")).intValue());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                out.println("Menu item deleted successfully");
            } else {
                out.println("Failed to delete menu item");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            handleSQLException(e, out, "Error deleting menu item");
        }
    }

    private void setMenuItemDetails(PreparedStatement stmt, JSONObject request) throws SQLException {
        stmt.setString(1, (String) request.get("name"));
        stmt.setFloat(2, ((Double) request.get("price")).floatValue());
        stmt.setInt(3, ((Long) request.get("rating")).intValue());
        stmt.setInt(4, ((Long) request.get("mealType")).intValue());
        stmt.setInt(5, ((Long) request.get("foodType")).intValue());
        stmt.setInt(6, ((Long) request.get("foodTaste")).intValue());
        stmt.setInt(7, ((Long) request.get("foodPreference")).intValue());
        stmt.setInt(8, ((Long) request.get("sweetTooth")).intValue());
    }

    private void handleGeneratedKeys(PreparedStatement stmt, PrintWriter out, String successMessage, String failureMessage) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int menuItemId = generatedKeys.getInt(1);
                NotificationServiceController notificationService = new NotificationServiceController();
                notificationService.addNotification(menuItemId, 1, out);
                out.println(successMessage);
            } else {
                out.println(failureMessage);
            }
        }
    }

    private void handleSQLException(SQLException e, PrintWriter out, String message) {
        out.println(message + ": " + e.getMessage());
        System.err.println(message + ": " + e.getMessage());
    }
}
