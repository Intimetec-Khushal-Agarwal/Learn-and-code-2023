package finalproject;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

class ReportService implements ClientRequestHandler {

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) {
        // Implement report generation logic
        out.println("Report generated successfully");
    }
}
