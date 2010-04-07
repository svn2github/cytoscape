
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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;

import java.util.List;


/**
 */
public abstract class AbstractCyEventHelperTest extends TestCase {

	protected CyEventHelper helper;
	protected StubCyListener service;
	protected StubCyMicroListener microListener;

	final protected MicroEventSource microEventSource = new MicroEventSource(); 

	/**
	 *  DOCUMENT ME!
	 */
	public void testSynchronous() {
		// TODO figure out why I need to cast the StubCyEventImpl
		helper.fireSynchronousEvent((StubCyEvent) new StubCyEventImpl("homer"),
		                            StubCyListener.class);
		assertEquals(1, service.getNumCalls());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAsynchronous() {
		try {
			// TODO figure out why I need to cast the StubCyEventImpl
			helper.fireAsynchronousEvent((StubCyEvent) new StubCyEventImpl("marge"),
			                             StubCyListener.class);
			Thread.sleep(500); // TODO is there a better way to wait?
			assertEquals(1, service.getNumCalls());
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			fail();
		}
	}

	public void testGetMicroListener() {
		microEventSource.testFire( helper, 5 );
		assertEquals("number of calls", 1,microListener.getNumCalls());
		assertEquals("value of event", 5,microListener.getEventValue());
	}
}
