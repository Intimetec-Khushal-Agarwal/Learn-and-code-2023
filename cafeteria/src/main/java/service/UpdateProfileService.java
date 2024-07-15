package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;
import serverconstant.QueryConstants;

public class UpdateProfileService {

    @SuppressWarnings("unchecked")
    public void updateUserProfile(JSONObject request, PrintWriter out) {
        int foodType = ((Long) request.get("foodType")).intValue();
        int foodTaste = ((Long) request.get("foodTaste")).intValue();
        int foodPreference = ((Long) request.get("foodPreference")).intValue();
        int sweetTooth = ((Long) request.get("sweetTooth")).intValue();
        String userId = (String) request.get("userId");

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.UPDATE_PROFILE)) {
            stmt.setInt(1, foodType);
            stmt.setInt(2, foodTaste);
            stmt.setInt(3, foodPreference);
            stmt.setInt(4, sweetTooth);
            stmt.setInt(5, Integer.parseInt(userId));
            int rowUpdated = stmt.executeUpdate();

            JSONObject sendResponse = new JSONObject();
            if (rowUpdated > 0) {
                sendResponse.put("message", "User profile updated successfully");
            } else {
                sendResponse.put("message", "User profile updation fail");
            }
            out.println(sendResponse.toJSONString());
            out.flush();
        } catch (SQLException ex) {
            ErrorHandler.handleSQLException(ex, out, "User updation failed");

        }
    }
}
