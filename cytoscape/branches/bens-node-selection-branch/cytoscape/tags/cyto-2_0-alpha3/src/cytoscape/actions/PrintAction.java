//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import cytoscape.giny.*;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.*;
import cytoscape.util.CytoscapeAction;

public class PrintAction extends CytoscapeAction  {
    
    
    public PrintAction () {
        super ("Print...");
        setPreferredMenu( "File" );
    }

    public void actionPerformed(ActionEvent e) {

	    PhoebeNetworkView phoebeView = ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView();
	    phoebeView.getCanvas().getLayer().print();
     	
    } // actionPerformed
}

