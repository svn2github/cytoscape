package cytoscape.genomespace;


import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLWriter;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.data.writers.XGMMLWriter;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.net.URISyntaxException;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.ui.GSFileBrowserDialog;


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
		super("Save Network As",
		      new ImageIcon(SaveNetworkToGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.Export.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String networkType =
				(new NetworkTypeSelectionDialog(Cytoscape.getDesktop())).getNetworkType();
			if (networkType == null)
				return;
			networkType = networkType.toLowerCase();
			final GsSession client = GSUtils.getSession();
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add(networkType.toLowerCase());
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							GSFileBrowserDialog.DialogType.SAVE_AS_DIALOG);

			String saveFileName = dialog.getSaveFileName();
			if (saveFileName == null)
				return;

			// Make sure the file name ends with the network type extension:
			if (!saveFileName.toLowerCase().endsWith("." + networkType))
				saveFileName += "." + networkType;

			final File localNetworkFile = saveNetworkLocally( networkType ); 

            GSFileMetadata uploadedFileMetadata = dataManagerClient.uploadFile(localNetworkFile, 
			                                                    GSUtils.dirName(saveFileName),
			                                                    GSUtils.baseName(saveFileName));
            localNetworkFile.delete();

		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}

	private File saveNetworkLocally(String type) throws IOException, URISyntaxException {
		final File tempFile = File.createTempFile("tempNetwork", type);
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();

		OutputStreamWriter fileWriter = null;
		if ( type.equalsIgnoreCase("xgmml") ) {
			fileWriter = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
			final XGMMLWriter writer = new XGMMLWriter(network, view);
			writer.write(fileWriter);
		} else if ( type.equalsIgnoreCase("sif") ) {
			fileWriter = new FileWriter(tempFile);
			InteractionWriter.writeInteractions(network, fileWriter, null);
		} else if ( type.equalsIgnoreCase("gml") ) {
			List list = new Vector();
			fileWriter = new FileWriter(tempFile);
			GMLWriter gmlWriter = new GMLWriter();
			gmlWriter.writeGML(network, view, list);
			GMLParser.printList(list, fileWriter);
		} else {
			throw new UnsupportedOperationException("Can't write " + type + " format files to GenomeSpace.");
		}

		if ( fileWriter != null )
			fileWriter.close();

		return tempFile;
	}
}
