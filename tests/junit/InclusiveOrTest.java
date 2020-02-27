package junit;

import junit.framework.TestCase;
import pass.InclusiveOr;

public class InclusiveOrTest extends TestCase {
	private InclusiveOr inclusiveOr;

	protected void setUp() throws Exception {
		super.setUp();
		inclusiveOr = new InclusiveOr();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInclusiveOr() {
		this.assertEquals(inclusiveOr.inclusiveOr(5, 3), 7);
		this.assertEquals(inclusiveOr.inclusiveOr(2, 8), 10);
	}
}
