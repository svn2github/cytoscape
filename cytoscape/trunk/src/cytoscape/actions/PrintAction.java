//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import phoebe.*;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class PrintAction extends AbstractAction  {
    NetworkView networkView;
    
    public PrintAction(NetworkView networkView) {
        super ("Print...");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
        PGraphView ginyView = (PGraphView)networkView.getView();
        ginyView.getCanvas().getLayer().print();
    } // actionPerformed
}

