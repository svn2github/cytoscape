package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.Dimension;
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
	if (networkView.getView() == null) return;  
    JDialog dialog = new JDialog();
    PGraphView pview = (PGraphView)networkView.getView();
    
    dialog.setTitle("Birds Eye View" );
    dialog.getContentPane().add( pview.getBirdsEyeView() );
    
    
    dialog.pack();
    dialog.setSize(new Dimension ( 200, 200));
    dialog.setVisible( true );
  }

}
