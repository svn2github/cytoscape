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
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Task;

public class AllFactoryTest {

	CyNetworkManager netmgr;
	CyNetwork net;

	@Before
	public void setUp() throws Exception {
		net = mock(CyNetwork.class);
		netmgr = mock(CyNetworkManager.class);
	}

	@Test
	public void testDeselectAllEdgesTaskFactory() {
		executeTest( new DeselectAllEdgesTaskFactory(netmgr) );
	}

	@Test
	public void testDeselectAllNodesTaskFactory() {
		executeTest( new DeselectAllNodesTaskFactory(netmgr) );
	}

	@Test
	public void testDeselectAllTaskFactory() {
		executeTest( new DeselectAllTaskFactory(netmgr) );
	}

	@Test
	public void testInvertSelectedEdgesTaskFactory() {
		executeTest( new InvertSelectedEdgesTaskFactory(netmgr) );
	}

	@Test
	public void testInvertSelectedNodesTaskFactory() {
		executeTest( new InvertSelectedNodesTaskFactory(netmgr) );
	}

	@Test
	public void testSelectAdjacentEdgesTaskFactory() {
		executeTest( new SelectAdjacentEdgesTaskFactory(netmgr) );
	}

	@Test
	public void testSelectAllEdgesTaskFactory() {
		executeTest( new SelectAllEdgesTaskFactory(netmgr) );
	}

	@Test
	public void testSelectAllNodesTaskFactory() {
		executeTest( new SelectAllNodesTaskFactory(netmgr) );
	}

	@Test
	public void testSelectAllTaskFactory() {
		executeTest( new SelectAllTaskFactory(netmgr) );
	}

	@Test
	public void testSelectConnectedNodesTaskFactory() {
		executeTest( new SelectConnectedNodesTaskFactory(netmgr) );
	}

	@Test
	public void testSelectFirstNeighborsTaskFactory() {
		executeTest( new SelectFirstNeighborsTaskFactory(netmgr) );
	}

	@Test
	public void testSelectFromFileListTaskFactory() {
		executeTest( new SelectFromFileListTaskFactory(netmgr) );
	}


	private void executeTest(NetworkTaskFactory ntf) {
		ntf.setNetwork(net);
		TaskIterator ti = ntf.getTaskIterator();
		assertNotNull(ti);
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );
	}
}
