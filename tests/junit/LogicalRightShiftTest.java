package junit;

import junit.framework.TestCase;
import pass.LogicalRightShift;

public class LogicalRightShiftTest extends TestCase {
	private LogicalRightShift logicalRightShift;

	protected void setUp() throws Exception {
		super.setUp();
		logicalRightShift = new LogicalRightShift();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLogicalRightShift() {
		this.assertEquals(logicalRightShift.logicalRightShift(42, 1), 21);
		this.assertEquals(logicalRightShift.logicalRightShift(12, 2), 3);
	}
}
