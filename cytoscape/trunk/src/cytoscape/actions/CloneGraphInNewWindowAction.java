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
}

