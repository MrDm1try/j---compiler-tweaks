package junit;

import junit.framework.TestCase;
import pass.ExclusiveOr;

public class ExclusiveOrTest extends TestCase {
	private ExclusiveOr exclusiveOr;

	protected void setUp() throws Exception {
		super.setUp();
		exclusiveOr = new ExclusiveOr();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExclusiveOr() {
		this.assertEquals(exclusiveOr.exclusiveOr(5, 3), 6);
		this.assertEquals(exclusiveOr.exclusiveOr(2, 10), 8);
	}
}
