/*
 *  A plug-in example demonstrating how to add attribute functions.
 */
package cytoscape.tutorial23;


import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.Parser;
import cytoscape.plugin.CytoscapePlugin;


public class Tutorial23 extends CytoscapePlugin {
	public Tutorial23() {
		final AttribParser theParser = Parser.getParser();
		theParser.registerFunction(new IXor());
	}	
}
