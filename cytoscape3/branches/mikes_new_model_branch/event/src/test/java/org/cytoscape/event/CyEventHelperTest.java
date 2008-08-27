
package org.cytoscape.event;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;

import org.springframework.osgi.mock.*;
import org.osgi.framework.*;

import org.cytoscape.event.internal.*;

public class CyEventHelperTest extends TestCase {

	public static Test suite() {
		return new TestSuite(CyEventHelperTest.class);
	}

	CyEventHelper helper;
	ServiceReference reference;
	BundleContext bc;
	StubCyEventListenerImpl service;

	public void setUp() {
		service = new StubCyEventListenerImpl();
		reference = new MockServiceReference();
		bc = new MockBundleContext() {
				public ServiceReference getServiceReference(String clazz) {
					return reference;
				}

				public ServiceReference[] getServiceReferences(String clazz, String filter) 
						throws InvalidSyntaxException {
					return new ServiceReference[] { reference };
				}
				
				public Object getService(ServiceReference ref) {
					if (reference == ref)
					   return service;
					return super.getService(ref);
				}
			};

		helper = new CyEventHelperImpl(bc);
	}

	public void tearDown() {
	}

	public void testSychronous() {

		// TODO figure out why I need to cast the StubCyEventImpl
		helper.fireSynchronousEvent( (StubCyEvent) new StubCyEventImpl("homer"), StubCyEventListener.class );
		assertEquals( 1, service.getNumCalls() );

	}

	public void testASychronous() {
		try {
		// TODO figure out why I need to cast the StubCyEventImpl
		helper.fireAsynchronousEvent( (StubCyEvent) new StubCyEventImpl("marge"), StubCyEventListener.class );
		Thread.sleep(500); // TODO is there a better way to wait?
		assertEquals( 1, service.getNumCalls() );
		} catch (InterruptedException ie) { ie.printStackTrace(); fail(); }
	}
}
