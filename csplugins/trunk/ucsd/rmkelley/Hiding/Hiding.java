package csplugins.ucsd.rmkelley.Hiding;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.view.EdgeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.plugin.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.util.GinyFactory;
import cytoscape.util.CytoscapeMenuBar;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class Hiding extends AbstractPlugin{
    
  CyWindow cyWindow;
    
    
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public Hiding(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    CytoscapeMenuBar menuBar = cyWindow.getCyMenus().getMenuBar();
    JMenu hideMenu = new JMenu("Hiding");
    hideMenu.add(new HideSelectedObjectsAction());
    hideMenu.add(new RestoreObjectsAction());
    menuBar.add(hideMenu);
  }
    
  /**
   * This class gets attached to the menu item.
   */
  public class HideSelectedObjectsAction extends AbstractAction {
        
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public HideSelectedObjectsAction() {super("Hide Selected Objects");}
        
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Removes objects from the graph perspective");
      return sb.toString();
    }
        
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
                       
      //inform listeners that we're doing an operation on the network
      Thread t = new HidingTask(cyWindow); 
      t.start();
           
    }
  }

  /**
   * This class gets attached to the menu item.
   */
  public class RestoreObjectsAction extends AbstractAction {
        
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public RestoreObjectsAction() {super("Restore All Objects");}
        
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Restore all objects to the perspective");
      return sb.toString();
    }
        
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
                       
      //inform listeners that we're doing an operation on the network
      Thread t = new RestoreTask(cyWindow); 
      t.start();
           
    }
  }

  public class HidingTask extends Thread{
    CyWindow cyWindow;
    public HidingTask(CyWindow cyWindow){
      this.cyWindow = cyWindow;
    }
    public void run(){
    	
      GraphView view = cyWindow.getView();
      //these are really edge views
      List edgeViewList = view.getSelectedEdges();
      Vector tohideEdges = new Vector();
      for (Iterator  edgeViewIt = edgeViewList.iterator(); edgeViewIt.hasNext();) {
	EdgeView current = (EdgeView)edgeViewIt.next();
	//current.setSelected(false);
	tohideEdges.add(current.getEdge());
      } // end of for (Iterator  = .iterator(); .hasNext();)
      
      Vector tohideNodes = new Vector();
      List nodeViewList = view.getSelectedNodes();
      HashSet adjacentEdges = new HashSet();
      GraphPerspective perspective = view.getGraphPerspective();
      for (Iterator  nodeViewIt = nodeViewList.iterator(); nodeViewIt.hasNext();) {
	NodeView current = (NodeView)nodeViewIt.next();
	//current.setSelected(false);
	tohideNodes.add(current.getNode());
	adjacentEdges.addAll(perspective.getAdjacentEdgesList(current.getNode(),true,true,true ));
      } // end of for (Iterator  = .iterator(); .hasNext();)

      tohideEdges.addAll(adjacentEdges);
      //hide the edges in the graph perspective
      for (Iterator  edgeIt = tohideEdges.iterator(); edgeIt.hasNext();) {
	perspective.hideEdge((Edge)edgeIt.next());
      } // end of for (Iterator  = .iterator(); .hasNext();)
      
      for (Iterator  nodeIt = tohideNodes.iterator(); nodeIt.hasNext();) {
	perspective.hideNode((Node)nodeIt.next());
      } // end of for (Iterator  = .iterator(); .hasNext();)
      	
    }
	
  }

  public class RestoreTask extends Thread{
    CyWindow cyWindow;
    public RestoreTask(CyWindow cyWindow){
      this.cyWindow = cyWindow;
    }
    public void run(){
      //get all the nodes in the rootgraph
      GraphPerspective perspective = cyWindow.getView().getGraphPerspective();
      RootGraph root = perspective.getRootGraph();
      for (Iterator  nodeIt = root.nodesList().iterator(); nodeIt.hasNext();) {
	Node current = (Node)nodeIt.next();
	if (!perspective.containsNode(current,false)) {
	  perspective.restoreNode(current);
	} // end of if ()
      } // end of for (Iterator  = .iterator(); .hasNext();)
      for (Iterator  edgeIt = root.edgesList().iterator(); edgeIt.hasNext();) {
	Edge current = (Edge)edgeIt.next();
	if (!perspective.containsEdge(current, false)) {
	  perspective.restoreEdge(current);
	} // end of if ()
	
      } // end of for (Iterator  = .iterator(); .hasNext();)
      
    }
    
  }
}

