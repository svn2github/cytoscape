// ExportMatrix
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//-----------------------------------------------------------------------------------
import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
//-----------------------------------------------------------------------------------
public class ExportMatrix {

  protected DataMatrixBrowser browser;

//-----------------------------------------------------------------------------------
public ExportMatrix (DataMatrixBrowser browser) 
{
  this.browser = browser;
  browser.addActionToToolbar (new ThisAction ());
}
//-----------------------------------------------------------------------------------
class ThisAction extends AbstractAction {

  ThisAction () {super ("Export");}

  public void actionPerformed (ActionEvent e) {

    DataMatrixLens lens = browser.getCurrentLens ();
    JTable table = browser.getCurrentTable ();
    lens.setSelectedRows (browser.getCurrentTable().getSelectedRows());
    int selectedRowCount = lens.getSelectedRowCount ();

    if (selectedRowCount == 0) {
       String msg = "No rows are selected in this table.  Do you wish to export all rows?";
       int response = JOptionPane.showConfirmDialog (browser, msg, "No selections",
                                                     JOptionPane.YES_NO_OPTION);
       if (response == 1) return;
       lens.selectAllRows ();
        } // if no selections

   String matrixAsString = lens.toString (false);
   //String matrixWithAttributes = extendMatrixWithNodeAttributes (matrixAsString);
   JFileChooser chooser = new JFileChooser (browser.getCurrentDirectory ());

   if (chooser.showSaveDialog (browser) == chooser.APPROVE_OPTION) {
     String name = chooser.getSelectedFile ().toString ();
     browser.setCurrentDirectory (chooser.getCurrentDirectory());
     File matrixFile = chooser.getSelectedFile ();
     try {
       FileWriter matrixFileWriter = new FileWriter (matrixFile);
      //DRT       matrixFileWriter.write (matrixWithAttributes);
       matrixFileWriter.write (matrixAsString);
       matrixFileWriter.close ();
       } // try
     catch (IOException exc) {
       JOptionPane.showMessageDialog (null, exc.toString (),
                                     "Error Writing to \"" + matrixFile.getName()+"\"",
                                     JOptionPane.ERROR_MESSAGE);
       } // catch
     } // if chooser ->  save
   } // actionPerformed

} // ExportAction
//---------------------------------------------------------------------------------
} // class ExportMatrix
