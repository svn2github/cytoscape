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
import phoebe.util.*;

import cytoscape.view.NetworkView;

import org.freehep.util.export.ExportDialog;

//-------------------------------------------------------------------------
public class ExportAction extends AbstractAction  {
    NetworkView networkView;
    
    public ExportAction(NetworkView networkView) {
        super ("Export As...");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
	
	( (PGraphView)networkView.getView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");

	ExportDialog export = new ExportDialog();
	export.showExportDialog( ( (PGraphView)networkView.getView() ).getCanvas(), "Export view as ...", ( (PGraphView)networkView.getView() ).getCanvas(), "export" );
	
	( (PGraphView)networkView.getView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);
	
    } // actionPerformed
}

