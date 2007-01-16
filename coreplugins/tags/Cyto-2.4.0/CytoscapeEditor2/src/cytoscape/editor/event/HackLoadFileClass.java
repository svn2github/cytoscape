package cytoscape.editor.event;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.GraphReader;
import cytoscape.dialogs.VisualStyleBuilderDialog;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * short-term hack.  Clones some of the functionality of Cytoscape.ImportGraphFileAction
 * @author ajk
 *
 */
public class HackLoadFileClass {

	public HackLoadFileClass() {
		super();
	}
	
	public void loadFile(File file, boolean createVisualStyle,
			boolean skipMessage) {
		// Create LoadNetwork Task
		LoadNetworkTask task = new LoadNetworkTask(file, createVisualStyle);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(skipMessage);

		// Execute Task in New Thread; pops open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
}

/**
 * Task to Load New Network Data.
 */
class LoadNetworkTask implements Task {
	private File file;
	private boolean vsSwitch;

	private CyNetwork cyNetwork;
	private TaskMonitor taskMonitor;

	private GraphReader reader;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File.
	 * @param fileType
	 *            FileType, e.g. Cytoscape.FILE_SIF or Cytoscape.FILE_GML.
	 */

	public LoadNetworkTask(File file, boolean vsSwitch) {
		this.file = file;
		this.vsSwitch = vsSwitch;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Reading in Network Data...");

		try {
			String location = file.getAbsolutePath();
			taskMonitor.setPercentCompleted(-1);

			reader = ((GraphReader) Cytoscape.getImportHandler().getReader(
					location));

			taskMonitor.setStatus("Creating Cytoscape Network...");

			cyNetwork = Cytoscape.createNetworkFromFile(location);

			Object[] ret_val = new Object[2];
			ret_val[0] = cyNetwork;
			ret_val[1] = file.toURI();
			// ret_val[2] = new Integer(file_type);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null,
					ret_val);

			if (cyNetwork != null) {
				informUserOfGraphStats(cyNetwork);
			} else {
				StringBuffer sb = new StringBuffer();
				sb
						.append("Could not read network from file: "
								+ file.getName());
				sb.append("\nThis file may not be a valid file format.");
				taskMonitor.setException(new IOException(sb.toString()), sb
						.toString());
			}
			taskMonitor.setPercentCompleted(100);

			if (file.getName().endsWith(".gml") && vsSwitch == true) {

				VisualStyleBuilderDialog vsd = new VisualStyleBuilderDialog(
						cyNetwork.getTitle(), (GraphReader) cyNetwork
								.getClientData(Cytoscape.READER_CLIENT_KEY),
						Cytoscape.getDesktop(), true);
				vsd.show();

			}

		} catch (Exception e) {
			taskMonitor.setException(e, "Unable to load network file.");
		}
	}

	/**
	 * Inform User of Network Stats.
	 */
	// Mod. by Kei 08/26/2005
	//
	// For the new GML format import function, added some messages
	// for the users.
	//
	private void informUserOfGraphStats(CyNetwork newNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded network from:  " + file.getName());
		sb.append("\n\nNetwork contains "
				+ formatter.format(newNetwork.getNodeCount()));
		sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
		sb.append(" edges.\n\n");

		if (newNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit
				.getProperties().getProperty("viewThreshold"))) {
			sb.append("Network is under "
					+ CytoscapeInit.getProperties()
							.getProperty("viewThreshold")
					+ " nodes.  A view will be automatically created.");
		} else {
			sb.append("Network is over "
					+ CytoscapeInit.getProperties()
							.getProperty("viewThreshold")
					+ " nodes.  A view has not been created."
					+ "  If you wish to view this network, use "
					+ "\"Create View\" from the \"Edit\" menu.");
		}
		taskMonitor.setStatus(sb.toString());
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
		return new String("Loading Network");
	}

}

