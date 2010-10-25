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

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;

import java.util.List;
import java.util.ArrayList;

public class AbstractSelectTaskTester {

	CyNetworkManager netmgr;
	TaskMonitor tm;
	CyNetwork net; 
	CyRow r1;
	CyEdge e1;
	CyRow r2;
	CyEdge e2;
	CyRow r3;
	CyNode e3;
	CyRow r4;
	CyNode e4;

	public void setUp() throws Exception {
		net = mock(CyNetwork.class);

		CyNetworkView view = mock(CyNetworkView.class);
		when(view.getModel()).thenReturn(net);

		netmgr = mock(CyNetworkManager.class);
		when(netmgr.getNetworkView(anyLong())).thenReturn(view);

		tm = mock(TaskMonitor.class);


		r1 = mock(CyRow.class);
		e1 = mock(CyEdge.class);
		when(e1.attrs()).thenReturn(r1);

		r2 = mock(CyRow.class);
		e2 = mock(CyEdge.class);
		when(e2.attrs()).thenReturn(r2);

		List<CyEdge> el = new ArrayList<CyEdge>();
		el.add(e1);
		el.add(e2);
		when(net.getEdgeList()).thenReturn(el);

		r3 = mock(CyRow.class);
		e3 = mock(CyNode.class);
		when(e3.attrs()).thenReturn(r3);

		r4 = mock(CyRow.class);
		e4 = mock(CyNode.class);
		when(e4.attrs()).thenReturn(r4);

		List<CyNode> nl = new ArrayList<CyNode>();
		nl.add(e3);
		nl.add(e4);
		when(net.getNodeList()).thenReturn(nl);
	}
}
