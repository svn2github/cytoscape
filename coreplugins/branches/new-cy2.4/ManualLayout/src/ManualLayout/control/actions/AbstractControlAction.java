
package ManualLayout.control.actions;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.data.*;

import giny.view.*;
import giny.model.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class AbstractControlAction extends CytoscapeAction {

	protected double X_min;
	protected double X_max;
	protected double Y_min;
	protected double Y_max;
	protected NodeView node_view;
	protected Iterator sel_nodes;
 
	public AbstractControlAction ( ImageIcon icon ) {
		super( "", icon );
	}

	public void actionPerformed ( ActionEvent e ) {
		GraphView view = Cytoscape.getCurrentNetworkView();
		computeDimensions(view);
		control( view.getSelectedNodes() );
		view.updateView();
	}

	protected abstract void control(List l);

	/**
	 * This may look silly, but it is meant to be overridden
	 * with special cases.
	 */
	protected double getX(NodeView n) {
		return n.getXPosition();
	}

	/**
	 * This may look silly, but it is meant to be overridden
	 * with special cases.
	 */
	protected double getY(NodeView n) {
		return n.getYPosition();
	}

	protected void computeDimensions(GraphView view) {
   
		X_min = Double.POSITIVE_INFINITY; 
		X_max = Double.NEGATIVE_INFINITY;
		Y_min = Double.POSITIVE_INFINITY; 
		Y_max = Double.NEGATIVE_INFINITY;
		sel_nodes = view.getSelectedNodes().iterator();

		while ( sel_nodes.hasNext() ) {
			node_view = ( NodeView )sel_nodes.next();

			double X = getX(node_view);
		
			if ( X > X_max )
				X_max = X;
		
			if ( X < X_min ) 
				X_min = X;

			double Y = getY(node_view);
		
			if ( Y > Y_max )
				Y_max = Y;
		
			if ( Y < Y_min ) 
				Y_min = Y;
		}
  	}

	public boolean isInToolBar () {
		return false;
	}

	public boolean isInMenuBar () {
		return false;
	}


	public class XComparator implements Comparator {
		public int compare(Object o1, Object o2) {

			NodeView n1 = (NodeView)o1;
			NodeView n2 = (NodeView)o2;

			if ( getX(n1) == getX(n2) ) 
				return 0;
			else if ( getX(n1) < getX(n2) ) 
				return -1;
			else 
				return 1;
		}

		public boolean equals(Object o1, Object o2) {
			NodeView n1 = (NodeView)o1;
			NodeView n2 = (NodeView)o2;
			if ( getX(n1) == getX(n2) ) 
				return true;
			else
				return false;
		}
	}


	public class YComparator implements Comparator {
		public int compare(Object o1, Object o2) {

			NodeView n1 = (NodeView)o1;
			NodeView n2 = (NodeView)o2;

			if ( getY(n1) == getY(n2) )
				return 0;
			else if ( getY(n1) < getY(n2) )
				return -1;
			else
				return 1;
		}

		public boolean equals(Object o1, Object o2) {
			NodeView n1 = (NodeView)o1;
			NodeView n2 = (NodeView)o2;
			if ( getY(n1) == getY(n2) )
				return true;
			else
				return false;
		}
	}

}
