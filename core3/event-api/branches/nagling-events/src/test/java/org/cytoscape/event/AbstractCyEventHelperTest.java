
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

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


/**
 */
public abstract class AbstractCyEventHelperTest {

	protected CyEventHelper helper;
	protected StubCyListener service;
	protected StubCyPayloadListener payloadService;

	@Test
	public void testFireSynchronous() {
		helper.fireEvent(new StubCyEvent("homer",true));
		assertEquals(1, service.getNumCalls());
	}

	@Test
	public void testFireAsynchronous() {
		try {
		helper.fireEvent(new StubCyEvent("marge",false));
		Thread.sleep(500); // TODO is there a better way to wait?
		assertEquals(1, service.getNumCalls());
		} catch ( InterruptedException ie ) { throw new RuntimeException(ie); }
	}

	// This is a performance test that counts the number of events fired in 1 second. 
	// We verify that the microlistener approach is at least 3 times faster than the
	// event/listener combo. 
	/*
	@Test
	public void testLD1second() {
		final long duration = 1000000000;

		long end = System.nanoTime() + duration;
		int syncCount = 0;
		while ( end > System.nanoTime() ) {
			helper.fireEvent(new StubCyEvent("homer",true));
			syncCount++;
		}

		end = System.nanoTime() + duration;
		int asyncCount = 0;
		while ( end > System.nanoTime() ) {
			helper.fireEvent(new StubCyEvent("homer",false));
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
	*/

	@Test
	public void testSynchronousNoInstances() {
		helper.fireEvent(new FakeCyEvent(true));
	}

	@Test
	public void testAsynchronousNoInstances() {
		try {
		helper.fireEvent(new FakeCyEvent(false));
		Thread.sleep(500); // TODO is there a better way to wait?
		} catch ( InterruptedException ie ) { throw new RuntimeException(ie); }
	}

	@Test
	public void testSynchronousSilenced() {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,true));
		assertEquals(0, service.getNumCalls());
	}

	@Test
	public void testAsynchronousSilenced() {
		try {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,false));
		Thread.sleep(500); // TODO is there a better way to wait?
		assertEquals(0, service.getNumCalls());
		} catch ( InterruptedException ie ) { throw new RuntimeException(ie); }
	}

	@Test
	public void testSynchronousSilencedThenUnsilenced() {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,true));
		assertEquals(0, service.getNumCalls());
		helper.unsilenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,true));
		assertEquals(1, service.getNumCalls());
	}

	@Test
	public void testAsynchronousSilencedThenUnsilenced() {
		try {
		String source = "homer";
		helper.silenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,false));
		Thread.sleep(500); // TODO is there a better way to wait?
		assertEquals(0, service.getNumCalls());
		helper.unsilenceEventSource(source);
		helper.fireEvent(new StubCyEvent(source,false));
		Thread.sleep(500); // TODO is there a better way to wait?
		assertEquals(1, service.getNumCalls());
		} catch ( InterruptedException ie ) { throw new RuntimeException(ie); }
	}

	@Test
	public void testAddEventPayload() {
		try {
		helper.addEventPayload("source","homer",StubCyPayloadEvent.class);
		helper.addEventPayload("source","marge",StubCyPayloadEvent.class);
		Thread.sleep(500);
		assertTrue( payloadService.getNumCalls() >= 1 );
		} catch ( InterruptedException ie ) { throw new RuntimeException(ie); }
	}

	@Test(expected=NullPointerException.class)
	public void testAddEventPayloadNullSource() {
		helper.addEventPayload(null,"homer",StubCyPayloadEvent.class);
	}

	@Test(expected=NullPointerException.class)
	public void testAddEventPayloadNullPayload() {
		helper.addEventPayload("source",null,StubCyPayloadEvent.class);
	}

	@Test(expected=NullPointerException.class)
	public void testAddEventPayloadNullEventType() {
		helper.addEventPayload("source","bart",null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddEventPayloadMismatchedType() {
		helper.addEventPayload("source",new Integer(1),StubCyPayloadEvent.class);
	}
}
