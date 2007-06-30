package giny.util;

import giny.view.*;
import giny.model.*;

import java.util.*;
import java.awt.geom.Point2D;

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class Sugiyama {


  GraphView graphView;
  List nodeLevels;
  boolean vertical;

  public Sugiyama ( GraphView view ) {
    this.graphView = view;
    this.vertical = false;
  }

  /**
   * @param levels This is a List of Lists of Nodes, each list shows 
   *               the nodes that  are on the same level.  It is 
   *               assumed that the levels will be in order.
   */
  public void setNodeLevels ( List levels ) {
    this.nodeLevels = levels;
  }

  public void layout ( List levels, boolean vertical ) {
    ////////////////////// debug code
    if (levels != null)
    {
      //Let's see what's going on
      Iterator outer = levels.iterator ();
      int i = 0;
      int j = 0;
      while (outer.hasNext ())
      {
        List list = (List) outer.next ();
        Iterator inner = list.iterator ();
        j = 0;
        while (inner.hasNext ())
        {
          Node node = (Node) inner.next ();
          System.err.println ("In list #" + i + ", inner Node #" + j + " is named: " + node.getIdentifier ());
          j++;
        }
        i++;
      }
      ///////////////////// end debug code
      this.nodeLevels = levels;
      this.vertical = vertical;
      layout();
    }
    else
    {
      System.err.println ("In Sugiyama, list of list of nodes == null");
    }
  }


  public void layout () {

    Iterator level_iterator = nodeLevels.iterator();
    
    double major = 0; // Major refers to the axis that corresponds to 
                      // i.e. the Y-axis if a horizonatal layout is chosen.

    int numLevels = nodeLevels.size ();
    List thisTier, nextTier;
    for (int i = 0; i < numLevels; i++)
    {
      if (i == numLevels - 1)
      {
        thisTier = (List) nodeLevels.get (i);
        nextTier = null;
      }
      else
      {
        thisTier = (List)nodeLevels.get (i);
        nextTier = (List)nodeLevels.get (i + 1);
      }
      System.err.println ("tier = " + i);
      layoutLevel (thisTier, nextTier, major);
      major += 200;
    }

    // now go through and put the bends in the right place.
    level_iterator = nodeLevels.iterator();
    while ( level_iterator.hasNext() ) {
      layoutBends( ( List )level_iterator.next() );
    }
  }
  
  public void layoutBends ( List nodes ) {
    Iterator member_iterator = nodes.iterator();
    Iterator neighbors;
    EdgeView edge_view;
    NodeView from_view, to_view;
    Node from, to;
    double from_width, from_height, to_width, to_height;

    while ( member_iterator.hasNext() ) {
      from = ( Node )member_iterator.next();
      //System.err.println ("from = " + from.getIdentifier ());
      neighbors = graphView.getGraphPerspective().neighborsList( from ).iterator();
      while ( neighbors.hasNext() ) {
        to = ( Node )neighbors.next();
        //System.err.println ("\tto = " + to.getIdentifier ());
        if ( nodes.contains( to ) ) {
          // these nodes are on the same level
          // do nothing
          continue;
        }

        from_view = graphView.getNodeView( from.getRootGraphIndex() );
        to_view = graphView.getNodeView( to.getRootGraphIndex() );
        double from_x = from_view.getXPosition();
        double from_y = from_view.getYPosition();
        double to_x = to_view.getXPosition();
        double to_y = to_view.getYPosition();
        from_width = from_view.getWidth ();
        from_height = from_view.getHeight ();
        to_width = to_view.getWidth ();
        to_height = to_view.getHeight ();

        // Matt Wyatt changed to addition from subtraction
        double mid_x = Math.abs( from_x + to_x ) / 2;
        double mid_y = Math.abs( from_y + to_y ) / 2;

        int[] edges_array = graphView.getGraphPerspective().getEdgeIndicesArray( from.getRootGraphIndex(), to.getRootGraphIndex(), false );
       
        if ( edges_array != null )
          for ( int i = 0; i < edges_array.length; ++i ) {
            edge_view = graphView.getEdgeView( edges_array[i] );
            Bend bend = edge_view.getBend();
            Point2D from_point, to_point;
            edge_view.setLineType( EdgeView.STRAIGHT_LINES );

            //System.out.println( "From: "+from.getRootGraphIndex()+" X: "+from_x+" To: "+to.getRootGraphIndex()+" X: "+to_x+" Mid: "+mid_x+" Y: "+from_y+" To: "+to.getRootGraphIndex()+" Y: "+to_y+" Mid: "+mid_y );
            if ( !vertical ) {
              if (  to_y > from_y ) {
                from_point = new Point2D.Double( mid_x + from_x + from_width/2, mid_y + from_height/2);
                to_point = new Point2D.Double( mid_x + to_width/2, mid_y + to_height/2);
              } else {
                from_point = new Point2D.Double( mid_x + from_x + from_width/2, mid_y + from_height/2);
                to_point = new Point2D.Double( mid_x + to_width/2, mid_y + to_height/2);
              }
            } else {
              if (  to_x > from_x ) {
                from_point = new Point2D.Double( mid_x + from_x + from_width/2, mid_y + from_height/2);
                to_point = new Point2D.Double( mid_x + to_width/2, mid_y + to_height/2);
              } else {
                from_point = new Point2D.Double( mid_x + from_x + from_width/2, mid_y + from_height/2);
                to_point = new Point2D.Double( mid_x + to_width/2, mid_y + to_height/2);
              }
            }

            if ( bend != null ) {
              bend.addHandle (0, to_point);
            }
          }
      }
    }
  }

  public void layoutLevel (List nodes, List children, double major)
  {
    Iterator member_iterator = nodes.iterator ();
    NodeView node_view;
    double minor = 0;
    double loop = 1.0;
    List edgeList = null;
    double prevWidth = 0;
    int compWidth = graphView.getComponent ().getWidth ();
    System.err.println ("compWidth = " + compWidth);
    // TODO: we need to lay this out so that nodes that have a large number of children get more space
    // in their tier
    if (children != null)
    {
      //TODO: fix this to check for children, not just neighbors
      System.err.println ("children != null");
      int childCount;
      // first, find out if there are nodes in this tier that don't have children
      boolean allHaveChildren = true;
      int numChildlessNodes = 0;
      while (member_iterator.hasNext ())
      {
        Node thisNode = (Node) member_iterator.next ();
        System.err.println ("currently checking Node " + thisNode.getIdentifier () + " for children");
        Iterator childIterator = children.iterator ();
        boolean hasChild = false;
        while (childIterator.hasNext ())
        {
          Node child = (Node) childIterator.next ();
          System.err.println ("\tchecking node " + child.getIdentifier ());
          if (graphView.getGraphPerspective().getRootGraph().isNeighbor(thisNode, child))
          {
            System.err.println ("\t" + thisNode.getIdentifier () + " is " + child.getIdentifier () + "'s parent");
            hasChild = true;
          }
        }
        if (!hasChild)
        {
          allHaveChildren = false;
          numChildlessNodes++;
        }
      }
      if (allHaveChildren)
      {
        System.err.println ("allHaveChildren == true");
        int numChildren = children.size ();
        member_iterator = nodes.iterator ();
        while (member_iterator.hasNext ())
        {
          Iterator childrenIterator = children.iterator ();
          Node parent = (Node) member_iterator.next ();
          childCount = 0;
          while (childrenIterator.hasNext ())
          {
            Node child = (Node) childrenIterator.next ();
            edgeList = graphView.getEdgeViewsList (parent, child);
            if (edgeList != null)
            {
              childCount++;
            }
          }

          // now place the node, based on the number of children/total number of nodes in nextTier
          System.err.println ("Node " + parent.getIdentifier () + " has " + childCount + " children");
          double widthFactor = (double)childCount/(double)numChildren;
          double nodeSpaceWidth = widthFactor * (double)compWidth;
          double midpoint = (nodeSpaceWidth)/(double)2;
          minor = prevWidth + midpoint;
          System.err.println ("widthFactor = " + widthFactor + "\tnodeSpaceWidth = " + nodeSpaceWidth);
          System.err.println ("midpoint = " + midpoint + "\tminor = " + minor);
          node_view = graphView. getNodeView(parent.getRootGraphIndex ());
          if (!vertical)
          {
            node_view.setOffset (major, minor);
          }
          else
          {
            double nodeWidth = node_view.getWidth();
            node_view.setOffset (minor - (nodeWidth/(double)2), major);
          }
          prevWidth += nodeSpaceWidth;
          System.err.println ("new prevWidth = " + prevWidth);
          System.err.println ("--------------------------------");
        }
      }
      else // allHaveChildren == false
      {
        System.err.println ("allHaveChildren == false");
        Iterator iterator = nodes.iterator ();
        while (iterator.hasNext ())
        {
          Node parent = (Node)iterator.next ();
          // with no children, consider the number of nodes in this row plus the next row.
          int numChildren = children.size ();
          double numNodes = (double)numChildlessNodes + (double)numChildren;
          double widthFactor = (double)1/numNodes;
          System.err.println ("***there are " + children.size() + " children, and " + numChildlessNodes + " childless nodes");
          System.err.println ("***numNodes = " + numNodes);
          double nodeSpaceWidth = widthFactor * (double)compWidth;
          double midpoint = nodeSpaceWidth/(double)2;
          minor = prevWidth + midpoint;
          System.err.println ("widthFactor = " + widthFactor + "\tnodeSpaceWidth = " + nodeSpaceWidth);
          System.err.println ("midpoint = " + midpoint + "\tminor = " + minor);
          node_view = graphView. getNodeView(parent.getRootGraphIndex ());
          if (!vertical)
          {
            node_view.setOffset (major, minor);
          }
          else
          {
            double nodeWidth = node_view.getWidth();
            node_view.setOffset (minor - (nodeWidth/(double)2), major);
          }
          prevWidth += nodeSpaceWidth;
          System.err.println ("new prevWidth = " + prevWidth);
          System.err.println ("--------------------------------");
        }
      }
    }
    else // children == null
    {
      System.err.println ("Children == null");
      int numNodes = nodes.size ();
      while (member_iterator.hasNext ())
      {
        Node n = (Node) member_iterator.next ();
        System.out.println ("TEST: " + n.getRootGraphIndex ());
        node_view = graphView.getNodeView (n.getRootGraphIndex ());

        minor = loop / (double) (numNodes + 1) * (double) compWidth;
        System.err.println ("numNodes = " + numNodes + "\twidth = " + compWidth + "\tminor = " + minor);
        // changed from if (vertical) by Matt Wyatt, 11/7/03
        if (!vertical)
        {
          node_view.setOffset (major, minor);
        }
        else
        {
          node_view.setOffset (minor, major);
        }
        loop += 1.0;
        //minor += 200;
      }
    }
  }

}
