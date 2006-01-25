package rowan;

import giny.model.*;

import cern.colt.list.*;
import cern.colt.map.*;

/**
 * This class will provide spanning trees for GraphPerspectives....
 * it should be noted that unconnected graphs may behave oddly, and that
 * one is advised to send only connected graphs, perhaps by using a algorithm
 * that I will write later on.
 */
public class SpanningTrees {

  protected GraphPerspective perspective;
  OpenIntIntHashMap nodes;
  
  public SpanningTrees ( GraphPerspective perspective ) {
    this.perspective = perspective;
  }

  public GraphPerspective getGraphPerspective () {
    return perspective;
  }

  public void setGraphPerspective ( GraphPerspective perspective ) {
    this.perspective = perspective;
  }


  /**
   * Implements a Minimum Cost Spanning Tree:
   * Ref: Manber, UBI.  "Introduction to Algorithms: A Creative Approach"
   *    Ch. 7.1, page 211.
   * --Borrowed from Iliana Avila-Campillo
   * @return the array of edges that make up this MCST
   */
  public int[] mcst () {

    // Keep track of all edges that have been added to the new set.
    OpenIntIntHashMap edges = new OpenIntIntHashMap( perspective.getEdgeCount() );
    // Keep track of all nodes that are part of the set
    nodes = new OpenIntIntHashMap( perspective.getNodeCount() );
       
    // the list of edge indices
    int[] edge_indices = perspective.getEdgeIndicesArray();
   
    int target;
    int source;
    boolean include;
    boolean inititalized = false;
    // decrement this counter everytime we add a node
    int nodes_left = perspective.getNodeCount();

    // loop through while there are nodes nt included.
    while ( nodes_left > 0 ) {
      // System.out.println( "Nodes Left: "+nodes_left );
      for ( int i = 0; i < edge_indices.length; ++i ) {
        
         target = perspective.getEdgeTargetIndex( edge_indices[i] );
         source = perspective.getEdgeSourceIndex( edge_indices[i] );
           
        // if the node set is empty, then start here.
        // later we could start the spanning tree from a 
        // selected edge or something.
        if ( !inititalized ) {
          nodes.put( source, 1 );
          nodes.put( target, 1 );
          nodes_left--;
          nodes_left--;
          inititalized = true;
          edges.put( edge_indices[i], 1 );
        } else if ( edges.get( edge_indices[i] ) == 0 ) {
          // already initialized and edge not part of the set.
          if ( nodes.get( source ) == 1 && nodes.get( target ) == 1 ) {
            // both are part of the set
          } else if ( nodes.get( source ) == 1 && nodes.get( target ) == 0 ) {
            // the source is in the set, but the target is not, add the edge, and the target
            nodes.put( target, 1 );
            nodes_left--;
            edges.put( edge_indices[i], 1 );
          } else if ( nodes.get( source ) == 0 && nodes.get( target ) == 1 ) {
            nodes.put( source, 1 );
            nodes_left--;
            edges.put( edge_indices[i], 1 );
          }
        }
      }
    }

    IntArrayList forest = new IntArrayList( perspective.getEdgeCount() );
    for ( int i = 0; i < edge_indices.length; ++i ) {
      if ( edges.get( edge_indices[i] ) == 1 ) {
        forest.add( edge_indices[i] );
      }
    }
    forest.trimToSize();
    return forest.elements();
  }

  /**
   * Operates on this GraphPerspective
   */
  public void makeTree ( GraphPerspective perspective ) {
    int root = perspective.getRootGraphNodeIndex( 1 );
    nodes = new OpenIntIntHashMap( perspective.getNodeCount() );
    nodes.put( root, 1 );
    assignTree( root, perspective );
    
  }

  private void assignTree ( int parent, GraphPerspective perspective ) {
     RootGraph root_graph = perspective.getRootGraph();
     int[] neighbors = perspective.neighborsArray( parent );
     for ( int i = 0; i < neighbors.length; ++i ) {
       int child = perspective.getRootGraphNodeIndex( neighbors[i] );
       if ( nodes.get( child ) == 0 ) {
         // not yet visited
         // Create a MetaEdge from the Parent to this Child
         root_graph.addNodeMetaChild( parent, child );
         nodes.put( child, 1 );
         assignTree( child, perspective );
       }
     }
  }

  public int[] kruskal () {
    return null;
  }

  /**
   * @return An array of int[] which are the indicies of edges for the 
   * various connected graphs.
   */
  public int[] old_kruskal ( int starting_node ) {

    IntArrayList edges = new IntArrayList( perspective.getEdgeCount() );
    OpenIntIntHashMap nodes = new OpenIntIntHashMap( perspective.getNodeCount() );
    int[] node_indices = perspective.getNodeIndicesArray();
    int[] new_indices = new int[ node_indices.length - 1 ];
    System.arraycopy( node_indices, 1, new_indices, 0, new_indices.length );
    GraphPerspective spanning = perspective.getRootGraph().createGraphPerspective( new_indices, new int[] {} );
        
    System.out.println( "Spanning: "+spanning.getNodeCount()+" "+spanning.getEdgeCount() );
     int[] edge_indices = perspective.getEdgeIndicesArray();
    int target;
    int source;
    boolean include;
    for ( int i = 0; i < edge_indices.length; ++i ) {
      target = perspective.getEdgeTargetIndex( edge_indices[i] );
      source = perspective.getEdgeSourceIndex( edge_indices[i] );
      include = false;
      // System.out.println( "Testing: "+source+ " and "+target );

   


      if ( spanning.getDegree( source ) == 0 || spanning.getDegree( target ) == 0 ) {
       
        //System.out.println( "the nodes: "+source+" and "+target+" were not neighbors." );
        //      if ( nodes.get( target ) == 0 || nodes.get( source ) == 0 ) {
        //nodes.put( target, 1 );
        //nodes.put( source, 1 );
        edges.add( edge_indices[i] );
        //System.out.println( "Edge: "+edge_indices[i]+" perspective.getRootGraphEdgeIndex( edge_indices[i] )" );
        if ( edge_indices[i] > 0 ) {
          spanning.restoreEdge( perspective.getRootGraphEdgeIndex( edge_indices[i] ) );
        } else if ( edge_indices[i] == 0 ) {
          System.out.println( "Life Sucks." );
        } else {
          spanning.restoreEdge( edge_indices[i] );
        }
      }
    }
  
    // well that technically does 'er
    edges.trimToSize();
    return edges.elements();

  }
}
