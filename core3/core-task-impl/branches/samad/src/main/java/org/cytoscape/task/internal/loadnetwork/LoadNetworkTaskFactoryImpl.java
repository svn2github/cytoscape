/*
 File: LoadNetworkFileTask.java

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

import java.io.File;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.io.CyIOFactoryManager;
import org.cytoscape.io.read.CyNetworkViewReaderFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

/**
 * Specific instance of AbstractLoadNetworkTask that loads a File.
 */
public class LoadNetworkTaskFactoryImpl implements TaskFactory {
	CyNetworkFactory networkFactory;
	CyNetworkViewFactory networkViewFactory;
	CyLayouts layouts;
	CyIOFactoryManager<CyNetworkViewReaderFactory> manager;
	StreamUtil streamUtil;
	CyNetworkManager netManager;
	CyNetworkNaming networkNaming;

	public LoadNetworkTaskFactoryImpl(CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory, CyLayouts layouts, CyIOFactoryManager<CyNetworkViewReaderFactory> manager, StreamUtil streamUtil, CyNetworkManager netManager, CyNetworkNaming networkNaming)
	{
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.layouts = layouts;
		this.manager = manager;
		this.streamUtil = streamUtil;
		this.netManager = netManager;
		this.networkNaming = networkNaming;
	}

	public Task getTask()
	{
		return new LoadNetworkTask(networkFactory, networkViewFactory, layouts, manager, streamUtil, netManager, networkNaming);
	}
}

