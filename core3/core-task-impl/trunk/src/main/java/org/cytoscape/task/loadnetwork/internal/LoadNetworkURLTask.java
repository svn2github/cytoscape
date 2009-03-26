/*
 File: LoadNetworkURLTask.java

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

// $Revision: 8703 $
// $Date: 2006-11-06 23:17:02 -0800 (Mon, 06 Nov 2006) $
// $Author: pwang $
package org.cytoscape.task.loadnetwork.internal;

import java.net.URL;
import java.util.Properties;

import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import cytoscape.CyNetworkManager;
import cytoscape.util.CyNetworkNaming;

/**
 * Specific instance of AbstractLoadNetworkTask that loads a URL.
 */
public class LoadNetworkURLTask extends AbstractLoadNetworkTask {

	@Tunable(description="The URL to load")
	public URL url;

	public LoadNetworkURLTask(CyReaderManager mgr, GraphViewFactory gvf,
			CyLayouts cyl, CyNetworkManager netmgr, Properties props, CyNetworkNaming namingUtil) {
		super(mgr, gvf, cyl, netmgr, props, namingUtil);
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;

		if (url == null)
			throw new NullPointerException("Network url is null");

		name = url.toString();

		myThread = Thread.currentThread();

		try {
			taskMonitor.setStatusMessage("Opening url " + url);
			reader = mgr.getReader(url.toURI(),DataCategory.NETWORK);

			if (interrupted)
				return;

		} catch (Exception e) {
			url = null;
			throw new Exception("Unable to connect to URL " + name, e); 
		}

		loadNetwork(reader);
	}
}
