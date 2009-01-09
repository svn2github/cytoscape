// EdgeControlDialog

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

//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

import java.util.*;

import y.base.*;
import y.view.*;

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;

import cytoscape.undo.UndoableGraphHider;
//--------------------------------------------------------------------------------------
/**
 * present a JTree with edge attributes names as top level nodes, and attribute values
 * as child nodes; any node/s, once selected, are operated on by buttons in the bottom
 * of the dialog box.
 */
public class EdgeControlDialog extends JDialog {

  CytoscapeWindow cytoscapeWindow;
  Graph2D graph;
  String [] edgeNames;
  HashMap edgeNamesHash;
  TreePath [] selectedTreePaths;
  GraphObjAttributes edgeAttributes;
  UndoableGraphHider graphHider;
  JTree tree;
//--------------------------------------------------------------------------------------
public EdgeControlDialog (CytoscapeWindow cytoscapeWindow, 
                          HashMap edgeNamesHash, String title)

{
  super (cytoscapeWindow.getMainFrame (), false);
  this.cytoscapeWindow = cytoscapeWindow;
  this.edgeNamesHash = edgeNamesHash;
  this.graph = cytoscapeWindow.getGraph ();
  this.graphHider = cytoscapeWindow.getGraphHider ();
  this.edgeAttributes = cytoscapeWindow.getEdgeAttributes ();
  setTitle (title);
  setContentPane (createTreeViewGui ());

} // EdgeControlDialog ctor
//--------------------------------------------------------------------------------------
JPanel createTreeViewGui ()
{
  JPanel contentPane = new JPanel ();
  contentPane.setLayout (new BorderLayout ());
 
  JScrollPane scrollPane = new JScrollPane (createTreeView (edgeNamesHash));

  contentPane.add (scrollPane, BorderLayout.CENTER);

  JPanel actionButtonPanel = new JPanel ();
  actionButtonPanel.setLayout (new GridLayout(4, 2));

  JButton hideButton= new JButton ("Hide");
  JButton hideOthersButton= new JButton ("Hide Others");
  JButton hideAllButton= new JButton ("Hide All");
  JButton showAllButton= new JButton ("Show All");

  JButton selectButton= new JButton ("Select");
  JButton selectOthersButton= new JButton ("Select Others");
  JButton selectAllButton= new JButton ("Select All");
  JButton deselectAllButton= new JButton ("Deselect All");

  JButton dismissButton= new JButton ("Dismiss");


  actionButtonPanel.add (selectButton);
  actionButtonPanel.add (hideButton);

  actionButtonPanel.add (selectOthersButton);
  actionButtonPanel.add (hideOthersButton);

  actionButtonPanel.add (selectAllButton);
  actionButtonPanel.add (hideAllButton);

  actionButtonPanel.add (deselectAllButton);
  actionButtonPanel.add (showAllButton);

  hideButton.addActionListener (new HideAction ());
  hideOthersButton.addActionListener (new HideOthersAction ());
  hideAllButton.addActionListener (new HideAllAction ());
  showAllButton.addActionListener (new ShowAllAction ());

  selectButton.addActionListener (new SelectAction ());
  selectAllButton.addActionListener (new SelectAllAction ());
  deselectAllButton.addActionListener (new DeselectAllAction ());
  selectOthersButton.addActionListener (new SelectOthersAction ());

  dismissButton.addActionListener (new DismissAction ());

  JPanel allButtonsPanel = new JPanel ();
  allButtonsPanel.setLayout (new BorderLayout ());
  allButtonsPanel.add (actionButtonPanel, BorderLayout.CENTER);
  allButtonsPanel.add (dismissButton, BorderLayout.SOUTH);

  contentPane.add (allButtonsPanel, BorderLayout.SOUTH);

  return contentPane;

} // createTreeViewGui
//--------------------------------------------------------------------------------------
protected JTree createTreeView (HashMap edgeNamesHash)
{
  DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Edge Attributes");
  createTreeNodes (root, edgeNamesHash);
  tree = new JTree (root);
  tree.addTreeSelectionListener (new MyTreeSelectionListener ());
  return tree;

} // createTreeView
//--------------------------------------------------------------------------------------
class MyTreeSelectionListener implements TreeSelectionListener {

  public void valueChanged (TreeSelectionEvent e) {
    DefaultMutableTreeNode node = 
       (DefaultMutableTreeNode) tree.getLastSelectedPathComponent ();
    selectedTreePaths = tree.getSelectionPaths ();
    } // valueChanged

} // inner class MyTreeSelectionListener
//-----------------------------------------------------------------------------------
protected void createTreeNodes (DefaultMutableTreeNode root, HashMap edgeNamesHash)
{
  DefaultMutableTreeNode branch = null;
  DefaultMutableTreeNode leaf = null;
  String [] topLevelNames = (String []) edgeNamesHash.keySet().toArray (new String [0]);
  java.util.Arrays.sort (topLevelNames, String.CASE_INSENSITIVE_ORDER);

  for (int i=0; i < topLevelNames.length; i++) {
    branch = new DefaultMutableTreeNode (topLevelNames [i]);
    String [] children = (String []) edgeNamesHash.get (topLevelNames [i]);
    java.util.Arrays.sort (children, String.CASE_INSENSITIVE_ORDER);
    for (int j=0; j < children.length; j++) 
      branch.add (new DefaultMutableTreeNode (children [j]));
    root.add (branch);
    } // for i

} // createTreeNodes
//-----------------------------------------------------------------------------------
class SelectAction extends AbstractAction {
  SelectAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    if (selectedTreePaths == null || selectedTreePaths.length == 0) {
      EdgeControlDialog.this.getToolkit().beep ();
      return;
      }
    cytoscapeWindow.getGraph().unselectEdges ();
    for (int i=0; i < selectedTreePaths.length; i++)
      selectEdgesByName (selectedTreePaths [i]);
    cytoscapeWindow.redrawGraph ();
    } // actionPerformed

} // SelectAction
//------------------------------------------------------------------------------
class HideAction extends AbstractAction {
  HideAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    if (selectedTreePaths == null || selectedTreePaths.length == 0) {
      EdgeControlDialog.this.getToolkit().beep ();
      return;
      }
    graphHider.unhideEdges ();
    String action = e.getActionCommand ();
    for (int i=0; i < selectedTreePaths.length; i++)
      hideEdgesByName (selectedTreePaths [i]);
    cytoscapeWindow.redrawGraph ();
    } // actionPerformed

} // HideButtonAction
//------------------------------------------------------------------------------
boolean pathMatchesEdge (String edgeName, TreePath treePath, GraphObjAttributes edgeAttributes)
{
  Object [] objPath = treePath.getPath ();
  String [] pathNames = new String [objPath.length];

  for (int i=0; i < pathNames.length; i++)
    pathNames [i] = objPath [i].toString ();

  int pathLength = pathNames.length;

  if (pathLength < 2) 
    return false;

  if (!edgeAttributes.hasAttribute (pathNames [1], edgeName))
    return false;

  if (pathLength == 2)
    return true;

  if (pathLength == 3) {
    String [] values = edgeAttributes.getStringArrayValues (pathNames [1], edgeName);
    for (int i=0; i < values.length; i++)
      if (values [i].equalsIgnoreCase (pathNames [2]))
        return true;
    } // pathLength == 3

   return false;

} // pathMatchesEdge
//------------------------------------------------------------------------------
protected void hideEdgesByName (TreePath treePath)
{
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    String edgeName = edgeAttributes.getCanonicalName (edge);
    if (pathMatchesEdge (edgeName, treePath, edgeAttributes))
      graphHider.hide (edge);
    } // for ec
 
} // hideEdgesByName
//------------------------------------------------------------------------------
protected void hideOtherEdges ()
{
  Vector keepVisibleList = new Vector ();

  if (selectedTreePaths == null) return;
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    String canonicalName = edgeAttributes.getCanonicalName (edge);
    for (int p=0; p < selectedTreePaths.length; p++) {
      TreePath treePath = selectedTreePaths [p];
      if (pathMatchesEdge (canonicalName, treePath, edgeAttributes)) 
        keepVisibleList.add (edge);
      } // for p
   } // for ec

  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    if (!keepVisibleList.contains (edge))
      graphHider.hide (edge);
    }

} // hideOtherEdges
//------------------------------------------------------------------------------
protected void inverseHideEdgesByName (TreePath treePath)
{
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    String edgeName = edgeAttributes.getCanonicalName (edge);
    if (!pathMatchesEdge (edgeName, treePath, edgeAttributes))
      graphHider.hide (edge);
    } // for ec
 
} // inverseHideEdgesByName
//------------------------------------------------------------------------------
protected void selectEdgesByName (TreePath treePath)
{
  Vector list = new Vector ();
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    String canonicalName = edgeAttributes.getCanonicalName (edge);
    if (pathMatchesEdge (canonicalName, treePath, edgeAttributes)) 
      list.add (edge);
    } // for ec

   cytoscapeWindow.selectEdges ((Edge []) list.toArray (new Edge [0]), false);

 
} // selectEdgesByName
//------------------------------------------------------------------------------
class SelectOthersAction extends AbstractAction {
  SelectOthersAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    selectOtherEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // SelectOthersAction
//------------------------------------------------------------------------------
class DeselectAllAction extends AbstractAction {
  DeselectAllAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    deselectAllEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // DeselectAllAction
//------------------------------------------------------------------------------
class HideOthersAction extends AbstractAction {

  HideOthersAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    graphHider.unhideEdges ();
    hideOtherEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // HideOthersAction
//------------------------------------------------------------------------------
class HideAllAction extends AbstractAction {

  HideAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    graphHider.hideEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // HideAllAction
//------------------------------------------------------------------------------
class SelectAllAction extends AbstractAction {

  SelectAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    selectAllEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // HideAllAction
//------------------------------------------------------------------------------
class ShowAllAction extends AbstractAction {

  ShowAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    graphHider.unhideEdges ();
    cytoscapeWindow.redrawGraph ();
    }

} // ShowAllAction
//------------------------------------------------------------------------------
protected void selectAllEdges ()
{
  Vector list = new Vector ();
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ())
    list.add (ec.edge ());

  cytoscapeWindow.selectEdges ((Edge []) list.toArray (new Edge [0]), false);

}
//------------------------------------------------------------------------------
protected void invertEdgeSelection ()
{
  Edge [] edges = graph.getEdgeArray();
  for (int i=0; i < edges.length; i++) {
    EdgeRealizer edgeRealizer = graph.getRealizer(edges [i]);
    edgeRealizer.setSelected (!edgeRealizer.isSelected());
    }
}
//------------------------------------------------------------------------------
protected void selectOtherEdges ()
{
  Vector keepUnselectedList = new Vector ();

  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    String canonicalName = edgeAttributes.getCanonicalName (edge);
    for (int p=0; p < selectedTreePaths.length; p++) {
      TreePath treePath = selectedTreePaths [p];
      if (pathMatchesEdge (canonicalName, treePath, edgeAttributes)) 
        keepUnselectedList.add (edge);
      } // for p
   } // for ec

  Vector selectList = new Vector ();
  for (EdgeCursor ec = graph.edges (); ec.ok (); ec.next ()) {
    Edge edge = ec.edge ();
    EdgeRealizer edgeRealizer = graph.getRealizer(edge);
    edgeRealizer.setSelected (!keepUnselectedList.contains (edge));
    }
  

} // selectOtherEdges
//------------------------------------------------------------------------------
protected void deselectAllEdges ()
{
  graph.unselectEdges ();

}
//------------------------------------------------------------------------------
private void placeInCenter ()
{
  GraphicsConfiguration gc = getGraphicsConfiguration ();
  int screenHeight = (int) gc.getBounds().getHeight ();
  int screenWidth = (int) gc.getBounds().getWidth ();
  int windowWidth = getWidth ();
  int windowHeight = getHeight ();
  setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);

} // placeInCenter
//------------------------------------------------------------------------------
public class DismissAction extends AbstractAction 
{
  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    EdgeControlDialog.this.dispose ();
    }

} // DismissAction
//-----------------------------------------------------------------------------
} // class EdgeControlDialog


