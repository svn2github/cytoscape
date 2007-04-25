package sbmlreader;

import java.util.*;
import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.ImportHandler;

/**
 This plugin will allow the reading of an SBML level 2 file.
 *
 *W.P.A. Ligtenberg, Eindhoven University of Technology
 */
public class SBMLReaderPlugin extends CytoscapePlugin {
    
    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public SBMLReaderPlugin() {
    	ImportHandler ih = Cytoscape.getImportHandler();
	ih.addFilter( new SBMLFilter() );
    }
    
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("Loads an SBML Level 2 file");
        return sb.toString();
    }

}

