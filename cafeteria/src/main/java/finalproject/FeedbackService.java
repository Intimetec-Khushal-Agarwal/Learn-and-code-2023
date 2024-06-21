package finalproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

class FeedbackService implements ClientRequestHandler {

    @Override
    // public void handleRequest(BufferedReader in, PrintWriter out) throws IOException {
    //     int feedbackItemId = Integer.parseInt(in.readLine());
    //     int feedbackEmployeeId = Integer.parseInt(in.readLine());
    //     String comment = in.readLine();
    //     int rating = Integer.parseInt(in.readLine());
    //     Date date = new Date(); // Assuming current date
    //     try (Connection conn = Database.getConnection()) {
    //         PreparedStatement stmt = conn.prepareStatement("INSERT INTO Feedbacks (menu_item_id, employee_id, comment, rating, date) VALUES (?, ?, ?, ?, ?)");
    //         stmt.setInt(1, feedbackItemId);
    //         stmt.setInt(2, feedbackEmployeeId);
    //         stmt.setString(3, comment);
    //         stmt.setInt(4, rating);
    //         stmt.setDate(5, new java.sql.Date(date.getTime()));
    //         int rowsInserted = stmt.executeUpdate();
    //         if (rowsInserted > 0) {
    //             out.println("Feedback submitted successfully");
    //         } else {
    //             out.println("Failed to submit feedback");
    //         }
    //     } catch (SQLException e) {
    //         out.println("Error submitting feedback");
    //     }
    // }

    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        System.out.println("Inside handle request");

        String action = (String) jsonData.get("requestType");
        System.out.println("Inside handle request " + action);
        switch (action) {
            case "giveFeedback":
                giveFeedback(jsonData, out);
                break;
            case "checkExistingFeedback":
                checkExistingFeedback(jsonData, out);
            default:
                out.println("Invalid response");
        }
    }

    private void giveFeedback(JSONObject jsonData, PrintWriter out) {
        String menuId = (String) jsonData.get("itemId");
        String userId = (String) jsonData.get("userId");
        String comment = (String) jsonData.get("comment");
        String rating = (String) jsonData.get("rating");

        System.out.println("Inside give Feedback");
        // Validate input data
        if (menuId == null || menuId.isEmpty() || userId == null || userId.isEmpty() || rating == null || rating.isEmpty()) {
            out.println("Error: Missing required fields\n");
            return;
        }

        System.out.println("Insdie feedback");
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO feedbacks (menu_item_id, user_id, comment, rating, feedback_date) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, Integer.parseInt(menuId));
            stmt.setInt(2, Integer.parseInt(userId));
            stmt.setString(3, comment);
            stmt.setInt(4, Integer.parseInt(rating));
            stmt.setDate(5, currentDate);

            int rowsInserted = stmt.executeUpdate();
            System.out.println("rowIs inserted " + rowsInserted);
            if (rowsInserted > 0) {
                out.println("Menu item added successfully\n");
                out.flush();
            } else {
                out.println("Failed to add menu item\n");
                out.flush();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error adding menu item");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            out.println("Error: Invalid number format");
        }
    }

    private void checkExistingFeedback(JSONObject jsonData, PrintWriter out) {
        String menuId = (String) jsonData.get("itemId");
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        String checkQuery = "SELECT COUNT(*) FROM FEEDBACKS WHERE menu_item_id = ? AND feedback_date = ?";

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(checkQuery)) {

            stmt.setInt(1, Integer.parseInt(menuId));
            stmt.setDate(2, currentDate);
            ResultSet rs = stmt.executeQuery();

            boolean hasVoted = false;
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    hasVoted = true;
                }
            }

            System.out.println("Has voted: " + hasVoted);
            out.println(hasVoted + "\n");
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error checking feedback\n");
            out.flush();
        }
    }

}
