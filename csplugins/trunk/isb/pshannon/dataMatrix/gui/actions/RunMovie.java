// RunMovie
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
public class RunMovie {

   protected DataMatrixBrowser browser;

//-----------------------------------------------------------------------------------
public RunMovie (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Movie...");}

  public void actionPerformed (ActionEvent e) {
    if (!browser.hasCytoscapeParent ()) {
      JOptionPane.showMessageDialog (browser, "A running Cytoscape is needed to run a movie.",
                                     "No Cytoscape!",  JOptionPane.ERROR_MESSAGE);
      return;
      }
    else {
      String [] matrixAliases = browser.getMatrixAliases ();
      DataMatrixLens [] lenses = browser.getAllLenses ();
      JDialog dialog = new DataMatrixMovieDialog (lenses, matrixAliases);
      dialog.pack ();
      dialog.setLocationRelativeTo (browser);
      dialog.setVisible (true);
      }
    } // actionPerformed

} // ThisAction
//----------------------------------------------------------------------------
} // class RunMovie
