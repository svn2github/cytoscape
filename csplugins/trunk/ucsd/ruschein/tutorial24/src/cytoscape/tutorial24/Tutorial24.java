/*
 *  A plug-in example demonstrating how to add plugin-specific help to the main Cytoscape help.
 */
package cytoscape.tutorial24;


import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyHelpBroker;

import java.net.URL;

import javax.help.HelpSet;


public class Tutorial24 extends CytoscapePlugin {
	public Tutorial24() {
		addHelp();
	}	

	/**
	 *  Hook plugin help into the Cytoscape main help system:
	 */
	private void addHelp() {
		final String HELP_SET_NAME = "/help/jhelpset";
		final ClassLoader classLoader = Tutorial24.class.getClassLoader();
		URL helpSetURL;
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			if (!CyHelpBroker.addHelpSet(newHelpSet))
				System.err.println("Tutorial24: Failed to add help set!");
		} catch (final Exception e) {
			System.err.println("Tutorial24: Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}
}
