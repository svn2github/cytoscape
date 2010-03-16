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
    System.exit (0);
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
    System.exit (0);
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
    GMLReader gmlReader = new GMLReader (geometryFilename);
    graph = gmlReader.read ();
    System.out.println ("  done");
    title = geometryFilename;
    requestFreshLayout = false;
    }
  else if (interactionsFilename != null) {
    System.out.print ("reading " + interactionsFilename + "...");
    System.out.flush ();
    InteractionsReader reader = new InteractionsReader (interactionsFilename);
    reader.read ();
    graph = reader.getGraph ();
    GraphObjAttributes interactionEdgeAttributes = reader.getEdgeAttributes ();
    edgeAttributes.add (interactionEdgeAttributes);
    edgeAttributes.addNameMap (interactionEdgeAttributes.getNameMap ());
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

  String [] nodeAttributeFilenames = config.getNodeAttributeFilenames ();

  if (nodeAttributeFilenames != null)
    for (int i=0; i < nodeAttributeFilenames.length; i++)
      nodeAttributes.readFloatAttributesFromFile (nodeAttributeFilenames [i]);

  String [] edgeAttributeFilenames = config.getEdgeAttributeFilenames ();

  if (edgeAttributeFilenames != null)
    for (int i=0; i < edgeAttributeFilenames.length; i++)
      edgeAttributes.readFloatAttributesFromFile (edgeAttributeFilenames [i]);

  if (graph == null)
    graph = new Graph2D ();

  if (nodeAttributes != null)
    addNameMappingToAttributes (graph.getNodeArray (), nodeAttributes);

  cytoscapeWindow = new CytoscapeWindow (this, config,
                                         graph, expressionData, bioDataServer,
                                         nodeAttributes, edgeAttributes, 
                                         geometryFilename, expressionDataFilename,
                                         title, requestFreshLayout);

} // ctor
//------------------------------------------------------------------------------
/**
 * add node-to-canonical name mapping (and the same for edges), so that node 
 * attributes can be retrieved by simply knowing the y.base.node, which is the basic view
 * of data in this program.  for example:
 *
 *  NodeCursor nc = graph.selectedNodes (); 
 *  for (nc.toFirst (); nc.ok (); nc.next ()) {
 *    Node node = nc.node ();
 *    String canonicalName = nodeAttributes.getCanonicalName (node);
 *    HashMap nodeAttributeBundle = nodeAttributes.getAllAttributes (canonicalName);
 *    }
 * 
 * 
 */
protected void addNameMappingToAttributes (Object [] graphObjects, GraphObjAttributes attributes)
{
  for (int i=0; i < graphObjects.length; i++) {
    Object graphObj = graphObjects [i];
    String canonicalName = graphObj.toString ();
    attributes.addNameMapping (canonicalName, graphObj);
    }

} // addNameMappingToAttributes
//------------------------------------------------------------------------------
public void windowActivated   (WindowEvent e) {}
public void windowClosing     (WindowEvent e) {}
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
  windows.remove (e.getWindow ());

  if (windows.size () == 0) {
    System.out.println ("all windows closed, exiting...");
    System.exit (0);
    }

} // windowListener.windowClosed	
//------------------------------------------------------------------------------
public static void main (String args []) throws Exception
{
   cytoscape app = new cytoscape (args);

} // main
//------------------------------------------------------------------------------
} // cytoscape
