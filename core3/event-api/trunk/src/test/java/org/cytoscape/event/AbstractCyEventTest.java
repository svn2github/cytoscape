
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
public class AbstractCyEventTest extends TestCase {

	private static class TestEvent<T> extends AbstractCyEvent<T> {
		TestEvent(T src, Class c) {
			super(src,c);
		}
	}

	public void testGetSource() {
		Integer i = new Integer(1);
		TestEvent<Integer> e = new TestEvent<Integer>(i,Integer.class);
		assertEquals( i, e.getSource() ); 
	}

	public void testGetListenerClass() {
		Object i = new Object(); 
		TestEvent<Object> e = new TestEvent<Object>(i,Object.class);
		assertEquals( Object.class, e.getListenerClass() ); 
	}

	public void testNullSource() {
		try {
			new TestEvent<Object>(null, Object.class);
		} catch (NullPointerException npe) {
			return;
		}
		fail("should have thrown an exception");
	}

	public void testNullListenerClass() {
		try {
			new TestEvent<Object>(new Object(), null);
		} catch (NullPointerException npe) {
			return;
		}
		fail("should have thrown an exception");
	}
}
