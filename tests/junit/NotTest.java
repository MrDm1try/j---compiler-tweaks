package junit;

import junit.framework.TestCase;
import pass.Not;

public class NotTest extends TestCase {
	private Not not;

	protected void setUp() throws Exception {
		super.setUp();
		not = new Not();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRemainder() {
		this.assertEquals(not.not(7), -8);
		this.assertEquals(not.not(171), -172);
	}
}
