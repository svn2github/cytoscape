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
 *
 * An alternate constructor is provided that takes just the data objects
 * without a reference to a CytoscapeWindow. This is mainly for testing
 * purposes, allowing a Network to be created without a full CytoscapeWindow.
 */
public class Network {
    
    CytoscapeWindow cytoscapeWindow;
    Graph2D graph;
    GraphObjAttributes nodeAttributes;
    GraphObjAttributes edgeAttributes;
    
    public Network(CytoscapeWindow cytoscapeWindow) {
        this.cytoscapeWindow = cytoscapeWindow;
    }
    public Network(Graph2D g, GraphObjAttributes nodeA, GraphObjAttributes edgeA) {
        this.graph = g;
        this.nodeAttributes = nodeA;
        this.edgeAttributes = edgeA;
    }
        

    public Graph2D getGraph() {
        if (cytoscapeWindow != null) {
            return cytoscapeWindow.getGraph();
        } else {
            return graph;
        }
    }
    public GraphObjAttributes getNodeAttributes() {
        if (cytoscapeWindow != null) {
            return cytoscapeWindow.getNodeAttributes();
        } else {
            return nodeAttributes;
        }
    }
    public GraphObjAttributes getEdgeAttributes() {
        if (cytoscapeWindow != null) {
            return cytoscapeWindow.getEdgeAttributes();
        } else {
            return edgeAttributes;
        }
    }
}

