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
public class PrintAction extends AbstractAction  {
    NetworkView networkView;
    
    public PrintAction(NetworkView networkView) {
        super ("Print...");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {

   //    if ( System.getProperty("os.name").startsWith( "Windows" ) ) {
//         cytoscape.util.PrintUtilities.printComponent( ( (PGraphView)networkView.getView() ).getCanvas() );
//       } else {
//         PGraphView ginyView = (PGraphView)networkView.getView();
//         ginyView.getCanvas().getLayer().print();
//       }

      ( (PGraphView)networkView.getView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, "true");

      ExportDialog export = new ExportDialog();
      export.showExportDialog( ( (PGraphView)networkView.getView() ).getCanvas(), "Export view as ...", ( (PGraphView)networkView.getView() ).getCanvas(), "export" );
      
      ( (PGraphView)networkView.getView() ).getCanvas().getCamera().addClientProperty( PrintingFixTextNode.PRINTING_CLIENT_PROPERTY_KEY, null);

    } // actionPerformed
}

