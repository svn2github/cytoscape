package org.cytoscape.genomespace.internal;


import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class DeleteFileInGenomeSpace extends AbstractCyAction {
	private static final long serialVersionUID = 4234432889999989L;
	private static final Logger logger = LoggerFactory.getLogger(DeleteFileInGenomeSpace.class);
	private final CySwingApplication app;

	public DeleteFileInGenomeSpace(CyApplicationManager appMgr, CySwingApplication app) {
		// Give your action a name here
		super("Delete File in GenomeSpace",appMgr);
		this.app = app;

		// TODO put image in service props
		//      new ImageIcon(DeleteFileInGenomeSpace.class.getResource("/images/genomespace_icon.gif")));

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
				new GSFileBrowserDialog(app.getJFrame(), dataManagerClient,
							GSFileBrowserDialog.DialogType.FILE_DELETION_DIALOG);
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

	private String getSelectedFile(Collection<String> names) {
		String s = (String)JOptionPane.showInputDialog(
                    app.getJFrame(), "Select a file to delete:",
                    "Delete from GenomeSpace",
                    JOptionPane.WARNING_MESSAGE,
                    null, names.toArray() ,null);
		return s;
	}
}
