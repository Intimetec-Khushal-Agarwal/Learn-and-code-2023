package clientservice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class DiscardMenuItemService {

    private final ServerResponseReader serverResponse;
    private final ServerRequestSender serverRequest;

    public DiscardMenuItemService(ServerRequestSender serverRequest, ServerResponseReader serverResponse) {
        this.serverRequest = serverRequest;
        this.serverResponse = serverResponse;
    }

    public void sendRequest(JSONObject request) throws IOException {
        serverRequest.sendRequest(request);
    }

    public void readResponse() throws IOException {
        serverResponse.printResponse();
    }

    public boolean readDiscardItemResponse() {
        try {
            serverResponse.printResponse();
            String response = serverResponse.readJsonResponse();
            if (response != null && !response.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONObject responseJson = (JSONObject) parser.parse(response);
                String status = (String) responseJson.get("status");

                if ("success".equals(status)) {
                    String discardItemDate = (String) responseJson.get("date");
                    if (discardItemDate == null) {
                        return true;
                    }
                    LocalDate currentDate = LocalDate.now();
                    LocalDate lastDiscardItemDate = LocalDate.parse(discardItemDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    Period period = Period.between(lastDiscardItemDate, currentDate);
                    return period.toTotalMonths() >= 1;
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
}
