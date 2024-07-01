package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

public class FeedbackService implements ClientRequestHandler {

    private static final String CHECK_USER_FEEDBACK_SQL
            = "SELECT COUNT(*) FROM feedbacks WHERE user_id = ? AND menu_item_id = ? AND feedback_date = ?";
    private static final String INSERT_FEEDBACK_SQL
            = "INSERT INTO feedbacks (menu_item_id, user_id, comment, rating, feedback_date) VALUES (?, ?, ?, ?, ?)";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "giveFeedback" -> handleGiveFeedback(jsonData, out);
            case "checkExistingFeedback" -> handleCheckExistingFeedback(jsonData, out);
            default -> out.println("Invalid menu action");
        }
    }

    private void handleGiveFeedback(JSONObject jsonData, PrintWriter out) {
        String menuId = (String) jsonData.get("itemId");
        String userId = (String) jsonData.get("userId");
        String comment = (String) jsonData.get("comment");
        String rating = (String) jsonData.get("rating");

        if (isMissingRequiredFields(menuId, userId, rating)) {
            out.println("Error: Missing required fields\n");
            return;
        }

        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); 
             PreparedStatement stmt = createInsertFeedbackStatement(conn, menuId, userId, comment, rating, currentDate)) {

            int rowsInserted = stmt.executeUpdate();
            sendFeedbackResponse(out, rowsInserted > 0 ? "Feedback added successfully\n" : "Failed to add Feedback\n");

        } catch (SQLException e) {
            handleError(out, "Error adding feedback", e);
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format");
        }
    }

    private PreparedStatement createInsertFeedbackStatement(Connection conn, String menuId, String userId, String comment, String rating, Date currentDate) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(INSERT_FEEDBACK_SQL);
        stmt.setInt(1, Integer.parseInt(menuId));
        stmt.setInt(2, Integer.parseInt(userId));
        stmt.setString(3, comment);
        stmt.setInt(4, Integer.parseInt(rating));
        stmt.setDate(5, currentDate);
        return stmt;
    }

    private boolean isMissingRequiredFields(String menuId, String userId, String rating) {
        return menuId == null || menuId.isEmpty() || userId == null || userId.isEmpty() || rating == null || rating.isEmpty();
    }

    private void handleCheckExistingFeedback(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        String itemId = (String) jsonData.get("itemId");
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = Database.getConnection(); 
             PreparedStatement stmt = createCheckFeedbackStatement(conn, userId, itemId, currentDate)) {

            boolean hasFeedback = checkFeedback(stmt);
            sendFeedbackResponse(out, hasFeedback + "\n");

        } catch (SQLException e) {
            handleError(out, "Error in checking existing feedback", e);
        }
    }

    private PreparedStatement createCheckFeedbackStatement(Connection conn, String userId, String itemId, Date currentDate) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(CHECK_USER_FEEDBACK_SQL);
        stmt.setInt(1, Integer.parseInt(userId));
        stmt.setInt(2, Integer.parseInt(itemId));
        stmt.setDate(3, currentDate);
        return stmt;
    }

    private boolean checkFeedback(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private void sendFeedbackResponse(PrintWriter out, String message) {
        out.println(message+ "\n");
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private void handleError(PrintWriter out, String errorMessage, SQLException e) {
        out.println(errorMessage);
        out.println("END_OF_RESPONSE");
        out.flush();
        e.getMessage();
    }
}
