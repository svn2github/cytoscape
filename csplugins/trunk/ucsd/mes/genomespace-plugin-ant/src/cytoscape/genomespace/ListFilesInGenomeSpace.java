package cytoscape.genomespace;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.logger.CyLogger;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.io.File;
import java.awt.FileDialog;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import org.genomespace.client.GsFile;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class ListFilesInGenomeSpace extends CytoscapeAction {

	private static final long serialVersionUID = 1234487711999989L;
	private static final CyLogger logger = CyLogger.getLogger(ListFilesInGenomeSpace.class);

	public ListFilesInGenomeSpace() {
		// Give your action a name here
		super("List Available Files");

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
		logger.info("Files on GenomeSpace for " + user.getUsername());
		Vector<String> fileNames = new Vector<String>();
		for (GsFile aFile: myFiles) {
			fileNames.add(aFile.getFilename());
		}

		displayFiles(fileNames);
	
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

	private void displayFiles(Vector<String> fileNames) {
		JList jl = new JList( fileNames );
		jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(jl);
		scrollPane.setPreferredSize(new Dimension(250, 80));
		JPanel jp = new JPanel();
		jp.add(scrollPane);
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),jp);
	}
}
