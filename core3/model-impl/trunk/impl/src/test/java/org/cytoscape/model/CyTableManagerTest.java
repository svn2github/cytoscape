/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import java.util.Map;


import org.cytoscape.model.internal.CyTableManagerImpl;
import org.cytoscape.model.internal.CyTableFactoryImpl;
import org.cytoscape.model.internal.CyTableImpl;
import org.cytoscape.model.internal.ArrayGraph;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.equations.internal.interpreter.InterpreterImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;

import org.junit.Test;


public class CyTableManagerTest extends AbstractCyTableManagerTest {
	CyTableManagerImpl mgrImpl;

	public void setUp() {
		super.setUp();
		mgrImpl = new CyTableManagerImpl();
		mgr = mgrImpl; 
		CyEventHelper eh = new DummyCyEventHelper();
		final Interpreter interpreter = new InterpreterImpl();
		goodNetwork =
			new ArrayGraph(eh, mgrImpl,
				       new CyTableFactoryImpl(eh, mgrImpl, interpreter), true).getBaseNetwork();
	}

	public void tearDown() {
		mgr = null;
		goodNetwork = null;
	}

	public void immutableTableTest() {
		boolean exceptionWasThrown = false;
		try {
			mgr.deleteTable(goodNetwork.getDefaultNodeTable().getSUID());
		} catch (IllegalArgumentException e) {
			exceptionWasThrown = true;
		}
		assertTrue(exceptionWasThrown);
	}

	public void tableWithVirtColumnDeletionTest() {
		CyEventHelper eventHelper = new DummyCyEventHelper();
		final Interpreter interpreter = new InterpreterImpl();
		CyTable table = new CyTableImpl("homer", "SUID", Long.class, true, true, eventHelper,
						interpreter);
		CyTable table2 = new CyTableImpl("marge", "SUID", Long.class, true, true, eventHelper,
						 interpreter);

		table.createColumn("x", Long.class, false);
		CyColumn column = table.getColumn("x");
		assertNull(column.getVirtualTable());
		table2.createColumn("x2", Long.class, false);
		table2.createListColumn("b", Boolean.class, false);
		table.addVirtualColumn("b1", "b", table2, "x2", "x", true);

		mgrImpl.addTable(table);
		boolean caughtException = false;
		try {
			mgr.deleteTable(table.getSUID());
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
		table2.deleteColumn("b1");
		mgr.deleteTable(table.getSUID());
	}
}
