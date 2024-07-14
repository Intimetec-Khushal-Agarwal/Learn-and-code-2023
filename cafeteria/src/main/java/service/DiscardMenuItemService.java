package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONObject;

import database.DatabaseConnection;
import server.ErrorHandler;

public class DiscardMenuItemService {

    private final AdminService menuService;

    public DiscardMenuItemService() {
        this.menuService = new AdminService();
    }

    @SuppressWarnings("unchecked")
    public void showDiscardMenuItemList(PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.SHOW_ELIGIBLE_DISCARD_ITEMS); ResultSet rs = stmt.executeQuery()) {
            JSONObject jsonResponse = new JSONObject();
            if (rs.isBeforeFirst()) {
                out.printf("%-15s%-35s%-10s%-15s%-20s%-20s\n", "MenuItemID", "Name", "Rating", "Sentiments", "Sentiment Score", "MealType");
                out.println("-----------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    int menuItemId = rs.getInt("menu_item_id");
                    String name = rs.getString("name");
                    double rating = rs.getDouble("rating");
                    String sentiments = rs.getString("sentiments");
                    double sentimentScore = rs.getDouble("sentiment_score");
                    String mealTypeName = rs.getString("meal_type_name");

                    System.out.println("Inside discardMenu Item table");

                    out.printf("%-15d%-35s%-10.2f%-15s%-20.2f%-20s\n", menuItemId, name, rating, sentiments, sentimentScore, mealTypeName);
                }
                out.println("END_OF_RESPONSE");
                jsonResponse.put("status", "success");
                jsonResponse.put("date", getDiscardMenuItemList().toString());

            } else {
                jsonResponse.put("status", "fail");
                out.println("No discarded menu items found.");
                out.println("END_OF_RESPONSE");
            }
            String jsonData = jsonResponse.toJSONString();
            out.println(jsonData + "\n");
            out.flush();

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error Showing discard menu items");
        }
    }

    public void storeDiscardMenuItem(JSONObject request, PrintWriter out) {
        int menuItemId = ((Long) request.get("id")).intValue();
        int messageId = ((Long) request.get("messageId")).intValue();

        System.out.println("Inside storeDiscardMenuItem");

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.STORE_DISCARD_MENU_ITEM)) {
            System.out.println("Connection Established ");
            stmt.setInt(1, menuItemId);
            stmt.setInt(2, messageId);
            stmt.setInt(3, menuItemId);

            int rowsInserted = stmt.executeUpdate();
            System.out.println("Row Inserted " + rowsInserted);
            if (rowsInserted > 0 && messageId == 3) {
                menuService.deleteMenuItem(request, out);
                System.out.println("Menu Item deleted");
            } else if (rowsInserted > 0 && messageId == 4) {
                out.println("Item added for feedback response");
                System.out.println("Item added for feedback response");

            } else {
                out.println("Failed to add discard menu item");
                System.out.println("Failed to add discard menu item");

            }
            out.println("END_OF_RESPONSE");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error Storing discard menu items");
        }
    }

    public Date getDiscardMenuItemList() {
        Date currentDate = null;
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.GET_DISCARD_MENU_ITEM_LIST); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                currentDate = rs.getDate("discard_date");
                return currentDate;
            }

        } catch (SQLException e) {
            e.getMessage();
        }

        return currentDate;
    }

    @SuppressWarnings("unchecked")
    public void showDiscardMenuItems(PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.GET_LATEST_DISCARDED_ITEM); ResultSet rs = stmt.executeQuery()) {

            JSONObject jsonResponse = new JSONObject();
            System.out.println("Inside show discard menu Items");
            if (rs.isBeforeFirst()) {
                out.printf("%-15s%-20s%-18s%-70s\n", "MenuItemID", "Name", "discard_date", "message");
                out.println("-----------------------------------------------------------------------------------------------");
                System.out.println("Inside before first");

                while (rs.next()) {
                    int menuItemId = rs.getInt("menu_item_id");
                    String name = rs.getString("discard_item_name");
                    String date = rs.getDate("discard_date").toString();
                    String message = rs.getString("message");

                    String[] parts = message.split("\\?");
                    String text = message;
                    if (parts.length > 1) {
                        text = "Answer the below question";
                    }

                    System.out.println("Inside while");

                    out.printf("%-15s%-20s%-18s%-70s\n", menuItemId, name, date, text);
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", message);
                }
            } else {
                jsonResponse.put("status", "fail");
                System.out.println("Inside else");
            }

            out.println("END_OF_RESPONSE");
            out.println(jsonResponse.toJSONString());
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Showing discard menu item failed");

        }
    }
}
