package csplugins.layout.algorithms.graphPartition;

import giny.model.*;
import giny.view.*;

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

import cern.colt.list.*;
import cern.colt.map.*;

import java.util.*;

/**
 * Class that represents the Layout of a given graph.
 */
public class Layout {

  OpenIntDoubleHashMap nodeXMap;
  OpenIntDoubleHashMap nodeYMap;

  CyNetwork gp;

  public Layout ( CyNetwork gp ) {
    this.gp = gp;
    nodeXMap = new OpenIntDoubleHashMap(  PrimeFinder.nextPrime( gp.getNodeCount() ) );
    nodeYMap = new OpenIntDoubleHashMap(  PrimeFinder.nextPrime( gp.getNodeCount() ) );
  }

  public Layout ( CyNetworkView view, boolean load_current_values ) {
    this( view.getNetwork() );
    
    // initialize current values
    if ( load_current_values ) {
      Iterator i = view.getNodeViewsIterator();
      while ( i.hasNext() ) {
        NodeView nv = ( NodeView )i.next();
        setX( nv, nv.getXPosition() );
        setY( nv, nv.getYPosition() );
      }
    }
  }

  /**
   * Apply the layout to a given GraphView
   */
  public void applyLayout ( CyNetworkView view ) {
    
   Iterator i = view.getNodeViewsIterator();
   while ( i.hasNext() ) {
     NodeView nv = ( NodeView )i.next();
     nv.setXPosition( getX( nv ), false );
     nv.setYPosition( getY( nv ), false );
     nv.setNodePosition( true );
   }
  }

  // set

  public boolean setX ( int node, double x ) {
    return nodeXMap.put( node, x );
  }

  public boolean setY ( int node, double y ) {
    return nodeYMap.put( node, y );
  }

  public boolean setX ( Node node, double x ) {
    return nodeXMap.put( node.getRootGraphIndex(), x );
  }

  public boolean setY ( Node node, double y ) {
    return nodeYMap.put( node.getRootGraphIndex(), y );
  }

  public boolean setX ( NodeView node, double x ) {
    return nodeXMap.put( node.getRootGraphIndex(), x );
  }

  public boolean setY ( NodeView node, double y ) {
    return nodeYMap.put( node.getRootGraphIndex(), y );
  }
  
  // get

  public double getX ( int node ) {
    return nodeXMap.get( node );
  }

  public double getY ( int node ) {
    return nodeYMap.get( node );
  }

  public double getX ( Node node ) {
    return nodeXMap.get( node.getRootGraphIndex() );
  }

  public double getY ( Node node ) {
    return nodeYMap.get( node.getRootGraphIndex() );
  }

  public double getX ( NodeView node ) {
    return nodeXMap.get( node.getRootGraphIndex() );
  }

  public double getY ( NodeView node ) {
    return nodeYMap.get( node.getRootGraphIndex() );
  }


                                     
  


}
