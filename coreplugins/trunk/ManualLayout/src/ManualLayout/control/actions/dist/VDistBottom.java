package ManualLayout.control.actions.dist;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.data.*;

import giny.view.*;
import giny.model.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;


public class VDistBottom extends CytoscapeAction {

 protected CyNetworkView window;

  public VDistBottom ( CyNetworkView window, ImageIcon icon ) {
    super( "", icon );
    this.window = window;
  }

  public void actionPerformed ( ActionEvent e ) {
    GraphView view = window.getView();
    
    double min, max;
    double h, w;
    double value;
    NodeView node_view;
    List nodes = view.getSelectedNodes();
    Iterator sel_nodes;
    sel_nodes = nodes.iterator();

    if ( !sel_nodes.hasNext() ) {
      // cancel if no selected nodes
      return;
    }

    node_view = ( NodeView )sel_nodes.next();
    w = node_view.getWidth();
    h = node_view.getHeight();
    min = node_view.getYPosition();
    max = min; 

    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      w = node_view.getWidth();
      h = node_view.getHeight();
      value = node_view.getYPosition();
      
      if ( value > max )
        max = value;

      if ( value < min ) 
        min = value;
    }


    double d = max - min;
    d = d / nodes.size();

    for ( int i = 0; i < nodes.size(); ++i ) {
       ( ( NodeView )nodes.get(i) ).setYPosition( min + i*d );
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
