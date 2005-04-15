// $Revision$
// $Date$
// $Author$


package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;
import giny.model.*;
import giny.view.*;
import java.util.*;

import cytoscape.browsers.*;
import cytoscape.util.*;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;

public class DisplayBrowserAction extends CytoscapeAction  {

  Vector attributeCategoriesToIgnore;
  final static String invisibilityPropertyName = "nodeAttributeCategories.invisibleToBrowser";
  String webBrowserScript;

  public DisplayBrowserAction() {
    super ("Display attribute browser");
    setPreferredMenu( "Data" );
    setAcceleratorCombo( KeyEvent.VK_F5, 0 );
    Properties configProps = CytoscapeInit.getProperties();
    webBrowserScript = configProps.getProperty("webBrowserScript", "noScriptDefined");
    attributeCategoriesToIgnore = Misc.getPropertyValues(configProps, invisibilityPropertyName);
    for (int i=0; i < attributeCategoriesToIgnore.size(); i++) {
      System.out.println ("  ignore type " + attributeCategoriesToIgnore.get(i));
    }

  }
    

  public void actionPerformed ( ActionEvent ev ) {	
    // List nvlist = Cytoscape.getCurrentNetworkView().getSelectedNodes();
//     ArrayList nodeList = new ArrayList( nvlist.size() );
//     Iterator ni = nvlist.iterator();
//     while ( ni.hasNext() ) {
//       NodeView nview = ( NodeView )ni.next();
//       //giny.model.Node n = nview.getNode();
//       giny.model.Node n = Cytoscape.getRootGraph().getNode( nview.getRootGraphIndex() );
//       nodeList.add(n);
//     }//while
        
//     Node[] selectedNodes = ( giny.model.Node[] ) nodeList.toArray (new giny.model.Node [0]);
    giny.model.Node [] selectedNodes = (giny.model.Node[]) Cytoscape.getCurrentNetwork().getFlaggedNodes().toArray(new giny.model.Node[0]);
    
//     List evList = Cytoscape.getCurrentNetworkView().getSelectedEdges();
        
//     ArrayList edgeList = new ArrayList( evList.size() );
//     Iterator ei = evList.iterator();
//     while (ei.hasNext()) {
//       EdgeView eview =(EdgeView) ei.next();
//       //giny.model.Edge e = eview.getEdge();
//       giny.model.Edge e = Cytoscape.getRootGraph().getEdge( eview.getRootGraphIndex() );
//       edgeList.add(e);
//     }//while
        
//     giny.model.Edge[] selectedEdges = ( giny.model.Edge[] ) edgeList.toArray (new Edge [0]);
 
    giny.model.Edge [] selectedEdges = (giny.model.Edge[]) Cytoscape.getCurrentNetwork().getFlaggedEdges().toArray(new giny.model.Edge[0]);    
    TabbedBrowser nodeBrowser = null;
    TabbedBrowser edgeBrowser = null;
        
    if (selectedNodes.length == 0 && selectedEdges.length == 0) {
      JOptionPane.showMessageDialog (null, "No selected nodes or edges", "Error",
                                     JOptionPane.ERROR_MESSAGE);
    }
        
    if (selectedNodes.length > 0) {
      nodeBrowser = new TabbedBrowser (selectedNodes, 
                                       Cytoscape.getNodeNetworkData(),
                                       attributeCategoriesToIgnore, 
                                       webBrowserScript, 
                                       TabbedBrowser.BROWSING_NODES);
    }
        
    if (selectedEdges.length > 0) {
      edgeBrowser = new TabbedBrowser (selectedEdges, 
                                       Cytoscape.getEdgeNetworkData(),
                                       attributeCategoriesToIgnore, 
                                       webBrowserScript, 
                                       TabbedBrowser.BROWSING_EDGES);
    }
  }//action performed
}

