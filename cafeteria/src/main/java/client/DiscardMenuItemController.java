package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DiscardMenuItemController {

    private final ConsoleInputValidations inputValidations;
    private final ServerRequestResponse jsonRequestResponse;
    JSONObject menuItem;

    public DiscardMenuItemController(ConsoleInputValidations inputValidations, ServerRequestResponse jsonRequestResponse, BufferedReader consoleReader) {
        this.menuItem = new JSONObject();
        this.inputValidations = inputValidations;
        this.jsonRequestResponse = jsonRequestResponse;
    }

    @SuppressWarnings("unchecked")
    void discardMenuItems() {
        try {
            menuItem.put("requestType", "discardMenuItem");
            jsonRequestResponse.sendRequest(menuItem);
            boolean result = readDiscardItemResponse();
            if (result) {
                displayDiscardMenu(menuItem);
            } else {
                System.out.println("You have already performed action for month");
            }

        } catch (IOException e) {
            e.getMessage();
        }
    }

    @SuppressWarnings("null")
    private boolean readDiscardItemResponse() {
        try {
            jsonRequestResponse.readResponse();
            String response = jsonRequestResponse.readJSONresponse();

            boolean isOneMonthOrMore = false;
            if (response != null || !response.isEmpty() || response.isBlank()) {
                JSONParser parser = new JSONParser();
                JSONObject responseJson = (JSONObject) parser.parse(response);
                String status = (String) responseJson.get("status");

                if ("success".equals(status)) {
                    String discardItemDate = (String) responseJson.get("date");
                    if (discardItemDate == null) {
                        return true;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate currentDate = LocalDate.now();
                    LocalDate lastDiscardItemDate = LocalDate.parse(discardItemDate, formatter);
                    Period period = Period.between(lastDiscardItemDate, currentDate);
                    isOneMonthOrMore = period.toTotalMonths() >= 1;
                }
                return isOneMonthOrMore;
            }
        } catch (IOException | ParseException e) {
            e.getMessage();
        }

        return false;
    }

    private void displayDiscardMenu(JSONObject menuItem) throws IOException {
        System.out.println("Enter operation to perform:\n1.Delete Menu Item\n2.Ask for Feedback\n3.Exit");
        int command = inputValidations.getValidatedIntInput();

        switch (command) {
            case 1 ->
                performMenuAction(menuItem, 3);
            case 2 ->
                performMenuAction(menuItem, 4);
            case 3 -> {
                return;
            }
            default ->
                System.out.println("Invalid request");
        }

    }

    @SuppressWarnings("unchecked")
    private void performMenuAction(JSONObject menuItem, int messageId) throws IOException {
        System.out.println("Enter Item Id: ");
        int itemId = inputValidations.getValidatedIntInput();
        if (itemId < 1) {
            return;
        }
        menuItem.put("requestType", "storeDiscardedItem");
        menuItem.put("id", itemId);
        menuItem.put("messageId", messageId);
        displayMenu(menuItem);
    }

    private void displayMenu(JSONObject menuItem) throws IOException {
        jsonRequestResponse.sendRequest(menuItem);
        jsonRequestResponse.readResponse();
    }
}
