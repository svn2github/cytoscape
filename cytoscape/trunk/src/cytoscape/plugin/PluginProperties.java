/**
 * 
 */
package cytoscape.plugin;

import java.io.*;
import java.util.Properties;

public class PluginProperties extends Properties {
	private String configFileName = "plugin.props";


	public PluginInfo getPluginInfoObject() {
		if (!expectedPropertiesPresent()) {
			// TODO throw an exception
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
			pi.setPluginVersion(0.1);
		}
		
		pi.setDescription(desc);
		pi.setCategory(category);
		
		pi.setCytoscapeVersion(getProperty(cyVersion));
		
		// optional parameters
		if (containsKey(projUrl)) {
			pi.setProjectUrl(getProperty(projUrl));
		}
		
		if (containsKey(authors)) {
			// split up the value and add each
			String[] AuthInst = parseAuthors();
			for (String ai: AuthInst) {
				String[] CurrentAI = ai.split(",");
				if (CurrentAI.length > 2) {
					System.err.println("Author line '" + ai + "' incorrectly formatted. Please enter authors as 'Name1, Name2 and Name3: Institution");
					continue;
				}
				pi.addAuthor(CurrentAI[0], CurrentAI[1]);
			}
		}
		return pi;
	}

	private void readPluginProperties() throws IOException {
		InputStream is = CytoscapePlugin.class.getResourceAsStream(configFileName);
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
		String[] Authors = AuthorProp.split("\n");
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
