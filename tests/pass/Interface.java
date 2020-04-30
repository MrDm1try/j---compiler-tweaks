import java.lang.Exception;

interface A {
	double y = 2.0;
    public int f(int x);
}

public interface AA {
    public double g(double x) throws Exception;
    public static double z = 5.1;
}

class B {
	public String hi() {
		return "hi";
	}
}

public class C extends B implements A {
    public int f(int x) {
        return x * x;
    }
}

public class D implements A, AA {
    public int f(int x) {
        return x * x;
    }
    
    public double g(double x) {
        return x + y + z;
    }
 
}
