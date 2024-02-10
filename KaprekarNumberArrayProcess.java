package Assignment2;

public class KaprekarNumberArrayProcess {

    public void storeNumberInArray(int number, int[] arrayOfNumber) {
        int index = 0;

        while (number != 0) {
            arrayOfNumber[index] = number % 10;
            index++;
            number /= 10;
        }
    }

    public void sort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = array.length - 1; j > i; j--) {
                if (array[i] < array[j]) {
                    int temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                }
            }
        }
    }

    public int getDescendingNumber(int[] arrayOfNumber) {
        int number = arrayOfNumber[0];
        for (int index = 1; index < arrayOfNumber.length; index++) {
            number = number * 10 + arrayOfNumber[index];
        }
        return number;
    }

    public int getAscendingNumber(int number) {
        int reverseNumber = 0;
        while (number != 0) {
            reverseNumber = reverseNumber * 10 + number % 10;
            number /= 10;
        }
        return reverseNumber;
    }
}
