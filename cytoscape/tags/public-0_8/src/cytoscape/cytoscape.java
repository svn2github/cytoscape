// cytoscape.java  
//-------------------------------------------------------------------------------------
// $Revision$
// $Date$ 
// $Author$
//-------------------------------------------------------------------------------------
package cytoscape;
//-------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.File;
import java.util.*;

import y.base.Node;
import y.base.Edge;
import y.view.Graph2D;
import y.io.YGFIOHandler;
import y.io.GMLIOHandler;


import cytoscape.CytoscapeConfig;
import cytoscape.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.data.readers.*;
import cytoscape.data.servers.*;
//------------------------------------------------------------------------------
public class cytoscape implements WindowListener {
  Vector windows = new Vector ();
  private String logicFilename;
  private String geometryFilename;
  private GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  private GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  private ExpressionData expressionData = null;
  private String bioDataServerName;
  private static  BioDataServer bioDataServer;
  private CytoscapeVersion version = new CytoscapeVersion ();

//------------------------------------------------------------------------------
public cytoscape (String [] args) throws Exception
{
  CytoscapeConfig config = new CytoscapeConfig (args);

  if (config.helpRequested ()) {
    System.out.println (version);
    System.out.println (config.getUsage ());
    exit ();
    }    
  else if (config.inputsError ()) {
    System.out.println (version);
    System.out.println ("------------- Inputs Error");
    System.out.println (config.getUsage ());
    System.out.println (config);
    System.exit (1);
    }
  else if (config.displayVersion ()) {
    System.out.println (version);
    exit ();
    }

    //------------------------- run the program
  System.out.println (config);
  String geometryFilename = config.getGeometryFilename ();
  String bioDataDirectory = config.getBioDataDirectory ();
  String interactionsFilename = config.getInteractionsFilename ();
  String expressionDataFilename = config.getExpressionFilename ();
  Graph2D graph = null; 
  CytoscapeWindow cytoscapeWindow = null;
  String title = null;
  boolean requestFreshLayout = true;

  if (geometryFilename != null) {
    System.out.print ("reading " + geometryFilename + "...");
    System.out.flush ();
    graph = FileReadingAbstractions.loadGMLBasic(geometryFilename,edgeAttributes);
    System.out.println ("  done");
    title = geometryFilename;
    requestFreshLayout = false;
    }
  else if (interactionsFilename != null) {
    System.out.print ("reading " + interactionsFilename + "...");
    System.out.flush ();
    graph=FileReadingAbstractions.loadIntrBasic(interactionsFilename,edgeAttributes);
    System.out.println ("  done");
    title = interactionsFilename;
    }
  if (expressionDataFilename != null) {
    System.out.print ("reading " + expressionDataFilename + "...");
    System.out.flush ();
    expressionData = new ExpressionData (expressionDataFilename);
    System.out.println ("  done");
    }
  if (bioDataDirectory != null) {
    bioDataServer = BioDataServerFactory.create (bioDataDirectory);
    }

  if (graph == null)
    graph = new Graph2D ();

  FileReadingAbstractions.initAttribs(config,graph,nodeAttributes,edgeAttributes);

  cytoscapeWindow = new CytoscapeWindow (this, config,
                                         graph, expressionData, bioDataServer,
                                         nodeAttributes, edgeAttributes, 
                                         geometryFilename, expressionDataFilename,
                                         title, requestFreshLayout);

} // ctor
//------------------------------------------------------------------------------
public void windowActivated   (WindowEvent e) {}
/**
 * on linux (at least) a killed window generates a 'windowClosed' event; trap that here
 */
public void windowClosing     (WindowEvent e) {windowClosed (e);}
public void windowDeactivated (WindowEvent e) {}
public void windowDeiconified (WindowEvent e) {}
public void windowIconified   (WindowEvent e) {}
//------------------------------------------------------------------------------
public void windowOpened (WindowEvent e) 
{  
  windows.add (e.getWindow ());
}
//------------------------------------------------------------------------------
public void windowClosed (WindowEvent e) 
{ 
  Window window = e.getWindow ();
  if (windows.contains (window))
    windows.remove (window);

  if (windows.size () == 0) {
    System.out.println ("all windows closed, exiting...");
    exit ();
    }

} // windowListener.windowClosed	
//------------------------------------------------------------------------------
public void exit ()
{
  for (int i=0; i < windows.size (); i++) {
    Window w = (Window) windows.elementAt (i);
    w.dispose ();
    }

  System.exit (0);

} // exit
//------------------------------------------------------------------------------
public static void main (String args []) throws Exception
{
   cytoscape app = new cytoscape (args);

} // main
//------------------------------------------------------------------------------
} // cytoscape
