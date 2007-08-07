package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;
import cytoscape.util.CytoscapeAction;

public class GraphObjectSelectionAction extends CytoscapeAction {

  NetworkView networkView;

  public GraphObjectSelectionAction ( NetworkView networkView ) {
    super("Node Selection based on Attributes" );
    this.networkView = networkView;
    setPreferredMenu( "Select" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_T, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
  }

  public void actionPerformed (ActionEvent e) {
    JDialog dialog = new JDialog( );
    dialog.getContentPane().add( new GraphObjectSelection( networkView ) );
    dialog.pack();
    dialog.setVisible( true );
  }

}
