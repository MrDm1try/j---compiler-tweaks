import java.lang.Exception;

class ExceptionOne extends Exception {}
class ExceptionTwo extends Exception {}

public class ExceptionHandlers {
	public ExceptionHandlers() throws ExceptionOne {
		
	}
	
    private static void f() throws ExceptionOne, ExceptionTwo {
        throw new ExceptionOne();
    }
    
    public static void main(String[] args) {
        try {
            f();
        }
        catch (ExceptionOne e1) { ; }
        catch (ExceptionTwo e2) { ; }
        finally { ; }
    }
}
