
/*
  File: ImportExpressionMatrixAction.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import cytoscape.Cytoscape;
import cytoscape.data.ExpressionData;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

/**
 * User has requested loading of an Expression Matrix File.
 */
public class ImportExpressionMatrixAction extends CytoscapeAction {

	/**
	 * Constructor.
	 */
	public ImportExpressionMatrixAction() {
		super("Attribute Matrix...");
		setPreferredMenu("File.Import");
		setAcceleratorCombo(KeyEvent.VK_E, ActionEvent.CTRL_MASK);
	}

	/**
	 * User Initiated Request.
	 * 
	 * @param e
	 *            Action Event.
	 */
	public void actionPerformed(ActionEvent e) {

		CyFileFilter filter = new CyFileFilter();
		filter.addExtension("mrna");
		filter.addExtension("mRNA");
		filter.addExtension("pvals");
		filter.setDescription("Expression Matrix files");

		// get the file name
		final String name;
		try {
			name = FileUtil.getFile("Load Expression Matrix File",
					FileUtil.LOAD, new CyFileFilter[] { filter }).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}

		// Create the LoadExpressionTask
		ImportExpressionDataTask task = new ImportExpressionDataTask(name);
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Start Loading in a new Thread; and pop-open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
}

/**
 * Task to Load New Expression Data File.
 */
class ImportExpressionDataTask implements Task {
	private TaskMonitor taskMonitor;
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            File name containing expression data.
	 */
	public ImportExpressionDataTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes the Task.
	 */
	public void run() {
		taskMonitor.setStatus("Analyzing Expression Data File...");
		try {
			// Read in Expression Data File
			ExpressionData expressionData = new ExpressionData(fileName,
					taskMonitor);
			Cytoscape.setExpressionData(expressionData);

			// Copy Expression Data to Attributes
			taskMonitor.setStatus("Mapping Expression Data to "
					+ "Node Attributes...");
			expressionData.copyToAttribs(Cytoscape.getNodeAttributes(),
					taskMonitor);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
			Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED,
					null, expressionData);

			// We are done; inform user of expression data details.
			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus(expressionData.getDescription());
		} catch (Exception e) {
			taskMonitor.setException(e,
					"Unable to load expression matrix file.");
		}
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
		return new String("Loading Gene Expression Data");
	}
}
