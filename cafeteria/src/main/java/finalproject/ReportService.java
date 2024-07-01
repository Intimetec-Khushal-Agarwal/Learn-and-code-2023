package finalproject;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

class ReportService implements ClientRequestHandler {

    private static final String GENRATE_REPORT_QUERY = "SELECT mi.name, mi.rating, mi.sentiments, mi.sentiment_score, COUNT(pm.menu_item_id) AS count "
            + "FROM prepared_menu pm "
            + "JOIN menu_items mi ON pm.menu_item_id = mi.menu_item_id "
            + "GROUP BY mi.name, mi.rating, mi.sentiments, mi.sentiment_score";

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "generateReport" ->
            generateReport(out);
            default ->
                out.println("Invalid menu action");
        }
    }

    private void generateReport(PrintWriter out) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(GENRATE_REPORT_QUERY); ResultSet rs = stmt.executeQuery()) {
            out.println("Menu Report:");
            out.printf("%-25s%-10s%-20s%-25s%-10s\n", "Name", "Rating", "Sentiments", "Sentiment Score", "Count");
            out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                String name = rs.getString("name");
                float rating = rs.getFloat("rating");
                String sentiments = rs.getString("sentiments");
                double sentimentScore = rs.getDouble("sentiment_score");
                int count = rs.getInt("count");
                out.printf("%-35s%-10.2f%-20s%-15.2f%-10d\n", name, rating, sentiments, sentimentScore, count);
            }
            out.println("END_OF_RESPONSE\n");
            out.flush();
        } catch (SQLException e) {
            out.println("Error showing menu items: " + e.getMessage());
        }

    }
}
