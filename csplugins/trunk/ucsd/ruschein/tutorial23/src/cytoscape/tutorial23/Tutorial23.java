/*
 *  A plug-in example demonstrating how to add attribute functions.
 */
package cytoscape.tutorial23;


import cytoscape.plugin.CytoscapePlugin;

import org.cytoscape.equations.EqnParser;
import org.cytoscape.equations.Parser;


public class Tutorial23 extends CytoscapePlugin {
	public Tutorial23() {
		final EqnParser theParser = Parser.getParser();
		theParser.registerFunction(new IXor());
	}	
}
