import java.lang.Exception;
import java.lang.System;

interface A {
	double y = 2.0;
    public int f(int x);
}

interface AA {
	String s = "s";
}

interface AAA extends A, AA {
    public double g(double x) throws Exception;
    public static double z = y;
}

abstract class B {
	abstract public String hi();
	
	public String hi2() {
		return "hihi";
	}
}

class BB extends B {
	int a = 1;
	
	public String hi() {
		return "Wello Horld" + hi2();
	}
}

class C extends BB implements A, AA {	
	public static void main(String[] args) {
		C c = new C();
		System.out.println(c.hi());
		System.out.println(c.f(c.a));
	}
	
    public int f(int x) {
        return x * x + a;
    }
}

public class D implements AAA {
	
	public static void main(String[] args) {
		D d = new D();
		System.out.println(d.g(2d));
		System.out.println(D.s);
	}
	
    public int f(int x) {
        return x * x;
    }
    
    public double g(double x) {
        return x + y;
    }
 
}
