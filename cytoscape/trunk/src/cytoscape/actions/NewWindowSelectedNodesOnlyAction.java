//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

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
public class NewWindowSelectedNodesOnlyAction extends AbstractAction {
    CyWindow cyWindow;
    
    public NewWindowSelectedNodesOnlyAction(CyWindow cyWindow) {
        super("Selected nodes, All edges");
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
    
    
    void performInYFilesMode() {
        //save the vizmapper
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "NewWindowSelectedNodesOnlyAction.actionPerformed";
        oldNetwork.beginActivity(callerID);
        SelectedSubGraphFactory factory =
            new SelectedSubGraphFactory (oldNetwork.getGraph(),
                                         oldNetwork.getNodeAttributes(),
                                         oldNetwork.getEdgeAttributes() );
        Graph2D subGraph = factory.getSubGraph();
        GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                newEdgeAttributes, oldNetwork.getExpressionData() );
        newNetwork.setNeedsLayout(true);
        oldNetwork.endActivity(callerID);

        //might want a more interesting window title
        String title = "selection";
        try {
            boolean requestFreshLayout = true;
            //This constructor generates a WindowOpened event, which is caught by
            //cytoscape.java enabling that class to manage the set of windows and
            //quit when the last window disappears
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
        String callerID = "NewWindowSelectedNodesOnlyAction.actionPerformed";
        //oldNetwork.beginActivity(callerID);
        GraphView view = cyWindow.getView();
        // hide unselected edges
        //needed since the factory ignores selection state of edges
       int [] nodes = view.getSelectedNodeIndices();
      //int[] edges = view.getSelectedEdgeIndices();
      	GraphPerspective subGraph = view.getGraphPerspective().createGraphPerspective(nodes);//, edges);
	PGraphView subView = new PGraphView(subGraph);
	//LunaRootGraph rootGraph = new LunaRootGraph (subGraph);
        GraphObjAttributes newNodeAttributes = oldNetwork.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = oldNetwork.getEdgeAttributes();
	
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                newEdgeAttributes, oldNetwork.getExpressionData(), true );
        newNetwork.setNeedsLayout(true);
      
        //oldNetwork.endActivity(callerID);
        
        String title = " selection";
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

