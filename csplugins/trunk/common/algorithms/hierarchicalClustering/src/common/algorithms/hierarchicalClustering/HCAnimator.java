/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package common.algorithms.hierarchicalClustering;

/**
 * This class provides methods that allow the drawing of the Hierarchical Tree
 * as it is being built.
 * TODO: Reimplement for GINY
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 */

import java.util.*;
import java.lang.*;
import javax.swing.*;
import java.awt.*;
//import y.base.*;
//import y.view.*;
//import y.layout.*;
//import y.layout.tree.TreeLayouter;
import java.awt.event.*;
//import cytoscape.view.StraightLineMoveMode;
import java.awt.geom.Rectangle2D;

public class HCAnimator extends JPanel {
	public HCAnimator (){}

}

/*public class HCAnimator extends JPanel implements Graph2DSelectionListener{

  *//**
   * The default width of the UI that shows the hierarchical-tree.
   *//*
  public final static int DEFAULT_WIDTH = 350;
  
  *//**
   * The default height of the UI that shows the hierarchical-tree.
   *//*
  public final static int DEFAULT_HEIGHT = 200;

  *//**
   * A map of <code>y.base.Node</code> objects to their unique IDs
   *//*
  protected Map nodeToID;
  
  *//**
   * A map of unique IDs to <code>y.base.Node</code>
   *//*
  protected Map IDtoNode;
  
  *//**
   * The graphicical representation of the Hierarchical-Tree
   *//*
  protected Graph2D hcTree;

  *//**
   * Viewer of the <code>y.view.Graph2D</code> object that represents
   * the Hierarchical-Tree.
   *//*
  protected Graph2DView g2dView;

  *//**
   * The layouter for the hierarchical-tree.
   *//*
  protected Layouter treeLayouter;

  *//**
   * The main frame.
   *//*
  protected JFrame mainFrame;

  protected ZoomSelectedAction zoomSelectedAction;
  
  *//**
   * Constructor.
   *//*
  public HCAnimator (){
    this.nodeToID = new HashMap();
    this.IDtoNode = new HashMap();
    this.hcTree = new Graph2D();
    createUI();
  }//HCAnimator

  *//**
   * Constructor.
   *
   * @param leaves an array of leaves of the Hierarchical-Tree. Their toString()
   * method will be used to label the leaves of the <code>y.view.Graph2D</code>tree.
   *//*
  public HCAnimator (Object [] leaves){
    this.nodeToID = new HashMap();
    this.IDtoNode = new HashMap();
    this.hcTree = new Graph2D();
    createLeaves(leaves);
    createUI();
  }//HCAnimator

  *//**
   * Creates a <code>y.base.Node</code> for each object in the array.
   * The label of the nodes is the toString() calue of the objects in the array.
   *
   * @return an array of <code>y.base.Node</code> objects created.
   *//*
  public Node [] createLeaves (Object [] leaves){
    Node newNode;
    String id;
    ArrayList newNodes = new ArrayList();
    NodeRealizer nr;
    for(int i = 0; i < leaves.length; i++){
      id = leaves[i].toString();
      newNode = this.hcTree.createNode(0,0,id);
      nr = this.hcTree.getRealizer(newNode);
      nr.setFillColor(Color.RED);
      newNodes.add(newNode);
      this.nodeToID.put(newNode,id);
      this.IDtoNode.put(id, newNode);
    }
    return (Node[])newNodes.toArray(new Node[newNodes.size()]);
  }//createLeaves
  
  *//**
   * Creates the user interface where the hierarchical-tree will be
   * displayed.
   *//*
  protected void createUI (){
    setLayout(new BorderLayout());
    this.g2dView = new Graph2DView(this.hcTree);
    this.g2dView.addViewMode(new ReadOnlyGraphMode());
    this.treeLayouter = new TreeLayouter();
    DefaultBackgroundRenderer renderer = new DefaultBackgroundRenderer(this.g2dView);
    add(this.g2dView, BorderLayout.CENTER);
    this.g2dView.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    JToolBar toolbar = createToolBar ();
    add(toolbar, BorderLayout.NORTH);
    this.mainFrame = new JFrame("Hierarchical-Tree Viewer");
    this.mainFrame.setContentPane(this);
    this.mainFrame.pack();
  }//createUI

  *//**
   * Sets the dialog that displays the hierarchical tree visible (if true) or not visible (if false).
   *//*
  public void setVisible (boolean visible){
    this.mainFrame.setVisible(visible);
  }//setVisible

  *//**
   * Zooms into the selected nodes.
   *//*
  public void zoomSelected (){
    this.zoomSelectedAction.actionPerformed(null);
  }//zoomSelected

  *//**
   * Creates a tool-bar.
   *//*
  protected JToolBar createToolBar (){
    JToolBar bar = new JToolBar ();
    JButton zoomInButton = new JButton("Zoom In");
    zoomInButton.addActionListener(new ZoomAction(1.1));
    JButton zoomOutButton = new JButton("Zoom Out");
    zoomOutButton.addActionListener(new ZoomAction(0.9));
    JButton zoomSelectedButton = new JButton("Zoom Selected");
    this.zoomSelectedAction = new ZoomSelectedAction();
    zoomSelectedButton.addActionListener(this.zoomSelectedAction);
    JButton fitGraphButton = new JButton("Fit Graph");
    fitGraphButton.addActionListener(new FitGraphAction());
    JButton findButton = new JButton("Select Node");
    findButton.addActionListener(new FindNodeAction());
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new CloseAction());
    bar.add(zoomInButton);
    bar.add(zoomOutButton);
    bar.add(zoomSelectedButton);
    bar.add(fitGraphButton);
    bar.add(findButton);
    bar.add(closeButton);
    return bar;
  }//createToolBar

  *//**
   * Re-draws the hierarchical-tree.
   *
   * @param doLayout whether or not to layout the tree.
   *//*
  public void drawHCTree (boolean doLayout){
    if(doLayout){
      this.treeLayouter.doLayout(this.g2dView.getGraph2D());
      this.g2dView.fitContent();
    }
    this.g2dView.updateView();
    this.g2dView.paintImmediately(0,0,this.g2dView.getWidth(),this.g2dView.getHeight());
  }//drawHCTree

  *//**
   * Joins the two given nodes by creating a parent node that is connected to them,
   * and then redraws the hierarchical-tree.
   *
   * @param parentID the ID for the new node
   * @param child1ID the ID of the first child
   * @param child2ID the ID of the second child
   * @param redrawTree whether or not the tree should be redrawn
   *//*
  public void joinNodes (String parentID, 
                         String child1ID, 
                         String child2ID, 
                         boolean redrawTree){
    Node parentNode = this.hcTree.createNode(0,0,parentID);
    Node childNode1 = (Node)IDtoNode.get(child1ID);
    Node childNode2 = (Node)IDtoNode.get(child2ID);
    if(childNode1 == null){
      System.out.println(child1ID + " does not have a corresponding node.");
      return;
    }

    if(childNode2 == null){
      System.out.println(child2ID + " does not have a corresponding node.");
      return;
    }
    
    this.hcTree.createEdge(parentNode, childNode1);
    this.hcTree.createEdge(parentNode, childNode2);
    this.nodeToID.put(parentNode, parentID);
    this.IDtoNode.put(parentID, parentNode);
    if(redrawTree){
      drawHCTree(true);
    }
  }//joinNodes

  *//**
   * Selects the node whose name is given as an argument.
   *//*
  public void selectNode(String name){
    this.hcTree.unselectAll();
    Node node = (Node)this.IDtoNode.get(name.toUpperCase());
    if(node == null){
      node = (Node)this.IDtoNode.get(name.toLowerCase());
    }
    if(node != null){
      this.hcTree.setSelected(node, true);
    }
  }//selectNode
  
  *//**
   * Implements Graph2DSelectionListener.onGraph2DSelectionEvent()
   * Invoked when the structure of the graph has changed.
   *//*
  public void onGraph2DSelectionEvent(Graph2DSelectionEvent e){}

  // ------------------------- Internal Classes -----------------------------------//
  protected class ZoomAction extends AbstractAction {
    
    double factor;
    
    ZoomAction (double factor) {
      super();
      this.factor = factor;
    }//ZoomAction
    
    public void actionPerformed (ActionEvent e) {
      g2dView.setZoom(g2dView.getZoom()*factor);
      drawHCTree(false);
    }//actionPerformed
  }//ZoomAction
  //--------------------------------------------------------------------------------
  protected class ReadOnlyGraphMode extends EditMode {
    
    ReadOnlyGraphMode () { 
      super (); 
      allowNodeCreation (false);
      allowEdgeCreation (false);
      allowBendCreation (false);
      showNodeTips (true);
      showEdgeTips (true);
      setMoveSelectionMode(new StraightLineMoveMode());
    }//ReadOnlyGraphMode
    
  }//ReadOnlyGraphMode
  //--------------------------------------------------------------------------------
  protected class ZoomSelectedAction extends AbstractAction  {
    ZoomSelectedAction ()  { super (); }
    public void actionPerformed (ActionEvent e) {
      Graph2D g = g2dView.getGraph2D ();
      NodeCursor nc = g.selectedNodes (); 
      if (nc.ok ()) {
        Rectangle2D box = g.getRealizer(nc.node ()).getBoundingBox();
        for(nc.next (); nc.ok (); nc.next ()){
          g.getRealizer (nc.node ()).calcUnionRect(box);
        }
        g2dView.zoomToArea (box.getX(),box.getY(),box.getWidth(),box.getHeight());
        if (g2dView.getZoom () > 2.0) g2dView.setZoom (2.0);
        drawHCTree(false);
      }
    }//actionPerformed
  }//ZoomSelectedAction
  //--------------------------------------------------------------------------------
  protected class FindNodeAction extends AbstractAction {
    FindNodeAction () {super();}
    public void actionPerformed (ActionEvent e){
      String answer = 
        (String) JOptionPane.showInputDialog (mainFrame, 
                                              "Select node whose canonical name is:");
      if(answer != null && answer.length () > 0){
        selectNode(answer.trim ());
      }
    }//actionPerformed
  }//FindNodeAction
  //--------------------------------------------------------------------------------
  protected class CloseAction extends AbstractAction{
    CloseAction() {super();}
    public void actionPerformed (ActionEvent e){
      mainFrame.dispose();
    }//actionPerformed
  }//CloseAction
  //--------------------------------------------------------------------------------
  protected class FitGraphAction extends AbstractAction {
    FitGraphAction() {super();}
    public void actionPerformed (ActionEvent e){
      hcTree.setSelected(hcTree.nodes(), true);
      zoomSelectedAction.actionPerformed(null);
      hcTree.setSelected(hcTree.nodes(), false);
    }//actionPerformed
  }//FitGraphAction
}//HCAnimator
*/