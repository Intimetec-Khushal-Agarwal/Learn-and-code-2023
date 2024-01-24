package Assignment2;

public class KaprekarResultPrinter {
	
	public static void print(boolean check){
		if(check == false){
			System.out.println("The number is not a Karprekar Constant as all the digits of number are identical");
		}
		if(check == true){
			System.out.println("The number is Karprekar Constant");
		}
	}
}
