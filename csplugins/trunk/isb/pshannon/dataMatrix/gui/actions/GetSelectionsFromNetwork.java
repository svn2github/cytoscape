// GetSelectionsFromNetwork
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//-----------------------------------------------------------------------------------
import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
//-----------------------------------------------------------------------------------
public class GetSelectionsFromNetwork {

   protected DataMatrixBrowser browser;

//-----------------------------------------------------------------------------------
public GetSelectionsFromNetwork (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Get Selections from Network ");}

  public void actionPerformed (ActionEvent e) {
    if (!browser.hasCytoscapeParent ()) {
      JOptionPane.showMessageDialog (browser, "A running Cytoscape is needed to run a movie.",
                                     "No Cytoscape!",  JOptionPane.ERROR_MESSAGE);
      return;
      }

    JTable [] tables = browser.getAllTables ();
    for (int tableIndex=0; tableIndex < tables.length; tableIndex++) {
      JTable table = tables [tableIndex];
      browser.setUpdateSelectionsToCytoscape (false);
      table.getSelectionModel().clearSelection ();
      browser.setUpdateSelectionsToCytoscape (true);
      ArrayList selectedNodeNames = browser.getSelectedNodeNames ();
      browser.selectRowsByName (tableIndex, (String []) selectedNodeNames.toArray (new String [0]));
      browser.setUpdateSelectionsToCytoscape (true);
      } // for tableIndex

    } // actionPerformed

} // ThisAction
//----------------------------------------------------------------------------
} // class GetSelectionsFromNetwork

