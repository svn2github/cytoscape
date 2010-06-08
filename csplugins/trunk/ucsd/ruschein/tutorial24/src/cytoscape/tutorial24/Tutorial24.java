/*
 *  A plug-in example demonstrating how to add plugin-specific help to the main Cytoscape help.
 */
package cytoscape.tutorial24;


import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.Parser;
import cytoscape.plugin.CytoscapePlugin;


public class Tutorial24 extends CytoscapePlugin {
	public Tutorial24() {
		final AttribParser theParser = Parser.getParser();
		theParser.registerFunction(new IXor());
	}	
}
