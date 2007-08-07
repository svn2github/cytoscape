//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import y.base.Graph;
import y.view.Graph2D;
//----------------------------------------------------------------------------
/**
 * This class encapsulates the graph and attribute data structures held by
 * a CytoscapeWindow object. This object should be given to methods that
 * require access to the data structures but not to any of the other
 * features of CytoscapeWindow.
 */
public class Network {
    
    CytoscapeWindow cytoscapeWindow;
    
    public Network(CytoscapeWindow cytoscapeWindow) {
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public Graph2D getGraph() {return cytoscapeWindow.getGraph();}
    public GraphObjAttributes getNodeAttributes() {
        return cytoscapeWindow.getNodeAttributes();
    }
    public GraphObjAttributes getEdgeAttributes() {
        return cytoscapeWindow.getEdgeAttributes();
    }
}

