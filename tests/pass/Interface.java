import java.lang.Exception;

interface A {
	double y = 2.0;
    public int f(int x);
}

interface AA {
	String s = "s";
}

public interface AAA extends A, AA {
    public double g(double x) throws Exception;
    public static double z = y;
}

abstract class B {
	abstract public String hi();
}

class BB extends B {
	public String hi() {
		return "Wello Horld";
	}
}

public class C extends B implements A, AA {
	int a = 1;
	
	public String hi() {
		return "hi";
	}
	
    public int f(int x) {
        return x * x + a;
    }
}

public class D implements AAA {
    public int f(int x) {
        return x * x;
    }
    
    public double g(double x) {
        return x + y + z;
    }
 
}
