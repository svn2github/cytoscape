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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.layout.Tunable;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;


/**
 * BioMart client main GUI.
 */
public class BiomartMainDialog extends JDialog implements PropertyChangeListener {
	
	private static final long serialVersionUID = 8693726765795163080L;
	
	// Actual dialog.  This is a singleton.
	private static BiomartMainDialog mainDialog = null;
	protected static Object pcsO = new Object();
	protected static PropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);

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
	 * Build and display Dialog 
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
		} else
			mainDialog.setVisible(true);
	}

	private BiomartMainDialog(final TaskMonitor monitor) throws Exception {
		super(Cytoscape.getDesktop(), false);
		setTitle("BioMart Web Service Client");

		// Create a tabbed pane
		final JTabbedPane tabs = new JTabbedPane();
		final List<Tunable> tunables = WebServiceClientManager.getClient("biomart").getProps()
		                                                .getTunables();
		
		final JPanel tunablePanel = new JPanel();
		tunablePanel.setBackground(Color.white);
		final JPanel tPanel = new JPanel();
		final Dimension panelSize = new Dimension(220, 250);
		tPanel.setMinimumSize(panelSize);
		tPanel.setMaximumSize(panelSize);
		tPanel.setSize(panelSize);
		
		
		tPanel.setBackground(Color.white);
		tPanel.setLayout(new GridLayout(0,1));
		
		for (Tunable t : tunables) {
			final JPanel propPanel = t.getPanel();
			propPanel.setBackground(Color.white);
			tPanel.add(propPanel);
		}
		
		tunablePanel.add(tPanel);
		
        
		panel = new BiomartAttrMappingPanel(monitor);
		panel.addPropertyChangeListener(this);
		tabs.addTab("Query", panel);
		tabs.addTab("Options", tunablePanel);
				
		add(tabs);

		pack();
	}

	static class SetupUITask implements Task {
		private TaskMonitor taskMonitor;

		public SetupUITask() {
			super();
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public String getTitle() {
			return "Checking accessible BioMart Services...";
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
			taskMonitor.setStatus("Checking available Mart services.\n\nThis process may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			try {
				mainDialog = new BiomartMainDialog(taskMonitor);
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
