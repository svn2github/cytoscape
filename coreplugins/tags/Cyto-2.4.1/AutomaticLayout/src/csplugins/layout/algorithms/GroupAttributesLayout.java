package csplugins.layout.algorithms;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import giny.view.GraphView;
import giny.model.Node;
import giny.model.GraphPerspective;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/*
  This layout partitions the graph according to the selected node attribute's values.
  The nodes of the graph are broken into discrete partitions, where each partition has
  the same attribute value. For example, assume there are four nodes, where each node
  has the "IntAttr" attribute defined. Assume node 1 and 2 have the value "100" for
  the "IntAttr" attribute, and node 3 and 4 have the value "200." This will place nodes
  1 and 2 in the first partition and nodes 3 and 4 in the second partition.  Each
  partition is drawn in a circle.
*/

public class GroupAttributesLayout extends JMenuItem implements ActionListener
{
  /*
    Layout parameters:
      - spacingx: Horizontal spacing (on the x-axis) between two partitions in a row.
      - spacingy: Vertical spacing (on the y-axis) between the largest partitions of two rows.
      - maxwidth: Maximum width of a row
      - minrad:   Minimum radius of a partition.
      - radmult:  The scale of the radius of the partition. Increasing this value
                  will increase the size of the partition proportionally.
   */
  private static final double spacingx = 400.0;
  private static final double spacingy = 400.0;
  private static final double maxwidth = 5000.0;
  private static final double minrad   = 100.0;
  private static final double radmult  = 50.0;

  private String attributeName;
  private byte attributeType;

  private GraphView graphView;
  private GraphPerspective graphPerspective;
  private CyAttributes nodeAttributes;
  
  public GroupAttributesLayout(String attributeName)
  {
    super(attributeName);
    this.attributeName = attributeName;
    this.addActionListener(this);
  }

  /*
    Psuedo-procedure:
    1. Call makeDiscrete(). This will create a map for each value of the
       node attribute to the list of nodes with that attribute value.
       Each of these lists will become a partition in the graph.
       makeDiscrete() will also add nodes to the invalidNodes list
       that do not have a value associated with the attribute.
    2. Call sort(). This will return a list of partitions that is
       sorted based on the value of the attribute. Add the invalid
       nodes to the end of the sorted list. All the invalid nodes
       will be grouped together in the last partition of the layout.
    3. Begin plotting each partition.
       a. Call encircle(). This will plot the partition in a circle.
       b. Store the diameter of the last circle plotted.
       c. Update maxheight. This stores the height of the largest circle
          in a row.
       d. Update offsetx. If we've reached the end of the row,
          reset offsetx and maxheight; update offsety so that
	  it will store the y-axis location of the next row.
  */

  public void actionPerformed(ActionEvent e)
  {
    nodeAttributes = Cytoscape.getNodeAttributes();
    graphView = Cytoscape.getCurrentNetworkView();
    graphPerspective = graphView.getGraphPerspective();

    if (nodeAttributes == null)
    {
      JOptionPane.showMessageDialog(null,
      		  "The Group Attributes Layout Plugin could\n"
		+ "not access the node attributes.\n"
		+ "(Cytoscape.getNodeAttributes() == null)\n\n"
		+ "Make sure there is a network open and visible.\n"
		+ "If the problem persists, try restarting Cytoscape.",
	"Group Attributes Layout Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (graphView == null || graphPerspective == null)
    {
      JOptionPane.showMessageDialog(null,
      		  "The Group Attributes Layout Plugin could\n"
		+ "not access the network.\n"
		+ "(Cytoscape.getCurrentNetworkView() == null ||\n"
		+ " graphView.getGraphPerspective() == null)\n\n"
		+ "Make sure there is a network open and visible.\n"
		+ "If the problem persists, try restarting Cytoscape.",
	"Group Attributes Layout Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    attributeType = nodeAttributes.getType(attributeName);
    if (attributeType != CyAttributes.TYPE_INTEGER  &&
        attributeType != CyAttributes.TYPE_STRING   &&
        attributeType != CyAttributes.TYPE_FLOATING &&
        attributeType != CyAttributes.TYPE_BOOLEAN)
    {
      JOptionPane.showMessageDialog(null,
      		  "The Group Attributes Layout does not\n"
		+ "support the attribute \"" + attributeName + "\" because\n"
		+ "it is not of type integer, string,\n"
		+ "floating, or boolean.",
	"Group Attributes Layout Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    System.out.println("attributeType: " + attributeType);
    System.out.println("integer = " + CyAttributes.TYPE_INTEGER);
    System.out.println("string  = " + CyAttributes.TYPE_STRING);
    System.out.println("float   = " + CyAttributes.TYPE_FLOATING);
    System.out.println("boolean = " + CyAttributes.TYPE_BOOLEAN);
    
    Map<Comparable, List<Node>> partitionMap = new TreeMap<Comparable, List<Node>>();
    List<Node> invalidNodes = new ArrayList<Node>();
    makeDiscrete(partitionMap, invalidNodes);
    List<List<Node>> partitionList = sort(partitionMap);
    partitionList.add(invalidNodes);
  
    double offsetx = 0.0;
    double offsety = 0.0;
    double maxheight = 0.0;

    for (List<Node> partition : partitionList)
    {
      double radius = encircle(partition, offsetx, offsety);
      
      double diameter = 2.0 * radius;
      if (diameter > maxheight) maxheight = diameter;
      
      offsetx += diameter;
      if (offsetx > maxwidth)
      {
	offsety += maxheight + spacingy;
        offsetx = 0.0;
	maxheight = 0.0;
      }
      else
        offsetx += spacingx;
    }
    graphView.updateView();
  }

  private void makeDiscrete(Map<Comparable, List<Node>> map, List<Node> invalidNodes)
  {
    if (map == null) return;
    
    Iterator iterator = graphPerspective.nodesIterator();
    while (iterator.hasNext())
    {
      Node node = (Node) iterator.next();
      
      Comparable key = null;
      switch (attributeType)
      {
	case CyAttributes.TYPE_INTEGER:  key = nodeAttributes.getIntegerAttribute(node.getIdentifier(), attributeName); break;
        case CyAttributes.TYPE_STRING:   key = nodeAttributes.getStringAttribute(node.getIdentifier(), attributeName);  break;
        case CyAttributes.TYPE_FLOATING: key = nodeAttributes.getDoubleAttribute(node.getIdentifier(), attributeName);  break;
        case CyAttributes.TYPE_BOOLEAN:  key = nodeAttributes.getBooleanAttribute(node.getIdentifier(), attributeName); break;
      }

      if (key == null)
      {
        if (invalidNodes != null) invalidNodes.add(node);
      }
      else
      {
        if (!map.containsKey(key)) map.put(key, new ArrayList<Node>());
        map.get(key).add(node);
      }
    }
  }

  private List<List<Node>> sort(final Map<Comparable, List<Node>> map)
  {
    if (map == null) return null;
    
    List<Comparable> keys = new ArrayList<Comparable>(map.keySet());
    Collections.sort(keys);

    Comparator<Node> comparator = new Comparator<Node>()
    {
      public int compare(Node node1, Node node2)
      {
        String a = node1.getIdentifier();
	String b = node2.getIdentifier();
        return a.compareTo(b);
      }
    };
    
    List<List<Node>> sortedlist = new ArrayList<List<Node>>(map.keySet().size());
    for (Comparable key : keys)
    {
      List<Node> partition = map.get(key);
      Collections.sort(partition, comparator);
      sortedlist.add(partition);
    }
    
    return sortedlist;
  }

  private double encircle(List<Node> partition, double offsetx, double offsety)
  {
    if (partition == null) return 0.0;
    
    if (partition.size() == 1)
    {
      Node node = partition.get(0);
      graphView.getNodeView(node).setXPosition(offsetx);
      graphView.getNodeView(node).setYPosition(offsety);
      return 0.0;
    }

    double radius = radmult * Math.sqrt(partition.size());
    if (radius < minrad) radius = minrad;
    
    double phidelta = 2.0 * Math.PI / partition.size();
    double phi = 0.0;
    for (Node node : partition)
    {
      double x = offsetx + radius + radius * Math.cos(phi);
      double y = offsety + radius + radius * Math.sin(phi);
      graphView.getNodeView(node).setXPosition(x);
      graphView.getNodeView(node).setYPosition(y);
      phi += phidelta;
    }

    return radius;
  }
}
