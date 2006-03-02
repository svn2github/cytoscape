package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;


/**
 * "Save Session As" Action
 * -- Same as SaveSessionAction, but always opens file
 *     chooser.
 */
public class SaveSessionAsAction extends CytoscapeAction {

	// Extension for the new cytoscape session file
	public static String SESSION_EXT = ".cys";
	
	/**
	 * Constructor.
	 *
	 */
	public SaveSessionAsAction() {
		super("Save As...");
		setPreferredMenu("File");
		setAcceleratorCombo( java.awt.event.KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
	}

	// If no current session file exists, open dialog box to save new session,
	// and if it exists, overwrite the file.
	public void actionPerformed(ActionEvent e) {

		String name; // file name

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
		
		// Create Task
		SaveSessionTask task = new SaveSessionTask(name);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} // SaveAsGMLAction

