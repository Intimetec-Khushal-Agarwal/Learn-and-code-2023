package finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UpdateProfile {

    private final PrintWriter socketWriter;
    private final BufferedReader consoleReader;
    private final BufferedReader socketReader;
    private final String employeeId;

    public UpdateProfile(BufferedReader socketReader, PrintWriter socketWriter, BufferedReader consoleReader, String employeeId) {
        this.socketWriter = socketWriter;
        this.socketReader = socketReader;
        this.consoleReader = consoleReader;
        this.employeeId = employeeId;
    }

    @SuppressWarnings("unchecked")
    public void updateUserProfile() throws IOException, ParseException {
        JSONObject jsonRequest = new JSONObject();

        int dietaryPreference = getDietaryPreference();
        int spiceLevel = getSpiceLevel();
        int foodPreference = getFoodPreference();
        int sweetTooth = getSweetTooth();

        jsonRequest.put("requestType", "updateUserProfile");
        jsonRequest.put("foodType", dietaryPreference);
        jsonRequest.put("foodTaste", spiceLevel);
        jsonRequest.put("foodPreference", foodPreference);
        jsonRequest.put("sweetTooth", sweetTooth);
        jsonRequest.put("userId", employeeId);

        sendRequest(jsonRequest);
        readResponse();
    }

    private int getDietaryPreference() throws IOException {
        System.out.println("1) Please select one:");
        System.out.println("1. Vegetarian");
        System.out.println("2. Non Vegetarian");
        System.out.println("3. Eggetarian");
        return getValidatedOption(3);
    }

    private int getSpiceLevel() throws IOException {
        System.out.println("2) Please select your spice level:");
        System.out.println("1. High");
        System.out.println("2. Medium");
        System.out.println("3. Low");
        return getValidatedOption(3);
    }

    private int getFoodPreference() throws IOException {
        System.out.println("3) What do you prefer most?");
        System.out.println("1. North Indian");
        System.out.println("2. South Indian");
        System.out.println("3. Other");
        return getValidatedOption(3);
    }

    private int getSweetTooth() throws IOException {
        System.out.println("4) Do you have a sweet tooth?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int option = getValidatedOption(2);
        if (option == 2) {
            return 0;
        }
        return option;
    }

    private int getValidatedOption(int maxOption) throws IOException {
        int option;
        while (true) {
            try {
                option = Integer.parseInt(consoleReader.readLine());
                if (option >= 1 && option <= maxOption) {
                    break;
                } else {
                    System.out.println("Invalid option. Please select a number between 1 and " + maxOption + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return option;
    }

    private void sendRequest(JSONObject jsonRequest) {
        String request = jsonRequest.toJSONString();
        socketWriter.println(request + "\n");
        socketWriter.flush();
    }

    private void readResponse() throws IOException, ParseException {
        String serverResponse = socketReader.readLine();
        System.out.println("Server response " + serverResponse);

        JSONObject responseJson = (JSONObject) new JSONParser().parse(serverResponse);
        String message = (String) responseJson.get("message");
        System.out.println(message);
    }

}
