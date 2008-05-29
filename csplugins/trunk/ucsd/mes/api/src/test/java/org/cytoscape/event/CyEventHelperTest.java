
package org.cytoscape.event;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;

public class CyEventHelperTest extends TestCase {

	public static Test suite() {
		return new TestSuite(CyEventHelperTest.class);
	}

	MockListenerProvider lp;

	public void setUp() {
		// MockCyEvent MockCyEventListener MockListenerProvider
		
		lp = new MockListenerProvider(3);
	}

	public void tearDown() {
	}

	public void testSychronous() {
		CyEventHelper.fireSynchronousEvent( new MockCyEvent("homer"), lp );
		for ( MockCyEventListener c : lp.getListeners() )
			assertEquals( 1, c.getNumCalls() );
	}

	public void testASychronous() {
		try {
		CyEventHelper.fireAsynchronousEvent( new MockCyEvent("marge"), lp );
		Thread.sleep(500); // TODO is there a better way to wait?
		for ( MockCyEventListener c : lp.getListeners() )
			assertEquals( 1, c.getNumCalls() );
		} catch (InterruptedException ie) { ie.printStackTrace(); fail(); }
	}
}
