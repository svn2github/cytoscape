package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadAttrsFromGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 7577788473487659L;
	private static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromGenomeSpace.class);

	public LoadAttrsFromGenomeSpace() {
		super("Load Attrs");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		File tempFile = null;
		try {
			final GsSession client = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			// Select the GenomeSpace file:
			final AttrFileSelectionDialog dialog =
				new AttrFileSelectionDialog(Cytoscape.getDesktop(),
							    dataManagerClient);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			// Download the GenomeSpace file:
			tempFile = File.createTempFile("temp", "cyattrs");
			dataManagerClient.downloadFile(fileMetadata, tempFile, true);

			final boolean useNodeAttrs = dialog.useNodeAttrs();
			final CyAttributes attrs = useNodeAttrs ? Cytoscape.getNodeAttributes()
			                                        : Cytoscape.getEdgeAttributes();
			CyAttributesReader.loadAttributes(attrs, new FileReader(tempFile));
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
