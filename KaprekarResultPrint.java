package Assignment2;

public class KaprekarResultPrint {

    public static void print(int iteration) {
        if (iteration != 0) {
            System.out.println("The number is a Kaprekar Constant.");
            System.out.println("It took " + iteration + " iterations to reach the Kaprekar constant.");
        } else {
            System.out.println("The number is not a Kaprekar Constant, as all the digits of the number are identical.");
        }
    }
}
