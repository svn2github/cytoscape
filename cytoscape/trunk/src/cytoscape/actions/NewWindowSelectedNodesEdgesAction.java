//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.base.*;
import y.view.Graph2D;
import y.util.GraphHider;

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import cytoscape.SelectedSubGraphFactory;
//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public NewWindowSelectedNodesEdgesAction(CytoscapeWindow cytoscapeWindow) {
        super("Selected nodes, Selected edges");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        //save the vizmapper
        cytoscapeWindow.saveCalculatorCatalog();
        Graph2D graph = cytoscapeWindow.getGraph();
        // allows us to temporarily hide unselected nodes/edges
        //yes, but why are we doing this in the first place?? -AM 2003/06/24
        GraphHider hider = new GraphHider(graph);

        // hide unselected nodes
        for (NodeCursor nodes = graph.nodes(); nodes.ok(); nodes.next()) {
            if (!graph.isSelected(nodes.node())) {hider.hide(nodes.node());}
        }

        // hide unselected edges
        for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
            if (!graph.isSelected(edges.edge())) {hider.hide(edges.edge());}
        }

        SelectedSubGraphFactory factory
            = new SelectedSubGraphFactory(graph,
                                          cytoscapeWindow.getNodeAttributes(),
                                          cytoscapeWindow.getEdgeAttributes() );
      Graph2D subGraph = factory.getSubGraph();
      GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
      GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();

      // unhide unselected nodes & edges
      hider.unhideAll();

      String title = "selection";
      try {
          boolean requestFreshLayout = true;
          //this call creates a WindowOpened event, which is caught by
          //cytoscape.java, enabling that class to manage the set of windows
          //and quit when the last window is closed
          CytoscapeWindow newWindow =
              new CytoscapeWindow (cytoscapeWindow.getParentApp(),
                                   cytoscapeWindow.getConfiguration(),
                                   cytoscapeWindow.getLogger(), subGraph,
                                   cytoscapeWindow.getExpressionData(),
                                   cytoscapeWindow.getBioDataServer(),
                                   newNodeAttributes, newEdgeAttributes, 
                                   "dataSourceName",
                                   cytoscapeWindow.getExpressionDataFileName(),
                                   title, requestFreshLayout);
      }
      catch (Exception e00) {
          System.err.println ("exception when creating new window");
          e00.printStackTrace ();
      }
    } // actionPerformed
}

