package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
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
public class SaveSessionToGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 9988760123456789L;
	private static final CyLogger logger = CyLogger.getLogger(UploadFileToGenomeSpace.class);

	public SaveSessionToGenomeSpace() {
		// Give your action a name here
		super("Save Session As");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			final GsSession client = GSUtils.getSession();
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final List<String> acceptableExtensions = new ArrayList<String>();
			acceptableExtensions.add("cys");
			final TreeSelectionDialog dialog =
				new TreeSelectionDialog(Cytoscape.getDesktop(), dataManagerClient,
							acceptableExtensions,
							/* isSaveAsDialog = */ true);
			final String saveFileName = dialog.getSaveFileName();
			if (saveFileName == null)
				return;
System.err.println("++++++++++++++++++++++++++++++++++ saveFileName="+saveFileName);
/*
			File f =  FileUtil.getFile("Select file to upload", FileDialog.LOAD);
			if (f == null)
				return;


			final String targetDirectoryPath =
				dmc.listDefaultDirectory().getDirectory().getPath();
			final GSFileMetadata uploadedFileMetadata =
				dmc.uploadFile(f, targetDirectoryPath, f.getName());
			if (uploadedFileMetadata != null)
				JOptionPane.showMessageDialog(
						Cytoscape.getDesktop(),
						f.getName() + " successfully uploaded to GenomeSpace!",
						 "Information", JOptionPane.INFORMATION_MESSAGE);
*/
		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}
}
