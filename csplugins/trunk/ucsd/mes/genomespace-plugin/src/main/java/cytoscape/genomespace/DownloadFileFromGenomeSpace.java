package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.genomespace.filechoosersupport.GenomeSpaceFileSystemView;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class DownloadFileFromGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 7777788473487659L;
	private static final CyLogger logger = CyLogger.getLogger(DownloadFileFromGenomeSpace.class);

	public DownloadFileFromGenomeSpace() {
		// Give your action a name here
		super("Download File");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			final GsSession client = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = client.getDataManagerClient();

			final TreeSelectionDialog dialog =
				new TreeSelectionDialog(Cytoscape.getDesktop(), dataManagerClient);
			final GSFileMetadata fileMetadata = dialog.getSelectedFileMetadata();
			if (fileMetadata == null)
				return;

			final JFileChooser saveChooser = new JFileChooser();
			final File defaultSaveFile =
				new File(System.getProperty("user.home") + File.separator
					 + fileMetadata.getName());
			saveChooser.setSelectedFile(defaultSaveFile);
			if (saveChooser.showSaveDialog(Cytoscape.getDesktop())
			    != JFileChooser.APPROVE_OPTION)
				return;

			final File downloadTarget = saveChooser.getSelectedFile();
			dataManagerClient.downloadFile(fileMetadata, downloadTarget, true);
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}

	private String getSelectedFile(Collection<String> names) {
		String s = (String)JOptionPane.showInputDialog(
                    Cytoscape.getDesktop(), "Select a file to download:",
                    "Download from GenomeSpace",
                    JOptionPane.PLAIN_MESSAGE,
                    null, names.toArray() ,null);
		return s;
	}
}
