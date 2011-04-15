package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.genomespace.filechoosersupport.GenomeSpaceFileSystemView;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
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
			GsSession client = GSUtils.getSession(); 
			DataManagerClient dmc = client.getDataManagerClient();

			final JFileChooser chooser = new JFileChooser(new GenomeSpaceFileSystemView(dmc));
			int returnVal = chooser.showDialog(Cytoscape.getDesktop(), "Download");
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;
			final String selectedFile = chooser.getSelectedFile().getName();

			// Download the file from GenomeSpace:
			if (selectedFile != null) {
				logger.info("Downloading " + selectedFile);
				final String path =
					dmc.listDefaultDirectory().getDirectory().getPath()
					+ "/" + selectedFile;
				final GSFileMetadata downloadFileMetadata =
					dmc.getMetadata(path);
				final JFileChooser saveChooser = new JFileChooser();
				final File defaultSaveFile =
					new File(System.getProperty("user.home") + File.separator
						 + selectedFile);
				saveChooser.setSelectedFile(defaultSaveFile);
				returnVal = saveChooser.showSaveDialog(Cytoscape.getDesktop());
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;
				final File downloadTarget = saveChooser.getSelectedFile();
				dmc.downloadFile(downloadFileMetadata, downloadTarget, true);
				logger.info("Saved downloaded file as " + downloadTarget);
			}
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
