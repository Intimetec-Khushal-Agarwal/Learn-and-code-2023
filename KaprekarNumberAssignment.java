package Assignment2;

public class KaprekarNumberAssignment {

    public static void main(String[] args) {
        int userInputNumber = 0;
        int iteration = 0;

        userInputNumber = UserInput.getUserInput();
        iteration = KaprekarNumberProcess.isKaprekarConstant(userInputNumber);
        KaprekarResultPrint.print(iteration);
    }
}
