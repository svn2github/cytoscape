/*
 * @(#)RadialTreeLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004, Michael J. Lawley All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package csplugins.layout.jgraphaddons;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;
import cytoscape.layout.*;

import cern.colt.map.*;
import cern.colt.list.*;

/**
 * Lays out the nodes in a graph as a radial tree (root at the centre, children in concentric ovals).
 * <p>
 * The layout algorithm is similar to that described in the paper
 * <em>"Radial Tree Graph Drawing Algorithm for Representing Large Hierarchies"</em>
 * by Greg Book and Neeta Keshary.
 * <p>
 * The algorithm is modified from that in the above paper since it
 * contains bugs and the sample code contains major inefficiencies.
 * <p>
 * Since this algorithm needs to be applied to a tree but we have
 * a directed graph, a spanning tree is first constructed then the
 * algorithm is applied to it.
 *
 * @author <a href="mailto:lawley@dstc.edu.au">michael j lawley</a>
 * @author Rowan
 * @since 3.1
 * @version 1.0 init
 */

public class RadialTreeLayoutAlgorithm extends AbstractLayout {

  /**
   * Property key for specifying maximum width of layout area.
   *
   * If WIDTH is specified, then CENTRE_X and RADIUS_X are
   * calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_WIDTH = "Width";

  /**
   * Property key for specifying maximum height of layout area.
   *
   * If HEIGHT is specified, then CENTRE_Y and RADIUS_Y are
   * calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_HEIGHT = "Height";

  /**
   * Property key for specifying the X-axis coordinate of the centre of the layout.
   *
   * If WIDTH is specified, then the supplied value is ignored and
   * CENTRE_X is calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_CENTRE_X = "CentreX";

  /**
   * Property key for specifying the Y-axis coordinate of the centre of the layout.
   *
   * If HEIGHT is specified, then the supplied value is ignored and
   * CENTRE_Y is calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_CENTRE_Y = "CentreY";

  /**
   * Property key for specifying the maximum horizontal distance between a parent and child node.
   *
   * If WIDTH is specified, then the supplied value is ignored and
   * RADIUS_X is calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_RADIUS_X = "RadiusX";

  /**
   * Property key for specifying the maximum vertical distance between a parent and child node.
   *
   * If WIDTH is specified, then the supplied value is ignored and
   * RADIUS_X is calculated from it based on the maximum depth of the
   * spanning tree.
   */
  public static final String KEY_RADIUS_Y = "RadiusY";

  //    public static final String KEY_CONTINUOUS = "Continuous";

  private static final String RADIAL_TREE_VISITED = "RadialTreeVisited";

  private static final double TWO_PI = Math.PI * 2.0;

  private double RADIUSX;
  private double RADIUSY;
  private double ROOTX;
  private double ROOTY;

  private CyNetworkView view;
  private CyNetwork network;
  OpenIntIntHashMap radialTreeVisited;
  int layoutStep;


  public RadialTreeLayoutAlgorithm ( CyNetworkView view ) {
    super( view );
    this.network = view.getNetwork();
  }

  protected void initialize_local() {}
  protected void initialize_local_node_view( NodeView v) {}
  public void advancePositions() {}

  public String getName () {
    return "RadialTreeLayoutAlgorithm";
  }

  /**
   * Applies a radial tree layout to nodes in the graph 
   */
  public Object construct () {
    

    view = networkView;
    
    radialTreeVisited = new OpenIntIntHashMap( view.getNodeViewCount() );
  

    // search all roots
    int[] roots = getRoots();

    TreeNode tree = getSpanningTree( roots);
        
    if (null == tree) {
      return null;
    }

    double depth = tree.getDepth();
        
    double WIDTH = getCurrentSize().getWidth();

    ROOTX = WIDTH / 2.0;
    RADIUSX = ROOTX / depth;
        
    double HEIGHT = getCurrentSize().getHeight();
    ROOTY = HEIGHT / 2.0;
    RADIUSY = ROOTY / depth;
        
   
    layoutTree0( tree);


    Iterator nodes = networkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }


    return tree;

  }

  private void layoutTree0( TreeNode node) {
  
    node.angle = 0;
    node.x = ROOTX;
    node.y = ROOTY;
    node.rightBisector = 0;
    node.rightTangent = 0;
    node.leftBisector = TWO_PI;
    node.leftTangent = TWO_PI;

    int node_index = node.getNode();
    placeView( node_index, ROOTX, ROOTY);
    if (node_index != 0 ) {
      System.out.println( "Placed root: "+node_index+network.getNode( node_index ).getIdentifier()+" X: "+ROOTX+" Y: "+ROOTY );
    } else {
      System.out.println( "ghost root:" +node.getChildren().size());
    }
   
    List parent = new ArrayList(1);
    parent.add(node);
    layoutTreeN( 1, parent);

  }

  private void layoutTreeN( int level, List nodes) {

    double i;
    double prevAngle = 0.0;
    TreeNode parent, node, firstParent = null, prevParent = null;
    List parentNodes = new ArrayList();

    Iterator nitr = nodes.iterator();
    while (nitr.hasNext()) {
      parent = (TreeNode) nitr.next();

      List children = parent.getChildren();
      double rightLimit = parent.rightLimit();
      double angleSpace = (parent.leftLimit() - rightLimit) / children.size();

      Iterator itr = children.iterator();
      for (i = 0.5; itr.hasNext(); i++) {
        node = (TreeNode) itr.next();
        int node_index = node.getNode();

        node.angle = rightLimit + (i * angleSpace);
        node.x = ROOTX + ((level * RADIUSX) * Math.cos(node.angle));
        node.y = ROOTY + ((level * RADIUSY) * Math.sin(node.angle));

        placeView( node_index, node.x, node.y);

        // Is it a parent node?
        if (node.hasChildren()) {
          parentNodes.add(node);

          if (null == firstParent) {
            firstParent = node;
          }

          // right bisector limit
          double prevGap = node.angle - prevAngle;
          node.rightBisector = node.angle - (prevGap / 2.0);
          if (null != prevParent) {
            prevParent.leftBisector = node.rightBisector;
          }

          double arcAngle = level / (level + 1.0);
          double arc = 2.0 * Math.asin(arcAngle);

          node.leftTangent = node.angle + arc;
          node.rightTangent = node.angle - arc;

          prevAngle = node.angle;
          prevParent = node;
        }
      }
    }

    if (null != firstParent) {
      double remaningAngle = TWO_PI - prevParent.angle;
      firstParent.rightBisector = (firstParent.angle - remaningAngle) / 2.0;
      if (firstParent.rightBisector < 0) {
        prevParent.leftBisector = firstParent.rightBisector + TWO_PI + TWO_PI;
      } else {
        prevParent.leftBisector = firstParent.rightBisector + TWO_PI;
      }
    }

    if (parentNodes.size() > 0) {
      layoutTreeN( level + 1, parentNodes);
    }
  }

  private void placeView( int node_index, double x, double y) {
    view.setNodeDoubleProperty( node_index, CyNetworkView.NODE_X_POSITION, x );
    view.setNodeDoubleProperty( node_index, CyNetworkView.NODE_Y_POSITION, y );
  }

  

  private int[] getRoots () {
    
    // get the network, get the nodes
    CyNetwork network = view.getNetwork();
    int[] nodes = network.getNodeIndicesArray();

    IntArrayList roots = new IntArrayList();

    for (int i = 0; i < nodes.length; i++) {
      
      int[] in = network.getAdjacentEdgeIndicesArray( nodes[i], false, true, false );
      int[] out = network.getAdjacentEdgeIndicesArray( nodes[i], false, false, true );

      if ( in.length == 0 && out.length != 0 )
        roots.add( nodes[i] );
    }

    roots.trimToSize();
    return roots.elements();

  }

  /**
   * Algorithm assumes a single root node so if there are multiple roots
   * (nodes with no incoming edges), then we construct the spanning tree
   * with an invisible root node that is the parent of the real roots.
   */
  private TreeNode getSpanningTree ( int[] roots ) {

    // get the network, get the nodes
    CyNetwork network = view.getNetwork();
    int[] nodes = network.getNodeIndicesArray();

    TreeNode node;

    //if ( roots.length == 0 ) {
      // pick an arbitrary node
      
    roots = view.getSelectedNodeIndices();
    if ( roots.length == 0 ) {
      roots = new int[] {nodes[ (int )(nodes.length * Math.random()) ]};
    }

    if (roots.length > 1) {
      node = new TreeNode(0);
      buildSpanningTree( node, roots);
    } else {
      node = new TreeNode( roots[0] );
      radialTreeVisited.put( roots[0], 1 );
      buildSpanningTree( node, network.neighborsArray( roots[0] ) );
    }

    return node;
  }

  /**
   * Breadth-first traversal of the graph.
   */
  private void buildSpanningTree ( TreeNode node, int[] children ) {

    for ( int i = 0; i < children.length; i++ ) {
      
      if ( radialTreeVisited.get( children[i] ) != 1 ) {
        radialTreeVisited.put( children[i], 1 );
        TreeNode childNode = new TreeNode( children[i] );
        node.addChild(childNode);
      }
    }
    
    Iterator itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      TreeNode childNode = (TreeNode) itr.next();
      buildSpanningTree( childNode, network.neighborsArray( childNode.getNode() ) );
    }
  }


  private static class TreeNode {

    private int node;
    private List children = new ArrayList();

    public double angle, x, y, rightBisector, leftBisector, rightTangent, leftTangent;

    TreeNode( int node) {
      this.node = node;
    }

    public int getDepth() {
      int depth = 1;
      Iterator itr = children.iterator();
      while (itr.hasNext()) {
        TreeNode node = (TreeNode) itr.next();
        int childDepth = node.getDepth();
        if (childDepth >= depth) {
          depth = childDepth + 1;
        }
      }
      return depth;
    }

    public int getNode() {
      return node;
    }

    public void addChild(TreeNode node) {
      children.add(node);
    }

    public List getChildren() {
      return children;
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }

    public double leftLimit() {
      return Math.min(normalize(leftBisector), (leftTangent));
    }

    public double rightLimit() {
      return Math.max(normalize(rightBisector), (rightTangent));
    }

    private double normalize(double angle) {
      /*
        while (angle > TWO_PI) {
        angle -= TWO_PI;
        }
        while (angle < -TWO_PI) {
        angle += TWO_PI;
        }
      */
      return angle;
    }
  }

}
