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

import phoebe.util.*;

import cytoscape.giny.*;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import org.freehep.util.export.ExportDialog;

//-------------------------------------------------------------------------
public class ExportAction extends CytoscapeAction  {

    public final static String MENU_LABEL = "Export As...";
        
    public ExportAction () {
        super (MENU_LABEL);
        setPreferredMenu( "File" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
    }

    public void actionPerformed(ActionEvent e) {
	
      ( (PhoebeNetworkView)Cytoscape.getCurrentNetworkView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");

      ExportDialog export = new ExportDialog();
      export.showExportDialog( ( (PhoebeNetworkView)Cytoscape.getCurrentNetworkView() ).getCanvas(), "Export view as ...", ( (PhoebeNetworkView)Cytoscape.getCurrentNetworkView() ).getCanvas(), "export" );
      
      ( (PhoebeNetworkView)Cytoscape.getCurrentNetworkView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);
	
    } // actionPerformed
}

