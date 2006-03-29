package control.actions.align;

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

public class HAlignLeft extends CytoscapeAction {

 
  public HAlignLeft ( CyNetworkView window, ImageIcon icon ) {
    super( "", icon );
  }

  public void actionPerformed ( ActionEvent e ) {
   
    GraphView view = Cytoscape.getCurrentNetworkView();
    
    double min = Double.MAX_VALUE; 
    double max = Double.MIN_VALUE;
    double h, w;
    double value;
    NodeView node_view;
    Iterator sel_nodes;
    sel_nodes = view.getSelectedNodes().iterator();

    if ( !sel_nodes.hasNext() ) {
      // cancel if no selected nodes
      return;
    }

    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      w = node_view.getWidth();
      h = node_view.getHeight();
      value = node_view.getXPosition();
      
      if ( value > max )
        max = value;

      if ( value < min ) 
        min = value;
    }

    sel_nodes = view.getSelectedNodes().iterator();
    while ( sel_nodes.hasNext() ) {
      ( ( NodeView )sel_nodes.next() ).setXPosition( min );
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
