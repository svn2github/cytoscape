package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.awt.Dimension;
import cytoscape.view.CyNetworkView;
import phoebe.PGraphView;
import cytoscape.dialogs.GraphObjectSelection;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;

public class BirdsEyeViewAction extends CytoscapeAction {

  public BirdsEyeViewAction () {
    super("Birds Eye View");
    setPreferredMenu( "Visualization" );
  }

  public void actionPerformed (ActionEvent e) {
	
    JFrame dialog = new JFrame("Navigator");
    final PGraphView pview = (PGraphView)Cytoscape.getCurrentNetworkView();
    
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
