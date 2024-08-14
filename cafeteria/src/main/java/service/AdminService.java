package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;
import serverconstant.QueryConstants;

public class AdminService {

    public void addMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.ADD_MENU_ITEM, Statement.RETURN_GENERATED_KEYS)) {

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
            ErrorHandler.handleSQLException(e, out, "Error adding menu item");
        }
    }

    public void updateMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.UPDATE_MENU_ITEM)) {

            stmt.setFloat(1, ((Double) request.get("price")).floatValue());
            stmt.setString(2, (String) request.get("status"));
            stmt.setInt(3, ((Long) request.get("id")).intValue());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                NotificationService notificationService = new NotificationService();
                notificationService.addNotification(((Long) request.get("id")).intValue(), 2, out);
                out.println("Menu item updated successfully");
            } else {
                out.println("Failed to update menu item");
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error updating menu item");
        }
    }

    public void deleteMenuItem(JSONObject request, PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.DELETE_MENU_ITEM)) {

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
            ErrorHandler.handleSQLException(e, out, "Error deleting menu item");
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
                NotificationService notificationService = new NotificationService();
                notificationService.addNotification(menuItemId, 1, out);
                out.println(successMessage);
            } else {
                out.println(failureMessage);
            }
        }
    }
}
