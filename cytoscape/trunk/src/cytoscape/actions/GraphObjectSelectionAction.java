package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;

public class GraphObjectSelectionAction extends AbstractAction {

  NetworkView networkView;

  public GraphObjectSelectionAction ( NetworkView networkView ) {
    super("By Any Attribute...");
    this.networkView = networkView;
  }

  public void actionPerformed (ActionEvent e) {
    JDialog dialog = new JDialog( );
    dialog.getContentPane().add( new GraphObjectSelection( networkView ) );
    dialog.pack();
    dialog.setVisible( true );
  }

}
