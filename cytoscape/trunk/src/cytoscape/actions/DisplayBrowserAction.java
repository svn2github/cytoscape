//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import phoebe.*;

import phoebe.util.*;
import giny.model.*;
import giny.view.*;
import java.util.*;
import edu.umd.cs.piccolo.*;

import cytoscape.browsers.*;
import cytoscape.util.*;


import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class DisplayBrowserAction extends AbstractAction  {

    NetworkView networkView;
    Vector attributeCategoriesToIgnore;
    final static String invisibilityPropertyName = "nodeAttributeCategories.invisibleToBrowser";
    String webBrowserScript;

    public DisplayBrowserAction(NetworkView networkView) {
        super ("Display attribute browser");
        this.networkView = networkView;
	Properties configProps = networkView.getCytoscapeObj().getConfiguration().getProperties();
	 webBrowserScript = configProps.getProperty("webBrowserScript", "noScriptDefined");
	 attributeCategoriesToIgnore = Misc.getPropertyValues(configProps, invisibilityPropertyName);
	 for (int i=0; i < attributeCategoriesToIgnore.size(); i++) {
		 System.out.println ("  ignore type " + attributeCategoriesToIgnore.get(i));
	 }

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			GinyUtils.deselectAllNodes(networkView.getView());
			//GinyUtils.deselectAllEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

