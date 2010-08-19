
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

package org.cytoscape.model.events;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.model.CyDataTable;

import static org.mockito.Mockito.*;

/**
 * DOCUMENT ME!
 */
public class ColumnCreatedEventTest extends TestCase {

	ColumnCreatedEvent event;
	CyDataTable table;
	final String columnName = "asdf";

	public void setUp() {
		table = mock(CyDataTable.class); 
		event = new ColumnCreatedEvent(table,columnName);
	}

	public void testGetColumnName() {
		assertEquals( event.getColumnName(), columnName );
	}

	public void testGetSource() {
		assertEquals( event.getSource(), table );
	}

	public void testGetListenerClass() {
		assertEquals( event.getListenerClass(), ColumnCreatedListener.class );
	}

	public void testNullColumn() {
		try {
			ColumnCreatedEvent ev = new ColumnCreatedEvent(table, null);
		} catch (NullPointerException npe) {
			return;
		}
		fail("didn't catch expected npe for column");
	}

	public void testNullTable() {
		try {
			ColumnCreatedEvent ev = new ColumnCreatedEvent(null, columnName);
		} catch (NullPointerException npe) {
			return;
		}
		fail("didn't catch expected npe for table");
	}
}
