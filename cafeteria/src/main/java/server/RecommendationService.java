package server;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class RecommendationService implements ClientRequestHandler {

    private final Map<Integer, Double> sentimentScores;
    private final Map<Integer, Set<String>> sentimentWordsMap;
    private final Map<Integer, Double> averageRatings;

    public RecommendationService() {
        sentimentScores = new HashMap<>();
        sentimentWordsMap = new HashMap<>();
        averageRatings = new HashMap<>();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {

        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "viewRecommendations" -> {
                try {
                    viewRecommendations(out);
                } catch (SQLException ex) {
                    ex.getMessage();
                    out.println("Error fetching recommendations.");
                    out.println("END_OF_RESPONSE");
                    out.flush();
                }
            }
            default -> {
                out.println("Invalid request type.");
                out.println("END_OF_RESPONSE");
                out.flush();
            }
        }
    }

    private void viewRecommendations(PrintWriter out) throws SQLException {
        String query = """
            SELECT
                mi.menu_item_id,
                mi.name AS menu_item_name,
                mi.price AS menu_item_price,
                CONCAT_WS(' ', mi.sentiments, GROUP_CONCAT(f.comment SEPARATOR ' ')) AS sentiments_and_comments,
                mi.rating AS menu_item_rating,
                (AVG(f.rating) + mi.rating) / 2 AS avg_combined_rating
            FROM
                menu_items mi
            LEFT JOIN
                feedbacks f ON mi.menu_item_id = f.menu_item_id
            WHERE
                f.menu_item_id IS NOT NULL
            GROUP BY
                mi.menu_item_id, mi.name, mi.price, mi.sentiments, mi.rating
            """;

        String updateQuery = "UPDATE menu_items SET sentiment_score = ?, sentiments = ?, rating = ? WHERE menu_item_id = ?";

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int menuItemId = resultSet.getInt("menu_item_id");
                String sentimentsAndComments = resultSet.getString("sentiments_and_comments");
                double avgCombinedRating = resultSet.getDouble("avg_combined_rating");

                Map<Double, Set<String>> mappingMeal = SentimentWords.sentiments(sentimentsAndComments);
                Double sentimentScore = 50.00;
                Set<String> sentimentWords = new HashSet<>();

                for (Map.Entry<Double, Set<String>> entry : mappingMeal.entrySet()) {
                    sentimentScore = entry.getKey();
                    sentimentWords = entry.getValue();
                }

                updateMenuItemDetails(conn, updateQuery, sentimentScore, sentimentWords, avgCombinedRating, menuItemId);

                // Store data in maps for potential future use
                sentimentScores.put(menuItemId, sentimentScore);
                sentimentWordsMap.put(menuItemId, sentimentWords);
                averageRatings.put(menuItemId, avgCombinedRating);
            }

        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }

        // Output recommendations header
        out.println("ID  | Meal Item         | Meal Type | Price  | Rating | Sentiment Words");
        out.println("----|-------------------|-----------|--------|--------|----------------");

        // Queries to fetch top menu items by rating for each meal type
        String bfQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments, mi.sentiment_score FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Breakfast' "
                + "ORDER BY mi.rating DESC LIMIT 5 ";

        String lunchQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments,mi.sentiment_score  FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Lunch' "
                + "ORDER BY mi.rating DESC LIMIT 5 ";

        String dinnerQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments,mi.sentiment_score  FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Dinner' "
                + "ORDER BY mi.rating DESC LIMIT 5";

        // Display top menu items for each meal type
        displayTopMenuItems(bfQuery, out);
        displayTopMenuItems(lunchQuery, out);
        displayTopMenuItems(dinnerQuery, out);

        out.println("END_OF_RESPONSE");
        out.flush();
    }

    private void updateMenuItemDetails(Connection conn, String updateQuery, Double sentimentScore, Set<String> sentimentWords, double avgCombinedRating, int menuItemId) throws SQLException {
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setDouble(1, sentimentScore);
            updateStmt.setString(2, String.join(", ", sentimentWords));
            updateStmt.setFloat(3, (float) avgCombinedRating);
            updateStmt.setInt(4, menuItemId);
            updateStmt.executeUpdate();
        }
    }

    private void displayTopMenuItems(String mealQuery, PrintWriter out) throws SQLException {
        try (Connection connection = Database.getConnection(); PreparedStatement mealStatement = connection.prepareStatement(mealQuery); ResultSet mealResultSet = mealStatement.executeQuery()) {

            while (mealResultSet.next()) {
                int id = mealResultSet.getInt("menu_item_id");
                String name = mealResultSet.getString("name");
                String mealType = mealResultSet.getString("meal_type");
                double price = mealResultSet.getDouble("price");
                double avgRating = mealResultSet.getDouble("rating");
                String sentiments = mealResultSet.getString("sentiments");
                double sentimentScore = mealResultSet.getDouble("sentiment_score");

                List<String> sentimentWords = Arrays.asList(sentiments.split(", "));
                Collections.sort(sentimentWords);
                if (sentimentWords.size() > 3) {
                    sentimentWords = sentimentWords.subList(0, 3);
                }

                String formattedItem = String.format("%-4d| %-25s| %-13s| %-6.2f| %-6.2f| %-26s| %-6.2f",
                        id, name, mealType, price, avgRating, String.join(", ", sentimentWords), sentimentScore);

                out.println(formattedItem);
            }
            out.flush();
        } catch (SQLException e) {
            e.getMessage();
            out.println("Error fetching menu items.");
        }
    }
}
