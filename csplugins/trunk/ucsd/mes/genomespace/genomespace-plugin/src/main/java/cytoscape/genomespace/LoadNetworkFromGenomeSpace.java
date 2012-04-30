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
			acceptableExtensions.add("ndb");
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;
		
			GSDataFormat dataFormat = fileMetadata.getDataFormat();
			if ( dataFormat == null )
				throw new RuntimeException("file metadata has null data format");

			String ext = dataFormat.getFileExtension();
			if ( ext != null && ext.equalsIgnoreCase("adj") )
				dataFormat = GSUtils.findConversionFormat(fileMetadata.getAvailableDataFormats(), "xgmml");

			// Download the GenomeSpace file into a temp file
			final String origFileName = fileMetadata.getName();
			final String extension = GSUtils.getExtension(origFileName);
			tempFile = File.createTempFile("temp", "." + extension);
			dataManagerClient.downloadFile(fileMetadata, dataFormat, tempFile, true);

			System.out.println("attempting to load origFileName: " + origFileName);
			System.out.println("attempting to load extension: " + extension);
			System.out.println("attempting to tmpfile: " + tempFile.getPath());
			Cytoscape.createNetworkFromFile(tempFile.getPath()).setTitle(GSUtils.getNetworkTitle(origFileName));
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
}
