package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.CalculatorIO;
import cytoscape.visual.VisualMappingManager;

public class ExportVizmapAction extends CytoscapeAction {
	public ExportVizmapAction() {
		super("Vizmap Property File");
		setPreferredMenu("File.Export");
	}

	public void actionPerformed(ActionEvent e) {

		String name;
		try {
			name = FileUtil.getFile("Export Vizmaper as property file",
					FileUtil.SAVE, new CyFileFilter[] {}).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}

		if (!name.endsWith(".props"))
			name = name + ".props";

		// Create Task
		ExportVizmapTask task = new ExportVizmapTask(name);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

}

/**
 * Task to Save Graph Data to GML Format.
 */
class ExportVizmapTask implements Task {
	private String fileName;
	private String originalName;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 */
	ExportVizmapTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes Task
	 */
	public void run() {
		taskMonitor.setStatus("Saving Network...");
		taskMonitor.setPercentCompleted(-1);
		Cytoscape.firePropertyChange(Cytoscape.SESSION_SAVED, null, null);
		CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		VisualMappingManager vizmapper = cyDesktop.getVizMapManager();
		CalculatorCatalog catalog = vizmapper.getCalculatorCatalog();

		File userVizmapFile = new File(fileName);
		CalculatorIO.storeCatalog(catalog, userVizmapFile);

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Vizmaps successfully saved to:  " + fileName);

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
		return new String("Saving Vizmap");
	}

}
