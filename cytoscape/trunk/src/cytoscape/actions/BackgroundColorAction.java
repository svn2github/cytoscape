//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;

import giny.view.*;
import java.util.*;



import cytoscape.view.*;
//-------------------------------------------------------------------------
public class BackgroundColorAction extends AbstractAction  {

    NetworkView networkView;
    
    public BackgroundColorAction(NetworkView networkView) {
        super ("Change Background Color");
        this.networkView = networkView;
    }
    

    public void actionPerformed (ActionEvent ev) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		// Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
		 if (networkView.getView() == null) {
			CyWindow panel =  (CyWindow)networkView;
			panel.setBackground( color.showDialog( panel , "Choose a background Color", panel.getBackground()) );
			
		 }
		 else {
			 networkView.getView().setBackgroundPaint( color.showDialog( networkView.getView().getComponent() , "Choose a background Color", (java.awt.Color)networkView.getView().getBackgroundPaint()) );
                }
	       } } ); 
                  
	}//!Yfiles
			
		
    }//action performed

}

