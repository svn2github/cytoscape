// FindCorrelations
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
public class FindCorrelations {

   protected DataMatrixBrowser browser;

//-----------------------------------------------------------------------------------
public FindCorrelations (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Find Correlations...");}

  public void actionPerformed (ActionEvent e) {
    String [] matrixAliases = browser.getMatrixAliases ();
    DataMatrixLens [] lenses = browser.getAllLenses ();
    JDialog dialog = new CorrelationFinderDialog (browser);
    dialog.pack ();
    // dialog.setLocationRelativeTo (browser);
    dialog.setVisible (true);
    } // actionPerformed

} // ThisAction
//----------------------------------------------------------------------------
} // class FindCorrelations
