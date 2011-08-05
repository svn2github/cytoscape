/*
 *  A plug-in to add Cytoscape-specific attribute functions.
 */
package org.cytoscape.EquationFunctions;


import cytoscape.plugin.CytoscapePlugin;

import org.cytoscape.equations.EqnParser;
import org.cytoscape.equations.Parser;

import org.cytoscape.EquationFunctions.functions.*;


public class EquationFunctionsPlugin extends CytoscapePlugin {
	public EquationFunctionsPlugin() {
		final EqnParser theParser = Parser.getParser();
		theParser.registerFunction(new Degree());
		theParser.registerFunction(new InDegree());
		theParser.registerFunction(new OutDegree());
		theParser.registerFunction(new SourceID());
		theParser.registerFunction(new TargetID());
	}	
}
