package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import phoebe.PGraphView;
import cytoscape.dialogs.GraphObjectSelection;

public class BirdsEyeViewAction extends AbstractAction {

  NetworkView networkView;

  public BirdsEyeViewAction ( NetworkView networkView ) {
    super("Birds Eye View");
    this.networkView = networkView;
  }

  public void actionPerformed (ActionEvent e) {
    JDialog dialog = new JDialog( );
    PGraphView pview = (PGraphView)networkView.getView();
    dialog.getContentPane().add( pview.getBirdsEyeView() );
    dialog.pack();
    dialog.setVisible( true );
  }

}
