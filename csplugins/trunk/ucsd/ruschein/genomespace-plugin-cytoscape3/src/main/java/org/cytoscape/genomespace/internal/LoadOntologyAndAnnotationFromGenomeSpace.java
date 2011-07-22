package org.cytoscape.genomespace.internal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.session.CyApplicationManager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadOntologyAndAnnotationFromGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 7571788473486759L;
	private static final Logger logger = LoggerFactory.getLogger(LoadOntologyAndAnnotationFromGenomeSpace.class);
	private final CySwingApplication app;

	public LoadOntologyAndAnnotationFromGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		super("Load Ontology and Annotations...",appMgr);
		this.app = app;

		// TODO
//		      new ImageIcon(LoadAttrsFromGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

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
			final GSFileBrowserDialog browserDialog =
				new GSFileBrowserDialog(app.getJFrame(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = browserDialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "ont");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);
/* TODO
			final ImportTextTableDialog dialog =
				new ImportTextTableDialog(app.getJFrame(), tempFile,
							  fileMetadata.getName(),
							  ImportTextTableDialog.ONTOLOGY_AND_ANNOTATION_IMPORT);
			dialog.pack();
			dialog.setLocationRelativeTo(app.getJFrame());
			dialog.setVisible(true);
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
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos)).toLowerCase();
	}

	private static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}
}
