
package cytoscape.layout.label;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LayoutPartitionTest {

	Object o;

	/**
	 * Called before each Test is run. This is where you should construct any
	 * objects or data needed by your tests.  
	 */
	@Before
	public void setUp() {
		o = new Object();
	}

	/**
	 * Called after each Test is run.  Often this isn't used, but sometimes
	 * a test will change the state of something that you need to clean up.
	 */
	@After
	public void tearDown() {
		o = null;
	}

	/**
	 * This is a single test. Any objects or data you need should either be
	 * local to the class (initialized in @Before) or local to this method.
	 */
	@Test
	public void testSomething() {
		assertEquals(2,1+1);
	}

	/**
	 * This is a separate test. A test class (or test case) can have as 
	 * many tests as needed.  Test methods should be short and focused on
	 * testing one thing only.  It's better to have many short test methods than
	 * a few long methods.  This will help with debugging later on.
	 */
	@Test
	public void testSomethingElse() {
		assertNotNull( o );
	}

}
	

