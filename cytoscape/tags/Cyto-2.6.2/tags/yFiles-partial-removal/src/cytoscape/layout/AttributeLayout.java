// AttributeLayout: 

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.layout;
//----------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.rmi.*;
import javax.swing.JOptionPane;

import cytoscape.*;
import cytoscape.undo.*;
import cytoscape.view.*;

import y.base.*;
import y.view.*;
import y.util.GraphHider; // yFiles 2.01
//import y.algo.GraphHider; // yFiles 1.4


import java.util.*;
import java.util.logging.*;

//----------------------------------------------------------------------------------------
public class AttributeLayout {
  protected CyWindow cyWindow;
  protected GraphObjAttributes nodeAttributes; 
  protected GraphObjAttributes edgeAttributes; 
  protected Graph2D graph;
  protected HashMap categoryNodes = new HashMap ();
  UndoableGraphHider graphHider;
  String species;
  Properties props;
  String [] annotationAttributeNames;
  public static final int DO_LAYOUT = 0;
  public static final int CREATE_EDGES = 1;
//----------------------------------------------------------------------------------------
public AttributeLayout(CyWindow cyWindow) {
  this.cyWindow = cyWindow;
  this.nodeAttributes = cyWindow.getNetwork().getNodeAttributes();
  this.edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
  this.graph = cyWindow.getNetwork().getGraph();
  graphHider = cyWindow.getGraphHider();
} // ctor
//----------------------------------------------------------------------------------------
protected String [] getAnnotationAttributes (GraphObjAttributes nodeAttributes)
{
  Vector accumulator = new Vector ();

  String [] attributeNames = nodeAttributes.getAttributeNames ();
  for (int i=0; i < attributeNames.length; i++) {
    String attributeType = nodeAttributes.getCategory (attributeNames [i]);
    if (attributeType != null && attributeType.equals ("annotation"))
      accumulator.add (attributeNames [i]);
   } // for i

  return (String []) accumulator.toArray (new String [0]);

} // getAnnotationAttributes
//----------------------------------------------------------------------------------------
public void doCallback (String attributeName, int functionToPerform)
{
  if ( !(cyWindow.getCytoscapeObj().getConfiguration().isYFiles()) ) {
      //don't yet handle Giny graphs
      String title = "Operation not supported";
      String message = "This operation is not yet supported on Giny graphs.";
      JOptionPane.showMessageDialog(cyWindow.getMainFrame(), message, title,
                                    JOptionPane.ERROR_MESSAGE);
      return;
  }
 
  if (functionToPerform == DO_LAYOUT){
    // Added by iliana on 1.21.2003 (works with yFiles 2.01)
    // Remove graph listeners: (including undoManager)
    // This is so that the removing and adding of graph objects
    // does not fire graph events that slow down the application
    // and also makes it hard for other classes that are graph listeners
    // to distinguish between user-generated graph events, and side-effect
    // graph events.
    Iterator it = graph.getGraphListeners();
    ArrayList gls = new ArrayList();
    GraphListener gl;
    while(it.hasNext()){
      gl = (GraphListener)it.next();
      gls.add(gl);
    }
    for(int i = 0; i < gls.size(); i++){
      graph.removeGraphListener((GraphListener)gls.get(i));
    }
    //-----
    performLayoutByAttribute (attributeName);
    // Add back graph listeners:
    for(int i = 0; i < gls.size(); i++){
      graph.addGraphListener((GraphListener)gls.get(i));
    }
    //-----
  }else if (functionToPerform == CREATE_EDGES){
    createEdgesBetweenAllNodesWithSharedAttribute (attributeName);
  }
 
}
//----------------------------------------------------------------------------------------
public void createEdgesBetweenAllNodesWithSharedAttribute (String attributeName)
{
  
  deleteCategoryNodes ();
  cyWindow.redrawGraph ();

  String [] categories = getCurrentAttributeValues (attributeName);
  addCategoryEdgesBetweenNodes (attributeName); // , categories);
  cyWindow.redrawGraph ();

} // createEdgesBetweenAllNodesWithSharedAttribute
//----------------------------------------------------------------------------------------
public void performLayoutByAttribute (String attributeName)
{
  deleteCategoryNodes ();
  cyWindow.redrawGraph ();

  String [] categories = getCurrentAttributeValues (attributeName);
  createCategoryNodes (categories);
  graphHider.hideEdges ();
  addCategoryEdges (attributeName, categories);
  cyWindow.applyLayout (false);
  removeCategoryEdges (categories);
  graphHider.unhideEdges ();
  cyWindow.redrawGraph ();
  //AttributeValuesSelector valuesSelector = 
  //        new AttributeValuesSelector (this, cyWindow, attributeName,  
  //                                     categoryNodes, "Select nodes by attribute value");
  //valuesSelector.pack ();
  //valuesSelector.setLocationRelativeTo (cyWindow.getMainFrame ());
  //valuesSelector.setVisible (true);
  //cyWindow.setInteractivity (true);

} // performLayoutByAttribute
//----------------------------------------------------------------------------------------
/**
 *  create a new node for each annotation named category.  give each new node an
 *  attribute (categorizer='true') which may be used to control its visual appearance.
 *  register the new nodes node-to-name mapping.  finally, save a reference to this
 *  node in a hash, by name, so that 
 *  <ul>
 *     <li> they may be deleted later (as when another level of annotation layout is desired)
 *     <li> we can create temporary edges, for layout, between regular nodes (which have annotation)
 *          and these nodes (which represent the annotation category)
 *     <li> so that these temporary edges can be deleted
 *  </ul>
 */
protected void createCategoryNodes (String [] categories)
{
  for (int i=0; i < categories.length; i++) {
    String newNodeName = categories [i];
    Node categoryNode = graph.createNode (400, 100, 100, 100, newNodeName);
    nodeAttributes.set ("layoutCategorizer", categories [i], "true");
    nodeAttributes.addNameMapping (categories [i], categoryNode);
    categoryNodes.put (categories [i], categoryNode);
    }

  nodeAttributes.setCategory ("layoutCategorizer", "categorizer");

  cyWindow.redrawGraph ();

} // createCategoryNodes
//----------------------------------------------------------------------------------------
protected void deleteCategoryNodes ()
{
  if (categoryNodes == null || categoryNodes.size () == 0)
    return;

  
  
  String [] names = (String []) categoryNodes.keySet().toArray(new String [0]);
  // Added by iliana on 1.21.2003 (works with yFiles 2.01)
  // Remove graph listeners: (including undoManager)
  // This is so that the removing and adding of graph objects
  // does not fire graph events that slow down the application
  // and also makes it hard for other classes that are graph listeners
  // to distinguish between user-generated graph events, and side-effect
  // graph events.
  ArrayList gls = new ArrayList();
  if(names.length > 0){
    Iterator it = graph.getGraphListeners();
    GraphListener gl;
    while(it.hasNext()){
      gl = (GraphListener)it.next();
      gls.add(gl);
    }
    for(int i = 0; i < gls.size(); i++){
      graph.removeGraphListener((GraphListener)gls.get(i));
    }
  }
  //------
  for (int i=0; i < names.length; i++) {
    Node node = (Node) categoryNodes.get (names [i]);
    graph.removeNode (node);
    } // for i

  categoryNodes = new HashMap ();
  
  // Add back graph listeners:
  if(names.length > 0){
    for(int i = 0; i < gls.size(); i++){
      graph.addGraphListener((GraphListener)gls.get(i));
    }
  }
  //------
} // deleteCategoryNodes
//----------------------------------------------------------------------------------------
protected void removeCategoryEdges (String [] categories)
{
  for (int i=0; i < categories.length; i++) {
    Node node = (Node) categoryNodes.get (categories [i]);
    if (node == null) continue;
    EdgeCursor ec = node.edges ();
    while (ec.ok ()) {
      graph.removeEdge (ec.edge ());
      ec.next ();
      } // while
    } // for i

} // removeCategoryEdges
//----------------------------------------------------------------------------------------
String [] getCurrentAttributeValues (String attributeName)
{
  Node nodes [] = graph.getNodeArray ();
  Vector categoriesFound = new Vector ();

  for (int i=0; i < nodes.length; i++) {
    String canonicalName = nodeAttributes.getCanonicalName (nodes [i]);
    if (canonicalName == null) continue;
    Object value = nodeAttributes.getValue (attributeName, canonicalName);
    if (value == null) continue;
    String [] allValuesThisNode = GraphObjAttributes.unpackPossiblyCompoundStringAttributeValue (value);
    for (int c=0; c < allValuesThisNode.length; c++) {
      if (!categoriesFound.contains (allValuesThisNode [c]))
        categoriesFound.add (allValuesThisNode [c]);
      } // for c
    } // for i

   return (String []) categoriesFound.toArray (new String [0]);

} // getCurrentAttributeValues
//----------------------------------------------------------------------------------------
protected void addCategoryEdges (String attributeName, String [] categories)
{
  Node nodes [] = graph.getNodeArray ();
  
  for (int i=0; i < nodes.length; i++) {
    String canonicalName = nodeAttributes.getCanonicalName (nodes [i]);
    if (canonicalName == null) continue;
    Object attributeValue = nodeAttributes.getValue (attributeName, canonicalName);
    if (attributeValue == null) continue;
    String [] parsedCategories = GraphObjAttributes.unpackPossiblyCompoundStringAttributeValue (attributeValue);
    for (int c=0; c < parsedCategories.length; c++) {
      String parsedCategory = parsedCategories [c];
      Node groupingNode = (Node) categoryNodes.get (parsedCategory);
      graph.createEdge (groupingNode, nodes [i]);
      } // for c
    } // for i
  
} // addCategoryEdges
//----------------------------------------------------------------------------------------
protected void addCategoryEdgesBetweenNodes (String attributeName)
// 1) for the given attributeName (i.e., "Kegg Metabolic Pathway, level 3") find all of
//    the unique values.
// 2) use those values as hash keys, whose hash values are a list of nodes having that
//    value for that attribute
// 3) loop through the has, drawing lines between every pair in the list
{
  
  graph.firePreEvent();

  HashMap attributeHash = nodeAttributes.getAttribute (attributeName);
  if (attributeHash == null)
    return;
  
  String [] nodeKeys = (String []) attributeHash.keySet().toArray (new String [0]);
  Vector nonredundantList = new Vector ();
  for (int i=0; i < nodeKeys.length; i++) {
    Object obj = attributeHash.get (nodeKeys [i]);
    String [] nodeValues = (String []) ((Vector) obj).toArray (new String [0]);
    for (int j=0; j < nodeValues.length; j++)
      if (!nonredundantList.contains (nodeValues [j]))
        nonredundantList.add (nodeValues [j]);
    } // for i

  String [] attributeValues = (String []) nonredundantList.toArray (new String [0]);

  Node nodes [] = graph.getNodeArray ();

  HashMap nodeGroups = new HashMap ();
  for (int i=0; i < nodes.length; i++) {
    String canonicalName = nodeAttributes.getCanonicalName (nodes [i]);
    if (canonicalName == null) continue;
    Object attributeValue = nodeAttributes.getValue (attributeName, canonicalName);
    if (attributeValue == null) continue;
    String [] parsedCategories = GraphObjAttributes.unpackPossiblyCompoundStringAttributeValue (attributeValue);
    for (int c=0; c < parsedCategories.length; c++) {
      String parsedCategory = parsedCategories [c];
      if (!nodeGroups.containsKey (parsedCategory))
        nodeGroups.put (parsedCategory, new Vector ());
      Vector list = (Vector) nodeGroups.get (parsedCategory);
      list.add (nodes [i]);
      } // for c
    } // for i

  String [] keys = (String []) nodeGroups.keySet().toArray (new String [0]);
  for (int i=0; i < keys.length; i++) {
    Node [] groupedNodes = (Node [])((Vector) nodeGroups.get (keys [i])).toArray (new Node [0]);
    for (int n=0; n < groupedNodes.length; n++) {
      for (int m=n+1; m < groupedNodes.length; m++) {
        if (m >= groupedNodes.length) continue;
        Edge newEdge = graph.createEdge (groupedNodes [n], groupedNodes [m]);
        String nodeName1 = "node_" + n;
        String nodeName2 = "node_" + m;
        String canonicalName = nodeName1 + " (" + keys [i] + ") " + nodeName2;
          // todo (pshannon, 03 nov 2002): if the attributeName is "interaction" we get
          // todo: this exception: 
          //  +++ map does contain key interaction
          // java.lang.IllegalArgumentException: class mismatch during set for attribute interaction,
          //  object: node_0 (development) node_1
          //  expected null
          //  got class java.lang.String
          // 	at cytoscape.GraphObjAttributes.initializeAttributeAsRequired
          //      (GraphObjAttributes.java:456)
          // 	at cytoscape.GraphObjAttributes.set(GraphObjAttributes.java:498)
          // 	at cytoscape.layout.AttributeLayout.addCategoryEdgesBetweenNodes
          //       (AttributeLayout.java:306)
          // 	at cytoscape.layout.AttributeLayout.createEdgesBetweenAllNodesWithSharedAttribute
          //          (AttributeLayout.java:109)
          // 	at cytoscape.layout.AttributeLayout.doCallback(AttributeLayout.java:99)
          // 	at cytoscape.data.annotation.AnnotationGui$Gui$DrawSharedEdgesAnnotationAction.
          //            actionPerformed(AnnotationGui.java:383)
          // how come?
        edgeAttributes.set ("eInteraction", canonicalName, attributeName);
        edgeAttributes.set (attributeName, canonicalName, keys [i]);
        edgeAttributes.addNameMapping (canonicalName, newEdge);
        } // for m

      } // for n
    } // for i

  graph.firePostEvent();
  
} // addCategoryEdgesBetweenNodes
//----------------------------------------------------------------------------------------
} // class AttributeLayout


