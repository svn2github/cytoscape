//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.giny.*;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.*;
import cytoscape.util.CytoscapeAction;

public class PreferenceAction extends CytoscapeAction  {
    
    
    public PreferenceAction () {
        super ("Preference...");
        setPreferredMenu( "Edit" );
    }

    public void actionPerformed(ActionEvent e) {

     	System.out.println("Preference Menu Selected.");

    } // actionPerformed
}

