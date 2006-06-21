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

import ManualLayout.control.actions.AbstractControlAction;

public class HDistRight extends AbstractControlAction {

  public HDistRight( ImageIcon icon ) {
    super( icon );
  }

  protected void control ( List nodes ) {

    if ( nodes.size() <= 1 )
      return;

    Collections.sort( nodes, new XComparator() );

    double d = X_max - X_min;
    d = d / (nodes.size()-1);

    for ( int i = 0; i < nodes.size(); i++ ) {
      NodeView n = ( NodeView )nodes.get(i);
      double w = n.getWidth()/2;
      n.setXPosition( X_min + i*d - w );
    }
  }

  protected double getX(NodeView n) {
    double x = n.getXPosition();
    double w = n.getWidth()/2;
    return x+w;
  }
}

