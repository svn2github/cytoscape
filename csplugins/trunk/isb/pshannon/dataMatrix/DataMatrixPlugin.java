// DataMatrixPlugin
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//----------------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import java.awt.Window;
import java.util.*;
import java.io.*;
//----------------------------------------------------------------------------------------
import csplugins.isb.pshannon.dataMatrix.gui.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.Semantics;
//----------------------------------------------------------------------------------------
public class DataMatrixPlugin extends CytoscapePlugin {

  protected String [] matrixURIs;
  protected ArrayList matrices = new ArrayList ();
  protected DataMatrixBrowser browser;
  protected File currentDirectory;
  protected JFrame browserFrame;

//----------------------------------------------------------------------------------------
public DataMatrixPlugin () throws Exception
{

  CytoscapeAction loadMatrix = new LoadDataMatrixAction ();
  loadMatrix.setPreferredMenu ("Plugins");
  Cytoscape.getDesktop().getCyMenus().addAction(loadMatrix);

  CytoscapeAction showBrowser = new BrowseDataMatrices ();
  showBrowser.setPreferredMenu ("Plugins");
  Cytoscape.getDesktop().getCyMenus().addAction (showBrowser);

  init (new String [0]);
}
//----------------------------------------------------------------------------------------
public DataMatrixPlugin (String [] args) throws Exception
{
  init (args);
}
//----------------------------------------------------------------------------------------
public void init (String [] args) throws Exception
{
  String [] argv;
  if (Cytoscape.getCytoscapeObj () != null) {
    argv = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    }
  else {
    argv = args;
    }

  matrixURIs = extractMatrixFilenamesFromCommandLineArgs (argv);
  System.out.println ("number of matrixURIs: " + matrixURIs.length);
  for (int i=0; i < matrixURIs.length; i++) {
    DataMatrixReader reader = DataMatrixReaderFactory.createReader (matrixURIs [i]);
    reader.read ();
    DataMatrix [] result = reader.get ();
    System.out.println ("number of matrices read: " + result.length);
    matrices.add (reader.get ()[0]);
    File f = new File(".");
    String path = f.getAbsolutePath();
    path = path.replaceFirst("\\.$","");
    } // for i

} // ctor
//----------------------------------------------------------------------------------------
public String describe() {
	return "A plugin for handling Data Matrices and performing " +
			"various operations on them.";
}
//----------------------------------------------------------------------------------------
protected class LoadDataMatrixAction extends CytoscapeAction {

  LoadDataMatrixAction () { super ("Load Tab-Delimited Data Matrix..."); }
    
  public void actionPerformed (ActionEvent e) {
    JFileChooser chooser = new JFileChooser (currentDirectory);
    try {
      if (chooser.showOpenDialog (Cytoscape.getDesktop ()) == chooser.APPROVE_OPTION) {
        currentDirectory = chooser.getCurrentDirectory();
        String name = chooser.getSelectedFile ().toString ();
        DataMatrixReader reader = DataMatrixReaderFactory.createReader (name);
        reader.read ();
        matrices.add (reader.get ()[0]);
        } // if
      }
    catch (Exception ex) {
      ex.printStackTrace ();
      }
    } // actionPerformed

} // inner class LoadDataMatrixAction
//------------------------------------------------------------------------------
private String [] extractMatrixFilenamesFromCommandLineArgs (String [] args)
{
  ArrayList list = new ArrayList ();

  for (int i=0; i < args.length; i++) {
    if (args[i].equals ("--matrix")) {
      if (i+1 > args.length)
        throw new IllegalArgumentException ("error!  no --matrix value");
      else 
        list.add (args [i+1]);
      } // if "--matrix"
    } // for i

  System.out.println ("matrix uri's: " + list.size ());
  return (String []) list.toArray (new String [0]);

} // extractMatrixFilenamesFromCommandLineArgs
//----------------------------------------------------------------------------------------
protected class BrowseDataMatrices extends CytoscapeAction {

 BrowseDataMatrices () { super ("Browse Data Matrices..."); }
    
  public void actionPerformed (ActionEvent e) {
    try {
      browser = runBrowser ();
      }
   catch (Exception ex0) {
     ex0.printStackTrace ();
     String msg = "Failed to open DataMatrixBrowser: " + ex0.getMessage ();
     JOptionPane.showMessageDialog (Cytoscape.getDesktop (),
                                    msg, "DataMatrixBrowser errror",
                                    JOptionPane.ERROR_MESSAGE);
     } // catch

    } // actionPerformed

} // inner class BrowseDataMatrices

//------------------------------------------------------------------------------
public DataMatrixBrowser runBrowser () throws Exception
{
  DataMatrix [] dma = (DataMatrix []) matrices.toArray (new DataMatrix [0]);
  DataMatrixBrowser browser = new DataMatrixBrowser (dma); 
  browserFrame = new JFrame ();
  browserFrame.setTitle("Data Matrix Browser");
  browserFrame.getContentPane().add (browser);   
  browserFrame.pack ();
  if (Cytoscape.getCytoscapeObj () != null)
    browserFrame.setLocationRelativeTo (Cytoscape.getDesktop ());
  browserFrame.setVisible (true);

  return browser;

} // runBrowser
//------------------------------------------------------------------------------
} // class BrowseDataMatrices
