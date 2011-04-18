package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.genomespace.filechoosersupport.GenomeSpaceFileSystemView;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.datamanager.core.GSFileMetadata;


public class LoadNetworkFromGenomeSpace extends CytoscapeAction {
	static final class NetworkFileFilter extends FileFilter {
		public boolean accept(final File file) {
			final String extension = getFileExtension(file);
			return extension.equalsIgnoreCase("sif") || extension.equalsIgnoreCase("xgmml")
			       || extension.equalsIgnoreCase("gml");
		}

		public String getDescription() {
			return "Cytoscape network files";
		}

		private static String getFileExtension(final File file) {
			final String fileName = file.getName();
			final int lastDotPos = fileName.lastIndexOf('.');
			return (lastDotPos == -1) ? "" : fileName.substring(lastDotPos + 1);
		}
	}


	private static final long serialVersionUID = 7577788473487659L;
	private static final CyLogger logger = CyLogger.getLogger(LoadNetworkFromGenomeSpace.class);

	public LoadNetworkFromGenomeSpace() {
		super("Load Network");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		File tempFile = null;
		try {
			GsSession client = GSUtils.getSession(); 
			DataManagerClient dmc = client.getDataManagerClient();

			final JFileChooser chooser = new JFileChooser(new GenomeSpaceFileSystemView(dmc));
/*
			disableNewFolderButton(chooser);
			chooser.setFileFilter(new NetworkFileFilter());
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setDialogTitle("Select Network File");
*/
			int returnVal = chooser.showDialog(Cytoscape.getDesktop(), "Download");
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			final String selectedFile = chooser.getSelectedFile().getName();

			// Download the file from GenomeSpace:
			if (selectedFile != null) {
				logger.info("Downloading network file " + selectedFile);
				final String path =
					dmc.listDefaultDirectory().getDirectory().getPath()
					+ "/" + selectedFile;
				final GSFileMetadata downloadFileMetadata =
					dmc.getMetadata(path);

				tempFile = File.createTempFile("temp", "cynetwork");
				dmc.downloadFile(downloadFileMetadata, tempFile, true);
				logger.info("Saved downloaded file as " + tempFile);
			}
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						      ex.getMessage(), "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	public void disableNewFolderButton(final Container c) {
		int len = c.getComponentCount();
		
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton)comp;
				Icon icon = b.getIcon();
				if (((icon != null
				      && icon == UIManager.getIcon("FileChooser.newFolderIcon"))
				     || b.getText().equals("New Folder")))
{System.err.println("========================= Found the sucker!!");
					b.setEnabled(false);
}
			} else if (comp instanceof Container) // Recurse!
				disableNewFolderButton((Container)comp);
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
