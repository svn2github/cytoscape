package cytoscape.weblaunch;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JFileChooser;
import java.nio.channels.*;
import java.io.FileOutputStream;

public class LaunchHelper {
	private static String version = "Cytoscape_v2.8.3";
	private static String installerURL = "http://www.cytoscape.org/download.html";

	private static final String MAC = "mac os x";
	private static final String WINDOWS = "windows";

	public static void main(String[] args) {

		String os = System.getProperty("os.name").toLowerCase();

		String exe = getExecutable(os);
		String path = getBestGuessPath(os);

		if ( !executableExists(path,exe) ) {
			File file = getDirectory();
			if ( file != null )
				path = file.getAbsolutePath();
		}

		while ( !executableExists(path,exe) ) {
			int res = JOptionPane.showConfirmDialog(null, "We can't find the Cytoscape executable in the specified location.\nWould you like to try another location?", "Select Cytoscape Installation Directory", JOptionPane.YES_NO_OPTION);	

			if ( res == JOptionPane.YES_OPTION ) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select Cytoscape Installation Directory");
 
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) 
					path = fc.getSelectedFile().getAbsolutePath();
			} else {
				return;
			}
		}

		String[] command = createCommand(getFile(path,exe),downloadPlugins());

		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();
			int exitVal = p.waitFor();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
							  "Cannot launch "+exe+" in\n" + path,
							  "Cannot Launch Executable",
							  JOptionPane.ERROR_MESSAGE);
		}
	}

	private static String[] createCommand(File f, String[] plugins) {
		String[] command = new String[ (plugins.length * 2) + 1];
		int i = 0;
		command[i] = f.getAbsolutePath();
		for ( String p : plugins ) {
			command[++i] = "-p";
			command[++i] = p;
		}
		return command;
	}

	private static String[] downloadPlugins() {

		String[] urls = new String[] { 
			"http://chianti.ucsd.edu/cyto_web/plugins/pluginjardownload.php?id=496", // GenomeSpace libs
			"http://chianti.ucsd.edu/cyto_web/plugins/pluginjardownload.php?id=495", // GenomeSpace
			"http://chianti.ucsd.edu/cyto_web/plugins/pluginjardownload.php?id=494", // CyTable reader
			"http://chianti.ucsd.edu/cyto_web/plugins/pluginjardownload.php?id=493"  // NDB Reader
		};

		String[] filePaths = new String[urls.length];
		for (int i = 0; i < urls.length; i++) { 
			filePaths[i] = downloadURL(urls[i]).getAbsolutePath();	
		}
		return filePaths;
	}

	private static File downloadURL(String u) {
		File f = null;
		FileOutputStream fos = null; 
		try {
			URL url = new URL(u);
			f = File.createTempFile("plugin",".jar");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			fos = new FileOutputStream(f);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( fos != null ) {
				try { fos.close(); } catch (IOException ioe) { }
				fos = null;
			}
		}
		return f;
	}

	private static boolean executableExists(String path, String exe) {
		File file = getFile(path,exe); 
		return file.exists(); 
	}

	private static File getFile(String path, String exe) {
		return new File(path + System.getProperty("file.separator") + exe);
	}

	private static String getExecutable(String os) {
		String exe; 
		if (os.startsWith(WINDOWS))	
			exe = "cytoscape.bat"; 
		else 
			exe = "cytoscape.sh";

		return exe;
	}

	private static String getBestGuessPath(String os) {
		String path;
		if (os.equals(MAC))  
			path = "/Applications";
		else						
			path = System.getProperty("user.home");
		path += "/" + version;
		return path;
	}

	private static void openURL(String urlString) {
		URL url = null; 

		try {

			url = new URL(urlString);

			if (!Desktop.isDesktopSupported()) {
				JOptionPane.showMessageDialog(null,
								  "Java Desktop is not supported",
								  "Cannot Launch Link",
								  JOptionPane.WARNING_MESSAGE);
				return;
			}

			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) {
				JOptionPane.showMessageDialog(null,
								  "Java Desktop doesn't support the browse action",
								  "Cannot Launch Link",
								  JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (url == null) {
				JOptionPane.showMessageDialog(null,
								  "InvalidURL: Failed to launch the link to installer:\n"+installerURL,
								  "Cannot Launch Link",
								  JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			URI uri = new URI(url.toString());
			desktop.browse(uri);
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(null,
							  "URISyntaxException: Failed to launch the link to installer:\n"+url,
							  "Cannot Launch Link",
							  JOptionPane.ERROR_MESSAGE);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
							  "MalformedURLException: Failed to launch the link to installer:\n"+url,
							  "Cannot Launch Link",
							  JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
							  "IOException: Failed to launch the link to installer:\n"+url,
							  "Cannot Launch Link",
							  JOptionPane.ERROR_MESSAGE);
		}
	}

	private static File getDirectory() {
		int res = JOptionPane.showConfirmDialog(null, "Is Cytoscape installed on this computer?", "Select Cytoscape Installation Directory", JOptionPane.YES_NO_OPTION);	

		if ( res == JOptionPane.YES_OPTION ) {
			JOptionPane.showMessageDialog(null, "Please choose the directory where\nCytoscape is installed on your system.", "Select Cytoscape Installation Directory", JOptionPane.INFORMATION_MESSAGE);	
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("Select Cytoscape Installation Directory");
 
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
				return fc.getSelectedFile();
			else
				return null;
		} else {
			JOptionPane.showMessageDialog(null, "Please download and install Cytoscape.\nWe'll open a browser to the download page.", "Select Cytoscape Installation Directory", JOptionPane.INFORMATION_MESSAGE);	
			openURL(installerURL);
			return null;
		}
	}
}
