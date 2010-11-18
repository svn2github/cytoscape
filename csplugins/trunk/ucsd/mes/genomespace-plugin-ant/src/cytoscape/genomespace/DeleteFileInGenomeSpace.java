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
		GsSession client = new GsSession();
		String username = "test";
		String password = "password";
		User user = client.login(username, password);
		logger.info("Logged in to GenomeSpace: " + client.isLoggedIn() + " as " + user.getUsername());

		// list the files present for this user
		List<GsFile> myFiles = client.list();
		if (myFiles.size() > 0){
			logger.info("Deleting " + myFiles.get(0).getFilename());
			client.delete(myFiles.get(0));
		}

		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

}
