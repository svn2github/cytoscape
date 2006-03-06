
/*
  File: SaveSessionAction.java 
  
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

package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

//
//	"Save Project" Action
//		1. Extract current status
//		2. Generate file based on the extracted information
//		3. Write as XML.
//		4.
//
//
public class SaveSessionAction extends CytoscapeAction {

	// Extension for the new cytoscape session file
	public static String SESSION_EXT = ".cys";

	/**
	 * Constructor.
	 * 
	 */
	public SaveSessionAction() {
		super("Save");
		setPreferredMenu("File");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_S, ActionEvent.CTRL_MASK);
	}

	// If no current session file exists, open dialog box to save new session,
	// and if it exists, overwrite the file.
	public void actionPerformed(ActionEvent e) {

		// Call file chooser only when the currentFileName is null.
		String name = Cytoscape.getCurrentSessionFileName();

		if (name == null) {
			// Open Dialog to ask user the file name.
			try {
				name = FileUtil.getFile("Save Current Session as CYS File",
						FileUtil.SAVE, new CyFileFilter[] {}).toString();
			} catch (Exception exp) {
				// this is because the selection was canceled
				return;
			}

			if (!name.endsWith(SESSION_EXT))
				name = name + SESSION_EXT;

			Cytoscape.setCurrentSessionFileName(name);
		}
		// Create Task
		SaveSessionTask task = new SaveSessionTask(name);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} // SaveAsGMLAction

/**
 * Task to save current session as a project file.
 * 
 * To save the current status (call this 'session'), we need to create the
 * following structure: 0. Networks (includes the following) 1. Nodes with
 * attributes 2. Edges with attributes 3. Tree structure of the networks 4.
 * Vizmap 5. GraphML style properties (tentative) 6. Properties 7. Hidden node
 * info.
 * 
 * everything will be stored in XML. Schema will be available soon.
 */
class SaveSessionTask implements Task {

	private String fileName;
	private TaskMonitor taskMonitor;
	
	CytoscapeSessionWriter sw;

	// private CytoscapeSessionWriter sw;

	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Network Object.
	 * @param view
	 *            Network View Object.
	 * @throws JAXBException
	 */
	SaveSessionTask(String fileName) {
		this.fileName = fileName;

		// Create session writer object
		sw = new CytoscapeSessionWriter(fileName);

	}

	/**
	 * Executes Task
	 */
	public void run() {
		taskMonitor.setStatus("Saving Session...");
		taskMonitor.setPercentCompleted(-1);

		sw.write();

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session successfully saved to:  " + fileName);
		
		// Show the session Name as the window title.
		File shortName = new File(fileName);
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session Name: " + shortName.getName() + ")" );
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
		return new String("Saving Project");
	}

} // End of SaveSessionAction
