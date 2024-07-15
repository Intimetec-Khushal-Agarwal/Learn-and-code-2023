package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;
import error.ErrorHandler;

public class ShowMenuService {

    public void showMenuItems(PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.SHOW_MENU); ResultSet rs = stmt.executeQuery()) {

            out.println("Menu Items:");
            out.printf("%-10s%-35s%-20s%-10s%-15s%-15s\n",
                    "itemId", "name", "price", "status", "mealType", "sentiments");
            out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int itemId = rs.getInt("menu_item_id");
                String name = rs.getString("name");
                float price = rs.getFloat("price");
                String status = rs.getString("availability_status");
                String mealType = rs.getString("meal_type");
                String sentiments = rs.getString("sentiments");
                out.printf("%-10d%-35s%-20.2f%-10s%-15s%-15s\n", itemId, name, price, status, mealType, sentiments);
            }
            out.println("END_OF_RESPONSE\n");
            out.flush();
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error generating report");
        }
    }
}
