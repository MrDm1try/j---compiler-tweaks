import java.lang.Exception;
import java.lang.System;

class ExceptionOne extends Exception {}
class ExceptionTwo extends Exception {}

public class ExceptionHandlers {
	public ExceptionHandlers() throws ExceptionOne {
		
	}
	
    private static void g1() { System.out.println("g1");}
    private static void g2() { System.out.println("g2");}
    private static void g3() { System.out.println("not to forget");}

    private static void f() throws ExceptionOne, ExceptionTwo {
        throw new ExceptionOne();
//        throw new ExceptionTwo();
    }
    
    public static void main(String[] args) {
        try {
        	f();
        }
        catch (ExceptionOne e1) { g1(); }
        catch (ExceptionTwo e2) { g2(); }
        finally { g3(); }
    }
}
