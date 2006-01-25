package rowan;

import giny.model.*;
import cern.colt.list.*;
import cern.colt.map.*;
import java.util.*;


public abstract class DFS {

  /**
   * For a known connected graph, you can use this to get BFS order.
   */
  public static int[] DFS ( GraphPerspective perspective,  int start, boolean directional ) {

    int[] nodes = perspective.getNodeIndicesArray();

    // stack, use add() and
    // int size = stack.size(), get( size ), remove( size );
    IntArrayList stack = new IntArrayList();
    IntArrayList tree = new IntArrayList();
    OpenIntIntHashMap visited = new OpenIntIntHashMap( PrimeFinder.nextPrime( perspective.getNodeCount() ) );

    int current;
    if ( start >= 0 )
      current = nodes[0];
    else
      current = start;
    stack.add( current );
    visited.put( current, 1 );

    while ( !stack.isEmpty() ) {
      int size = stack.size();
      int x = stack.get( size );
      stack.remove( size );

      int[] adj;
      if ( directional ) {
        adj = perspective.getAdjacentEdgeIndicesArray( x, false, false, true );
      } else {
        adj = perspective.getAdjacentEdgeIndicesArray( x, true, true, true );
      }
      for ( int i = 0; i < adj.length; ++i ) {
        int s = perspective.getEdgeSourceIndex( adj[i] );
        int t = perspective.getEdgeTargetIndex( adj[i] );
        if ( visited.get( s ) == 0 ) {
          stack.add( s );
          visited.put( s, 1 );
          tree.add( s );
        } 
        if ( visited.get( t ) == 0 ) {
          stack.add( t );
          visited.put( t, 1 );
          tree.add( t );
        } 
      }
    } // end stack not empty
  
    tree.trimToSize();
    return tree.elements();
  }

}