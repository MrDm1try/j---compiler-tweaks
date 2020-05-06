import java.lang.System;

public class Operators {
    public static void main(String[] args) {
        System.out.println(true && false || true);
        System.out.println(false || false);
        double x = 42.0;
        x -= 2.0;
        System.out.println(x);
        x *= 2.0;
        System.out.println(x);
        x /= 10.0;
        System.out.println(x);
        x %= 3.0;
        System.out.println(x);
    }
}
