package filter.cytoscape;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

public class FilterPlugin extends CytoscapeAction {

  protected FilterUsePanel filterUsePanel;
  protected JFrame frame;

  public FilterPlugin () {
    super( "Use Filters" );
    setPreferredMenu( "Filters" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
  }

  public void actionPerformed (ActionEvent e) {
    if ( filterUsePanel == null ) {
      filterUsePanel = new FilterUsePanel();
    }
    if ( frame == null ) {
      frame = new JFrame( "Use Filters" );
      frame.getContentPane().add( filterUsePanel );
      frame.pack();
    }
    frame.setVisible( true );
  }

}
