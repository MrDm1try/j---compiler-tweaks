import java.lang.System;
import java.util.ArrayList;

public class ForStatements {
    public static void main(String[] args) {
        int sum1 = 0, sum2 = 0;
        for (int i = 1, j = 5; i <= 5 && j <= 9; i++, j++) {
            sum1 += 1;
        }
        for (int i = 6; i <= 8; i++) {
        	for (int j = 9; j <= 10; j++) {
                sum1 += 1;
        	}
    	}
        System.out.println(sum1);
        int[] is = {1, 2, 3, 4, 5};
        for (int i : is) {
            sum2 += i;
        }
        int[][] as = {{6, 7, 8}, {9, 10}};
        for (int[] a : as) {
	        for (int i : a) {
	            sum2 += i;
	        }
        }
        System.out.println(sum2);
        System.out.println(sum1 == sum2);
        
        ArrayList list = new ArrayList();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        
        for (Object i : list) {
        	System.out.println("found object");
        }
        
        for (;;) {
        	System.out.println("infinity");
        }
    }
}
