package pass;

import java.lang.Integer;
import java.lang.System;

public class UnaryPlus {
	public int unaryPlus(int x) {
		return +x;
	}
	
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        System.out.println(+a);
    }
}
