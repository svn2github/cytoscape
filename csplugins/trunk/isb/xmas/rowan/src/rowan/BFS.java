package rowan;

import giny.model.*;
import cern.colt.list.*;
import cern.colt.map.*;
import java.util.*;

public abstract class BFS {

  /**
   * Will retrun a List of int[]. Each array is the BFS ordering for a conneceted component in the given graph perspective.
   */
  public static List returnBFSTrees ( GraphPerspective perspective ) {
    
    return null;

  }

  /**
   * For a known connected graph, you can use this to get BFS order.
   */
  public static int[] BFS ( GraphPerspective perspective,  int start, boolean directional ) {

    int[] nodes = perspective.getNodeIndicesArray();

    //Queue, First In First Out, use add() and get(0)/remove(0)
    IntArrayList q = new IntArrayList();
    IntArrayList tree = new IntArrayList();
    // OpenIntIntHashMap edges = new OpenIntIntHashMap( PrimeFinder.nextPrime( perspective.getEdgeCount() ) );
    OpenIntIntHashMap visited = new OpenIntIntHashMap( PrimeFinder.nextPrime( perspective.getNodeCount() ) );

    int current;
    if ( start >= 0 )
      current = nodes[0];
    else
      current = start;

    q.add( current );
    tree.add( current );
    while ( !q.isEmpty() ) {
      int x = q.get(0);
      q.remove(0);
      int[] adj;
      visited.put( x, 1 );
      if ( directional ) {
        adj = perspective.getAdjacentEdgeIndicesArray( x, false, false, true );
      } else {
        adj = perspective.getAdjacentEdgeIndicesArray( x, true, true, true );
      }
      for ( int i = 0; i < adj.length; ++i ) {
        int s = perspective.getEdgeSourceIndex( adj[i] );
        int t = perspective.getEdgeTargetIndex( adj[i] );
        if ( visited.get( s ) == 0 ) {
          q.add( s );
          tree.add( s );
        } 
        if ( visited.get( t ) == 0 ) {
          q.add( t );
          tree.add( t );
        }
      }
    }// q empty


    tree.trimToSize();
    return tree.elements();
  }


}