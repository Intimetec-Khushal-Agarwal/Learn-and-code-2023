package finalproject;

import java.io.IOException;
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

public class AuthorizationService implements ClientRequestHandler {

    private static final String LOGIN_QUERY = "SELECT role_id FROM users WHERE user_id = ? AND name = ?";
    private static final String LOGS_QUERY = "INSERT INTO user_login_logs(user_id, login_time, logout_time, operations) VALUES (?, ?, ?, ?)";
    private static final String SHOW_USER_LOGS_QUERY = "SELECT l.log_id, l.user_id, u.name, r.role_name, l.login_time, l.logout_time, l.operations FROM user_login_logs l JOIN users u ON l.user_id = u.user_id JOIN roles r ON u.role_id = r.role_id ORDER BY l.log_id ASC";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");
        System.out.println("Handling request: " + action);
        switch (action) {
            case "login" ->
                login(jsonData, out);
            case "userLogs" ->
                insertUserLogs(jsonData, out);
            case "showUserLogs" ->
                showUserLogs(out);
            default ->
                out.println("Invalid menu action");
        }
    }

    @SuppressWarnings("unchecked")
    private void login(JSONObject jsonData, PrintWriter out) {
        String employeeId = (String) jsonData.get("userId");
        String name = (String) jsonData.get("name");

        System.out.println("employeeId " + employeeId);
        System.out.println("name" + name);
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(LOGIN_QUERY);
            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Inside rsNext login");
                int roleId = rs.getInt("role_id");
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("role", roleId);
                System.out.println(response.toJSONString());
                out.println(response.toJSONString());
            } else {
                JSONObject response = new JSONObject();
                response.put("status", "fail");
                out.println(response.toJSONString());
            }
            out.flush();
        } catch (SQLException e) {
            JSONObject response = new JSONObject();
            response.put("status", "error");
            response.put("message", e.getMessage());
            out.println(response.toJSONString());
        }
    }

    @SuppressWarnings("unchecked")
    private void insertUserLogs(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        String loginTimeString = (String) jsonData.get("loginTime");
        Timestamp loginTime = convertStringToTimestamp(loginTimeString);
        System.out.println(loginTime);
        Instant userLogoutTime = Instant.now();
        List<String> operationsList = (List<String>) jsonData.get("operations");
        String operations = String.join(", ", operationsList);

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(LOGS_QUERY);
            stmt.setString(1, userId);
            stmt.setTimestamp(2, loginTime);
            stmt.setTimestamp(3, Timestamp.from(userLogoutTime));
            stmt.setString(4, operations);
            //int rowsInserted = stmt.executeUpdate();
            stmt.executeUpdate();

            // JSONObject response = new JSONObject();
            // if (rowsInserted > 0) {
            //     response.put("status", "success");
            // } else {
            //     response.put("status", "fail");
            // }
            // out.println(response.toJSONString());
        } catch (SQLException e) {
            JSONObject response = new JSONObject();
            response.put("status", "error");
            response.put("message", e.getMessage());
            out.println(response.toJSONString());
        }
    }

    private Timestamp convertStringToTimestamp(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String[] parts = timeString.split("\\.");
        LocalDateTime time = LocalDateTime.parse(parts[0], formatter);
    
        if (parts.length > 1) {
            String fractionalSeconds = parts[1];
            fractionalSeconds = String.format("%-9s", fractionalSeconds).replace(' ', '0'); // Pad to nanoseconds
            time = time.plusNanos(Long.parseLong(fractionalSeconds));
        }
    
        return Timestamp.valueOf(time);
    }
    private void showUserLogs(PrintWriter out) {

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(SHOW_USER_LOGS_QUERY); ResultSet rs = stmt.executeQuery()) {
  
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
            out.println("Error showing user logs: " + e.getMessage());
        }
    }
}
