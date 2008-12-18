/*
 File: LoadNetworkTask.java

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
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CySwingApplication;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.util.CyNetworkNaming;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.GraphView;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.view.GraphViewFactory;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Task to load a new network.
 */
public class LoadNetworkTask implements Task {
	/**
	 *  Load a network from a url.  The reader code will attempt to determine
	 *  the format of the network (GML, XGMML, SIF) from the HTTP content-type
	 *  header.  If it is unable to figure it out from there, it will try writing
	 *  the HTTP stream to a file to look at the first couple of bytes.  Note that
	 *  the actual opening of the HTTP stream is postponed until the task is
	 *  initiated to facility the ability of the user to abort the attempt.
	 *
	 * @param u the URL to load the network from
	 * @param skipMessage if true, dispose of the task monitor dialog immediately
	 */
	public static void loadURL(URL u, boolean skipMessage, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		loadURL(u, skipMessage, null, mgr, gvf,cyl, dsk,netmgr);
	}

	/**
	 *  Load a network from a file.  The reader code will attempt to determine
	 *  the format of the network (GML, XGMML, SIF) from the file extension.
	 *  If it is unable to figure it out from there, it will try reading
	 *  the the first couple of bytes from the file.
	 *
	 * @param file the file to load the network from
	 * @param skipMessage if true, dispose of the task monitor dialog immediately
	 */
	public static void loadFile(File file, boolean skipMessage, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		// Create LoadNetwork Task
		loadFile(file, skipMessage, null, mgr, gvf,cyl, dsk,netmgr);
	}

	/**
	 *  Load a network from a url.  The reader code will attempt to determine
	 *  the format of the network (GML, XGMML, SIF) from the HTTP content-type
	 *  header.  If it is unable to figure it out from there, it will try writing
	 *  the HTTP stream to a file to look at the first couple of bytes.  Note that
	 *  the actual opening of the HTTP stream is postponed until the task is
	 *  initiated to facility the ability of the user to abort the attempt.
	 *
	 * @param u the URL to load the network from
	 * @param skipMessage if true, dispose of the task monitor dialog immediately
	 * @param layoutAlgorithm if this is non-null, use this algorithm to lay out the network
	 *                        after it has been read in (provided that a view was created).
	 */
	public static void loadURL(URL u, boolean skipMessage, CyLayoutAlgorithm layoutAlgorithm, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		LoadNetworkTask task = new LoadNetworkTask(u, layoutAlgorithm, mgr, gvf, cyl, dsk,netmgr);
		setupTask(task, skipMessage, true,dsk);
	}

	/**
	 *  Load a network from a file.  The reader code will attempt to determine
	 *  the format of the network (GML, XGMML, SIF) from the file extension.
	 *  If it is unable to figure it out from there, it will try reading
	 *  the the first couple of bytes from the file.
	 *
	 * @param file the file to load the network from
	 * @param skipMessage if true, dispose of the task monitor dialog immediately
	 * @param layoutAlgorithm if this is non-null, use this algorithm to lay out the network
	 *                        after it has been read in (provided that a view was created).
	 */
	public static void loadFile(File file, boolean skipMessage, CyLayoutAlgorithm layoutAlgorithm, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		LoadNetworkTask task = new LoadNetworkTask(file, layoutAlgorithm, mgr, gvf, cyl, dsk,netmgr);
		setupTask(task, skipMessage, true, dsk);
	}

	private static void setupTask(LoadNetworkTask task, boolean skipMessage, boolean cancelable, CytoscapeDesktop desk) {
		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(desk);
		jTaskConfig.displayCloseButton(true);

		if (cancelable)
			jTaskConfig.displayCancelButton(true);

		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(skipMessage);

		// Execute Task in New Thread; pops open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	private URI uri;
	private TaskMonitor taskMonitor;
	private CyNetworkReader reader;
	private String name;
	private URL url;
	private Thread myThread = null;
	private boolean interrupted = false;
	private CyLayoutAlgorithm layoutAlgorithm = null;
	private CyReaderManager mgr; 
	private GraphViewFactory gvf; 
	private CyLayouts cyl; 
	private CytoscapeDesktop dsk; 
	private CyNetworkManager netmgr; 

	private LoadNetworkTask(URL u, CyLayoutAlgorithm layout, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		url = u;
		name = u.toString();
		reader = null;
		layoutAlgorithm = layout;
		this.mgr = mgr;
		this.gvf = gvf;
		this.cyl = cyl;
		this.dsk = dsk;
		this.netmgr = netmgr;

		// Postpone getting the reader since we want to do that in a thread
	}

	private LoadNetworkTask(File file, CyLayoutAlgorithm layout, CyReaderManager mgr, GraphViewFactory gvf, CyLayouts cyl, CytoscapeDesktop dsk, CyNetworkManager netmgr) {
		this.mgr = mgr;
		this.gvf = gvf;
		this.cyl = cyl;
		this.dsk = dsk;
		this.netmgr = netmgr;
		try { 
		reader = mgr.getReader(file.getAbsolutePath());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			reader = null;
		}

		uri = file.toURI();
		name = file.getName();
		layoutAlgorithm = layout;

		if (reader == null) {
			uri = null;
			url = null;
			JOptionPane.showMessageDialog(dsk, "Unable to open file " + name,
			                              "File Open Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		if ((reader == null) && (url == null))
			return;

		myThread = Thread.currentThread();

		if ((reader == null) && (url != null)) {
			try {
				taskMonitor.setStatus("Opening URL " + url);
				reader = mgr.getReader(url);

				if (interrupted)
					return;

				uri = url.toURI();
			} catch (Exception e) {
				uri = null;
				taskMonitor.setException(e, "Unable to connect to URL " + name + ": " + e.getMessage());

				return;
			}

			if (reader == null) {
				uri = null;
				taskMonitor.setException(null, "Unable to connect to URL " + name);

				return;
			}

			// URL is open, things will get very messy if the user cancels the actual
			// network load, so prevent them from doing so
			((JTask) taskMonitor).setCancel(false);
		}

		taskMonitor.setStatus("Reading in Network Data...");

		try {
			taskMonitor.setPercentCompleted(-1);

			taskMonitor.setStatus("Creating Cytoscape Network...");

			reader.read();

			CyNetwork cyNetwork = reader.getReadNetwork(); 
			cyNetwork.attrs().set("name",CyNetworkNaming.getSuggestedNetworkTitle(name,netmgr));
			GraphView view = gvf.createGraphView( cyNetwork );

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

			netmgr.addNetwork( cyNetwork );
			netmgr.addNetworkView( view );

			if (cyNetwork != null) {
				informUserOfGraphStats(cyNetwork);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not read network from: ");
				sb.append(name);
				sb.append("\nThis file may not be a valid file format.");
				taskMonitor.setException(new IOException(sb.toString()), sb.toString());
			}

			taskMonitor.setPercentCompleted(100);
		} catch (Exception e) {
			taskMonitor.setException(e, "Unable to load network.");

			return;
		} finally {
			reader = null;
		}
		
	}

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfGraphStats(CyNetwork newNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Successfully loaded network from:  ");
		sb.append(name);
		sb.append("\n\nNetwork contains " + formatter.format(newNetwork.getNodeCount()));
		sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
		sb.append(" edges.\n\n");

		if (newNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
		                                                              .getProperty("viewThreshold"))) {
			sb.append("Network is under "
			          + CytoscapeInit.getProperties().getProperty("viewThreshold")
			          + " nodes.  A view will be automatically created.");
		} else {
			sb.append("Network is over "
			          + CytoscapeInit.getProperties().getProperty("viewThreshold")
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
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
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
