package Assignment2;

public class KaprekarNumberArrayProcessor {

    public void storeNumberInArray(int number, int[] arrayOfNumber) {
        int temp = number;
        int index = 0;

        while (temp != 0) {
            number = temp % 10;
            arrayOfNumber[index] = number;
            index++;
            temp /= 10;
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

    public int convertArrayToNumber(int[] arrayOfNumber) {
        int number = arrayOfNumber[0];
        for (int index = 1; index < arrayOfNumber.length; index++) {
            number = number * 10 + arrayOfNumber[index];
        }
        return number;
    }

    public int reverseDescendingNumber(int number) {
        int temp = 0;
        int reverseNumber = 0;
        while (number != 0) {
            temp = number % 10;
            reverseNumber = reverseNumber * 10 + temp;
            number /= 10;
        }
        return reverseNumber;
    }
}
