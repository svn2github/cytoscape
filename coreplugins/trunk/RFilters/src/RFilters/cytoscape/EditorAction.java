package filter.cytoscape;

import java.awt.event.*;
import javax.swing.*;

import filter.view.FilterView;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.CyNetwork;
public class EditorAction extends CytoscapeAction {

  protected FilterView filterView;
  protected JFrame frame;

  public EditorAction () {
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
