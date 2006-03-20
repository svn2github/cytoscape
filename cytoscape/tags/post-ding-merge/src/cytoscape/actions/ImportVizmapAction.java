package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.SwingUtilities;

import phoebe.PGraphView;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.GMLReader2;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyMenus;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PBounds;

public class ImportVizmapAction extends CytoscapeAction {
	public ImportVizmapAction() {
		super("Vizmap Property File...");
		setPreferredMenu("File.Import");
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		CyFileFilter propsFilter = new CyFileFilter();
		propsFilter.addExtension("props");
		propsFilter.setDescription("Property files");

		// Get the file name
		File file = FileUtil.getFile("Import Vizmap Property File",
				FileUtil.LOAD, new CyFileFilter[] { propsFilter });

		// if the name is not null, then load
		if (file != null) {

			// Create LoadNetwork Task
			LoadVizmapTask task = new LoadVizmapTask(file);

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pops open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);

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
		return new String("Importing Vizmap");
	}

}
