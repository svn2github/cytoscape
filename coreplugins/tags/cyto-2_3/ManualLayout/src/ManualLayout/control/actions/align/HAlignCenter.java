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

public class HAlignCenter extends AbstractControlAction {

  public HAlignCenter( ImageIcon i ) {
    super(i);
  }

  protected void control(List nodes) {
    Iterator sel_nodes = nodes.iterator();
    double mid = ( X_min + (X_max - X_min) / 2 );
    while ( sel_nodes.hasNext() ) {
      ( ( NodeView )sel_nodes.next() ).setXPosition( mid );
    }                       
  }
}
