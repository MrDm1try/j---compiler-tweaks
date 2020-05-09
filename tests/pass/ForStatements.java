import java.lang.System;

public class ForStatements {
    public static void main(String[] args) {
        int sum1 = 0, sum2 = 0;
        for (int i = 1, j = 5; i <= 5 && j <= 9; i++, j++) {
            sum1 += i;
        }
        for (int i = 6; i <= 10; i++) {
            sum1 += i;
        }
        System.out.println(sum1);
//        int[][] as = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
//        for (int[] a : as) {
//	        for (int i : a) {
//	            sum2 += i;
//	        }
//        }
//        System.out.println(sum2);
//        System.out.println(sum1 == sum2);
//        
        for (;;) {
        	System.out.println("infinity");
        }
    }
}
