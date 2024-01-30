package Assignment2;

public class KaprekarResultPrinter {

    public static void print(boolean isKaprekarConstant) {
        if (!isKaprekarConstant) {
            System.out.println("The number is not a Kaprekar Constant, as all the digits of the number are identical.");
        } else {
            System.out.println("The number is a Kaprekar Constant.");
        }
    }
}
