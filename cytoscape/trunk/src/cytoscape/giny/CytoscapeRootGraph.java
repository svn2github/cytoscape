package cytoscape.giny;

import giny.model.*;
import cytoscape.*;
import coltginy.*;
import cern.colt.map.*;

public class CytoscapeRootGraph extends ColtRootGraph {

  /**
   * The Nodes that are created, though they need not be are stored so has to allow 
   * modification of node values and to ensure node persistance, this may be later changed.
   */
  protected OpenIntObjectHashMap nodeIndexObjectMap;

  /**
   * The Edges that are created, though they need not be are stored so has to allow 
   * modification of edge values and to ensure edge persistance, this may be later changed.
   */
  protected OpenIntObjectHashMap edgeIndexObjectMap;


  //-------------------------------------------------------------------------//
  // Construction & initialization methods
  //-------------------------------------------------------------------------//

  /**
   * Default constructor delegates to the int, int constructor with the default
   * values ColtginyConstants.DEFAULT_NODE_CAPACITY and
   * ColtginyConstants.DEFAULT_EDGE_CAPACITY.
   */
  public CytoscapeRootGraph () {
    this(
         ColtginyConstants.DEFAULT_NODE_CAPACITY,
         ColtginyConstants.DEFAULT_EDGE_CAPACITY
         );
  } // <init>()

  /**
   * int, int constructor calls {@link #initializeColtRootGraph( int, int )}.
   */
  public CytoscapeRootGraph ( int node_capacity, int edge_capacity ) {
    // This is the only Luna Specific Code
    nodeIndexObjectMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( node_capacity ) );
    edgeIndexObjectMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( edge_capacity ) );
    // Back to ColtRootGraph
    initializeColtRootGraph( node_capacity, edge_capacity );
  } // <init>()


  /**
   * This implementation of the giny model will defer creation of Node objects
   * as long as possible.  This method creates the object corresponding to the
   * given <i>valid</i> node index.  Subsequent changes to this object must be
   * reflected when this RootGraph is queried about the node or its index, and
   * subsequent changes to the Node made via this RootGraph, including changes
   * made using only its index, will be reflected when querying the returned
   * Node.  To accomplish this it is recommended that the returned Node be a
   * SimpleColtNode or a subclass thereof, but this is not required.
   * @param node_index a valid Node index in this RootGraph.
   * @return a new Node that dynamically reflects all node data associated with
   * the given index.
   * @see SimpleColtNode
   */
  protected giny.model.Node createNode ( int node_index ) {

    if ( nodeIndexObjectMap.containsKey( node_index ) ) {
      return ( Node )nodeIndexObjectMap.get( node_index );
    } else {
      CyNode node = new CyNode( node_index, this );
      nodeIndexObjectMap.put( node_index, node );
      return node;
    }
  }

  /**
   * This implementation of the giny model will defer creation of Edge objects
   * as long as possible.  This method creates the object corresponding to the
   * given <i>valid</i> edge index.  Subsequent changes to this object must be
   * reflected when this RootGraph is queried about the edge or its index, and
   * subsequent changes to the Edge made via this RootGraph, including changes
   * made using only its index, will be reflected when querying the returned
   * Edge.  To accomplish this it is recommended that the returned Edge be a
   * SimpleColtEdge or a subclass thereof, but this is not required.
   * @param edge_index a valid Edge index in this RootGraph.
   * @return a new Edge that dynamically reflects all edge data associated with
   * the given index.
   * @see SimpleColtEdge
   */
  protected giny.model.Edge createEdge ( int edge_index ) {

    if ( edgeIndexObjectMap.containsKey( edge_index ) ) {
      return ( giny.model.Edge )edgeIndexObjectMap.get( edge_index );
    } else {
      CyEdge edge = new CyEdge( edge_index, this );
      edgeIndexObjectMap.put( edge_index, edge );
      return edge;
    }
  }


  public int createNode ( GraphPerspective perspective ) {

    int[] node_indices_array = null;
    int[] edge_indices_array = null;
    if( perspective != null ) {
      if( perspective.getRootGraph() != this ) {
        throw new IllegalArgumentException( "The given GraphPerspective is a perspective on a different RootGraph: "+perspective.getRootGraph()+"." );
      }
      node_indices_array = perspective.getNodeIndicesArray();
      edge_indices_array = perspective.getEdgeIndicesArray();
    }
    int new_node = createNode( node_indices_array, edge_indices_array );
    giny.model.Node cyto_node = createNode( new_node );
    ( ( CyNode )cyto_node ).setGraphPerspective( perspective );
    return new_node;
  } // createNode( GraphPerspective )


  
  public int createNode ( CyNetwork network ) {
    return createNode( ( GraphPerspective )network );
  }

  /**
   * Uses Code copied from ColtRootGraph to create a new CyNetwork.
   */
  public CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) {
    if( !coltRootGraphInitialized ) {
      throw new IllegalStateException( NOT_INITIALIZED_EXCEPTION_STRING );
    }
    int[] node_indices = new int[ nodes.length ];
    int node_index;
    for( int node_i = 0; node_i < nodes.length; node_i++ ) {
      if( nodes[ node_i ] == null ) {
        throw new IllegalArgumentException( "All Nodes must not be null.  The node at index "+node_i+" into the given nodes array is null." );
      }
      if( nodes[ node_i ].getRootGraph() != this ) {
        throw new IllegalArgumentException( "All Nodes must be from this RootGraph.  The node at index "+node_i+" into the given nodes array is from a different RootGraph: "+nodes[ node_i ].getRootGraph()+"." );
      }
      node_index = nodes[ node_i ].getRootGraphIndex();
      if( node_index == 0 ) {
        throw new IllegalArgumentException( "All Nodes must be from this RootGraph.  The node at index "+node_i+" into the given nodes array is no longer in this RootGraph (its getRootGraphIndex() method has returned 0)." );
      }
      node_indices[ node_i ] = node_index;
    } // End for each node, get its index.
    int[] edge_indices = new int[ edges.length ];
    int edge_index;
    for( int edge_i = 0; edge_i < edges.length; edge_i++ ) {
      if( edges[ edge_i ] == null ) {
        throw new IllegalArgumentException( "All Edges must not be null.  The edge at index "+edge_i+" into the given edges array is null." );
      }
      if( edges[ edge_i ].getRootGraph() != this ) {
        throw new IllegalArgumentException( "All Edges must be from this RootGraph.  The edge at index "+edge_i+" into the given edges array is from a different RootGraph: "+edges[ edge_i ].getRootGraph()+"." );
      }
      edge_index = edges[ edge_i ].getRootGraphIndex();
      if( edge_index == 0 ) {
        throw new IllegalArgumentException( "All Edges must be from this RootGraph.  The edge at index "+edge_i+" into the given edges array is no longer in this RootGraph (its getRootGraphIndex() method has returned 0)." );
      }
      edge_indices[ edge_i ] = edge_index;
    } // End for each edge, get its index.
    return createNetwork( node_indices, edge_indices );

  }

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] node_indices, int[] edge_indices ) {
   
    if( !coltRootGraphInitialized ) {
      throw new IllegalStateException( NOT_INITIALIZED_EXCEPTION_STRING );
    }
    
    OpenIntIntHashMap r_node_i_to_p_node_i_map =
      new OpenIntIntHashMap( node_indices.length );
    for( int node_index_i = 0;
         node_index_i < node_indices.length;
         node_index_i++ ) {
      if( node_indices[ node_index_i ] >= 0 ) {
        throw new IllegalArgumentException( "Node indices must be negative!  At index "+node_index_i+" into the given node_indices array, we find this: "+node_indices[ node_index_i ]+"." );
      }
      r_node_i_to_p_node_i_map.put(
                                   node_indices[ node_index_i ],
                                   ( node_index_i + 1 )
                                   );
    } // End building the r_node_i_to_p_node_i_map
    OpenIntIntHashMap r_edge_i_to_p_edge_i_map =
      new OpenIntIntHashMap( edge_indices.length );
    for( int edge_index_i = 0;
         edge_index_i < edge_indices.length;
         edge_index_i++ ) {
      if( edge_indices[ edge_index_i ] >= 0 ) {
        throw new IllegalArgumentException( "Edge indices must be negative!  At index "+edge_index_i+" into the given edge_indices array, we find this: "+edge_indices[ edge_index_i ]+"." );
      }
      r_edge_i_to_p_edge_i_map.put(
                                   edge_indices[ edge_index_i ],
                                   ( edge_index_i + 1 )
                                   );
    } // End building the r_edge_i_to_p_edge_i_map

    return ( CyNetwork )new ColtCyNetwork(
                             this,
                             r_node_i_to_p_node_i_map,
                             r_edge_i_to_p_edge_i_map
                             );
  }

 

}

