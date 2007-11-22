/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package edu.ucsd.bioeng.idekerlab.biomartui.ui;

import cytoscape.Cytoscape;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.layout.Tunable;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 *
 */
public class BiomartMainDialog extends JDialog implements PropertyChangeListener {
	private static BiomartMainDialog mainDialog = null;

	/**
	 *  DOCUMENT ME!
	 */
	public static void showUI() {
		if (mainDialog == null) {
			// Create Task
			final SetupUITask task = new SetupUITask();

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(false);
			jTaskConfig.displayCancelButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		} else {
			mainDialog.setVisible(true);
		}
	}

	private BiomartMainDialog() throws Exception {
		super(Cytoscape.getDesktop(), false);
		setTitle("Biomart Web Service Client");

		
		// Create a tabbed pane
		JTabbedPane tabs = new JTabbedPane();
		List<Tunable> tunables = WebServiceClientManager.getClient("biomart").getProps().getTunables();
		JPanel tPanel = new JPanel();
		for(Tunable t:tunables) {
			tPanel.add(t.getPanel());
		}
		BiomartAttrMappingPanel panel = new BiomartAttrMappingPanel();
		panel.addPropertyChangeListener(this);
		tabs.addTab("Query", panel);
		tabs.addTab("Options", tPanel);
		
		//tabs.addTab("PICR", new PICRPanel());
		
		add(tabs);

		pack();
	}

	static class SetupUITask implements Task {
		private TaskMonitor taskMonitor;

		public SetupUITask() {
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public String getTitle() {
			// TODO Auto-generated method stub
			return "Accessing Biomart Web Service...";
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void halt() {
			// TODO Auto-generated method stub
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run() {
			taskMonitor.setStatus("Initializing Biomart Web Service Client.\n\nIt may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			try {
				mainDialog = new BiomartMainDialog();
			} catch (Exception e) {
				taskMonitor.setException(e, "Failed to initialize the Biomart dialog.");
			}
			mainDialog.setLocationRelativeTo(Cytoscape.getDesktop());
			mainDialog.setVisible(true);
			taskMonitor.setPercentCompleted(100);
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param arg0 DOCUMENT ME!
		 *
		 * @throws IllegalThreadStateException DOCUMENT ME!
		 */
		public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
			this.taskMonitor = taskMonitor;
		}

		public void cancel() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getPropertyName().equals("CLOSE")) {
			dispose();
		}
	}
}
