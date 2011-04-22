package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.datamanager.core.GSFileMetadataImpl;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;


/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SaveSessionToGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 9988760123456789L;
	private static final CyLogger logger = CyLogger.getLogger(UploadFileToGenomeSpace.class);

	public SaveSessionToGenomeSpace() {
		// Give your action a name here
		super("Save Session As");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			final GsSession client = GSUtils.getSession();
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("cys");
			final TreeSelectionDialog dialog =
				new TreeSelectionDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							/* isSaveAsDialog = */ true);
			String saveFileName = dialog.getSaveFileName();
			if (saveFileName == null)
				return;

			// Make sure the file name ends with ".cys":
			if (!saveFileName.toLowerCase().endsWith(".cys"))
				saveFileName += ".cys";

			// Create Task
			final File tempFile = File.createTempFile("temp", "cysession");
			final Task task = new SaveSessionTask(tempFile.getPath(), saveFileName,
							      dataManagerClient);

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();

			jTaskConfig.displayCancelButton(false);
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}
}


/**
 * Save Session Task.<br>
 * Call the Session Writer to save the following:<br>
 * <ul>
 * <li>Networks with metadata</li>
 * <li>All attributes (for nodes, edges, and network)</li>
 * <li>Visual Styles</li>
 * <li>Cytoscape Properties</li>
 * </ul>
 *
 * @author kono
 *
 */
class SaveSessionTask implements Task {
	private final String localFileName;
	private final String uploadPath;
	private final DataManagerClient dataManagerClient;
	private final CytoscapeSessionWriter sw;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.<br>
	 *
	 * @param localFileName  Absolute path to the Session file.
	 * @param uploadPath     The path in the GenomeSpace S3 file system
	 */
	SaveSessionTask(final String localFileName, final String uploadPath,
			final DataManagerClient dataManagerClient)
	{
		this.localFileName = localFileName;
		this.uploadPath = uploadPath;
		this.dataManagerClient = dataManagerClient;
		sw = new CytoscapeSessionWriter(localFileName);
	}

	/**
	 * Execute task.<br>
	 */
	public void run() {
		taskMonitor.setStatus("Saving Cytoscape Session.\n\nIt may take a while.  Please wait...");
		taskMonitor.setPercentCompleted(-1);

		GSFileMetadata uploadedFileMetadata = null;
		try {
			sw.writeSessionToDisk();
			taskMonitor.setPercentCompleted(20);
			final File localFile = new File(localFileName);
			uploadedFileMetadata =
				dataManagerClient.uploadFile(localFile, dirName(uploadPath),
							     baseName(uploadPath));
			localFile.delete();
		} catch (Exception e) {
			taskMonitor.setException(e, "Could not write session to the file: "
						 + localFileName);
			return;
		}

		taskMonitor.setPercentCompleted(100);
		final String shortName = uploadedFileMetadata.getName();
			taskMonitor.setStatus("Session successfully saved to:  " + shortName
					      + " in GenomeSpace.");

		// Show the session Name as the window title.
		Cytoscape.setCurrentSessionFileName(localFileName);
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session: " + shortName + ")");
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
		return "Saving Cytoscape Session";
	}

	// Returns the directory component of "path"
	private String dirName(final String path) {
		final int lastSlashPos = path.lastIndexOf('/');
		return path.substring(0, lastSlashPos + 1);
	}


	// Returns the basename component of "path"
	private String baseName(final String path) {
		final int lastSlashPos = path.lastIndexOf('/');
		return lastSlashPos == -1 ? path : path.substring(lastSlashPos + 1);
	}
}
