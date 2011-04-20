package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.data.readers.XGMMLException;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadSessionFromGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 7577788473487659L;
	static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromGenomeSpace.class);

	public LoadSessionFromGenomeSpace() {
		super("Load Session");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		File tempFile = null;
		try {
			if (!destroyCurrentSession(Cytoscape.getDesktop()))
				return;

			final GsSession client = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			// Select the GenomeSpace file:
			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("cys");
			final TreeSelectionDialog dialog =
				new TreeSelectionDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "cysession");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);

			// Close all networks in the workspace.
			Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
			Cytoscape.createNewSession();
			Cytoscape.setSessionState(Cytoscape.SESSION_NEW);

			logger.info("Opening session file: " + tempFile.getName());

			// Create Task
			final OpenSessionTask task = new OpenSessionTask(tempFile.getName());

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayCancelButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		} catch (Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	private static boolean destroyCurrentSession(final Frame parent) {
		final int currentNetworkCount = Cytoscape.getNetworkSet().size();
		if (currentNetworkCount == 0)
			return true;

		final String warning = "The current session will be lost!\nDo you want to continue anyway?";
		final int result =
			JOptionPane.showConfirmDialog(parent, warning,
						      "Caution!", JOptionPane.YES_NO_OPTION,
						      JOptionPane.WARNING_MESSAGE, null);
		return result == JOptionPane.YES_OPTION;
	}

	private static String getExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos)).toLowerCase();
	}

	private static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}
}


class OpenSessionTask implements Task {
	private String fileName;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.<br>
	 *
	 * @param fileName
	 *            Session file name
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
		taskMonitor.setStatus("Opening Session File.\n\nIt may take a while.\nPlease wait...");
		taskMonitor.setPercentCompleted(0);

		CytoscapeSessionReader sr;

		try {
			sr = new CytoscapeSessionReader(fileName, taskMonitor);
			sr.read();
		} catch (IOException e) {
			taskMonitor.setException(e, "Cannot open the session file: " + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Cannot open the session file: "+ e.getMessage(), e);
		} catch (JAXBException e) {
			taskMonitor.setException(e, "Cannot unmarshall document: " + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Cannot unmarshall document: "+ e.getMessage(), e);
		} catch (XGMMLException e) {
			taskMonitor.setException(e, "XGMML format error in network: "+e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("XGMML format error in network "+ e.getMessage(), e);
		} catch (Exception e) { // catch any exception: the user should know something went wrong
			taskMonitor.setException(e, "Error while loading session " + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Error while loading session: "+ e.getMessage(), e);
		} finally {
			sr = null;
			Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
			System.gc();
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session file " + fileName + " successfully loaded.");

		Cytoscape.setCurrentSessionFileName(fileName);

		final File sessionFile = new File(fileName);
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session: " + sessionFile.getName()
		                                + ")");
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
		
		LoadSessionFromGenomeSpace.logger.info("HALT called!!!");
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Failed!!!");
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
		return "Opening Session File";
	}
}
