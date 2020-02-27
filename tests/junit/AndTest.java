package junit;

import junit.framework.TestCase;
import pass.And;

public class AndTest extends TestCase {
	private And and;

	protected void setUp() throws Exception {
		super.setUp();
		and = new And();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAnd() {
		this.assertEquals(and.and(5, 3), 1);
		this.assertEquals(and.and(3, 2), 2);
		this.assertEquals(and.and(6, 11), 2);
	}
}
