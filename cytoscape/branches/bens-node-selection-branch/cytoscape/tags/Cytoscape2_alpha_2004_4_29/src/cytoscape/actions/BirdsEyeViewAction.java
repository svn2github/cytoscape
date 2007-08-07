package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

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
    JFrame dialog = new JFrame("Navigator");
     final PGraphView pview = (PGraphView)networkView.getView();
    
     dialog.getContentPane().add( pview.getBirdsEyeView() );
    
     dialog.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
          ( (phoebe.event.BirdsEyeView)pview.getBirdsEyeView() ).disconnect();
         }
       });
     
     dialog.pack();
     dialog.setSize(new Dimension ( 200, 200));
     dialog.setVisible( true );
     
  }

}
