package error;

import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.simple.JSONObject;

public class ErrorHandler {

    public static void handleInvalidAction(PrintWriter socketWriter) {
        socketWriter.println("Invalid menu action");
        socketWriter.println("END_OF_RESPONSE");
        socketWriter.flush();
        System.err.println("Received an invalid menu action request");
    }

    @SuppressWarnings("unchecked")
    public static void handleJSONException(Exception e, PrintWriter socketWriter, String message) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message + ": " + e.getMessage());
        socketWriter.println(response.toJSONString());
        socketWriter.flush();
        System.err.println(message + ": " + e.getMessage());
    }

    public static void handleException(Exception e, PrintWriter socketWriter) {
        socketWriter.println("Error processing request: " + e.getMessage());
        socketWriter.println("END_OF_RESPONSE");
        socketWriter.flush();
    }

    public static void handleSQLException(SQLException e, PrintWriter socketWriter, String message) {
        socketWriter.println(message + ": " + e.getMessage());
        System.err.println(message + ": " + e.getMessage());
    }

    public static void handlerNumberFormatException(PrintWriter socketWriter, String errorMessage, Exception e) {
        socketWriter.println(errorMessage + " : " + e.getMessage());
        socketWriter.println("END_OF_RESPONSE");
        socketWriter.flush();
    }

    public static void handleIOException(Exception e) {
        System.out.println("Error " + " : " + e.getMessage());
    }
}
