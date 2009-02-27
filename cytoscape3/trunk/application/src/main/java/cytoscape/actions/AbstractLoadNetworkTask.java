/*
 File: AbstractLoadNetworkTask.java

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

package cytoscape.actions;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;

import cytoscape.CyNetworkManager;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;
import cytoscape.util.CyNetworkNaming;

/**
 * Task to load a new network.
 */
abstract class AbstractLoadNetworkTask implements Task {

	protected CyReader reader;
	protected URI uri;
	protected TaskMonitor taskMonitor;
	protected String name;
	protected Thread myThread = null;
	protected boolean interrupted = false;
	protected CyLayoutAlgorithm layoutAlgorithm;
	protected CyReaderManager mgr;
	protected GraphViewFactory gvf;
	protected CyLayouts cyl;
	protected CyNetworkManager netmgr;
	protected Properties props;

	public AbstractLoadNetworkTask(CyLayoutAlgorithm layout,
			CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl,
			CyNetworkManager netmgr, Properties props) {
		this.mgr = mgr;
		this.gvf = gvf;
		this.cyl = cyl;
		this.netmgr = netmgr;
		this.props = props;
		layoutAlgorithm = layout;
	}

	protected void loadNetwork(CyReader reader) {
		if (reader == null)
			taskMonitor.setException(new IOException("Could not read file"),
					"Could not read file");

		myThread = Thread.currentThread();

		taskMonitor.setStatus("Reading in Network Data...");

		try {
			taskMonitor.setPercentCompleted(-1);

			taskMonitor.setStatus("Creating Cytoscape Network...");

			reader.read();

			CyNetwork cyNetwork = reader.getReadData(CyNetwork.class);
			cyNetwork.attrs().set("name",
					CyNetworkNaming.getSuggestedNetworkTitle(name, netmgr));
			GraphView view = gvf.createGraphView(cyNetwork);

			if ((layoutAlgorithm != null) && (view != null)) {
				// Yes, do it
				// Layouts are, in general cancelable
				((JTask) taskMonitor).setCancel(true);
				taskMonitor.setStatus("Performing layout...");
				layoutAlgorithm.doLayout(view, taskMonitor);
				taskMonitor.setStatus("Layout complete");
			} else {
				cyl.getDefaultLayout().doLayout(view);
			}
			// TODO NEED RENDERER
			view.fitContent();

			netmgr.addNetwork(cyNetwork);
			netmgr.addNetworkView(view);

			if (cyNetwork != null) {
				informUserOfGraphStats(cyNetwork);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not read network from: ");
				sb.append(name);
				sb.append("\nThis file may not be a valid file format.");
				taskMonitor.setException(new IOException(sb.toString()), sb
						.toString());
			}

			taskMonitor.setPercentCompleted(100);
		} catch (Exception e) {
			taskMonitor.setException(e, "Unable to load network.");

			return;
		} finally {
			reader = null;
		}
	}

	abstract public void run();

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfGraphStats(CyNetwork newNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Successfully loaded network from:  ");
		sb.append(name);
		sb.append("\n\nNetwork contains "
				+ formatter.format(newNetwork.getNodeCount()));
		sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
		sb.append(" edges.\n\n");

		String thresh = props.getProperty("viewThreshold");

		if (newNetwork.getNodeCount() < Integer.parseInt(thresh)) {
			sb.append("Network is under " + thresh
					+ " nodes.  A view will be automatically created.");
		} else {
			sb.append("Network is over " + thresh
					+ " nodes.  A view has not been created."
					+ "  If you wish to view this network, use "
					+ "\"Create View\" from the \"Edit\" menu.");
		}

		taskMonitor.setStatus(sb.toString());
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
		System.out.println("Halt called");

		if (myThread != null) {
			myThread.interrupt();
			this.interrupted = true;
			((JTask) taskMonitor).setDone();
		}
	}

	/**
	 * Sets the Task Monitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Loading Network");
	}
}
