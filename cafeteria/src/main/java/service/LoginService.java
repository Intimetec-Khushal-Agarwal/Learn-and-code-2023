package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;
import serverconstant.QueryConstants;

public class LoginService {

    @SuppressWarnings("unchecked")
    public void login(JSONObject jsonData, PrintWriter out) {
        String employeeId = (String) jsonData.get("userId");
        String name = (String) jsonData.get("name");
         
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.LOGIN)) {

            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setString(2, name);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleJSONException(e, out, "Error during login");
        }
    }
}
