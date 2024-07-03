package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.json.simple.JSONObject;

public class NotificationServiceController implements ClientRequestHandler {

    private static final String ADD_NOTIFICATION_QUERY = "INSERT INTO notifications (menu_item_id, message_id, notification_date) VALUES (?, ?, ?)";
    private static final String VIEW_NOTIFICATIONS_QUERY
            = "SELECT n.menu_item_id, m.name, m.price, m.availability_status, nm.message "
            + "FROM notifications n JOIN menu_items m ON n.menu_item_id = m.menu_item_id "
            + "JOIN notifiedMessages nm ON n.message_id = nm.Id "
            + "WHERE n.notification_date = ?";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "viewNotification" -> viewNotifications(out);
            default -> out.println("Invalid request");
        }
    }

    public void addNotification(int menuItemId, int messageId, PrintWriter out) {
        Date currentDate = new Date(System.currentTimeMillis());
        try (Connection conn = Database.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(ADD_NOTIFICATION_QUERY)) {

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
            out.println("Error adding notification: " + e.getMessage());
        }
    }

    public void viewNotifications(PrintWriter out) {
        LocalDate currentDate = LocalDate.now();
        try (Connection conn = Database.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(VIEW_NOTIFICATIONS_QUERY)) {

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
            out.println("Error viewing notifications: " + e.getMessage());
        }
    }
}
