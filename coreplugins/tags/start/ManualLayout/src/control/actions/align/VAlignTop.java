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

public class VAlignTop extends CytoscapeAction {

 protected CyWindow window;

  public VAlignTop ( CyWindow window, ImageIcon icon ) {
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

				min = Double.POSITIVE_INFINITY; 
    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      value = node_view.getYPosition();
      if ( value < min ) 
        min = value;
    }

    sel_nodes = view.getSelectedNodes().iterator();
    while ( sel_nodes.hasNext() ) {
      ( ( NodeView )sel_nodes.next() ).setYPosition( min );
    }                       
  
  
  }

  public boolean isInToolBar () {
    return false;
  }

  public boolean isInMenuBar () {
    return false;
  }


}
