/*

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.view.model.CyNetworkViewFactory;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Task;

import org.cytoscape.property.CyProperty;

import java.util.Properties;

/**
 * Task to load a new network.
 */
public class LoadNetworkURLTaskFactoryImpl implements TaskFactory {

	private CyReaderManager mgr;
	private CyNetworkViewFactory gvf;
	private CyLayouts cyl;
	private CyNetworkManager netmgr;
	private Properties props;
	private StreamUtil streamUtil;

	private CyNetworkNaming cyNetworkNaming;

	public LoadNetworkURLTaskFactoryImpl(CyReaderManager mgr,
			CyNetworkViewFactory gvf, CyLayouts cyl, CyNetworkManager netmgr,
			CyProperty<Properties> cyProps, CyNetworkNaming cyNetworkNaming,
			StreamUtil streamUtil) {
		this.mgr = mgr;
		this.gvf = gvf;
		this.cyl = cyl;
		this.netmgr = netmgr;
		this.props = cyProps.getProperties();
		this.cyNetworkNaming = cyNetworkNaming;
		this.streamUtil = streamUtil;
	}

	public void setNamingUtil(CyNetworkNaming namingUtil) {
		this.cyNetworkNaming = namingUtil;
	}

	public Task getTask() {
		return new LoadNetworkURLTask(mgr, gvf, cyl, netmgr, props, cyNetworkNaming, streamUtil);
	}
}
