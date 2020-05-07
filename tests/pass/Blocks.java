import java.lang.System;

public class Blocks {
	static double x;
	int y;
	
    static {
        x = 3.14159;
    }
    
    public static void main(String[] args) {
    	System.out.println(x);
    	
    	Blocks blocks = new Blocks();
    	System.out.println(blocks.y);
    	
    	BlocksTwo blocksTwo = new BlocksTwo();
    	System.out.println(blocksTwo.x);
    }

    {
        y = 42;
    }
}

class BlocksTwo {
	int x;
	
	{
		x = 24;
	}
	
	public BlocksTwo() {
		// explicit
	}
}
