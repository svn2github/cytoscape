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

import y.view.Graph2DPrinter;
import y.option.OptionHandler;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class PrintAction extends AbstractAction  {
    NetworkView networkView;
    PageFormat pageFormat;
    OptionHandler printOptions;
    
    public PrintAction(NetworkView networkView) {
        super ("Print...");
        this.networkView = networkView;
        printOptions = new OptionHandler("Print Options");
        printOptions.addInt("Poster Rows",1);
        printOptions.addInt("Poster Columns",1);
        printOptions.addBool("Add Poster Coords",false);
        final String[] area = {"View","Graph"};
        printOptions.addEnum ("Clip Area",area,1);
    }

    
    public void actionPerformed(ActionEvent e) {
	if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {	 
	    String callerID = "PrintAction.actionPerformed";
	    networkView.getNetwork().beginActivity(callerID);
	    Graph2DPrinter gprinter = new Graph2DPrinter(networkView.getGraphView());
	    PrinterJob printJob = PrinterJob.getPrinterJob();
	    if (pageFormat == null) pageFormat = printJob.defaultPage();
	    printJob.setPrintable(gprinter, pageFormat);
	    
	    if (printJob.printDialog()) try {
		printJob.print ();  
	    }
	    catch (Exception ex) {
		ex.printStackTrace ();
	    }
	    networkView.getNetwork().endActivity(callerID);

	    } else {// using giny
		     
		PGraphView ginyView = (PGraphView)networkView.getView();
		ginyView.getCanvas().getLayer().print();
	    }

    } // actionPerformed
    
}

