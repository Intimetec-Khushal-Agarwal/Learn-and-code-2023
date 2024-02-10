package Assignment2;

public class KaprekarNumberProcess {
    public static int iteration = 0;

    public static int isKaprekarConstant(int number) {
        int[] arrayOfNumber = new int[Constants.SIZE_OF_ARRAY];
        KaprekarNumberArrayProcess processor = new KaprekarNumberArrayProcess();

        for (iteration = 0; iteration < Constants.ITERATION_COUNT; iteration++) {
            processor.storeNumberInArray(number, arrayOfNumber);
            processor.sort(arrayOfNumber);

            int descendingNumber = processor.getDescendingNumber(arrayOfNumber);
            int ascendingNumber = processor.getAscendingNumber(descendingNumber);
            
            if (descendingNumber == ascendingNumber) {
                break;
            }
            number = descendingNumber - ascendingNumber;

            System.out.println(descendingNumber + " - " + ascendingNumber + " = " + number);
            
            number = ValidateNumber.validateAndAdjustNumber(number);
        }
        return iteration;
    }
}
