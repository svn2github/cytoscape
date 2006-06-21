package ManualLayout.control.actions.align;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.data.*;

import giny.view.*;
import giny.model.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import ManualLayout.control.actions.AbstractControlAction;


public class HAlignRight extends AbstractControlAction {

  public HAlignRight( ImageIcon i ) {
    super(i);
  }

  protected void control( List nodes ) {
    Iterator sel_nodes = nodes.iterator();
    while ( sel_nodes.hasNext() ) {
      NodeView n = ( NodeView )sel_nodes.next();
      double w = n.getWidth()/2;
      n.setXPosition( X_max - w );
    }                       
  }

  protected double getX(NodeView n) {
    double x = n.getXPosition();
    double w = n.getWidth()/2;
    return x+w;
  }
}
