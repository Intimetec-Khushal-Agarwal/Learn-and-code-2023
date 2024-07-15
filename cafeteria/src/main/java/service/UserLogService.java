package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;
import serverconstant.QueryConstants;

public class UserLogService {

    @SuppressWarnings("unchecked")
    public void insertUserLogs(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        String loginTimeString = (String) jsonData.get("loginTime");
        Timestamp loginTime = convertStringToTimestamp(loginTimeString);
        Instant userLogoutTime = Instant.now();
        List<String> operationsList = (List<String>) jsonData.get("operations");
        String operations = String.join(", ", operationsList);

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.INSERT_LOG)) {

            stmt.setString(1, userId);
            stmt.setTimestamp(2, loginTime);
            stmt.setTimestamp(3, Timestamp.from(userLogoutTime));
            stmt.setString(4, operations);
            stmt.executeUpdate();

            out.println("User logged out successfully");
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error inserting user logs");

        }
    }

    private Timestamp convertStringToTimestamp(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String[] parts = timeString.split("\\.");
        LocalDateTime time = LocalDateTime.parse(parts[0], formatter);

        if (parts.length > 1) {
            String fractionalSeconds = parts[1];
            fractionalSeconds = String.format("%-9s", fractionalSeconds).replace(' ', '0');
            time = time.plusNanos(Long.parseLong(fractionalSeconds));
        }

        return Timestamp.valueOf(time);
    }

    public void showUserLogs(PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.SHOW_USER_LOGS); ResultSet rs = stmt.executeQuery()) {

            out.println("User Logs:");
            out.printf("%-7s%-10s%-15s%-10s%-25s%-25s%-200s\n", "Log Id", "UserId", "Name", "Role", "loginTime", "LogoutTime", "Operations");
            out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int logId = rs.getInt("log_id");
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String role = rs.getString("role_name");
                Timestamp loginTime = rs.getTimestamp("login_time");
                Timestamp logoutTime = rs.getTimestamp("logout_time");
                String operations = rs.getString("operations");

                out.printf("%-7s%-10s%-15s%-10s%-25s%-25s%-200s\n", logId, userId, name, role, loginTime, logoutTime, operations);
            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error showing user logs");

        }
    }
}
