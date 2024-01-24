package Assignment2;

public class KaprekarConstant {
    public static void main(String[] args) {
    	
    	int userInputNumber = 0;
        boolean isKaprekarConstant= false;
    
        userInputNumber = ValidateInput.userInput();
        isKaprekarConstant = KaprekarNumberProcessor.isKaprekarConstant(userInputNumber);
        KaprekarResultPrinter.print(isKaprekarConstant);
    
        return;    
    }
}
