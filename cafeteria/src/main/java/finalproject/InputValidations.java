package finalproject;

import java.io.BufferedReader;
import java.io.IOException;

public class InputValidations {

    private final BufferedReader consoleReader;

    public InputValidations(BufferedReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    public int getValidatedIntInput() throws IOException {
        while (true) {
            try {
                return Integer.parseInt(consoleReader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a valid number:");
            }
        }
    }

    public float getValidatedFloatInput() throws IOException {
        while (true) {
            try {
                return Float.parseFloat(consoleReader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a valid number:");
            }
        }
    }

    public String getValidatedStringInput() throws IOException {
        while (true) {
            String input = consoleReader.readLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            } else {
                System.out.println("Invalid input, please enter a non-empty string:");
            }
        }
    }

    public String getValidatedBooleanInput() throws IOException {
        while (true) {
            String input = consoleReader.readLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                return input;
            } else {
                System.out.println("Invalid input, please enter 'yes' or 'no':");
            }
        }
    }

    public int getValidatedOption(int maxOption) throws IOException {
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

}
