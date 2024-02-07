package Assignment2;
public class KaprekarNumberProcess {

    private static final int SIZE_OF_ARRAY = 4;
    private static final int ITERATION_COUNT = 7;
    public static int iteration = 0;

    public static int isKaprekarConstant(int number) {
        int[] arrayOfNumber = new int[SIZE_OF_ARRAY];
        KaprekarNumberArrayProcess processor = new KaprekarNumberArrayProcess();

        for (iteration = 0; iteration < ITERATION_COUNT; iteration++) {
            processor.storeNumberInArray(number, arrayOfNumber);
            processor.sort(arrayOfNumber);

            int descendingNumber = processor.getDescendingNumber(arrayOfNumber);
            int ascendingNumber = processor.getAscendingNumber(descendingNumber);
            number = descendingNumber - ascendingNumber;

            System.out.println(descendingNumber + " - " + ascendingNumber + " = " + number);
            number = ValidateNumber.validateAndAdjustNumber(number);
        }
        return iteration;
    }
}
