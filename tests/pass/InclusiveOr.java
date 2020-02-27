package pass;

import java.lang.Integer;
import java.lang.System;

public class InclusiveOr {
	public int inclusiveOr(int x, int y) {
		return x | y;
	}
	
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println(a | b);
    }
}
