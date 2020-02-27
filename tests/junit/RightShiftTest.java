package junit;

import junit.framework.TestCase;
import pass.RightShift;

public class RightShiftTest extends TestCase {
	private RightShift rightShift;

	protected void setUp() throws Exception {
		super.setUp();
		rightShift = new RightShift();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRightShift() {
		this.assertEquals(rightShift.rightShift(-105, 1), -53);
		this.assertEquals(rightShift.rightShift(23, 2), 5);
	}
}
