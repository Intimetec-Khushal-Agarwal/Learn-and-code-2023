package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseEventScheduler {

    public static void enableEventScheduler() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SHOW VARIABLES LIKE 'event_scheduler'");
            if (!stmt.getResultSet().next()) {
                return;
            }
            String eventSchedulerStatus = stmt.getResultSet().getString("Value");
            if (!eventSchedulerStatus.equalsIgnoreCase("ON")) {
                String enableScheduler = "SET GLOBAL event_scheduler = ON;";
                stmt.execute(enableScheduler);
            }
        } catch (SQLException e) {
            System.out.println("Failed to enable Event Scheduler: " + e.getMessage());
        }
    }

    public static void createScheduledEvent() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            String createEvent = "CREATE EVENT IF NOT EXISTS daily_cleanup "
                    + "ON SCHEDULE EVERY 1 DAY "
                    + "STARTS CURRENT_TIMESTAMP "
                    + "DO "
                    + "BEGIN "
                    + "DELETE FROM feedbacks WHERE feedback_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM rollout_menu_items WHERE rollout_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM user_vote WHERE vote_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM notifications WHERE notification_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM prepared_menu WHERE prepared_date < NOW() - INTERVAL 1 MONTH; "
                    + "END;";
            stmt.execute(createEvent);
            System.out.println("Scheduled event created.");
        } catch (SQLException e) {
            System.out.println("Failed to create scheduled event: " + e.getMessage());
        }
    }
}
