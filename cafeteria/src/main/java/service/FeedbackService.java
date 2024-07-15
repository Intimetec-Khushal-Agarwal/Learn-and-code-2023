package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import error.ErrorHandler;

public class FeedbackService {

    public void giveFeedback(JSONObject jsonData, PrintWriter out) {
        String menuId = (String) jsonData.get("itemId");
        String userId = (String) jsonData.get("userId");
        String comment = (String) jsonData.get("comment");
        String rating = (String) jsonData.get("rating");

        if (isMissingRequiredFields(menuId, userId, rating)) {
            sendFeedbackResponse(out, "Error: Missing required fields\n");
            return;
        }

        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = createInsertFeedbackStatement(conn, menuId, userId, comment, rating, currentDate)) {

            int rowsInserted = stmt.executeUpdate();
            sendFeedbackResponse(out, rowsInserted > 0 ? "Feedback added successfully\n" : "Failed to add Feedback\n");

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error adding feedback");
        } catch (NumberFormatException e) {
            ErrorHandler.handlerNumberFormatException(out, "Error: Invalid number format", e);
        }
    }

    private PreparedStatement createInsertFeedbackStatement(Connection conn, String menuId, String userId, String comment, String rating, Date currentDate) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(QueryConstants.INSERT_FEEDBACK);
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

    public void checkExistingFeedback(JSONObject jsonData, PrintWriter out) {
        String userId = (String) jsonData.get("userId");
        String itemId = (String) jsonData.get("itemId");
        Date currentDate = new Date(System.currentTimeMillis());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = createCheckFeedbackStatement(conn, userId, itemId, currentDate)) {

            boolean hasFeedback = checkFeedback(stmt);
            sendFeedbackResponse(out, hasFeedback + "\n");

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error in checking existing feedback");
        }
    }

    private PreparedStatement createCheckFeedbackStatement(Connection conn, String userId, String itemId, Date currentDate) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(QueryConstants.CHECK_USER_FEEDBACK);
        stmt.setInt(1, Integer.parseInt(userId));
        stmt.setInt(2, Integer.parseInt(itemId));
        stmt.setDate(3, currentDate);
        return stmt;
    }

    private boolean checkFeedback(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void sendFeedbackResponse(PrintWriter out, String message) {
        out.println(message);
        out.println("END_OF_RESPONSE");
        out.flush();
    }
}
