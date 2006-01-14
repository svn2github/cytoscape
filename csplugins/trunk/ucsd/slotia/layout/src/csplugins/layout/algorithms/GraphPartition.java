package csplugins.layout.algorithms;

import giny.model.*;
import cern.colt.list.*;
import cern.colt.map.*;
import java.util.*;

/**
 * This class will find all of the unconnected subgraphs of a GraphPerspective
 */
public abstract class GraphPartition {

  /**
   * This can be done very much like a spanning tree.
   * @return a list of int[] which are the nodes in the graph partitions.
   */
  public static List partition ( GraphPerspective perspective ) {

   
    ArrayList partitions = new ArrayList();


    // Keep track of all edges that have been added to the new set.
    OpenIntIntHashMap edges = new OpenIntIntHashMap( perspective.getEdgeCount() );
    // Keep track of all nodes that are part of the set
    OpenIntIntHashMap nodes = new OpenIntIntHashMap( perspective.getNodeCount() );
       

    int[] nodes_arrays = perspective.getNodeIndicesArray();
    IntArrayList un_connected = new IntArrayList();
    IntArrayList connected = new IntArrayList();
    for ( int i = 0; i < nodes_arrays.length; ++i ) {
      if ( perspective.getDegree( nodes_arrays[i] ) == 0 ) {
        // no edges on this node
        un_connected.add( nodes_arrays[i] );
        //System.out.println( perspective.getNode( nodes_arrays[i] ).getIdentifier()+" is un_connected" );
      } else {
        connected.add( nodes_arrays[i] );
        //System.out.println( perspective.getNode( nodes_arrays[i] ).getIdentifier()+" is CONnected" );
      }
    }
    
    connected.trimToSize();
    un_connected.trimToSize();

    
    // the list of edge indices
    int[] edge_indices = perspective.getEdgeIndicesArray();
   
    int target;
    int source;
    boolean include;
    boolean inititalized = false;
    // decrement this counter everytime we add a node
    int nodes_left = connected.size();
    int last_nodes_left = nodes_left;
    // loop through while there are nodes nt included.
    boolean found_new = true;
    while ( nodes_left > 0 ) {
      //System.out.println( "Nodes Left: "+nodes_left );

      //if ( nodes_left == last_nodes_left && inititalized && found_new) {
      if (  nodes_left == last_nodes_left && inititalized && !found_new) {
        //System.out.println( "Partition Created" );
        // now we need to find a non-set node.
         IntArrayList forest = new IntArrayList( perspective.getEdgeCount() );
         for ( int i = 0; i < edge_indices.length; ++i ) {
           if ( edges.get( edge_indices[i] ) == 1 ) {
             edges.put( edge_indices[i], 2 );
             forest.add( edge_indices[i] );
           }
         }
         forest.trimToSize();
         partitions.add( forest.elements() );
         System.out.println( "un inititalized, added: "+forest.elements().length );
         inititalized = false;
         //for ( int j = 0; j < connected.size(); ++j ) {
//            if ( nodes.get( j ) == 0 ) {
//              nodes.put( j, 1 );
//              nodes_left--;
//              System.out.println( "break nodes_left--: "+j );
//              try {
//                System.out.println( "nodes_left--: "+perspective.getNode(j).getIdentifier() );
//              } catch ( Exception e ) {}
//              break;
//             }
//          }
      }

     
      last_nodes_left = nodes_left;

      
      // find a mcst
      found_new = false;
      for ( int i = 0; i < edge_indices.length; ++i ) {

        target = perspective.getEdgeTargetIndex( edge_indices[i] );
        source = perspective.getEdgeSourceIndex( edge_indices[i] );
          
        // if the node set is empty, then start here.
        // later we could start the spanning tree from a 
        // selected edge or something.
        if ( !inititalized  && edges.get( edge_indices[i] ) == 0 ) {
          nodes.put( source, 1 );
          nodes.put( target, 1 );
          nodes_left--;
          nodes_left--;
          System.out.println( "inititalized" );
          inititalized = true;
          found_new = true;
          edges.put( edge_indices[i], 1 );
        } else if ( edges.get( edge_indices[i] ) == 0 ) {
          // already initialized and edge not part of the set.
          if ( nodes.get( source ) == 1 && nodes.get( target ) == 1 ) {
            // both are part of the set
          } else if ( nodes.get( source ) == 1 && nodes.get( target ) == 0 ) {
            // the source is in the set, but the target is not, add the edge, and the target
            nodes.put( target, 1 );
            nodes_left--;
            found_new = true;
            edges.put( edge_indices[i], 1 );
          } else if ( nodes.get( source ) == 0 && nodes.get( target ) == 1 ) {
            nodes.put( source, 1 );
            nodes_left--;
            found_new = true;
            edges.put( edge_indices[i], 1 );
          } 
        }
      }

      
    }

    // run one more time to catch the last mcst found
    IntArrayList forest = new IntArrayList( perspective.getEdgeCount() );
    for ( int i = 0; i < edge_indices.length; ++i ) {
      if ( edges.get( edge_indices[i] ) == 1 ) {
        edges.put( edge_indices[i], 2 );
        forest.add( edge_indices[i] );
      }
    }
    forest.trimToSize();
    partitions.add( forest.elements() );
    for ( int j = 0; j < perspective.getNodeCount(); ++j ) {
      if ( nodes.get( j ) == 0 ) {
        nodes.put( j, 1 );
        nodes_left--;
        break;
      }
    }
    
    for ( int i = 0; i < partitions.size(); ++i )
      partitions.set( i, perspective.getConnectingNodeIndicesArray( ( int[] )partitions.get( i ) ) );
    
    for ( int i = 0; i < un_connected.size(); ++i ) 
      partitions.add( new int[] { un_connected.get( i ) } );
    



    Object[] parts = partitions.toArray();
    Arrays.sort( parts, new Comparator () {
        public int compare(Object o1, Object o2) {
          int[] one = ( int[] )o1;
          int[] two = ( int[] )o2;
          if ( one.length == two.length )
            return 0;
          else if ( one.length < two.length )
            return 1;
          else if ( one.length > two.length )
            return -1;
          return 0;
        }
        public boolean	equals(Object obj) {
          return false;
        }
      } );
    return Arrays.asList( parts );

  }

}
