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
package org.cytoscape.task.internal.loadnetwork;

import java.net.URL;
import java.io.IOException;
import java.util.Properties;

import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
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
	

	StreamUtil streamUtil;

	static String BAD_INTERNET_SETTINGS_MSG = "<html><p>Cytoscape has failed to connect to the URL. Please ensure that:</p><p><ol><li>the URL is correct,</li><li>your computer is able to connect to the Internet, and</li><li>your proxy settings are correct.</li></ol></p><p>The reason for the failure is: %s</html>";

	public LoadNetworkURLTask(CyReaderManager mgr, CyNetworkViewFactory gvf,
			CyLayouts cyl, CyNetworkManager netmgr, Properties props,
			CyNetworkNaming namingUtil, StreamUtil streamUtil) {
		super(mgr, gvf, cyl, netmgr, props, namingUtil);
		this.streamUtil = streamUtil;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (url == null)
			throw new NullPointerException("url is null");

		myThread = Thread.currentThread();
		this.taskMonitor = taskMonitor;
		name = url.toString();

		taskMonitor.setTitle(String.format("Loading Network from \'%s\'", name));

		taskMonitor.setStatusMessage("Checking URL...");
		try
		{
			streamUtil.getURLConnection(url).connect();
		}
		catch (IOException e)
		{
			throw new Exception(String.format(BAD_INTERNET_SETTINGS_MSG, e.getMessage()), e);
		}

		taskMonitor.setStatusMessage("Reading network...");
		reader = mgr.getReader(url.toURI(),DataCategory.NETWORK);

		taskMonitor.setStatusMessage("Loading network...");
		loadNetwork(reader);
	}
}
