package cytoscape.tutorial16;

import java.awt.event.ActionEvent;
import java.io.File;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;


/**
 *  This sample plugin will add a menu item "Tutorial16" under Plugins menu.
 *  Click on the menu item will prompt a dialog box "Import Vizmap Property File".
 *  Most code adoptd from the Cytoscape core class "cytoscape.actions.ImportVizmapAction"
 */
public class Tutorial16 extends CytoscapePlugin {

	/**
	 *
	 */
	public Tutorial16() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);

	}

	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial16 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial16");
			setPreferredMenu("Plugins");
		}
		/**
		 *  DOCUMENT ME!
		 *
		 * @param e DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			final CyFileFilter propsFilter = new CyFileFilter();
			propsFilter.addExtension("props");
			propsFilter.setDescription("Property files");

			// Get the file name
			final File file = FileUtil.getFile("Import Vizmap Property File", FileUtil.LOAD,
					new CyFileFilter[] { propsFilter });

			// if the name is not null, then load
			if (file != null) {
				// Create LoadNetwork Task
				LoadVizmapTask task = new LoadVizmapTask(file);

				// Configure JTask Dialog Pop-Up Box
				final JTaskConfig jTaskConfig = new JTaskConfig();
				jTaskConfig.setOwner(Cytoscape.getDesktop());
				jTaskConfig.displayCloseButton(true);
				jTaskConfig.displayStatus(true);
				jTaskConfig.setAutoDispose(false);

				// Execute Task in New Thread; pops open JTask Dialog Box.
				TaskManager.executeTask(task, jTaskConfig);
			}
		}
	}

}


class LoadVizmapTask implements Task {
	private File file;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 *
	 */
	public LoadVizmapTask(File file) {
		this.file = file;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Reading Vizmap File...");
		taskMonitor.setPercentCompleted(-1);
		// this even will load the file
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, file.getAbsolutePath());
		taskMonitor.setStatus("Vizmapper updated by the file: " + file.getName());
		taskMonitor.setPercentCompleted(100);
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
		return new String("Importing Vizmap");
	}
}
