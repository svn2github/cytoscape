//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.util;
//-------------------------------------------------------------------------
import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.view.GraphView;
import luna.LunaRootGraph;
import phoebe.PGraphView;
//-------------------------------------------------------------------------
/**
 * This class provides factory methods for creating Giny objects, specifically
 * RootGraphs and GraphViews. These methods allow the rest of Cytoscape to
 * be independent of the specific implementation of the Giny interfaces.<P>
 *
 * Currently these methods are implemented with objects from the luna library.
 */
public class GinyFactory {
    
    /**
     * Creates a new instance of a Giny RootGraph. Currently this returns
     * a LunaRootGraph.
     */
    public static RootGraph createRootGraph ( int nodes, int edges ) {
      return new LunaRootGraph( nodes, edges );
    }

    /**
     * Creates a new instance of a Giny RootGraph. Currently this returns
     * a LunaRootGraph.
     */
    public static RootGraph createRootGraph() {
        return new LunaRootGraph();
    }
    
    /**
     * Creates a new GraphPerspective on the supplied RootGraph.
     *
     * @throws NullPointerException  if the argument is null
     */
    public static GraphPerspective createGraphPerspective(RootGraph rootGraph) {
        return rootGraph.createGraphPerspective( rootGraph.getNodeIndicesArray(),
                                                 rootGraph.getEdgeIndicesArray() );
    }
    
    /**
     * Creates a GraphView instance on the supplied graph.
     */
    public static GraphView createGraphView(GraphPerspective perspective) {
        return new PGraphView(perspective);
    }
}

