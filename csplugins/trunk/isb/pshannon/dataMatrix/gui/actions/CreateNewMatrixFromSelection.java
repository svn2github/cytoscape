// CreateNewMatrixFromSelection
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//-----------------------------------------------------------------------------------
import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
//-----------------------------------------------------------------------------------
public class CreateNewMatrixFromSelection {

   protected DataMatrixBrowser browser;

//-----------------------------------------------------------------------------------
public CreateNewMatrixFromSelection (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Create from Selection");}

  public void actionPerformed (ActionEvent e) {
    DataMatrixLens lens = browser.getCurrentLens ();
    JTable table = browser.getCurrentTable ();
    lens.setSelectedRows (browser.getCurrentTable().getSelectedRows());

    if (!browser.hasSelectedRows ()) {
      JOptionPane.showMessageDialog (browser, "No rows are selected!",
                                     "Selection Error",  JOptionPane.ERROR_MESSAGE);
      return;
      }
    String newMatrixName = JOptionPane.showInputDialog (browser, "Please enter a name for this matrix:");
    if (newMatrixName == null)
      return;

    try {
      DataMatrix subMatrix = lens.getSelectedSubMatrix ();
      subMatrix.setName (newMatrixName);
      browser.addMatrixToGui (subMatrix);
      }
    catch (Exception e0) {
      e0.printStackTrace ();
      }
    } // actionPerformed

} // CreateNewMatrixFromSelection
//----------------------------------------------------------------------------
} // class CreateNewMatrixFromSelection
