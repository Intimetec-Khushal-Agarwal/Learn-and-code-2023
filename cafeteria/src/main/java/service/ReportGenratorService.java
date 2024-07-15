package service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;
import error.ErrorHandler;

public class ReportGenratorService {

    public void generateReport(PrintWriter out) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryConstants.GENERATE_REPORT); ResultSet rs = stmt.executeQuery()) {

            out.println("Menu Report:");
            out.printf("%-35s%-10s%-20s%-15s%-10s\n", "Name", "Rating", "Sentiments", "Sentiment Score", "Count");
            out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                String name = rs.getString("name");
                float rating = rs.getFloat("rating");
                String sentiments = rs.getString("sentiments");
                double sentimentScore = rs.getDouble("sentiment_score");
                int count = rs.getInt("count");

                out.printf("%-35s%-10.2f%-20s%-15.2f%-10d\n", name, rating, sentiments, sentimentScore, count);
            }

            out.println("END_OF_RESPONSE");
            out.flush();

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e, out, "Error generating report");

        }
    }
}
