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

public class VAlignCenter extends AbstractControlAction {

  public VAlignCenter( ImageIcon i ) {
    super(i);
  }

  protected void control(List nodes) {
    Iterator sel_nodes = nodes.iterator();
    double mid =  Y_min + ((Y_max - Y_min)/2);
    while ( sel_nodes.hasNext() ) {
        ( ( NodeView )sel_nodes.next() ).setYPosition( mid ); 
    }                       
  }
}
