// NodeBrowser.java

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
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
    
  if (selectedNodes.length > 0) {
    nodeBrowser = new TabbedBrowser (selectedNodes, cytoscapeWindow.getNodeAttributes (),
                                     attributeCategoriesToIgnore, webBrowserScript);
    }

  if (selectedEdges.length > 0) {
    edgeBrowser = new TabbedBrowser (selectedEdges, cytoscapeWindow.getEdgeAttributes (),
                                     attributeCategoriesToIgnore, webBrowserScript);
    }

   return null;

} // getSelectionPopup
//---------------------------------------------------------------------------------------
} // class NodeBrowsingMode


