/*
 *  A plug-in example demonstrating how to add attribute functions.
 */
package cytoscape.tutorial23;


import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.Parser;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;


public class Tutorial23 extends CytoscapePlugin {
	public Tutorial23() {
		final AttribParser theParser = Parser.getParser();
		theParser.registerFunction(new IXor());
	}	
}
