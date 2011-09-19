package org.cytoscape.genomespace.internal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;


/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class UploadFileToGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 9988760123456789L;
	private static final Logger logger = LoggerFactory.getLogger(UploadFileToGenomeSpace.class);
	private final CySwingApplication app;

	public UploadFileToGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		// Give your action a name here
		super("Upload File",appMgr);
		this.app = app;

		// TODO
		// new ImageIcon(UploadFileToGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("File.Export.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			File f =  null; /* TODO FileUtil.getFile("Select file to upload", FileDialog.LOAD); */
			if (f == null)
				return;

			GsSession client = GSUtils.getSession(); 
			DataManagerClient dmc = client.getDataManagerClient();

			final String targetDirectoryPath =
				dmc.listDefaultDirectory().getDirectory().getPath();
			final GSFileMetadata uploadedFileMetadata =
				dmc.uploadFile(f, targetDirectoryPath, f.getName());
			if (uploadedFileMetadata != null)
				JOptionPane.showMessageDialog(
						app.getJFrame(),
						f.getName() + " successfully uploaded to GenomeSpace!",
						 "Information", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (final Exception ex) {
			logger.error("GenomeSpace failed", ex);
			JOptionPane.showMessageDialog(app.getJFrame(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	}
}
