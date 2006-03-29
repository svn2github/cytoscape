package control.actions.align;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.data.*;

import giny.view.*;
import giny.model.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class VAlignCenter extends CytoscapeAction {

 protected CyNetworkView window;

  public VAlignCenter ( CyNetworkView window, ImageIcon icon ) {
    super( "", icon );
    this.window = window;
  }

  public void actionPerformed ( ActionEvent e ) {
   
    GraphView view = window.getView();
    
    double min, max;
    double h, w;
    double value;
    NodeView node_view;
    Iterator sel_nodes;
    sel_nodes = view.getSelectedNodes().iterator();

    if ( !sel_nodes.hasNext() ) {
      // cancel if no selected nodes
      return;
    }

				double sum = 0.0;
				int count = 0;
    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      h = node_view.getHeight();
      value = node_view.getYPosition()+ 0.5*h;
						sum += value;
						count++;
    }
    double average = sum/count;
    sel_nodes = view.getSelectedNodes().iterator();
    while ( sel_nodes.hasNext() ) {
								node_view = ( NodeView )sel_nodes.next();
								h = node_view.getHeight();
								node_view.setYPosition(average - 0.5*h);
    }                       
    view.updateView();
  }

  public boolean isInToolBar () {
    return false;
  }

  public boolean isInMenuBar () {
    return false;
  }


}
