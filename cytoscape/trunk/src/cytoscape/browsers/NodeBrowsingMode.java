// NodeBrowser.java
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.browsers;
//----------------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;

import y.base.*;
import y.view.*;

import cytoscape.*;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------------------
/** 
  * 
 */
public class NodeBrowsingMode extends PopupMode {
  protected CytoscapeWindow cytoscapeWindow;
  protected Graph2D graph;
  Vector attributeCategoriesToIgnore;
  final static String invisibilityPropertyName = "nodeAttributeCategories.invisibleToBrowser";
  String webBrowserScript;
//----------------------------------------------------------------------------------------
public void set (CytoscapeWindow cytoscapeWindow)
{
  this.cytoscapeWindow = cytoscapeWindow;
  Properties props = cytoscapeWindow.getConfiguration().getProperties();
  webBrowserScript = 
      cytoscapeWindow.getConfiguration().getProperties().getProperty ("webBrowserScript", "noScriptDefined");
  cytoscapeWindow.getNodeAttributes().setCategory ("tissueCount", "hideFromBrowser");
  attributeCategoriesToIgnore = Misc.getPropertyValues (props, invisibilityPropertyName);
  for (int i=0; i < attributeCategoriesToIgnore.size(); i++)
    System.out.println ("  ignore type " + attributeCategoriesToIgnore.get (i));
  
} // ctor
//----------------------------------------------------------------------------------------
public JPopupMenu getNodePopup (Node v) 
{
    graph = cytoscapeWindow.getGraph();
    boolean selectedState = graph.isSelected(v);
    graph.setSelected(v, true);
    getSelectionPopup(graph.getCenterX(v), graph.getCenterY(v));
    graph.setSelected(v, selectedState);
    return null;
}
//----------------------------------------------------------------------------------------
public JPopupMenu getPaperPopup (double x, double y) 
{
  return null;
}
//----------------------------------------------------------------------------------------
public JPopupMenu getSelectionPopup (double x, double y) 
{
  graph = cytoscapeWindow.getGraph();
  NodeCursor nc = graph.selectedNodes (); 
  Vector nodeList = new Vector ();
  while (nc.ok ()) {
    nodeList.add (nc.node ());
    nc.next ();
    }

  Node [] selectedNodes = (Node []) nodeList.toArray (new Node [0]);

  EdgeCursor ec = graph.selectedEdges (); 
  Vector edgeList = new Vector ();
  while (ec.ok ()) {
    edgeList.add (ec.edge ());
    ec.next ();
    }

  Edge [] selectedEdges = (Edge []) edgeList.toArray (new Edge [0]);

  TabbedBrowser nodeBrowser = null;
  TabbedBrowser edgeBrowser = null;

  if (selectedNodes.length == 0 && selectedEdges.length == 0) {
    JOptionPane.showMessageDialog (null, "No selected nodes or edges", "Error",
                                   JOptionPane.ERROR_MESSAGE);
    return null;
    }
    
  if (selectedNodes.length > 0)
    nodeBrowser = new TabbedBrowser (selectedNodes, cytoscapeWindow.getNodeAttributes (),
                                     attributeCategoriesToIgnore, webBrowserScript);

  if (selectedEdges.length > 0)
    edgeBrowser = new TabbedBrowser (selectedEdges, cytoscapeWindow.getEdgeAttributes (),
                                     attributeCategoriesToIgnore, webBrowserScript);

   return null;

} // getSelectionPopup
//---------------------------------------------------------------------------------------
} // class NodeBrowsingMode
