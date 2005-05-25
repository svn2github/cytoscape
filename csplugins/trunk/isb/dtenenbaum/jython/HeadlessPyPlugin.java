// HeadlessPyPlugin.java
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.isb.dtenenbaum.jython;
//----------------------------------------------------------------------------------------

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

import org.python.util.*;



/** 
 * 
 */
public class HeadlessPyPlugin extends CytoscapePlugin {

	
	public HeadlessPyPlugin() {
		String scriptName = getScriptName();
		if (null == scriptName)return;
		PythonInterpreter interp = new PythonInterpreter();
		interp.execfile(scriptName);
	}


	private String getScriptName() {
		String [] args = null;
		if (Cytoscape.getCytoscapeObj () != null) {
		  args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
		}


		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--script")) {
				if (i + 1 > args.length)
					throw new IllegalArgumentException("error!  no --script value");
				else
					return(args[i + 1]);
			} 
		}
		return null; 
	}

	

	//------------------------------------------------------------------------------
} // class HeadlessPyPlugin
