//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.base.Node;
import y.view.Graph2D;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.view.*;
import luna.LunaRootGraph; //For instantiating new RootGraph
import phoebe.PGraphView;  //for instanciating new Graph View


import cytoscape.GraphObjAttributes;
import cytoscape.SelectedSubGraphFactory;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
public class CloneGraphInNewWindowAction extends AbstractAction {
    CyWindow cyWindow;
    
    public CloneGraphInNewWindowAction(CyWindow cyWindow) {
        super("Whole graph");
        this.cyWindow = cyWindow;
    }
    
     public void actionPerformed(ActionEvent e) {
	 if (cyWindow.getCytoscapeObj().getConfiguration().isYFiles()) {  
		 performInYFilesMode();
	 }
	 else{
		 performInGinyMode();
	 }
    }//acionPerformed
    
    
    public void performInYFilesMode() {
        //save the vizmapper
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "CloneGraphInNewWindowAction.actionPerformed";
        oldNetwork.beginActivity(callerID);
        Graph2D oldGraph = oldNetwork.getGraph();
        
        //select every node in graph for the factory
        //save unselected nodes to restore initial state later
        Node[] nodes = oldGraph.getNodeArray();
        Set unselectedNodes = new HashSet();
        for (int i=0; i < nodes.length; i++) {
            if ( !oldGraph.isSelected(nodes[i]) ) {
                unselectedNodes.add(nodes[i]);
                oldGraph.setSelected(nodes[i], true);
            }
        }
        SelectedSubGraphFactory factory = new SelectedSubGraphFactory(oldGraph,
                                              oldNetwork.getNodeAttributes(),
                                              oldNetwork.getEdgeAttributes() );
        Graph2D subGraph = factory.getSubGraph();
        GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                               newEdgeAttributes, oldNetwork.getExpressionData() );
        newNetwork.setNeedsLayout(true);
        
        //deselect originally unselected nodes
        for (Iterator i = unselectedNodes.iterator(); i.hasNext(); ) {
            oldGraph.setSelected( (Node)i.next(), false );
        }
        oldNetwork.endActivity(callerID);
        
        String title = "selection";
        try {
            CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
                                              newNetwork, title);
            newWindow.showWindow();
        } catch (Exception e00) {
            System.err.println("exception when creating new window");
            e00.printStackTrace();
        }
    }
    
    void performInGinyMode() {
	    
	     //save the vizmapper catalog
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "NewWindowSelectedNodesEdgesAction.actionPerformed";
        //oldNetwork.beginActivity(callerID);
        GraphView view = cyWindow.getView();
        // hide unselected edges
        //needed since the factory ignores selection state of edges
       //int [] nodes = view.getNodeIndices();
       //int[] edges = view.getEdgeIndices();
      	GraphPerspective subGraph = view.getGraphPerspective(); //.createGraphPerspective(nodes, edges);
	PGraphView subView = new PGraphView(subGraph);
	//LunaRootGraph rootGraph = new LunaRootGraph (subGraph);
        GraphObjAttributes newNodeAttributes = oldNetwork.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = oldNetwork.getEdgeAttributes();
	
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                newEdgeAttributes, oldNetwork.getExpressionData(), true );
        newNetwork.setNeedsLayout(true);
      
        //oldNetwork.endActivity(callerID);
        
        String title = " cloned whole graph";
        try {
            //this call creates a WindowOpened event, which is caught by
            //cytoscape.java, enabling that class to manage the set of windows
            //and quit when the last window is closed
            CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
                                              newNetwork, title);
            newWindow.showWindow();
        } catch (Exception e00) {
            System.err.println("exception when creating new window");
            e00.printStackTrace();
        }
    }//end performinginyMode()
    
}

