/*
 * Created on May 24, 2005
 *
 */
package cytoscape.editor.actions;

import javax.swing.JMenuItem;

import phoebe.PNodeView;


import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import edu.umd.cs.piccolo.PNode;

/**
 * action assigned to Add Node button on toolbar in CytoscapeEditor
 * sets cursor to "add cursor" and sets editor's mode to "ADD_MODE"
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 */
public class NodeAction {
	
	  public NodeAction () {
	  }


	  /**
	   * gets the label for a NodeView
	   * @param unused
	   * @param node 
	   * @return
	   */
	  public static String getTitle (Object unused[], PNode node ) {
	      if (node instanceof PNodeView) {
	          return ((PNodeView)node).getLabel().getText();
	      } else {
	          return "";
	      }
	  }

	  /**
	   * gets the context (right-click) menu item that is associated with the input NodeView
	   * this should be a delete action
	   * @param args arguments, first argument should be the Network view
	   * @param node 
	   * @return the menu item that is associated with the input NodeView
	   */
	  public static JMenuItem getContextMenuItem (Object[] args, PNode node) {
	  	

		    final CyNetworkView nv = ( CyNetworkView )args[0];
		    CyNode cyNode;

		    if ( node instanceof PNodeView ) 
		    {
		    	
		    	String myName = ((PNodeView) node).getLabel().getText();
		    	
		    	cyNode =
			         (CyNode)((PNodeView) node).getNode();
		    	
		    	return (new JMenuItem(new DeleteAction (cyNode, myName)));

		    }
			return (new JMenuItem(new DeleteAction ())) ;
	
		}	 
}
