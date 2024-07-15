package service;

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

import database.DatabaseConnection;
import error.ErrorHandler;

public class RecommendationService {

    private final Map<Integer, Double> sentimentScores;
    private final Map<Integer, Set<String>> sentimentWordsMap;
    private final Map<Integer, Double> averageRatings;

    public RecommendationService() {
        sentimentScores = new HashMap<>();
        sentimentWordsMap = new HashMap<>();
        averageRatings = new HashMap<>();
    }

    public void viewRecommendations(PrintWriter out) throws SQLException {
        String query = QueryConstants.PROCESS_RECOMMENDATION;
        String updateQuery = QueryConstants.STORE_RECOMMENDATION;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int menuItemId = resultSet.getInt("menu_item_id");
                String sentimentsAndComments = resultSet.getString("sentiments_and_comments");
                double avgCombinedRating = resultSet.getDouble("avg_combined_rating");

                System.out.println("sentimentsAndComments " + sentimentsAndComments);
                System.out.println("avgCombinedRating " + avgCombinedRating);

                Map<Double, Set<String>> mappingMeal = SentimentAnalysis.analyzeSentiments(sentimentsAndComments);
                Double sentimentScore = 50.00;
                Set<String> sentimentWords = new HashSet<>();

                for (Map.Entry<Double, Set<String>> entry : mappingMeal.entrySet()) {
                    sentimentScore = entry.getKey();
                    sentimentWords = entry.getValue();
                }

                updateMenuItemDetails(conn, updateQuery, sentimentScore, sentimentWords, avgCombinedRating, menuItemId);

                sentimentScores.put(menuItemId, sentimentScore);
                sentimentWordsMap.put(menuItemId, sentimentWords);
                averageRatings.put(menuItemId, avgCombinedRating);
            }

            displayTopMenuItems(QueryConstants.GET_BREAKFAST_ITEMS, out);
            displayTopMenuItems(QueryConstants.GET_LUNCH_ITEMS, out);
            displayTopMenuItems(QueryConstants.GET_DINNER_ITEMS, out);

            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error viewing Recommendation");
        }
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
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement mealStatement = connection.prepareStatement(mealQuery); ResultSet mealResultSet = mealStatement.executeQuery()) {

            while (mealResultSet.next()) {
                int id = mealResultSet.getInt("menu_item_id");
                String name = mealResultSet.getString("name");
                String mealType = mealResultSet.getString("meal_type");
                double price = mealResultSet.getDouble("price");
                double avgRating = mealResultSet.getDouble("rating");
                String sentiments = mealResultSet.getString("sentiments");
                double sentimentScore = mealResultSet.getDouble("sentiment_score");

                List<String> sentimentWordsList = Arrays.asList(sentiments.split(", "));
                Collections.sort(sentimentWordsList);
                List<String> sentimentWords = sentimentWordsList.size() > 3 ? sentimentWordsList.subList(0, 3) : sentimentWordsList;

                String formattedItem = String.format("%-4d| %-25s| %-1s| %-7.2f| %-6.2f| %-34s| %-6.2f",
                        id, name, mealType, price, avgRating, String.join(", ", sentimentWords), sentimentScore);

                out.println(formattedItem);
            }
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error fetching menu items");

        }
    }
}
