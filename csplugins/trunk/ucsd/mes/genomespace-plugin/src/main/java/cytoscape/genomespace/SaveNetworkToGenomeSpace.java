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
public class SaveNetworkToGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 9988760123456789L;
	private static final CyLogger logger = CyLogger.getLogger(UploadFileToGenomeSpace.class);

	public SaveNetworkToGenomeSpace() {
		// Give your action a name here
		super("Save Network As");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String networkType =
				(new NetworkTypeSelectionDialog(Cytoscape.getDesktop())).getNetworkType();
			if (networkType == null)
				return;
			networkType = networkType.toLowerCase();
System.err.println("++++++++++++++++++ networkType="+networkType);
			final GsSession client = GSUtils.getSession();
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add(networkType.toLowerCase());
			final TreeSelectionDialog dialog =
				new TreeSelectionDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							/* isSaveAsDialog = */ true);

			String saveFileName = dialog.getSaveFileName();
			if (saveFileName == null)
				return;

			// Make sure the file name ends with the network type extension:
			if (!saveFileName.toLowerCase().endsWith("." + networkType))
				saveFileName += "." + networkType;
/*
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
*/
		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}
}
