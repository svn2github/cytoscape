package cytoscape.weblaunch;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class LaunchHelper {

	private static String version = "Cytoscape_v2.8.3";
	private static String installerURL = "http://www.cytoscape.org/download.html";

	private static final String MAC = "mac os x";
	private static final String WINDOWS = "windows";
	private static final String PREFERRED_PATH = "preferred.path";

	public static void main(String[] args) {

		String os = System.getProperty("os.name").toLowerCase();

		String exe = getExecutable(os);
		String path = getBestGuessPath(os);
		path = validatePath(path, exe); 

		if ( path == null )
			return;

		// OK, all systems go!

		String[] plugins = downloadPlugins();
		String[] command = createCommand(getFile(path,exe),plugins);

		storePreferredPath(path);

		launch(command);
	}

	private static void launch(final String[] command) {
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
							  "Error launching: " + command[0] + "\n\nCaused by\n" + e.getMessage(),
							  "Could Not Launch Cytoscape",
							  JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void storePreferredPath(String path) {
		try {
			Properties props = new Properties();
			props.setProperty(PREFERRED_PATH,path);
			props.store( new FileOutputStream( getPropsFile() ), "Properties for GenomeSpace");
		} catch (IOException ioe) { ioe.printStackTrace(System.err); }
	}

	private static String validatePath(final String inpath, final String exe) {
		String path = inpath;

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
				return null;
			}
		}

		return path;
	}

	private static String[] createCommand(final File f, final String[] plugins) {
		String[] command = new String[ (plugins.length * 2) + 1];
		int i = 0;
		command[i] = f.getAbsolutePath();
		for ( String plugin : plugins ) {
			command[++i] = "-p";
			command[++i] = plugin;
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

		List<String> filePaths = new ArrayList<String>(); 
		for (String url : urls) { 
			File f = downloadURL(url);
			if ( f != null )
				filePaths.add( f.getAbsolutePath() );
			else
				System.err.println("Couldn't download plugin URL: " + url);
		}

		return filePaths.toArray(new String[filePaths.size()]);
	}

	private static File downloadURL(final String u) {
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
			e.printStackTrace(System.err);
		} finally {
			if ( fos != null ) {
				try { fos.close(); } catch (IOException ioe) { ioe.printStackTrace(System.err); }
				fos = null;
			}
		}
		return f;
	}

	private static boolean executableExists(final String path, final String exe) {
		File file = getFile(path,exe); 
		return file.exists(); 
	}

	private static File getFile(final String path, final String exe) {
		return new File(path + System.getProperty("file.separator") + exe);
	}

	private static String getExecutable(final String os) {
		String exe; 
		if (os.startsWith(WINDOWS))	
			exe = "cytoscape.bat"; 
		else 
			exe = "cytoscape.sh";

		return exe;
	}

	private static String getBestGuessPath(final String os) {
		String path = getPreferredPath();
		if ( path != null )
			return path;
		
		if (os.equals(MAC))  
			path = "/Applications";
		else						
			path = System.getProperty("user.home");
		path += "/" + version;
		return path;
	}

	private static File getPropsFile() {
		File f = new File( System.getProperty("user.home") + System.getProperty("file.separator") + 
		                   ".cytoscape" +  System.getProperty("file.separator") + "genomespace-cytoscape.props" );
		return f;
	}

	private static String getPreferredPath() {
		try { 
			File f = getPropsFile(); 
			if ( !f.exists() )
				return null;

			Properties props = new Properties();
			props.load( new FileInputStream(f) );
			return props.getProperty(PREFERRED_PATH);

		} catch (IOException ioe) { ioe.printStackTrace(System.err); }
		return null;
	}

	private static void openURL(final String urlString) {
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
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
							  "Exception: Failed to launch the link to installer:\n"+url,
							  "Could Not Open Link",
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
