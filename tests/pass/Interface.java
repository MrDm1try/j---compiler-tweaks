interface A {
    public int f(int x);
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
