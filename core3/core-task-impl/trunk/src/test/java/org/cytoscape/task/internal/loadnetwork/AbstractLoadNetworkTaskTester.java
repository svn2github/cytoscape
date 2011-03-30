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


package org.cytoscape.task.internal.loadnetwork;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.Properties;
import java.net.URI;

import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.property.CyProperty;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Task;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.io.read.CyNetworkViewReader;

public class AbstractLoadNetworkTaskTester {

	URI uri;

    CyNetworkViewReaderManager mgr;
	CyNetworkManager netmgr;
	CyNetworkViewManager networkViewManager;
	CyProperty<Properties> props;
	CyNetworkNaming namingUtil;

	CyNetwork net; 
	CyNetworkView view; 
	CyNetworkView[] views; 
	CyNetworkViewReader reader; 

	public void setUp() throws Exception {
		CyRow attrs = mock(CyRow.class);

		net = mock(CyNetwork.class);
		when(net.getNodeCount()).thenReturn(2);
		when(net.getEdgeCount()).thenReturn(1);
		when(net.getCyRow()).thenReturn(attrs);

		view = mock(CyNetworkView.class);
		when(view.getModel()).thenReturn(net);

		views = new CyNetworkView[] {view};

		reader = mock(CyNetworkViewReader.class);
		when(reader.getNetworkViews()).thenReturn(views);

		mgr = mock(CyNetworkViewReaderManager.class);
		when(mgr.getReader(eq(uri),anyString())).thenReturn(reader);

		netmgr = mock(CyNetworkManager.class);
		networkViewManager = mock(CyNetworkViewManager.class);

		Properties p = new Properties();
		p.setProperty("viewThreshold","1000");

		props = mock(CyProperty.class);
		when(props.getProperties()).thenReturn(p);

		namingUtil = mock(CyNetworkNaming.class);
	}
}
