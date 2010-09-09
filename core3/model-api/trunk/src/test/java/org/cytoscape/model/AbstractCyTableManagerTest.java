
/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import java.lang.RuntimeException;

import java.util.*;

import static org.mockito.Mockito.*; 


/**
 * DOCUMENT ME!
 */
public abstract class AbstractCyTableManagerTest extends TestCase {

	protected CyTableManager mgr;
	private CyNetwork n;

	public void setUp() {
		n = mock(CyNetwork.class);
	}

	// we want to return null if a table map has not been set
	public void testGetEmptyTableMap() {
		assertNull( mgr.getTableMap("NETWORK",n) );
		assertNull( mgr.getTableMap("NODE",n) );
		assertNull( mgr.getTableMap("EDGE",n) );
	}

	public void testGetSetTableMap() {
	 	assertNotNull(n);	
		checkGetSet("NETWORK");
		checkGetSet("NODE");
		checkGetSet("EDGE");
	}

	private void checkGetSet(String type) {
	 	assertNotNull(n);	
		Map<String, CyTable> map = new HashMap<String,CyTable>();

		mgr.setTableMap(type, n, map);
	
		assertNotNull(mgr.getTableMap(type, n));
		assertEquals(map,mgr.getTableMap(type, n));
	}

	// to clean up a table map
	public void testSetNullTableMap() {
		Map<String, CyTable> map = new HashMap<String,CyTable>();

		mgr.setTableMap("NETWORK", n, map);
	
		assertNotNull(mgr.getTableMap("NETWORK", n));
		assertEquals(map,mgr.getTableMap("NETWORK", n));

		mgr.setTableMap("NETWORK", n, null);

		assertNull(mgr.getTableMap("NETWORK", n));
	}

	public void testNullGetTableMap() {
		assertNull(mgr.getTableMap("NETWORK", null));
	}

	public void testSetNullGraphObjectTableMap() {
		Map<String, CyTable> map = new HashMap<String,CyTable>();
		try {
		mgr.setTableMap("NETWORK", null, map);
		} catch (NullPointerException npe) {
			return;
		}
		fail("did not catch expected exception");
	}
}
