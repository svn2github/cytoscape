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

public class VDistTop extends AbstractControlAction {

  public VDistTop ( ImageIcon icon ) {
    super( icon );
  }

  protected void control ( List nodes ) {

    if ( nodes.size() <= 1 )
      return;

    Collections.sort(nodes, new YComparator());

    double d = Y_max - Y_min;
    d = d / (nodes.size()-1);

    for ( int i = 0; i < nodes.size(); i++ ) {
       NodeView n = ( NodeView )nodes.get(i);
       double h = n.getHeight()/2; 
       n.setYPosition( Y_min + i*d + h );
    }
  }

  protected double getY(NodeView n) {
    double y = n.getYPosition();
    double h = n.getHeight()/2;
    return y-h;
  }
}
