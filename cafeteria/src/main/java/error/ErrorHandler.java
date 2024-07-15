package error;

import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.simple.JSONObject;

public class ErrorHandler {

    public static void handleInvalidAction(PrintWriter out) {
        out.println("Invalid menu action");
        out.println("END_OF_RESPONSE");
        out.flush();
        System.err.println("Received an invalid menu action request");
    }

    @SuppressWarnings("unchecked")
    public static void handleJSONException(Exception e, PrintWriter out, String message) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message + ": " + e.getMessage());
        out.println(response.toJSONString());
        out.flush();
        System.err.println(message + ": " + e.getMessage());
    }

    public static void handleException(Exception e, PrintWriter out) {
        out.println("Error processing request: " + e.getMessage());
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    public static void handleSQLException(SQLException e, PrintWriter out, String message) {
        out.println(message + ": " + e.getMessage());
        System.err.println(message + ": " + e.getMessage());
    }

    public static void handlerNumberFormatException(PrintWriter out, String errorMessage, Exception e) {
        out.println(errorMessage + " : " + e.getMessage());
        out.println("END_OF_RESPONSE");
        out.flush();
    }

    public static void handleIOException(Exception e) {
        System.out.println("Error " + " : " + e.getMessage());
    }
}
