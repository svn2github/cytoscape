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
import java.util.logging.*;

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
  protected Vector windows = new Vector ();
  protected String logicFilename;
  protected String geometryFilename;
  protected GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  protected GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  protected ExpressionData expressionData = null;
  protected String bioDataServerName;
  protected static  BioDataServer bioDataServer;
  protected CytoscapeVersion version = new CytoscapeVersion ();
  protected Logger logger;

//------------------------------------------------------------------------------
public cytoscape (String [] args) throws Exception
{
  CytoscapeConfig config = new CytoscapeConfig (args);
  setupLogger (config);

  if (config.helpRequested ()) {
    System.out.println (version);
    System.out.println (config.getUsage ());
    exit (0);
    }    
  else if (config.inputsError ()) {
    System.out.println (version);
    System.out.println ("------------- Inputs Error");
    System.out.println (config.getUsage ());
    System.out.println (config);
    exit (1);
    }
  else if (config.displayVersion ()) {
    System.out.println (version);
    exit (0);
    }

    //------------------------- run the program
  logger.info (config.toString ());
  String geometryFilename = config.getGeometryFilename ();
  String bioDataDirectory = config.getBioDataDirectory ();
  String interactionsFilename = config.getInteractionsFilename ();
  String expressionDataFilename = config.getExpressionFilename ();
  Graph2D graph = null; 
  CytoscapeWindow cytoscapeWindow = null;
  String title = null;
  boolean requestFreshLayout = true;

  if (geometryFilename != null) {
    logger.info ("reading " + geometryFilename + "...");
    graph = FileReadingAbstractions.loadGMLBasic(geometryFilename,edgeAttributes);
    logger.info ("  done");
    title = geometryFilename;
    requestFreshLayout = false;
    }
  else if (interactionsFilename != null) {
    logger.info ("reading " + interactionsFilename + "...");
    graph=FileReadingAbstractions.loadIntrBasic(interactionsFilename,edgeAttributes);
    logger.info ("  done");
    title = interactionsFilename;
    }
  if (expressionDataFilename != null) {
    logger.info ("reading " + expressionDataFilename + "...");
    expressionData = new ExpressionData (expressionDataFilename);
    logger.info ("  done");
    }
  if (bioDataDirectory != null) {
    bioDataServer = BioDataServerFactory.create (bioDataDirectory);
    }

  if (graph == null)
    graph = new Graph2D ();

  FileReadingAbstractions.initAttribs(config,graph,nodeAttributes,edgeAttributes);

  cytoscapeWindow = new CytoscapeWindow (this, config, logger,
                                         graph, expressionData, bioDataServer,
                                         nodeAttributes, edgeAttributes, 
                                         geometryFilename, expressionDataFilename,
                                         title, requestFreshLayout);

} // ctor
//------------------------------------------------------------------------------
/**
 * configure logging:  cytoscape.props specifies what level of logging
 * messages are written to the console; by default, only SEVERE messages
 * are written.  in time, more control of logging (i.e., optional logging
 * to a file, disabling console logging, per-window or per-plugin logging) 
 * can be provided
 */
protected void setupLogger (CytoscapeConfig config)
{
  logger = Logger.getLogger ("global"); 
  Properties properties = config.getProperties ();
  String level = properties.getProperty ("logging", "SEVERE");

  if (level.equalsIgnoreCase ("severe"))
    logger.setLevel (Level.SEVERE);
  else if (level.equalsIgnoreCase ("warning"))
    logger.setLevel (Level.WARNING);
  else if (level.equalsIgnoreCase ("info"))
    logger.setLevel (Level.INFO);
  else if (level.equalsIgnoreCase ("config"))
    logger.setLevel (Level.CONFIG);
  else if (level.equalsIgnoreCase ("all"))
    logger.setLevel (Level.ALL);
  else if (level.equalsIgnoreCase ("none"))
    logger.setLevel (Level.OFF);
  else if (level.equalsIgnoreCase ("off"))
    logger.setLevel (Level.OFF);

} // setupLogger
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
    logger.info ("all windows closed, exiting...");
    exit (0);
    }

} // windowListener.windowClosed	
//------------------------------------------------------------------------------
public void exit (int exitCode)
{
  for (int i=0; i < windows.size (); i++) {
    Window w = (Window) windows.elementAt (i);
    w.dispose ();
    }

  System.exit (exitCode);

} // exit
//------------------------------------------------------------------------------
public static void main (String args []) throws Exception
{
  cytoscape app = new cytoscape (args);

} // main
//------------------------------------------------------------------------------
} // cytoscape
