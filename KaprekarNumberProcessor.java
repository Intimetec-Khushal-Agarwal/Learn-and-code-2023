package Assignment2;

public class KaprekarNumberProcessor {

    private static final int SIZE_OF_ARRAY = 4;
    private static final int ITERATION_COUNT = 7;
    private static final int KAPREKAR_CONSTANT = 6174;

    public static boolean isKaprekarConstant(int number) {
        int[] arrayOfNumber = new int[SIZE_OF_ARRAY];
        KaprekarNumberArrayProcessor processor = new KaprekarNumberArrayProcessor();

        for (int iteration = 0; iteration < ITERATION_COUNT; iteration++) {
            processor.storeNumberInArray(number, arrayOfNumber);
            processor.sort(arrayOfNumber);

            int descendingNumber = processor.convertArrayToNumber(arrayOfNumber);
            int ascendingNumber = processor.reverseDescendingNumber(descendingNumber);

            number = descendingNumber - ascendingNumber;

            System.out.println(descendingNumber + " - " + ascendingNumber + " = " + number);

            if (ascendingNumber == descendingNumber) {
                return false;
            }

            if (number == KAPREKAR_CONSTANT) {
                return true;
            }

            number = ValidateInput.validateAndAdjustNumber(number);
        }

        return false;
    }
}
