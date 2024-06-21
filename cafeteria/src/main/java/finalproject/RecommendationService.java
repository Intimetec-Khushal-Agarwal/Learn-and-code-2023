package finalproject;

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

class RecommendationService implements ClientRequestHandler {

    private Map<Integer, Integer> sentimentScores;
    private Map<Integer, Set<String>> sentimentWordsMap;
    private Map<Integer, Double> averageRatings;

    public RecommendationService() {
        sentimentScores = new HashMap<>();
        sentimentWordsMap = new HashMap<>();
        averageRatings = new HashMap<>();
    }

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException {
        System.out.println("Inside handle request");

        String action = (String) jsonData.get("requestType");
        System.out.println("Inside handle request " + action);
        switch (action) {
            case "viewRecommendations":
                try {
                    viewRecommendations(out);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void viewRecommendations(PrintWriter out) throws SQLException {
        // Query to fetch feedback data and compute average rating
        String query = "SELECT f.menu_item_id, GROUP_CONCAT(f.comment SEPARATOR ' ') AS feedbacks, AVG(f.rating) AS avg_rating, mi.sentiments, mi.rating AS menu_item_rating "
                + "FROM feedbacks f JOIN menu_items mi ON f.menu_item_id = mi.menu_item_id "
                + "GROUP BY f.menu_item_id, mi.sentiments, mi.rating";
        System.out.println("Inside view Recommendation");

        // Query to update sentiments and rating in menu_items table
        String query1 = "UPDATE menu_items SET sentiments = ?, rating = ? WHERE menu_item_id = ?";

        try (Connection conn = Database.getConnection(); PreparedStatement statement = conn.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int menuItemId = resultSet.getInt("menu_item_id");
                String feedbacks = resultSet.getString("feedbacks");
                double avgRating = resultSet.getDouble("avg_rating");

                // Compute sentiments
                Map<Integer, Set<String>> mappingMeal = SentimentWords.sentimentann(feedbacks);
                int sentimentScore = 3; // Default sentiment score
                Set<String> sentimentWords = new HashSet<>();

                for (Map.Entry<Integer, Set<String>> entry : mappingMeal.entrySet()) {
                    sentimentScore = entry.getKey();
                    sentimentWords = entry.getValue();
                    sentimentScores.put(menuItemId, sentimentScore);
                    sentimentWordsMap.put(menuItemId, sentimentWords);
                }
                averageRatings.put(menuItemId, avgRating);

                // Update sentiments and rating in menu_items table
                if (!sentimentWordsMap.isEmpty()) {
                    try (PreparedStatement menuStatement = conn.prepareStatement(query1)) {
                        menuStatement.setString(1, String.join(", ", sentimentWords));
                        menuStatement.setFloat(2, (float) avgRating);
                        menuStatement.setInt(3, menuItemId);
                        int rowsInserted = menuStatement.executeUpdate();
                        System.out.println("Rows updated: " + rowsInserted);
                    }
                }
            }
        }

        // Output recommendations header
        out.println("ID  | Meal Item         | Meal Type | Price  | Rating | Sentiment Words");
        out.println("----|-------------------|-----------|--------|--------|----------------");

        // Queries to fetch top 3 menu items by rating for each meal type
        String bfQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Breakfast' "
                + "ORDER BY mi.rating DESC "
                + "LIMIT 3";

        String lunchQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Lunch' "
                + "ORDER BY mi.rating DESC "
                + "LIMIT 3";

        String dinnerQuery = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments FROM menu_items mi "
                + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
                + "WHERE mt.meal_type = 'Dinner' "
                + "ORDER BY mi.rating DESC "
                + "LIMIT 3";

        // Display top menu items for each meal type
        displayTopMenuItems(bfQuery, out);
        displayTopMenuItems(lunchQuery, out);
        displayTopMenuItems(dinnerQuery, out);
    }

// Method to display top menu items based on the query
    private void displayTopMenuItems(String mealQuery, PrintWriter out) throws SQLException {
        try (Connection connection = Database.getConnection(); PreparedStatement mealStatement = connection.prepareStatement(mealQuery); ResultSet mealResultSet = mealStatement.executeQuery()) {

            while (mealResultSet.next()) {
                int id = mealResultSet.getInt("menu_item_id");
                String name = mealResultSet.getString("name");
                String mealType = mealResultSet.getString("meal_type");
                double price = mealResultSet.getDouble("price");
                double avgRating = mealResultSet.getDouble("rating");
                String sentiments = mealResultSet.getString("sentiments");

                // Extract the top 3 sentiment words if they exist
                List<String> sentimentWords = Arrays.asList(sentiments.split(", "));
                Collections.sort(sentimentWords);
                if (sentimentWords.size() > 3) {
                    sentimentWords = sentimentWords.subList(0, 3);
                }

                // Print the details
                System.out.printf("%-4d| %-25s| %-13s| %-6.2f| %-6.2f| %-16s%n", id, name, mealType, price, avgRating, String.join(", ", sentimentWords));
                out.printf("%-4d| %-35s| %-15s| %-6.2f| %-6.2f| %-16s%n", id, name, mealType, price, avgRating, String.join(", ", sentimentWords));
                out.flush();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error fetching menu items.");
        }
    }

}
