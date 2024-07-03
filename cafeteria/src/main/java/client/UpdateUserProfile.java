package client;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class UpdateUserProfile {

    private final String employeeId;
    private final ConsoleInputValidations inputValidations;
    private final ServerRequestResponse jsonRequestResponse;

    public UpdateUserProfile(ConsoleInputValidations inputValidations, ServerRequestResponse jsonRequestResponse, String employeeId) {
        this.employeeId = employeeId;
        this.inputValidations = inputValidations;
        this.jsonRequestResponse = jsonRequestResponse;
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

        jsonRequestResponse.sendRequest(jsonRequest);
        jsonRequestResponse.readJSONresponse();
    }

    public int getDietaryPreference() throws IOException {
        System.out.println("1) Please select one:");
        System.out.println("1. Vegetarian");
        System.out.println("2. Non Vegetarian");
        System.out.println("3. Eggetarian");
        return inputValidations.getValidatedOption(3);
    }

    public int getSpiceLevel() throws IOException {
        System.out.println("2) Spice level:");
        System.out.println("1. High");
        System.out.println("2. Medium");
        System.out.println("3. Low");
        return inputValidations.getValidatedOption(3);
    }

    public int getFoodPreference() throws IOException {
        System.out.println("3) Food Preference?");
        System.out.println("1. North Indian");
        System.out.println("2. South Indian");
        System.out.println("3. Other");
        return inputValidations.getValidatedOption(3);
    }

    public int getSweetTooth() throws IOException {
        System.out.println("4) Sweet tooth?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int option = inputValidations.getValidatedOption(2);
        if (option == 2) {
            return 0;
        }
        return option;
    }
}
