import java.lang.Integer;
import java.lang.System;

public class ConditionalExpression {
    public static void main(String[] args) {
    	int x = 5;
    	System.out.print(x);
		System.out.println(" is " + (x % 2 == 0 ? "even" : "odd"));
    	x = 8;
    	System.out.print(x);
		System.out.println(" is " + (x % 2 == 0 ? "even" : "odd"));

		// Test me when for loops are done
		// for (int x = 0; x < 10; x++) {
		//     System.out.print(x);
		//     System.out.println(" is " + (x % 2 == 0 ? "even" : "odd"));
		// }
    }
}
