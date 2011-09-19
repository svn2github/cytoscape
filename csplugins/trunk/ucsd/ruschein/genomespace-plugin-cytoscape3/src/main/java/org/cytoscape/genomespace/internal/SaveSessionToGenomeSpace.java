package org.cytoscape.genomespace.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;


/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SaveSessionToGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 9988760123456789L;
	private static final Logger logger = LoggerFactory.getLogger(UploadFileToGenomeSpace.class);
	private final CySwingApplication app;

	public SaveSessionToGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		// Give your action a name here
		super("Save Session As",appMgr);
		this.app = app;

		// TODO
		// new ImageIcon(SaveSessionToGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.Export.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			final GsSession client = GSUtils.getSession();
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("cys");
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(app.getJFrame(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.SAVE_AS_DIALOG);
			String saveFileName = dialog.getSaveFileName();
			if (saveFileName == null)
				return;

			// Make sure the file name ends with ".cys":
			if (!saveFileName.toLowerCase().endsWith(".cys"))
				saveFileName += ".cys";

			// Create Task
			final File tempFile = File.createTempFile("temp", "cysession");
			/* TODO
			final Task task = new SaveSessionTask(tempFile.getPath(), saveFileName,
							      dataManagerClient);

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();

			jTaskConfig.displayCancelButton(false);
			jTaskConfig.setOwner(app.getJFrame());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
			*/
		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(app.getJFrame(),
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
class SaveSessionTask implements Task {
	private final String localFileName;
	private final String uploadPath;
	private final DataManagerClient dataManagerClient;
	private final CytoscapeSessionWriter sw;
	private TaskMonitor taskMonitor;

	SaveSessionTask(final String localFileName, final String uploadPath,
			final DataManagerClient dataManagerClient)
	{
		this.localFileName = localFileName;
		this.uploadPath = uploadPath;
		this.dataManagerClient = dataManagerClient;
		sw = new CytoscapeSessionWriter(localFileName);
	}

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

	public void halt() {
		// Task can not currently be halted.
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

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
 */
