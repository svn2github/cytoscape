//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class BackgroundColorAction extends AbstractAction  {

    NetworkView networkView;
    
    public BackgroundColorAction(NetworkView networkView) {
        super ("Change Background Color");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent ev) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JColorChooser color = new JColorChooser();
                Paint newPaint = color.showDialog( networkView.getView().getComponent(),
                                                   "Choose a background Color",
                                                   (java.awt.Color)networkView.getView().getBackgroundPaint() );
                networkView.getView().setBackgroundPaint(newPaint);
            }
        }); 
    }//action performed
}

