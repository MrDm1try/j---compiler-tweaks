import java.lang.Double;
import java.lang.System;

public class DoubleX {
    public static void main(String[] args) {
        double r = 1.0;
        r += r;
        r += 1.2d;
        double x = 3.14159D * r * r + r - r / r;
        System.out.println(Double.toString(x));
    }
}
