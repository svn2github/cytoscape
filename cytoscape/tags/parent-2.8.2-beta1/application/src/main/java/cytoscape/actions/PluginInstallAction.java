/**
 * 
 */
package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import cytoscape.dialogs.plugins.PluginUpdateDialog;

import cytoscape.plugin.DownloadableInfo;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerException;
import cytoscape.plugin.PluginStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class PluginInstallAction extends CytoscapeAction {
	protected static CyLogger logger = CyLogger.getLogger(PluginInstallAction.class);

	public PluginInstallAction() {
		super("Install Plugin from File");
		setPreferredMenu("Plugins");

		if (PluginManager.usingWebstartManager()) {
			setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (PluginManager.usingWebstartManager())
			return;

		PluginManager manager = PluginManager.getPluginManager();

		// Get the jar file
		File[] jarFiles = FileUtil.getFiles("Select Plugin File", FileUtil.LOAD, 
	  	                              new CyFileFilter[] {new CyFileFilter("jar")});

		if (jarFiles == null || jarFiles.length == 0) return;

		File tempDir = manager.getPluginManageDirectory();

		for (File file: jarFiles) {
			// Copy the file into the temp directory
			File outputFile = new File(tempDir, file.getName());
			try {
				copyfile(file, outputFile);

			// Now we know what we want to load -- load it.
				manager.loadPlugin(outputFile);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Unable to install plugin: "+ex, 
				                              "Plugin install error", JOptionPane.ERROR_MESSAGE);
				logger.error("Unable to install plugin: "+ex.getMessage(), ex);
			}
		}
		return;
	}

	private void copyfile(File src, File dest) throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);

		byte [] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

}
