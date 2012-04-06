package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.logger.CyLogger;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.ui.GSFileBrowserDialog;


public class DeleteFileInGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 4234432889999989L;
	private static final CyLogger logger = CyLogger.getLogger(DeleteFileInGenomeSpace.class);

	public DeleteFileInGenomeSpace() {
		// Give your action a name here
		super("Delete File in GenomeSpace",
		      new ImageIcon(DeleteFileInGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			final GsSession session = GSUtils.getSession(); 
			final DataManagerClient dataManagerClient = session.getDataManagerClient();
			final GSFileBrowserDialog dialog =
				new GSFileBrowserDialog(Cytoscape.getDesktop(), dataManagerClient,
							GSFileBrowserDialog.DialogType.FILE_DELETION_DIALOG);
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

	private String getSelectedFile(Collection<String> names) {
		String s = (String)JOptionPane.showInputDialog(
                    Cytoscape.getDesktop(), "Select a file to delete:",
                    "Delete from GenomeSpace",
                    JOptionPane.WARNING_MESSAGE,
                    null, names.toArray() ,null);
		return s;
	}
}
