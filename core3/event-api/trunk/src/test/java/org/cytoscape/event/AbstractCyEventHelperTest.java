
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.event;

import junit.framework.TestCase;


/**
 */
public abstract class AbstractCyEventHelperTest extends TestCase {

	protected CyEventHelper helper;
	protected StubCyListener service;

	final protected MicroEventSource microEventSource = new MicroEventSource(); 

	public void testSynchronous() {
		helper.fireSynchronousEvent(new StubCyEvent("homer"));
		assertEquals(1, service.getNumCalls());
	}

	public void testAsynchronous() {
		try {
			helper.fireAsynchronousEvent(new StubCyEvent("marge"));
			Thread.sleep(500); // TODO is there a better way to wait?
			assertEquals(1, service.getNumCalls());
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail();
		}
	}

	// This is a performance test that counts the number of events fired in 1 second. 
	// We verify that the microlistener approach is at least 3 times faster than the
	// event/listener combo. 
	public void testLD1second() {
		final long duration = 1000000000;

		long end = System.nanoTime() + duration;
		int syncCount = 0;
		while ( end > System.nanoTime() ) {
			helper.fireSynchronousEvent((StubCyEvent) new StubCyEvent("homer"));
			syncCount++;
		}

		end = System.nanoTime() + duration;
		int asyncCount = 0;
		while ( end > System.nanoTime() ) {
			helper.fireAsynchronousEvent((StubCyEvent) new StubCyEvent("homer"));
			asyncCount++;
		}
	
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		end = System.nanoTime() + duration;
		int microCount = 0;
		while ( end > System.nanoTime() ) {
			microEventSource.testFire( helper, 5 );
			microCount++;
		}

		System.out.println("syncCount  : " + syncCount);
		System.out.println("asyncCount : " + asyncCount);
		System.out.println("microCount : " + microCount);
		System.out.println("speedup micro/sync : " + ((double)microCount/(double)syncCount));
		System.out.println("speedup micro/async: " + ((double)microCount/(double)asyncCount));
		System.out.println("speedup async/sync : " + ((double)asyncCount/(double)syncCount));

		assertTrue( microCount > (syncCount*3) );
	}

	public void testAddMicroListener() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 1,stub1.getNumCalls());
		assertEquals("value of event", 5,stub1.getEventValue());

		microEventSource.testFire( helper, 10 );
		assertEquals("number of calls", 2,stub1.getNumCalls());
		assertEquals("value of event", 10,stub1.getEventValue());
	}

	// any exception thrown here is a problem
	// firing with no registered listeners should be OK
	public void testAddNullMicroListener() {
		StubCyMicroListener stub1 = null; 
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);
		microEventSource.testFire( helper, 5 );
	}

	// any exception thrown here is a problem
	// at most a message should be logged
	public void testAddNullEventSource() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, null);
	}

	public void testRemoveMicroListener() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 1,stub1.getNumCalls());
		assertEquals("value of event", 5,stub1.getEventValue());

		helper.removeMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 10 );
		// these values should reflect the previous values
		assertEquals("number of calls", 1,stub1.getNumCalls());
		assertEquals("value of event", 5,stub1.getEventValue());
	}

	public void testRemoveMicroListenerFromWrongSource() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 1,stub1.getNumCalls());
		assertEquals("value of event", 5,stub1.getEventValue());

		helper.removeMicroListener(stub1, StubCyMicroListener.class, new Object());

		microEventSource.testFire( helper, 10 );
		// these values should reflect be updated because we removed the listener from
		// the wrong source obj
		assertEquals("number of calls", 2,stub1.getNumCalls());
		assertEquals("value of event", 10,stub1.getEventValue());
	}

	public void testSynchronousNoInstances() {
		helper.fireSynchronousEvent(new FakeCyEvent());
	}

	public void testAsynchronousNoInstances() {
		try {
			helper.fireAsynchronousEvent(new FakeCyEvent());
			Thread.sleep(500); // TODO is there a better way to wait?
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail();
		}
	}

	public void testSynchronousSilenced() {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireSynchronousEvent(new StubCyEvent(source));
		assertEquals(0, service.getNumCalls());
	}

	public void testAsynchronousSilenced() {
		try {
			String source = "homer";
			helper.silenceEventSource(source);
			helper.fireAsynchronousEvent(new StubCyEvent(source));
			Thread.sleep(500); // TODO is there a better way to wait?
			assertEquals(0, service.getNumCalls());
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail();
		}
	}

	public void testAddMicroListenerSilenced() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);
		helper.silenceEventSource(microEventSource);

		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 0,stub1.getNumCalls());

		microEventSource.testFire( helper, 10 );
		assertEquals("number of calls", 0,stub1.getNumCalls());
	}

	public void testSynchronousSilencedThenUnsilenced() {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireSynchronousEvent(new StubCyEvent(source));
		assertEquals(0, service.getNumCalls());
		helper.unsilenceEventSource(source);
		helper.fireSynchronousEvent(new StubCyEvent(source));
		assertEquals(1, service.getNumCalls());
	}

	public void testAsynchronousSilencedThenUnsilenced() {
		try {
			String source = "homer";
			helper.silenceEventSource(source);
			helper.fireAsynchronousEvent(new StubCyEvent(source));
			Thread.sleep(500); // TODO is there a better way to wait?
			assertEquals(0, service.getNumCalls());
			helper.unsilenceEventSource(source);
			helper.fireAsynchronousEvent(new StubCyEvent(source));
			Thread.sleep(500); // TODO is there a better way to wait?
			assertEquals(1, service.getNumCalls());
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail();
		}
	}

	public void testAddMicroListenerSilencedThenUnsilenced() {
		StubCyMicroListener stub1 = new StubCyMicroListenerImpl();	
		helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);
		helper.silenceEventSource(microEventSource);

		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 0,stub1.getNumCalls());

		helper.unsilenceEventSource(microEventSource);

		microEventSource.testFire( helper, 10 );
		assertEquals("number of calls", 1,stub1.getNumCalls());
	}

	public void testMultipleSameClassRowListenersAddedAndRemoved() {

        StubCyMicroListener stub1 = new StubCyMicroListenerImpl();
        helper.addMicroListener(stub1, StubCyMicroListener.class, microEventSource);

        StubCyMicroListener stub2 = new StubCyMicroListenerImpl();
        helper.addMicroListener(stub2, StubCyMicroListener.class, microEventSource);

        StubCyMicroListener stubOther = new OtherStubCyMicroListenerImpl();
        helper.addMicroListener(stubOther, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 10 );

		assertEquals( 1, stub1.getNumCalls() );
		assertEquals( 1, stub2.getNumCalls() );
		assertEquals( 1, stubOther.getNumCalls() );

		microEventSource.testFire( helper, 11 );

		assertEquals( 2, stub1.getNumCalls() );
		assertEquals( 2, stub2.getNumCalls() );
		assertEquals( 2, stubOther.getNumCalls() );

        helper.removeMicroListener(stub1, StubCyMicroListener.class, microEventSource);

		microEventSource.testFire( helper, 12 );

		assertEquals( 2, stub1.getNumCalls() );
		assertEquals( 3, stub2.getNumCalls() );
		assertEquals( 3, stubOther.getNumCalls() );
	}

	private class OtherStubCyMicroListenerImpl implements StubCyMicroListener {
		int called = 0;
		int eventValue = Integer.MIN_VALUE;

		public void handleMicroEvent(int x) {
			called++;
			eventValue = x;
		}
	
		public int getNumCalls() { return called; }
	
		public int getEventValue() { return eventValue; }
	
		public String toString() {
			return "OtherStubCyMicroListenerImpl: " + called + " " + eventValue;
		}
	}
}
