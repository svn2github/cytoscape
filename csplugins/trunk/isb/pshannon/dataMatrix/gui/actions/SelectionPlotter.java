// SelectionPlotter
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//-----------------------------------------------------------------------------------
import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

import csplugins.isb.dtenenbaum.plot2d.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import org.jfree.chart.*;
import org.jfree.chart.entity.*;
//-----------------------------------------------------------------------------------
public class SelectionPlotter {

   protected DataMatrixBrowser browser;
   private Plot2D plotter;

//-----------------------------------------------------------------------------------
public SelectionPlotter (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new PlotSelectedRowsAction ());
}
//-----------------------------------------------------------------------------------
class PlotSelectedRowsAction extends AbstractAction {

  PlotSelectedRowsAction () {super ("Plot Selected");}

  public void actionPerformed (ActionEvent e) {

    DataMatrixLens lens = browser.getCurrentLens ();
    JTable table = browser.getCurrentTable ();
    lens.setSelectedRows (browser.getCurrentTable().getSelectedRows());

    JTable currentlyVisibleTable = browser.getCurrentTable ();
    if (browser.hasSelectedRows ()) {
      plotter = null;
      try {
        String dataName = lens.getMatrixName ();
        plotter = new Plot2D (dataName + " Profiles", "Condition", dataName, lens);
        // resetPlotter ();
        class CML implements ChartMouseListener {
           public void chartMouseMoved (ChartMouseEvent e) {}
           public void chartMouseClicked (ChartMouseEvent e) {
             ChartEntity ent = e.getEntity ();
             String shapeType = null;
             try {
               shapeType = ent.getShapeType ();
               } 
             catch (NullPointerException ex) {
               return; 
               }
             XYMetaData md =  XYMetaData.parseToolTip (ent.getToolTipText ());
               // Here is where you could do other stuff, like tell a table or
               // graph what row or node to select.
             try {
               } 
             catch (NullPointerException ex) {;}
             } // chartMouseClicked
          } // inner class CML
        plotter.addChartMouseListener (new CML ());
        plotter.pack ();
        plotter.placeInCenter ();
        plotter.show ();
        } 
      catch (Exception ex0) {
        ex0.printStackTrace();
        //if (Cytoscape.getCytoscapeObj() != null)
        //  cytoscapeWindow.setInteractivity (true);
        return;
        }
      //if (cytoscapeWindow != null)
      //  cytoscapeWindow.setInteractivity (true);
      } // if selection
    } // actionPerformed

} // PlotSelectedNodesAction
//----------------------------------------------------------------------------
/**********************************
private void resetPlotter() 
{
  if (null != plotter) {
    JTable table = (JTable)tableList.get(getCurrentTabAndTableIndex());
    DataMatrixLens lens = (DataMatrixLens) lensList.get (getCurrentTabAndTableIndex ());
    lens.setSelectedRows (table.getSelectedRows());
    plotter.populateFromLens (lens);
    }

} // resetPlotter
***********************/
//----------------------------------------------------------------------------
} // class SelectionPlotter
