package csplugins.isb.dtenenbaum.sharedData.tests;

import csplugins.isb.dtenenbaum.sharedData.*;
import junit.framework.*;

/**
 * A series of unit tests of the SharedDataSingleton object, using the
 * JUnit framework.
 * Note that junit.jar is now in $/csplugins/lib (in cvsdir4 anyway)
 * to facilitate and encourage its use in unit testing.
 *
 * @author Dan Tenenbaum  
 */
public class SharedDataTest extends TestCase {

	public SharedDataTest(String name) { 
		super(name);
	}
	
	private SharedDataSingleton sds = SharedDataSingleton.getInstance();
	private SharedDataSingleton sds2 = SharedDataSingleton.getInstance();
	
	
	/**
	 * Make sure neither of two instances are null. 
	 *
	 */
	public void testNotNull() {
		Assert.assertFalse((null == sds) || (null == sds2));
	}
	
	/**
	 * Make sure that two instances are actually the same object.
	 *
	 */
	public void testUnique() {
		Assert.assertEquals(true, sds == sds2);
	}
	
	/**
	 * Make sure that the instance can be written to.
	 *
	 */
	public void canPut() {
		sds.put("foo","bar");
	}
	
	/**
	 * Make sure that objects can be retrieved, and that they
	 * match what is expected. Note that the get() is performed
	 * upon an object with a different name than the object used
	 * here for the get() operation, underscoring the fact that 
	 * we are really dealing with a single object here.
	 *
	 */
	public void canGet() {
		 String value = (String)sds2.get("foo");
		 Assert.assertTrue(value.equals("bar"));
	}
}
