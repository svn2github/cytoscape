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
package edu.ucsd.bioeng.idekerlab.biomartclient.ui;

import cytoscape.Cytoscape;

import cytoscape.data.webservice.WebServiceClientManager;

import cytoscape.layout.Tunable;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import cytoscape.util.ModuleProperties;
import cytoscape.util.SwingWorker;

import java.awt.Color;
import java.awt.GridLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 *
 */
public class BiomartMainDialog extends JDialog implements PropertyChangeListener {
	private static BiomartMainDialog mainDialog = null;
	protected static Object pcsO = new Object();
	protected static PropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);
	private JPanel filterPanel;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	private static boolean initialized = true;
	private static BiomartAttrMappingPanel panel;

	/**
	 *  DOCUMENT ME!
	 */
	public static void showUI() {
		if ((mainDialog == null) || (initialized == false)) {
			// Create Task
			final SetupUITask task = new SetupUITask();

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(false);
			jTaskConfig.displayCancelButton(true);
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
		List<Tunable> tunables = WebServiceClientManager.getClient("biomart").getProps()
		                                                .getTunables();
		final JPanel tPanel = new JPanel();
		
		tPanel.setBackground(Color.white);
		tPanel.setLayout(new java.awt.GridBagLayout());
		
		java.awt.GridBagConstraints gridBagConstraints;
		
		for (Tunable t : tunables) {
			JPanel propPanel = t.getPanel(); 
			
			propPanel.setBackground(Color.white);
			
			if (t.getDescription().equalsIgnoreCase("Import all available entries")){
				gridBagConstraints = new java.awt.GridBagConstraints();
		        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		        tPanel.add(propPanel, gridBagConstraints);
			}
			if (t.getDescription().equalsIgnoreCase("Show all available filters")){
		        gridBagConstraints = new java.awt.GridBagConstraints();
		        gridBagConstraints.gridy = 1;
		        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		        tPanel.add(propPanel, gridBagConstraints);
			}
			if (t.getDescription().equalsIgnoreCase("Biomart Base URL")){
		        gridBagConstraints = new java.awt.GridBagConstraints();
		        gridBagConstraints.gridy = 2;
		        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		        gridBagConstraints.weightx = 1.0;
		        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
			    tPanel.add(propPanel, gridBagConstraints);
			}
		}
		
		// Add a label for place holder 
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tPanel.add(jLabel4, gridBagConstraints);
		//
        
		panel = new BiomartAttrMappingPanel();
		panel.addPropertyChangeListener(this);
		tabs.addTab("Query", panel);
		tabs.addTab("Property", tPanel);
				
		add(tabs);

		pack();
	}

	static class SetupUITask implements Task {
		private TaskMonitor taskMonitor;
		SwingWorker worker;

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
			pcs.firePropertyChange("CANCEL", null, null);
			initialized = false;
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run() {
			taskMonitor.setStatus("Initializing Biomart Web Service Client.\n\nIt may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			try {
				mainDialog = new BiomartMainDialog();
			} catch (InterruptedException ie) {
				//System.out.println("============== GOT interaption");
			} catch (Exception e) {
				taskMonitor.setException(e, "Failed to initialize the Biomart dialog.");
			}

			taskMonitor.setPercentCompleted(100);

			if ((panel != null) && panel.isInitialized()) {
				mainDialog.setLocationRelativeTo(Cytoscape.getDesktop());
				mainDialog.setVisible(true);
				initialized = true;
			} else {
				//System.out.println("Biomart initialization process canceled by user.");
			}
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
	}

	private void showStatusReport() {
		JOptionPane.showInternalMessageDialog(Cytoscape.getDesktop(), "Biomart", "information",
		                                      JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("CLOSE")) {
			dispose();
		}
	}
}
