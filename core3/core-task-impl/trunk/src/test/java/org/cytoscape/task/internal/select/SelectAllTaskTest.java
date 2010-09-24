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


package org.cytoscape.task.internal.select;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.ArrayList;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

public class SelectAllTaskTest extends AbstractSelectTaskTester {


	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testRun() throws Exception {
		// configure the mocks
		CyNetwork net = mock(CyNetwork.class);

		CyRow r1 = mock(CyRow.class);
		CyEdge e1 = mock(CyEdge.class);
		when(e1.attrs()).thenReturn(r1);

		CyRow r2 = mock(CyRow.class);
		CyEdge e2 = mock(CyEdge.class);
		when(e2.attrs()).thenReturn(r2);

		List<CyEdge> el = new ArrayList<CyEdge>();
		el.add(e1);
		el.add(e2);
		when(net.getEdgeList()).thenReturn(el);

		CyRow r3 = mock(CyRow.class);
		CyNode e3 = mock(CyNode.class);
		when(e3.attrs()).thenReturn(r3);

		CyRow r4 = mock(CyRow.class);
		CyNode e4 = mock(CyNode.class);
		when(e4.attrs()).thenReturn(r4);

		List<CyNode> nl = new ArrayList<CyNode>();
		nl.add(e3);
		nl.add(e4);
		when(net.getNodeList()).thenReturn(nl);


		// run the task
		Task t = new SelectAllTask(net,netmgr);
		t.run(tm);

		// check that the expected rows were set
		verify(r1, times(1)).set("selected",true);
		verify(r2, times(1)).set("selected",true);
		verify(r3, times(1)).set("selected",true);
		verify(r4, times(1)).set("selected",true);
	}
}
