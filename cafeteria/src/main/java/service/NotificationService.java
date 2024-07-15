package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import database.DatabaseConnection;
import error.ErrorHandler;

public class NotificationService {

    public void addNotification(int menuItemId, int messageId, PrintWriter out) {
        Date currentDate = new Date(System.currentTimeMillis());
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.ADD_NOTIFICATION)) {

            stmt.setInt(1, menuItemId);
            stmt.setInt(2, messageId);
            stmt.setDate(3, currentDate);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("Notification added successfully");
            } else {
                out.println("Failed to add notification");
            }
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error adding notification");

        }
    }

    public void viewNotifications(PrintWriter out) {
        LocalDate currentDate = LocalDate.now();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.VIEW_NOTIFICATIONS)) {

            stmt.setDate(1, Date.valueOf(currentDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    out.printf("%-10s%-35s%-20s%-10s%-15s\n", "ItemId", "Name", "Price", "Status", "Message");

                    do {
                        int menuItemId = rs.getInt("menu_item_id");
                        String name = rs.getString("name");
                        float price = rs.getFloat("price");
                        String status = rs.getString("availability_status");
                        String message = rs.getString("message");

                        out.printf("%-10d%-35s%-20.2f%-10s%-15s\n", menuItemId, name, price, status, message);
                    } while (rs.next());
                } else {
                    out.println("No New Notification for the day");
                }
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error viewing notifications");

        }
    }
}
