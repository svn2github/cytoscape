package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;

public class BirdsEyeViewAction extends AbstractAction {

  NetworkView networkView;

  public BirdsEyeViewAction ( NetworkView networkView ) {
    super("Birds Eye View");
    this.networkView = networkView;
  }

  public void actionPerformed (ActionEvent e) {
    JDialog dialog = new JDialog( );
    dialog.getContentPane().add( networkView.getView().getBirdsEyeView() );
    dialog.pack();
    dialog.setVisible( true );
  }

}
