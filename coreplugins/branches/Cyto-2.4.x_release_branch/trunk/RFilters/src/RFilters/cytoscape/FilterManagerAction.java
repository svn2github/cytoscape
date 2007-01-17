package filter.cytoscape;

import java.awt.event.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.CyNetwork;
import filter.model.*;
import filter.view.*;

import javax.swing.*;

public class FilterManagerAction extends CytoscapeAction {

  protected FilterView filterView;
  protected JFrame frame;

  public FilterManagerAction () {
    super( "Edit Filters" );
    setPreferredMenu( "Select" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_E, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
  }

  public void actionPerformed (ActionEvent e) {
    if ( filterView == null ) {
      filterView = new FilterView();
    }
    if ( frame == null ) {
      frame = new JFrame( "Edit Filters" );
      frame.getContentPane().add( filterView );
      frame.pack();
    }
    frame.setVisible( true );
  }
}
