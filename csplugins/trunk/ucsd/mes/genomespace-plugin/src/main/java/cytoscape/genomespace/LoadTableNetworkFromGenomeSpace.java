package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;

import edu.ucsd.bioeng.coreplugin.tableImport.ui.ImportTextTableDialog;

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


public class LoadTableNetworkFromGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 7577788473487659L;
	private static final CyLogger logger = CyLogger.getLogger(LoadTableNetworkFromGenomeSpace.class);

	public LoadTableNetworkFromGenomeSpace() {
		super("Load Network from Table...",
		      new ImageIcon(LoadAttrsFromGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

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
				new GSFileBrowserDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = browserDialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "network");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);

			final ImportTextTableDialog dialog =
				new ImportTextTableDialog(Cytoscape.getDesktop(), tempFile,
							  fileMetadata.getName(),
							  ImportTextTableDialog.NETWORK_IMPORT);
			dialog.pack();
			dialog.setLocationRelativeTo(Cytoscape.getDesktop());
			dialog.setVisible(true);
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

	private static String getExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos)).toLowerCase();
	}

	private static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}
}
