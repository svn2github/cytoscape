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

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import cytoscape.SelectedSubGraphFactory;
//-------------------------------------------------------------------------
public class NewWindowSelectedNodesOnlyAction extends AbstractAction {
  CytoscapeWindow cytoscapeWindow;
  
  public NewWindowSelectedNodesOnlyAction(CytoscapeWindow cytoscapeWindow) {
      super("Selected nodes, All edges");
      this.cytoscapeWindow = cytoscapeWindow;
  }

  public void actionPerformed(ActionEvent e) {
    //save the vizmapper
    cytoscapeWindow.saveCalculatorCatalog();
    SelectedSubGraphFactory factory =
            new SelectedSubGraphFactory (cytoscapeWindow.getGraph(),
                                         cytoscapeWindow.getNodeAttributes(),
                                         cytoscapeWindow.getEdgeAttributes() );
    Graph2D subGraph = factory.getSubGraph();
    GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
    GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();

    //might want a more interesting window title
    String title = "selection";
    try {
      boolean requestFreshLayout = true;
      //This constructor generates a WindowOpened event, which is caught by
      //cytoscape.java enabling that class to manage the set of windows and
      //quit when the last window disappears
      CytoscapeWindow newWindow =
          new CytoscapeWindow(cytoscapeWindow.getParentApp(),
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

