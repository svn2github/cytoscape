package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
//import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

/**
 * Open session file. This class will load all networks and session state in the
 * cys file.
 * 
 * @author kono
 * 
 */
public class OpenSessionAction extends CytoscapeAction {

	// Extension for the new cytoscape session file
	public static String SESSION_EXT = "cys";

	public OpenSessionAction() {
		super("Open Session");
		setPreferredMenu("File");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
	}

	public OpenSessionAction(boolean label) {
		super();
	}

	// If no current session file exists, open dialog box to save new session,
	// and if it exists, overwrite the file.
	public void actionPerformed(ActionEvent e) {

		String name; // file name to be opened.

		boolean proceed = prepare();

		if (proceed == true) {

			// Create FileFilters
			CyFileFilter sessionFilter = new CyFileFilter();

			// Add accepted File Extensions
			sessionFilter.addExtension(SESSION_EXT);
			sessionFilter.setDescription("Cytoscape Session files");

			// Open Dialog to ask user the file name.
			try {
				name = FileUtil.getFile("Open a Session File", FileUtil.LOAD,
						new CyFileFilter[] { sessionFilter }).toString();
			} catch (Exception exp) {
				// this is because the selection was canceled
				return;
			}

			// Close all networks in the workspace.
			cleanWorkspace();
			
			System.out.println("Opening session file: " + name);

			// Create Task
			OpenSessionTask task = new OpenSessionTask(name);

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
	 * Before loading the new session, we need to clean up current session.
	 * 
	 */
	private boolean prepare() {

		int currentNetworkCount = Cytoscape.getNetworkSet().size();

		if (currentNetworkCount != 0) {
			// Show warning
			String warning = "Current session will be lost.\nDo you want to continue?";

			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					warning, "Caution!", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {

				return true;
			} else {
				return false;
			}

		} else {
			return true;
		}

	}

	// Clean-up current session
	// Destroy all networks
	private void cleanWorkspace() {

		Set netSet = Cytoscape.getNetworkSet();
		Iterator it = netSet.iterator();

		while (it.hasNext()) {
			CyNetwork net = (CyNetwork) it.next();
			Cytoscape.destroyNetwork(net);
		}

	}

} // SaveAsGMLAction

class OpenSessionTask implements Task {

	private String fileName;
	private TaskMonitor taskMonitor;

	//private CytoscapeSessionReader sr;

	/**
	 * Constructor.
	 *
	 */
	OpenSessionTask(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Executes Task
	 * 
	 * @throws
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Opening Session File...");
		taskMonitor.setPercentCompleted(-1);
		
		//sr = new CytoscapeSessionReader(fileName);
		
//		try {
//			sr.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			
//			taskMonitor.setException(e, "Cannot open the session file.");
//			
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session file " + fileName
				+ " successfully loaded.");
		
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
		return new String("Opening Session");
	}

}
