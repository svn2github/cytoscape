
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

import java.io.IOException;

import javax.swing.JDialog;


import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;


/**
 *
 */
public class BiomartMainDialog extends JDialog {
	
	private static BiomartMainDialog mainDialog = null;
	
	/**
	 *  DOCUMENT ME!
	 */
	public static void showUI() {
		if(mainDialog == null) {
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

	private BiomartMainDialog() {
		super(Cytoscape.getDesktop(), false);
		setTitle("Web Service Clients");

		try {
			BiomartNameMappingPanel panel = new BiomartNameMappingPanel();
			add(panel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pack();
		
	}
	
	public static void importID() {
		
		try {

			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	static class SetupUITask implements Task  {
		
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
			
			mainDialog = new BiomartMainDialog();
			mainDialog.setLocationRelativeTo(Cytoscape.getDesktop());
			mainDialog.setVisible(true);
			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Done!");

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

	
	
}
