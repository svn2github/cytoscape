//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;



import giny.model.*;
import giny.view.*;
import java.util.*;

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
    

    public void actionPerformed (ActionEvent ev) {	
        List nvlist = networkView.getView().getSelectedNodes();
        Vector nodeList = new Vector();
        Iterator ni = nvlist.iterator();
        while (ni.hasNext()) {
            NodeView nview =(NodeView) ni.next();
            giny.model.Node n = nview.getNode();
            nodeList.add(n);
        }//while
        
        Node [] selectedNodes = (giny.model.Node []) nodeList.toArray (new giny.model.Node [0]);
        
        List evList = networkView.getView().getSelectedEdges();
        
        Vector edgeList = new Vector();
        Iterator ei = evList.iterator();
        while (ei.hasNext()) {
            EdgeView eview =(EdgeView) ei.next();
            giny.model.Edge e = eview.getEdge();
            edgeList.add(e);
        }//while
        
        giny.model.Edge [] selectedEdges = (giny.model.Edge []) edgeList.toArray (new Edge [0]);
        
        TabbedBrowser nodeBrowser = null;
        TabbedBrowser edgeBrowser = null;
        
        if (selectedNodes.length == 0 && selectedEdges.length == 0) {
            JOptionPane.showMessageDialog (null, "No selected nodes or edges", "Error",
                                           JOptionPane.ERROR_MESSAGE);
        }
        
        if (selectedNodes.length > 0) {
            nodeBrowser = new TabbedBrowser (selectedNodes, networkView.getNetwork().getNodeAttributes(),
                    attributeCategoriesToIgnore, webBrowserScript, TabbedBrowser.BROWSING_NODES);
        }
        
        if (selectedEdges.length > 0) {
            edgeBrowser = new TabbedBrowser (selectedEdges, networkView.getNetwork().getEdgeAttributes(),
                    attributeCategoriesToIgnore, webBrowserScript, TabbedBrowser.BROWSING_EDGES);
        }
    }//action performed
}

