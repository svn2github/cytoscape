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
public class UploadFileToGenomeSpace extends CytoscapeAction {

	private static final long serialVersionUID = 9988760123456789L;
	private static final CyLogger logger = CyLogger.getLogger(UploadFileToGenomeSpace.class);

	public UploadFileToGenomeSpace() {
		// Give your action a name here
		super("Upload File");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
        setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {

		try {
		File f =  FileUtil.getFile("",FileDialog.LOAD);
		if ( f == null )
			return;

		// login to GenomeSpace
		GsSession client = new GsSession();
		String username = "test";
		String password = "password";
		User user = client.login(username, password);
		System.out.println("Logged in to GenomeSpace: " + client.isLoggedIn() + " as " + user.getUsername());

	
	

		GsFile gsf = new GsFile(f); 
		client.uploadFile(gsf);
	
		// list the files present for this user
		List<GsFile> myFiles = client.list();
		System.out.println("Files on GenomeSpace for " + user.getUsername());
		for (GsFile aFile: myFiles){
			System.out.println("\t" + aFile.getFilename());
		}
	
		// Download the file back from GenomeSpace
		myFiles = client.list();
		if (myFiles.size() > 0){
			System.out.println("Downloading " + myFiles.get(0).getFilename());
			GsFile localCopy = client.downloadFile(myFiles.get(0));
			System.out.println("\t saved to: " + localCopy.getFile().getAbsolutePath());
		}
	

		// Delete a file from GenomeSpace
		myFiles = client.list();
		if (myFiles.size() > 0){
			System.out.println("Deleting " + myFiles.get(0).getFilename());
			client.delete(myFiles.get(0));
		}
		myFiles = client.list();
		System.out.println("After deletion, files on GenomeSpace for " + user.getUsername());
		for (GsFile aFile: myFiles){
			System.out.println("\t" + aFile.getFilename());
		}
	
	
		// register a new user account
		// (note this will fail for a duplicate username since there can only be one account for each name)
		String newUsername = "test_cdk_" + System.currentTimeMillis();
		GsSession newClient = new GsSession();

		User newUser = newClient.registerUser(newUsername, "password", "test@noreply.org");
		System.out.println("Logged in to GenomeSpace: " + newClient.isLoggedIn() +" as " + newUser.getUsername());
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

}
