package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

class AuthorizationService implements ClientRequestHandler {

    @SuppressWarnings("unchecked")
    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {

        System.out.println("Authorize servie");
        String employeeId = (String) jsonData.get("userId");
        String name = (String) jsonData.get("name");
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT role_id FROM users WHERE user_id = ? AND name = ?");
            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int roleId = rs.getInt("role_id");
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("role", roleId);
                out.println(response.toJSONString());
            } else {
                JSONObject response = new JSONObject();
                response.put("status", "fail");
                out.println(response.toJSONString());
            }
        } catch (SQLException e) {
            JSONObject response = new JSONObject();
            response.put("status", "error");
            response.put("message", e.getMessage());
            out.println(response.toJSONString());
        }
    }
}
