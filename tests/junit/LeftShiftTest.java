package junit;

import junit.framework.TestCase;
import pass.LeftShift;

public class LeftShiftTest extends TestCase {
	private LeftShift leftShift;

	protected void setUp() throws Exception {
		super.setUp();
		leftShift = new LeftShift();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLeftShift() {
		this.assertEquals(leftShift.leftShift(23, 1), 46);
		this.assertEquals(leftShift.leftShift(23, 2), 92);
	}
}
