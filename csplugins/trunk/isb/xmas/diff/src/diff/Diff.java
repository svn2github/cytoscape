package diff;

import giny.model.*;
import filter.model.Filter;
import java.util.*;

public abstract class Diff {

  /**
   * @return  a List of CyNodes
   */
  public static List nodesDiff ( List node_list_1, List node_list_2 ) {
    HashSet set1 = new HashSet( node_list_1 );
    HashSet set2 = new HashSet( node_list_2 );
    HashSet both = new HashSet( node_list_1 );
    both.addAll( node_list_2 );

    List diff = new LinkedList();

    Iterator i = both.iterator();
    while( i.hasNext() ) {
      Node node = ( Node )i.next();
      if ( set1.contains( node ) && set2.contains( node ) ) {
        // thy both conain the node
        //System.out.println( "Both Contain: "+node );
      } else {
        //System.out.println( "Added to diff: "+node );
        diff.add( node );
      }
    }
    return diff;
  }

  /**
   * @return  a List of CyEdges
   */
  public static List edgesDiff ( List edge_list_1, List edge_list_2 ) {
    HashSet set1 = new HashSet( edge_list_1 );
    HashSet set2 = new HashSet( edge_list_2 );
    HashSet both = new HashSet( edge_list_1 );
    both.addAll( edge_list_2 );

    List diff = new LinkedList();

    Iterator i = both.iterator();
    while( i.hasNext() ) {
      Edge edge = ( Edge )i.next();
      if ( set1.contains( edge ) && set2.contains( edge ) ) {
        // thy both conain the edge
        //System.out.println( "Both Contain: "+edge );
      } else {
        //System.out.println( "Added to diff: "+edge );
        diff.add( edge );
      }
    }
    return diff;
  }

  public static List nodesDiff ( GraphPerspective network1, GraphPerspective network2 ) {
    return nodesDiff( network1.nodesList(), network2.nodesList() );
  }

  public static List edgesDiff ( GraphPerspective network1, GraphPerspective network2 ) {
    return edgesDiff( network1.edgesList(), network2.edgesList() );
  }
  
  public static List nodesDiff ( Filter filter1, 
                                 Filter filter2,  
                                 List nodes ) {
    List list1 = new LinkedList();
    List list2 = new LinkedList();
    Node node;
    Iterator nodes_i = nodes.iterator();
    while ( nodes_i.hasNext() ) {
      Object o = nodes_i.next(); 
      if ( o instanceof Node ) 
        node = ( Node )o;
      else
        continue;
      try {
        if ( filter1.passesFilter(node) ) {
          list1.add( node );
        }
      } catch(StackOverflowError soe){
        //
      }
    }

    nodes_i = nodes.iterator();
    while ( nodes_i.hasNext() ) {
      Object o = nodes_i.next();
      if ( o instanceof Node ) 
        node = ( Node )o;
      else
        continue;
      try {
        if ( filter2.passesFilter(node) ) {
          list2.add( node );
        }
      } catch(StackOverflowError soe){
        //
      }
    }
    return( nodesDiff( list1, list2 ) );
  }  

  public static List edgesDiff ( Filter filter1, 
                                 Filter filter2,
                                 List edges ) {
    List list1 = new LinkedList();
    List list2 = new LinkedList();
    Edge edge;
    Iterator edges_i = edges.iterator();
    while ( edges_i.hasNext() ) {
      Object o = edges_i.next(); 
      if ( o instanceof Edge ) 
        edge = ( Edge )o;
      else
        continue;
      try {
        if ( filter1.passesFilter(edge) ) {
          list1.add( edge );
        }
      } catch(StackOverflowError soe){
        //
      }
    }

    edges_i = edges.iterator();
    while ( edges_i.hasNext() ) {
      Object o = edges_i.next();
      if ( o instanceof Edge ) 
        edge = ( Edge )o;
      else
        continue;
      try {
        if ( filter2.passesFilter(edge) ) {
          list2.add( edge );
        }
      } catch(StackOverflowError soe){
        //
      }
    }
    return( edgesDiff( list1, list2 ) );
  }  


}
