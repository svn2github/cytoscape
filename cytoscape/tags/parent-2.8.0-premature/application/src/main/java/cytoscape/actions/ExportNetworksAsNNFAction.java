/*
  File: ExportNetworksAsNNFAction.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.writers.XGMMLWriter;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import javax.swing.event.MenuEvent;


/**
 * This action is for exporting network and attributes in XGMML file.
 *
 * @author kono
 *
 */
public class ExportNetworksAsNNFAction extends CytoscapeAction {
	/**
	 * Creates a new ExportNetworksAsNNFAction object.
	 */
	public ExportNetworksAsNNFAction() {
		super("Networks as NNF File...");
		setPreferredMenu("File.Export");
	}

	/**
	 * Creates a new ExportNetworksAsNNFAction object.
	 *
	 * @param label  DOCUMENT ME!
	 */
	public ExportNetworksAsNNFAction(boolean label) {
		super();
	}

	protected boolean checkNetworkCount() {
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		if (networks.isEmpty()) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No networks in this session!",
			                              "No network Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else
			return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (!checkNetworkCount())
			return;

		// Create FileFilters
		CyFileFilter filter = new CyFileFilter("nnf");
		filter.setDescription("All NNF network files");

		String fileName;
		try {
			fileName = FileUtil.getFile("Export Networks as NNF", FileUtil.SAVE, new CyFileFilter[] { filter }).toString();
		} catch (Exception exp) {
			// this is because the selection was cancelled
			return;
		}

		if (!fileName.endsWith(".nnf"))
			fileName = fileName + ".nnf";

		final BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(fileName));
		} catch (final IOException ioe) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Could not open file: " + ioe.getMessage(),
			                              "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Create Task
		final ExportAsNNFTask task = new ExportAsNNFTask(fileName, output);

		// Configure JTask Dialog Pop-Up Box
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	public void menuSelected(MenuEvent e) {
		enableForNetwork();	
	}
} 


/**
 * Task to Save Graph Data to NNF Format.
 */
class ExportAsNNFTask implements Task {
	private final String fileName;
	private final BufferedWriter output;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 *
	 * @param network
	 *            Network Object.
	 * @param view
	 *            Network View Object.
	 */
	public ExportAsNNFTask(final String fileName, final BufferedWriter output) {
		this.fileName = fileName;
		this.output = output;
	}

	/**
	 * Executes Task
	 *
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Exporting Networks...");
		taskMonitor.setPercentCompleted(0);

		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		final float networkCount = networks.size();
		try {
			float writtenCount = 0.0f;
			for (final CyNetwork network : networks) {
				writeNetwork(network);
				++writtenCount;
				taskMonitor.setPercentCompleted(Math.round(writtenCount / networkCount));
			}
		} catch (Exception e) {
			taskMonitor.setException(e, "Cannot export networks as NNF.");
		}

		try {
			output.close();
		} catch (Exception e) {
			// Intentionally empty!
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Networks have been successfully saved to:  " + fileName);

		CyLogger.getLogger().info("Networks have been exported as an NNF file: " + fileName);
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
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
		return new String("Exporting Networks");
	}

	private void writeNetwork(final CyNetwork network) throws IOException {
		final String title = network.getTitle();

		final CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		final Set<String> encounteredNodes = new HashSet<String>();

		final List<CyEdge> edges = (List<CyEdge>)network.edgesList();
		for (final CyEdge edge : edges) {
			output.write(escapeID(title) + " ");

			final String sourceID = ((CyNode)edge.getSource()).getIdentifier();
			encounteredNodes.add(sourceID);
			output.write(escapeID(sourceID) + " ");

			String interactionName = edgeAttrs.getStringAttribute(edge.getIdentifier(),
									      Semantics.INTERACTION);
			if (interactionName == null)
				interactionName = "xx";
			output.write(escapeID(interactionName) + " ");

			final String targetID = ((CyNode)edge.getTarget()).getIdentifier();
			encounteredNodes.add(targetID);
			output.write(escapeID(targetID) + "\n");
		}

		final List<CyNode> nodes = (List<CyNode>)network.nodesList();
		for (final CyNode node : nodes) {
			final String nodeID = node.getIdentifier();
			if (!encounteredNodes.contains(nodeID))
				output.write(escapeID(title) + " " + escapeID(nodeID) + "\n");
		}
	}

	private String escapeID(final String ID) {
		final StringBuilder builder = new StringBuilder(ID.length());
		for (int i = 0; i < ID.length(); ++i) {
			final char ch = ID.charAt(i);
			if (ch == ' ' || ch == '\t' || ch == '\\')
				builder.append('\\');
			builder.append(ch);
		}

		return builder.toString();
	}
}
