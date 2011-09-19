package org.cytoscape.genomespace.internal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadSessionFromGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 7577788473487659L;
	static final Logger logger = LoggerFactory.getLogger(LoadNetworkFromGenomeSpace.class);
	private final CySwingApplication app;

	public LoadSessionFromGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		super("Load Session...",appMgr);
		this.app = app;

		// TODO
//		      new ImageIcon(LoadSessionFromGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.Import.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (!destroyCurrentSession(app.getJFrame()))
				return;

			final GsSession client = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			// Select the GenomeSpace file:
			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("cys");
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(app.getJFrame(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			final File tempFile = File.createTempFile("temp", "cysession");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);
/* TODO
			// Close all networks in the workspace.
			Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
			Cytoscape.createNewSession();
			Cytoscape.setSessionState(Cytoscape.SESSION_NEW);

			logger.info("Opening session file: " + tempFile.getName());

			final Task task = new OpenSessionTask(tempFile, fileMetadata.getName());

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(app.getJFrame());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayCancelButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
			*/
		} catch (Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(app.getJFrame(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}

	private static boolean destroyCurrentSession(final Frame parent) {
		final int currentNetworkCount =  0; /// TODO netManager.getNetworkSet().size();
		if (currentNetworkCount == 0)
			return true;

		final String warning = "The current session will be lost!\nDo you want to continue anyway?";
		final int result =
			JOptionPane.showConfirmDialog(parent, warning,
						      "Caution!", JOptionPane.YES_NO_OPTION,
						      JOptionPane.WARNING_MESSAGE, null);
		return result == JOptionPane.YES_OPTION;
	}
}
/*

class OpenSessionTask implements Task {
	private final File localFile;
	private final String origFileName;
	private TaskMonitor taskMonitor;

	OpenSessionTask(final File localFile, final String origFileName) {
		this.localFile = localFile;
		this.origFileName = origFileName;
	}

	public void run() {
		taskMonitor.setStatus("Opening Session File.\n\nIt may take a while.\nPlease wait...");
		taskMonitor.setPercentCompleted(0);

		CytoscapeSessionReader sr;

		try {
			sr = new CytoscapeSessionReader(localFile.getPath(), taskMonitor);
			sr.read();
		} catch (IOException e) {
			taskMonitor.setException(e, "Cannot open the session file: " + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Cannot open the session file: "
								+ e.getMessage(), e);
		} catch (JAXBException e) {
			taskMonitor.setException(e, "Cannot unmarshall document: " + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Cannot unmarshall document: "
								+ e.getMessage(), e);
		} catch (XGMMLException e) {
			taskMonitor.setException(e, "XGMML format error in network: "
						 + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("XGMML format error in network "
								+ e.getMessage(), e);
		} catch (Exception e) { // catch any exception: the user should know something went wrong
			taskMonitor.setException(e, "Error while loading session "
						 + e.getMessage());
			LoadSessionFromGenomeSpace.logger.error("Error while loading session: "
								+ e.getMessage(), e);
		} finally {
			sr = null;
			app.getJFrame().getVizMapperUI().initVizmapperGUI();
			System.gc();
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session file " + origFileName
				      + " successfully loaded from GenomeSpace.");

		Cytoscape.setCurrentSessionFileName(origFileName);

		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session: " + origFileName + ")");

		// Remove the temporary file:
		localFile.delete();
	}

	public void halt() {
		// Task can not currently be halted.
		
		LoadSessionFromGenomeSpace.logger.info("HALT called!!!");
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Failed!!!");
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Opening Session File";
	}
}
*/
