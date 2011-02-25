package cytoscape.genomespace;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.logger.CyLogger;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import java.io.File;
import java.awt.FileDialog;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.genomespace.client.GsFile;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class DeleteFileInGenomeSpace extends CytoscapeAction {

	private static final long serialVersionUID = 4234432889999989L;
	private static final CyLogger logger = CyLogger.getLogger(DeleteFileInGenomeSpace.class);

	public DeleteFileInGenomeSpace() {
		// Give your action a name here
		super("Delete File");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
        setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {

		try {

		// login to GenomeSpace
		GsSession session = new GsSession();
		String username = "test";
		String password = "password";
		User user = session.login(username, password);
		logger.info("Logged in to GenomeSpace: " + session.isLoggedIn() + " as " + user.getUsername());

		// list the files present for this user
		DataManagerClient dmClient = gsSession.getDataManagerClient();
		GSDirectoryListing homeDirInfo = dmClient.listDefaultDirectory(); 

		String selectedFile = getSelectedFile( homeDirInfo.get list of files ); 

		// Delete the file from GenomeSpace
		if (selectedFile != null && files.get(selectedFile) != null) {
			logger.info("Deleting " + selectedFile);
			client.delete(files.get(selectedFile));
		}
	
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
