package giny.util;

import giny.model.*;
import java.util.Arrays;

/**
 * This is a convenience class that offers a number of useful 
 * methods for GraphPerspectives.
 */

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public abstract class GraphPerspectiveUtil {

  /**
   * This method will hide the node and optionally all connected
   * edges.  The only time I can think of when you might not want to 
   * hide all the edges is if you are going to permanantly remove this 
   * node from the root graph and replace it with another.
   * @param gp the GraphPerspective
   * @param node the index of the node to be worked with
   * @param hide_adjacent_edges boolean to hide the connected edges
   */
  public static int[] hideNode ( GraphPerspective gp, 
                                 int node, 
                                 boolean hide_adjacent_edges ) {
    
    if ( hide_adjacent_edges ) {
      // we need to hide the adjacent edges
      int[] edges = gp.hideEdges( gp.getAdjacentEdgeIndicesArray( node, true, true, true ) );
      gp.hideNode( node );
      return edges;
    } else {
      // we don't need to hide the adjacent edges
      gp.hideNode( node );
      // return an empty array
      return new int[] {};
    }

  }


  /**
   * This method is a convience method for unhiding all of the edges 
   * that connect a node to an edge in the Perspective
   * @param gp the GraphPerspective
   * @param node the index of the node to be worked with
   * @param unhide_all_edges boolean to do unhide all or not
   */
  public void unhideNode ( GraphPerspective gp, 
                           int node, 
                           boolean unhide_all_edges ) { 
    
    gp.restoreNode( node );
    if ( unhide_all_edges ) {
      RootGraph rg = gp.getRootGraph();
      int neighbors[] = gp.neighborsArray( node );
      for ( int i = 0; i < neighbors.length; ++i ) {
        int s_t[] = rg.getEdgeIndicesArray( node, neighbors[i], true );
        int t_s[] = rg.getEdgeIndicesArray( neighbors[i], node, false );
        for ( int j = 0; j < s_t.length; ++j ) {
          gp.restoreEdge( s_t[j] );
        }
        for ( int j = 0; j < t_s.length; ++j ) {
          gp.restoreEdge( t_s[j] );
        }
      }
    }
  }
   
  public void unhideNode ( GraphPerspective gp, 
                           int node, 
                           boolean unhide_all_edges, 
                           int[] excluded_edges ) {

    gp.restoreNode( node );
    Arrays.sort( excluded_edges );
    if ( unhide_all_edges ) {
      RootGraph rg = gp.getRootGraph();
      int neighbors[] = gp.neighborsArray( node );
      for ( int i = 0; i < neighbors.length; ++i ) {
        int s_t[] = rg.getEdgeIndicesArray( node, neighbors[i], true );
        int t_s[] = rg.getEdgeIndicesArray( neighbors[i], node, false );
        for ( int j = 0; j < s_t.length; ++j ) {
          if ( Arrays.binarySearch( excluded_edges, s_t[j] ) >= 0 ) 
            gp.restoreEdge( s_t[j] );
        }
        for ( int j = 0; j < t_s.length; ++j ) {
          if ( Arrays.binarySearch( excluded_edges, t_s[j] ) >= 0 ) 
            gp.restoreEdge( t_s[j] );
        }
      }
    }


  }


}
