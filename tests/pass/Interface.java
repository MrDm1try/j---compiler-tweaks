import java.lang.Exception;

interface A {
	int y = 5;
    public int f(int x);
}

public interface AA {
    public int g(int x) throws Exception;
    int z = 5;
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
    public int g(int x) {
        return x + z;
    }
}
