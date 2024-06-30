package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DiscardMenuItemController {

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;
    private final BufferedReader consoleReader;
    JSONObject menuItem;

    public DiscardMenuItemController(BufferedReader socketReader,  PrintWriter socketWriter,BufferedReader consoleReader){
        this.socketReader = socketReader;
        this.consoleReader = consoleReader;
        this.socketWriter = socketWriter;
        this.menuItem = new JSONObject();
    }

    @SuppressWarnings("unchecked") 
    void discardMenuItems() {
        try {
            System.out.println("Inside discard Menu Items");
            menuItem.put("requestType", "discardMenuItem");
            sendRequest(menuItem);
            boolean result = readDiscardItemResponse();
            if (result) {
                displayDiscardMenu(menuItem);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("null")
    private boolean readDiscardItemResponse() {
        try {
            String serverResponse;
            System.out.println("server Response: ");

            while ((serverResponse = socketReader.readLine()) != null) {
                if (serverResponse.equals("END_OF_RESPONSE")) {
                    System.out.println("Inside end of response");
                    break;
                }
                System.out.println("Inside while");
                System.out.println(serverResponse);
            }
            System.out.println("outside while");

            String response = socketReader.readLine();
            System.out.println("Response: " + response);

            boolean isOneMonthOrMore = false;
            if (response != null || !response.isEmpty() || response.isBlank()) {
                JSONParser parser = new JSONParser();
                JSONObject responseJson = (JSONObject) parser.parse(response);
                String status = (String) responseJson.get("status");

                if ("success".equals(status)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String discardItemDate = (String) responseJson.get("date");
                    LocalDate currentDate = LocalDate.now();
                    LocalDate lastDiscardItemDate = LocalDate.parse(discardItemDate, formatter);
                    Period period = Period.between(lastDiscardItemDate, currentDate);
                    isOneMonthOrMore = period.toTotalMonths() >= 1;
                }
                return isOneMonthOrMore;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return false; // Return false if there's any exception or if status is not "success"
    }


    private void displayDiscardMenu(JSONObject menuItem) throws IOException {
        System.out.println("Enter operation to perform:\n1.Delete Menu Item\n2.Ask for Feedback\n3.Exit");
        String command = consoleReader.readLine().trim();

        switch (command) {
            case "1" ->
                performMenuAction(menuItem, 3);
            case "2" ->
                performMenuAction(menuItem, 4);
            case "3" -> {
                return;
            }
            default ->
                System.out.println("Invalid request");
        }

    }

    @SuppressWarnings("unchecked")
    private void performMenuAction(JSONObject menuItem, int messageId) throws IOException {
        System.out.println("Inside perform action");
        System.out.println("Enter Item Id: ");
        int itemId = getValidatedIntInput();
        if (itemId < 1) {
            return;
        }
        menuItem.put("requestType", "storeDiscardedItem");
        menuItem.put("id", itemId);
        menuItem.put("messageId", messageId);
        displayMenu(menuItem);
    }

    private void displayMenu(JSONObject menuItem) throws IOException {
        System.out.println("inside display menu");
        sendRequest(menuItem);
        readResponse();
    }

    private void sendRequest(JSONObject menuItem) {
        System.out.println("Sending Request");
        String request = menuItem.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    private void readResponse() throws IOException {
        String serverResponse;
        System.out.println("server Response: ");
        while ((serverResponse = socketReader.readLine()) != null) {
            if (serverResponse.equals("END_OF_RESPONSE")) {
                break;
            }
            System.out.println(serverResponse);
        }
    }

    private int getValidatedIntInput() throws IOException {
        while (true) {
            try {
                return Integer.parseInt(consoleReader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a valid number:");
            }
        }
    }

}
