package clientservice;

import java.io.BufferedReader;
import java.io.IOException;

public class ConsoleInputValidator {

    private final BufferedReader consoleReader;

    public ConsoleInputValidator(BufferedReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    public int getValidatedIntInput() throws IOException {
        return getValidatedInput("Invalid input, please enter a valid number:", this::parseInt);
    }

    public float getValidatedFloatInput() throws IOException {
        return getValidatedInput("Invalid input, please enter a valid number:", this::parseFloat);
    }

    public String getValidatedStringInput() throws IOException {
        return getValidatedInput("Invalid input, please enter a non-empty string:", this::validateString);
    }

    public String getValidatedBooleanInput() throws IOException {
        return getValidatedInput("Invalid input, please enter 'yes' or 'no':", this::validateBoolean);
    }

    public int getValidatedOption(int maxOption) throws IOException {
        return getValidatedInput("Invalid option. Please select a number between 1 and " + maxOption + ".", input -> validateOption(input, maxOption));
    }

    private <T> T getValidatedInput(String errorMessage, InputParser<T> parser) throws IOException {
        while (true) {
            try {
                String input = consoleReader.readLine();
                return parser.parse(input);
            } catch (IllegalArgumentException e) {
                System.out.println(errorMessage);
            }
        }
    }

    private int parseInt(String input) {
        return Integer.parseInt(input);
    }

    private float parseFloat(String input) {
        return Float.parseFloat(input);
    }

    private String validateString(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }
        return input.trim();
    }

    private String validateBoolean(String input) {
        String trimmedInput = input.trim().toLowerCase();
        if (!trimmedInput.equals("yes") && !trimmedInput.equals("no")) {
            throw new IllegalArgumentException("Invalid input");
        }
        return trimmedInput;
    }

    private int validateOption(String input, int maxOption) {
        int option = Integer.parseInt(input);
        if (option < 1 || option > maxOption) {
            throw new IllegalArgumentException("Invalid input");
        }
        return option;
    }

    @FunctionalInterface
    private interface InputParser<T> {

        T parse(String input) throws IllegalArgumentException;
    }
}
