package org.cytoscape.genomespace.internal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadNetworkFromGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 7577788473487659L;
	private static final Logger logger = LoggerFactory.getLogger(LoadNetworkFromGenomeSpace.class);
	private final CySwingApplication app;

	public LoadNetworkFromGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		super("Load Network...", appMgr);
		this.app = app;

		// TODO
//		      new ImageIcon(LoadNetworkFromGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.Import.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		File tempFile = null;
		try {
			final GsSession client = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			// Select the GenomeSpace file:
			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("sif");
			acceptableExtensions.add("xgmml");
			acceptableExtensions.add("gml");
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(app.getJFrame(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "cynetwork");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);

			// Select the type of network reader:
			/* TODO
			final String origFileName = fileMetadata.getName();
			final String extension = getExtension(origFileName);
			final GraphReader reader;
			if (extension.equals("sif"))
				reader = new InteractionsReader(tempFile.getPath());
			else if (extension.equals("gml"))
				reader = new GMLReader(tempFile.getPath());
			else
				reader = new XGMMLReader(tempFile.getPath());

			Cytoscape.createNetwork(reader, true, null)
				.setTitle(getNetworkTitle(origFileName));
				*/
		} catch (Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(app.getJFrame(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	private static String getExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos + 1)).toLowerCase();
	}

	private static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}
}
