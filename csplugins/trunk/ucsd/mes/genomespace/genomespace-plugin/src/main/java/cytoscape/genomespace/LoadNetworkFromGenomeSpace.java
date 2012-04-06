package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

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
import org.genomespace.datamanager.core.GSDataFormat;


public class LoadNetworkFromGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 7577788473487659L;
	private static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromGenomeSpace.class);

	public LoadNetworkFromGenomeSpace() {
		super("Load Network...",
		      new ImageIcon(LoadNetworkFromGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

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
			acceptableExtensions.add("adj");
			acceptableExtensions.add("cdb");
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;
		
			GSDataFormat dataFormat = fileMetadata.getDataFormat();
			if ( fileMetadata.getDataFormat().getFileExtension().equalsIgnoreCase("adj") )
				dataFormat = GSUtils.findConversionFormat(fileMetadata.getAvailableDataFormats(), "xgmml");

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "cynetwork");
			dataManagerClient.downloadFile(fileMetadata, dataFormat, tempFile, true);

			// Select the type of network reader:
			final String origFileName = fileMetadata.getName();
			final String extension = getExtension(origFileName);
			final GraphReader reader;
			if (extension.equals("sif"))
				reader = new InteractionsReader(tempFile.getPath());
			else if (extension.equals("gml"))
				reader = new GMLReader(tempFile.getPath());
			else
				reader = new XGMMLReader(tempFile.getPath());

			Cytoscape.createNetwork(reader, /* create_view */ true, /* parent = */ null)
				.setTitle(getNetworkTitle(origFileName));
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
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos + 1)).toLowerCase();
	}

	private static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}
}
