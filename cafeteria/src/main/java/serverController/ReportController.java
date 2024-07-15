package serverController;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

import error.ErrorHandler;
import service.ReportGenratorService;

public class ReportController implements ClientRequestHandler {

    private final ReportGenratorService reportGenerator = new ReportGenratorService();

    @Override
    public void handleRequest(JSONObject jsonData, PrintWriter out) {
        String action = (String) jsonData.get("requestType");

        switch (action) {
            case "generateReport" -> {
                reportGenerator.generateReport(out);
            }
            default -> {
                ErrorHandler.handleInvalidAction(out);
            }
        }
    }
}
