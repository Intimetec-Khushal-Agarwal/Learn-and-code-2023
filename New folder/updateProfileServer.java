package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.simple.JSONObject;

public class updateProfileServer implements ClientRequestHandler {

    private static final String UPDATE_PROFILE_QUERY = "UPDATE users SET food_type_id=?, food_taste_id=?, food_prefernece_id=?, sweetTooth=? WHERE user_id=?";

    public void handleRequest(JSONObject request, PrintWriter out) throws IOException {
        String action = (String) request.get("requestType");

        switch (action) {
            case "updateUserProfile": {
                updateUserProfile(request, out);
                break;
            }
            default:
                out.println("Invalid menu action");
        }
    }

    @SuppressWarnings("unchecked")
    private void updateUserProfile(JSONObject request, PrintWriter out) {
        int foodType = ((Long) request.get("foodType")).intValue();
        int foodTaste = ((Long) request.get("foodTaste")).intValue();
        int foodPreference = ((Long) request.get("foodPreference")).intValue();
        int sweetTooth = ((Long) request.get("sweetTooth")).intValue();
        String userId = (String) request.get("userId");

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_PROFILE_QUERY)) {
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
            System.out.println("User updation failed" + ex.getMessage());
        }
    }
}
