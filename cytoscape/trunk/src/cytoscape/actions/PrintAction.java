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

import y.view.Graph2DPrinter;
import y.option.OptionHandler;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class PrintAction extends AbstractAction  {
  CytoscapeWindow cytoscapeWindow;
  PageFormat pageFormat;
  OptionHandler printOptions;
    
  public PrintAction (CytoscapeWindow cytoscapeWindow) {
    super ("Print...");
    this.cytoscapeWindow = cytoscapeWindow;
    printOptions = new OptionHandler ("Print Options");
    printOptions.addInt ("Poster Rows",1);
    printOptions.addInt ("Poster Columns",1);
    printOptions.addBool ("Add Poster Coords",false);
    final String[] area = {"View","Graph"};
    printOptions.addEnum ("Clip Area",area,1);
    }

  public void actionPerformed (ActionEvent e) {

    Graph2DPrinter gprinter = new Graph2DPrinter (cytoscapeWindow.getGraphView());
    PrinterJob printJob = PrinterJob.getPrinterJob ();
    if (pageFormat == null) pageFormat = printJob.defaultPage ();
    printJob.setPrintable (gprinter, pageFormat);
      
    if (printJob.printDialog ()) try {
      cytoscapeWindow.setInteractivity (false);
      printJob.print ();  
      }
    catch (Exception ex) {
      ex.printStackTrace ();
      }
    cytoscapeWindow.setInteractivity (true);
    } // actionPerformed

}

