/**
 * 
 */
package cytoscape.plugin;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PluginProperties extends Properties {
	private String configFileName = "plugin.props";
	
	public PluginProperties(JarFile jar) throws IOException {
		JarEntry Entry = jar.getJarEntry(configFileName);
		if (Entry != null) 
			readPluginProperties(jar.getInputStream(Entry));
	}
	
	private void readPluginProperties(InputStream is) throws IOException {
		if (is == null || is.available() == 0) {
			// throw an error!
			String Msg = "";
			if (is == null) {
				Msg = "input stream is null";
			} else if (is.available() == 0) {
				Msg = "0 bytes in input stream";
			}
	
			IOException Error = new IOException("Unable to load "
					+ configFileName + ": " + Msg);
			throw Error;
		} else {
			load(is);
		}
	}

	public PluginInfo getPluginInfoObject() throws ManagerException {
		if (!expectedPropertiesPresent()) {
			throw new ManagerException("Required properties are missing from plugins.props file");
		}
		
		PluginInfo pi;
		if (containsKey("uniqueID")) {
			pi = new PluginInfo(getProperty("uniqueID"));
		} else {
			pi = new PluginInfo();
		}
		
		// required parameters
		pi.setName(getProperty(name));

		try {
			pi.setPluginVersion( Double.valueOf(getProperty(version)) );
		} catch (java.lang.NumberFormatException ne) { // skip it or set it to a default value??
			System.err.println(pi.getName() + " version is incorrectly formatted, format is: \\d+.\\d+. Version set to 0.1 to allow plugin to load");
			ne.printStackTrace();
			pi.setPluginVersion(0.1);
		}
		
		pi.setDescription(getProperty(desc));
		pi.setCategory(getProperty(category));
		
		pi.setCytoscapeVersion(getProperty(cyVersion));
		
		// optional parameters
		if (containsKey(projUrl)) {
			pi.setProjectUrl(getProperty(projUrl));
		}
		
		if (containsKey(authors)) {
			// split up the value and add each
			String[] AuthInst = parseAuthors();
			for (String ai: AuthInst) {
				String[] CurrentAI = ai.split(":");
				if (CurrentAI.length != 2) {
					System.err.println("Author line '" + ai + "' incorrectly formatted. Please enter authors as 'Name1, Name2 and Name3: Institution");
					continue;
				}
				pi.addAuthor(CurrentAI[0], CurrentAI[1]);
			}
		}
		return pi;
	}


	private boolean expectedPropertiesPresent() {
		if (!containsKey(name) ||			
			!containsKey(desc) ||
			!containsKey(version) ||
			!containsKey(cyVersion) ||
			!containsKey(category))
			return false;
		else return true;
	}
	
	private String[] parseAuthors() {
		String AuthorProp = getProperty(authors);
		String[] Authors = AuthorProp.split(";");
		return Authors;
	}
	
	// Property file keys
	private String name = "pluginName";
	private String desc = "pluginDescription";
	private String version = "pluginVersion";
	private String cyVersion = "cytoscapeVersion";
	private String category = "pluginCategory";
	private String projUrl = "projectURL";
	private String authors = "pluginAuthorsIntsitutions";
}
