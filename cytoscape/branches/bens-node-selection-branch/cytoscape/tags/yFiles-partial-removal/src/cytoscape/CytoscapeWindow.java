// CytoscapeWindow.java:  a yfiles, GUI tool for exploring genetic networks

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JOptionPane;
import javax.swing.filechooser.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import y.base.*;
import y.view.*;

import y.layout.Layouter;
import y.layout.GraphLayout;

import y.layout.circular.CircularLayouter;
import y.layout.hierarchic.*;
import y.layout.organic.OrganicLayouter;
import y.layout.random.RandomLayouter;

import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import y.util.GraphHider; // yFiles 2.01
//import y.algo.GraphHider; // yFiles 1.4


import giny.model.RootGraph;

 // printing
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import javax.print.attribute.*;

import y.option.OptionHandler; 
import y.view.Graph2DPrinter;

import cytoscape.actions.*;
import cytoscape.data.*;
import cytoscape.data.annotation.*;
import cytoscape.data.readers.*;
import cytoscape.data.servers.*;
import cytoscape.dialogs.*;
import cytoscape.browsers.*;
import cytoscape.layout.*;
import cytoscape.jarLoader.*;
import cytoscape.visual.*;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;
import cytoscape.view.*;
import cytoscape.undo.*;
import cytoscape.util.MutableString;
import cytoscape.util.MutableBool;
import cytoscape.util.CyFileFilter;

import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 *  CytoscapeWindow used to be the main class for Cytoscape. After the refactoring,
 *  most of the data members and methods have been removed to other classes, although
 *  in many cases a public method remains in CytoscapeWindow that immediately redirects
 *  to a method in one of the classes that replace CytoscapeWindow, so that external
 *  classes that use a CytoscapeWindow reference can continue to work.
 *
 *  See external documentation for more details.
 *
 * old CytoscapeWindow header:
 *  CytoscapeWindow is the <b>main class</b> for the application cytoscape.
 *  Plugin writers need only use public functions of CytoscapeWindow to
 *  gain access to the cytoscape data structures.
 */
public class CytoscapeWindow extends JPanel implements FilterDialogClient, Graph2DSelectionListener {

    public static final String NO_PLUGINS = "No plugins loaded";

    //this object contains all the window and view stuff
    //everything else is accessed through this reference
    protected CyWindow cyWindow;

    /** default species for all genes in the CytoscapeWindow */
    protected String defaultSpecies;
    
    protected String geometryFilename;
    protected String expressionDataFilename;
    
//------------------------------------------------------------------------------
public CytoscapeWindow(CytoscapeObj globalInstance, CyNetwork network,
String title) {
    this.defaultSpecies = null;
    this.geometryFilename = null;
    this.expressionDataFilename = null;
    this.cyWindow = new CyWindow(globalInstance, network, title, this);
    cyWindow.showWindow();
    loadPlugins();
}
//------------------------------------------------------------------------------
/**
 * This constructor should only be called from the CyWindow constructor. This
 * provides a simple wrapper around that CyWindow. Plugin loading should be done
 * by calling loadPlugins() after constructing this class.
 */
public CytoscapeWindow(CyWindow cyWindow) {
    this.defaultSpecies = null;
    this.geometryFilename = null;
    this.expressionDataFilename = null;
    this.cyWindow = cyWindow;
    //loadPlugins();
}
//------------------------------------------------------------------------------
/**
 * This is the classic CytoscapeWindow constructor, which is called by
 * cytoscape.java as well as several plugins. It now uses the arguments
 * to construct instances of the new objects that replace CytoscapeWindow.
 */
public CytoscapeWindow (cytoscape parentApp,
                        CytoscapeConfig config,
                        Logger logger,
                        Graph2D graph, 
                        ExpressionData expressionData,
                        BioDataServer bioDataServer,
                        GraphObjAttributes nodeAttributes,
                        GraphObjAttributes edgeAttributes,
                        String geometryFilename,
                        String expressionDataFilename,
                        String title,
                        boolean doFreshLayout)
   throws Exception {
  this.geometryFilename = geometryFilename;
  this.expressionDataFilename = expressionDataFilename;

  CytoscapeObj globalInstance = new CytoscapeObj(parentApp, config, logger, bioDataServer);
  
  CyNetwork network = new CyNetwork(graph, nodeAttributes,
                               edgeAttributes, expressionData);

  network.setNeedsLayout(doFreshLayout);
  this.cyWindow = new CyWindow(globalInstance, network, title, this);

  //these probably don't belong in the Cytoscape core
  assignSpeciesAttributeToAllNodes();
  displayCommonNodeNames();

  //now show the window
  cyWindow.showWindow();
  
  //plugins need to be loaded here, as many different callers
  //of the constructor expect this behavior
  loadPlugins();
} // ctor

//------------------------------------------------------------------------------
/**
 * This is the  CytoscapeWindow constructor, which is called by
 * cytoscape.java  when the giny library is used instead of y-files It now uses the arguments
 * to construct instances of the new objects that replace CytoscapeWindow.
 */
public CytoscapeWindow (cytoscape parentApp,
                        CytoscapeConfig config,
                        Logger logger,
                        RootGraph rootGraph, 
                        ExpressionData expressionData,
                        BioDataServer bioDataServer,
                        GraphObjAttributes nodeAttributes,
                        GraphObjAttributes edgeAttributes,
                        String geometryFilename,
                        String expressionDataFilename,
                        String title,
                        boolean doFreshLayout)
   throws Exception {
  this.geometryFilename = geometryFilename;
  this.expressionDataFilename = expressionDataFilename;

  CytoscapeObj globalInstance = new CytoscapeObj(parentApp, config, logger, bioDataServer);
  
  CyNetwork network = new CyNetwork(rootGraph, nodeAttributes,
                               edgeAttributes, expressionData);

  this.cyWindow = new CyWindow(globalInstance, network, title, this);
  
  //now show the window
  cyWindow.showWindow();
  
  //plugins need to be loaded here, as many different callers
  //of the constructor expect this behavior
  loadPlugins();
} // ctor

//------------------------------------------------------------------------------
//---------INITIALIZATION METHODS-----------------------------------------------
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
/**
 * Saves the CalculatorCatalog to the file 'vizmap.props' in the user's
 * home directory.
 */
public void saveCalculatorCatalog() {
    getCytoscapeObj().saveCalculatorCatalog();
}
//------------------------------------------------------------------------------
//NOTE: the following two methods are redundant now that the new vizmapper
//controls node labels. They should be removed from CytoscapeWindow and every
//place that calls them, possibly replaced with calls to the vizmapper to set
//the label controller appropriately, but more likely the visual styles should
//specify what to do with the node labels.
/**
 *  loop over all nodes, to assign and display common node name's (rather
 *  than the less human-friendly 'canonicalName').
 *  this assignment is made, in order of preference, from
 *  <ol>
 *    <li> the 'commonName' attribute of the node, if it has already been assigned
 *    <li> the annotation server, if it is non-null, and has a commonName value for this
 *         canonical node name, for this species
 *    <li> if all else fails, the commonName is assigned from the canonicalName
 *  </ol>
 *
 *  after the best commonName has been found, it is displayed as the label of the
 *  node, and is written (perhaps redundantly, if it was already there) into the
 *  commonNode attribute of the node.
 */
public void displayCommonNodeNames ()
{
  Graph2D theGraph = this.getGraph();
  Node [] nodes = theGraph.getNodeArray ();
  GraphObjAttributes nodeAttributes = this.getNodeAttributes();
  String source = "";

  for (int i=0; i < nodes.length; i++) {
    Node node = nodes [i];
    NodeRealizer r = theGraph.getRealizer(node);
    String commonName = r.getLabelText (); 
    String canonicalName = getCanonicalNodeName (node);
    String [] commonNamesFromAttributes = nodeAttributes.getStringArrayValues ("commonName", canonicalName);
    if (commonNamesFromAttributes != null && commonNamesFromAttributes.length > 0) {
      commonName = commonNamesFromAttributes [0];
      source = "from pre-existing node attribute";
      }
    else try {
      String [] synonyms = this.getBioDataServer().getAllCommonNames(getSpecies (node), canonicalName);
      if (synonyms != null && synonyms.length > 0)
          commonName = synonyms [0];
      if (commonName == null || commonName.length() == 0)
        commonName = canonicalName;
      source = " from annotation server";
      } // else try
    catch (Exception exc) {
      commonName = canonicalName;
      source = "from canonicalName";
      }
    nodeAttributes.set ("commonName", canonicalName, commonName);
    r.setLabelText (commonName);
    } // for i

} // displayCommonNodeNames
//------------------------------------------------------------------------------
/**
 *  displayNodeLabels()
 *  attempts to display the hashed graphObjAttribute value
 *  at key "key" as the label on every node.  Special case
 *  if the key is "canonicalName": canonicalName is stored
 *  in a different data structure, so we access it differently.
 */
public void displayNodeLabels (String key)
{
    Graph2D theGraph = this.getGraph();
    Node [] nodes = theGraph.getNodeArray ();
    GraphObjAttributes nodeAttributes = this.getNodeAttributes();

    for (int i=0; i < nodes.length; i++) {
        Node node = nodes [i];
        String canonicalName = getCanonicalNodeName(node);
        String newName = "";
        if(!(key.equals("canonicalName"))) {
            if (nodeAttributes.getClass (key) == "string".getClass ())
               newName = nodeAttributes.getStringValue (key, canonicalName);
            else {
              HashMap attribmap = nodeAttributes.getAttributes(canonicalName);
              Object newObjectWithName  = (Object)attribmap.get(key);
              if(newObjectWithName != null)
                newName = newObjectWithName.toString();
              }
        } // if key is not canonicalName
        else
            newName = canonicalName;
        NodeRealizer r = theGraph.getRealizer(node);
        r.setLabelText (newName);
    } // for i
    
} // displayNodeLabels
//------------------------------------------------------------------------------
protected void assignSpeciesAttributeToAllNodes ()
{
  Node [] nodes = this.getGraph().getNodeArray();
  GraphObjAttributes nodeAttributes = this.getNodeAttributes();
  for (int i=0; i < nodes.length; i++)
    nodeAttributes.set("species", 
                       nodeAttributes.getCanonicalName(nodes[i]), 
                       this.getSpecies(nodes[i]));

} // assignSpeciesAttributeToAllNodes
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
/**
 * Uses the plugin loader to load the appropriate plugins.
 */
public void loadPlugins() {
    PluginLoader pluginLoader
        = new PluginLoader (this.getCyWindow(),
                            this.getConfiguration(),
                            this.getNodeAttributes(),
                            this.getEdgeAttributes());

    pluginLoader.load();
    getLogger().info(pluginLoader.getMessages());

    // add default unselectable "no plugins loaded" if none loaded
    getCyWindow().getCyMenus().refreshOperationsMenu();

    JarLoaderUI jlu = new JarLoaderUI(this.getCyWindow(),
                      this.getCyMenus().getLoadSubMenu() );

}


//------------------------------------------------------------------------------
//----------BASIC SET/GET METHODS-----------------------------------------------
//------------------------------------------------------------------------------

public CyNetwork getNetwork() {return getCyWindow().getNetwork();}
//------------------------------------------------------------------------------
public Graph2D getGraph() {return this.getNetwork().getGraph();}
//------------------------------------------------------------------------------
public void setGraph(Graph2D graph) {
    getCyWindow().setNewGraph(graph, false);//don't do layout on the graph
}
//------------------------------------------------------------------------------
public GraphObjAttributes getNodeAttributes() {
  return this.getNetwork().getNodeAttributes();
}
//------------------------------------------------------------------------------
public void setNodeAttributes(GraphObjAttributes newValue) {
  setNodeAttributes(newValue, false);
  //displayCommonNodeNames (); now vizmapper controls node labels
}
//------------------------------------------------------------------------------
public void setNodeAttributes(GraphObjAttributes newValue, boolean skipAddNameMapping) {
    if (!skipAddNameMapping) {
        //first add all the mappings of existing canonical names of nodes
        GraphObjAttributes oldNodeAttributes =
                this.getNetwork().getNodeAttributes();
        Node[] allNodes = this.getNetwork().getGraph().getNodeArray();
        for (int i=0; i<allNodes.length; i++) {
            String canonicalName = oldNodeAttributes.getCanonicalName(allNodes[i]);
            newValue.addNameMapping(canonicalName, allNodes[i]);
        }
    }
    //now switch to the new node attributes
    this.getNetwork().setNodeAttributes(newValue);
    
    //displayCommonNodeNames (); now vizmapper controls node labels
}
//------------------------------------------------------------------------------
public GraphObjAttributes getEdgeAttributes() {
  return this.getNetwork().getEdgeAttributes();
}
//------------------------------------------------------------------------------
public void setEdgeAttributes(GraphObjAttributes edgeAttributes) {
    this.getNetwork().setEdgeAttributes(edgeAttributes);
}
//------------------------------------------------------------------------------
/**
 * @deprecated The GraphProps data structure is no longer supported and will be
 *             removed in a future version. Use an instance of CyNetwork
 *             and the getNetwork method instead.
 */
public GraphProps getProps() {
    return new GraphProps(this.getNetwork().getGraph(),
                          this.getNetwork().getNodeAttributes(),
                          this.getNetwork().getNodeAttributes() );
}
//------------------------------------------------------------------------------
/**
 * @deprecated The ExpressionData object holds its own filename. Use
 *             getNetwork().getExpressionData().getFilename() instead.
 */
public String getExpressionDataFileName() {
    return expressionDataFilename;
}
//------------------------------------------------------------------------------
public ExpressionData getExpressionData() {
    return this.getNetwork().getExpressionData();
}
//------------------------------------------------------------------------------
public void setExpressionData(ExpressionData expData) {
    this.getNetwork().setExpressionData(expData);
    if (expData != null) {expressionDataFilename = expData.getFileName();}
}
//------------------------------------------------------------------------------
/**
 * @deprecated The ExpressionData object holds its own filename. Use
 *             setExpressionData(ExpressionData) instead.
 */
public void setExpressionData(ExpressionData expData, String expressionDataFilename) {
    this.getNetwork().setExpressionData(expData);
    this.expressionDataFilename = expressionDataFilename;
}
//------------------------------------------------------------------------------
public CytoscapeObj getCytoscapeObj() {return getCyWindow().getCytoscapeObj();}
//------------------------------------------------------------------------------
public cytoscape getParentApp() {return getCytoscapeObj().getParentApp();}
//------------------------------------------------------------------------------
public Logger getLogger() {return getCytoscapeObj().getLogger();}
//------------------------------------------------------------------------------
public CytoscapeConfig getConfiguration() {
    return getCytoscapeObj().getConfiguration();
}
//------------------------------------------------------------------------------
public BioDataServer getBioDataServer() {
    return getCytoscapeObj().getBioDataServer();
}
//------------------------------------------------------------------------------
public void setBioDataServer(BioDataServer newBioDataServer) {
    getCytoscapeObj().setBioDataServer(newBioDataServer);
}
//------------------------------------------------------------------------------
public String getDefaultSpecies() {
    if (this.defaultSpecies != null) {return this.defaultSpecies;}
    String species = getConfiguration().getDefaultSpeciesName();
    if (species != null) {return species;}
    
    species = getConfiguration().getProperties().getProperty("species", "unknown");
    return species;
}
//------------------------------------------------------------------------------
public void setDefaultSpecies(String newValue) {defaultSpecies = newValue;}
//------------------------------------------------------------------------------
public CyWindow getCyWindow() {return cyWindow;}
//------------------------------------------------------------------------------
public Graph2DView getGraphView() {
    return getCyWindow().getGraphView();
}
//------------------------------------------------------------------------------
public VisualMappingManager getVizMapManager() {
    return getCyWindow().getVizMapManager();
}
//------------------------------------------------------------------------------
public VizMapUI getVizMapUI() {return getCyWindow().getVizMapUI();}
//------------------------------------------------------------------------------
public Layouter getLayouter() {return getCyWindow().getLayouter();}
//------------------------------------------------------------------------------
public void setLayouter(Layouter newLayouter) {
    getCyWindow().setLayouter(newLayouter);
}
//------------------------------------------------------------------------------
/**
 * Returns the window's current UndoManager.
 *
 * added by dramage 2002-08-22
 */
public CytoscapeUndoManager getUndoManager() {
    return getCyWindow().getUndoManager();
}
//------------------------------------------------------------------------------
public UndoableGraphHider getGraphHider() {
    return getCyWindow().getGraphHider();
}
//------------------------------------------------------------------------------
/**  Cytoscape keeps track of the current directory being browsed;
 *   it is recommended that all methods with file dialogs use
 *   getCurrentDirectory() to begin browsing for files.
 */
public File getCurrentDirectory() {
    return getCytoscapeObj().getCurrentDirectory();
}
//------------------------------------------------------------------------------
/**  Cytoscape keeps track of the current directory being browsed;
 *   it is recommended that all methods with file dialogs use
 *   setCurrentDirectory(dir) to record the destination directory for
 *   future browsing.
 */
public void setCurrentDirectory(File dir) {
    getCytoscapeObj().setCurrentDirectory(dir);
}
//------------------------------------------------------------------------------
public JFrame getMainFrame() {
    return getCyWindow().getMainFrame();
}
//------------------------------------------------------------------------------
public String getWindowTitle() {
    return getCyWindow().getWindowTitle();
}
//------------------------------------------------------------------------------
public void setWindowTitle(String newTitle) {
    getCyWindow().setWindowTitle(newTitle);
}
//------------------------------------------------------------------------------
public CyMenus getCyMenus() {
    return getCyWindow().getCyMenus();
}
//------------------------------------------------------------------------------
public JMenuBar getMenuBar() {
    return getCyWindow().getCyMenus().getMenuBar();
}
//------------------------------------------------------------------------------
public JToolBar getToolBar() {
    return getCyWindow().getCyMenus().getToolBar();
}
//------------------------------------------------------------------------------
public JMenu getSelectMenu() {
    return getCyWindow().getCyMenus().getSelectMenu();
}
//------------------------------------------------------------------------------
public JMenu getLayoutMenu() {
    return getCyWindow().getCyMenus().getLayoutMenu();
}
//------------------------------------------------------------------------------
public JMenu getVizMenu() {
    return getCyWindow().getCyMenus().getVizMenu();
}
//------------------------------------------------------------------------------
public JMenu getOperationsMenu() {
    return getCyWindow().getCyMenus().getOperationsMenu();
}

//------------------------------------------------------------------------------
//-------------ACTION METHODS---------------------------------------------------
//------------------------------------------------------------------------------


public void setInteractivity (boolean newState) {
    getCyWindow().setInteractivity(newState);
} // setInteractivity
//------------------------------------------------------------------------------
public void redrawGraph() {
    // Do not do a layout and apply appearances
    redrawGraph(false,true);
}
//------------------------------------------------------------------------------
public void redrawGraph(boolean doLayout) {
    // apply appearances by default
    redrawGraph(doLayout, true);
}
//------------------------------------------------------------------------------
// Added by iliana
public void redrawGraph (boolean doLayout, boolean applyAppearances) {
    getCyWindow().redrawGraph(doLayout, applyAppearances);
}
//------------------------------------------------------------------------------
public void applyLayout(boolean animated) {
    getCyWindow().applyLayout(animated);
} // applyLayout
//------------------------------------------------------------------------------
// applyLayoutSelection
//
// apply layout, but only on currently selected nodes
public void applyLayoutSelection() {
    getCyWindow().applyLayoutSelection();
}
//------------------------------------------------------------------------------
public void updateStatusText() {
    updateStatusText(0,0);
}
//------------------------------------------------------------------------------
/**
 * Resets the info label status bar text with the current number of
 * nodes, edges, selected nodes, and selected edges.
 *
 * The Adjust fields is an ugly hack that is necessary because of a
 * yFiles API quirk.  See selectionStateChanged() for details.
 *
 * added by dramage 2002-08-16
 */
public void updateStatusText(int nodeAdjust, int edgeAdjust) {
    getCyWindow().updateStatusText(nodeAdjust, edgeAdjust);
}
//------------------------------------------------------------------------------
/**
 * Updates the undoMenuItem and redoMenuItem enabled status depending
 * on the number of available undo and redo actions
 *
 * added by dramage 2002-08-21
 */
public void updateUndoRedoMenuItemStatus() {
    getCyWindow().getCyMenus().updateUndoRedoMenuItemStatus();
}
//------------------------------------------------------------------------------
public boolean loadGML(String filename) {
    Graph2D newGraph = FileReadingAbstractions.loadGMLBasic(filename,
                                               this.getEdgeAttributes(),
					       this.getConfiguration().getCanonicalize());
    if (newGraph == null) {return false;}//couldn't read the graph
    
    //set graph after initializing data attributes
    //setGraph (newGraph);//sets class variable graph to reference newGraph object
    FileReadingAbstractions.initAttribs (this.getBioDataServer(),
                                         this.getDefaultSpecies(),
                                         this.getConfiguration(),
                                         newGraph,
                                         this.getNodeAttributes(),
                                         this.getEdgeAttributes());
    //vizmapper now controls node labels
    //displayCommonNodeNames (); // fills in canonical name for blank common names
    geometryFilename = filename;
    getCyWindow().setWindowTitle(filename);
    //loadPlugins();  //don't reload plugins
    //displayNewGraph (false);
    getCyWindow().setNewGraph(newGraph, false);
    return true;

} // loadGML
//------------------------------------------------------------------------------
public void loadInteraction(String filename) {
  
  if (this.getConfiguration().isYFiles()) {
	  System.out.println("Calling FileReadingAbstractions.loadIntrBasic...");
	  Graph2D newGraph =
		    FileReadingAbstractions.loadIntrBasic (this.getBioDataServer(), 
							   this.getDefaultSpecies(), 
							   filename,
							   this.getEdgeAttributes(), 
							   this.getConfiguration().getCanonicalize());
	  
	  
	  
	  FileReadingAbstractions.initAttribs (this.getBioDataServer(),
					       this.getDefaultSpecies(),
					       this.getConfiguration(),
					       newGraph, //this.getGraph() == null !
					       this.getNodeAttributes(),
					       this.getEdgeAttributes());
	  //vizmapper now controls node labels
	  //displayCommonNodeNames (); // fills in canonical name for blank common names
	  geometryFilename = null;
	  getCyWindow().setWindowTitle(filename);
	  getCyWindow().setNewGraph(newGraph, true);// set the new graph and do a layout
	  //loadPlugins();  //don't reload plugins
	  //displayNewGraph (true);
  }
  else {
	  System.out.println("Calling FileReadingAbstractions.loadInteractionsBasic...");
	  RootGraph newRootGraph = FileReadingAbstractions.loadInteractionsBasic(this.getBioDataServer(), 
							   this.getDefaultSpecies(), 
							   filename,
							   this.getEdgeAttributes(), 
							   this.getConfiguration().getCanonicalize());
	 					   
	  FileReadingAbstractions.initAttributes(this.getBioDataServer(),
					       this.getDefaultSpecies(),
					       this.getConfiguration(),
					       newRootGraph, //this.getGraph() == null !
					       this.getNodeAttributes(),
					       this.getEdgeAttributes());
	  geometryFilename = null;
	  getCyWindow().setWindowTitle(filename);
	  CyNetwork newNetwork = new CyNetwork(newRootGraph, this.getNodeAttributes(), this.getEdgeAttributes(), null);
	  getCyWindow().setNewNetwork(newNetwork);
  }
} // loadInteraction
//------------------------------------------------------------------------------
/**
 * Load the named expression data filename.
 *
 * added by dramage 2002-08-21
 * modified by owen 2003-03-03
 * modified by amarkiel 2003-04-09 to check for a valid read
 * modified by iliana 2003-05-06 made it public (some plug-ins may want to reload expression data)
 */
public boolean loadExpressionData (String filename) {
    ExpressionData newData = new ExpressionData();
    boolean validLoad = newData.loadData(filename);
    if (validLoad) {
        setExpressionData(newData, filename);
    }
    return validLoad;
}
//------------------------------------------------------------------------------
/**
 *  create a new CytoscapeWindow with an exact copy of the current window,
 *  nodes, edges, and all attributes.  <p>
 *  as a temporary expedient, this method piggybacks upon the already
 *  existing and tested 'SelectedSubGraphFactory' class, by first selecting all
 *  nodes in the current window and then invoking the factory.  when time
 *  permits, the two stages (select, clone) should be refactored into two 
 *  independent operations,  probably by creating a GraphFactory class, which
 *  clones nodes, edges, and attributes.
 */
public void cloneWindow ()
{
  //save the vizmapper
  saveCalculatorCatalog();
  Graph2D currentGraph = this.getNetwork().getGraph();
  Node [] nodes = currentGraph.getNodeArray();
  for (int i=0; i < nodes.length; i++) {
    currentGraph.setSelected(nodes[i], true);
  }
  SelectedSubGraphFactory factory = new SelectedSubGraphFactory (currentGraph,
                                    this.getNetwork().getNodeAttributes(),
                                    this.getNetwork().getEdgeAttributes() );
  Graph2D subGraph = factory.getSubGraph();
  GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
  GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();
  currentGraph.unselectAll();

  String title = "selection";
  try {
    boolean requestFreshLayout = true;
    CytoscapeWindow newWindow =
        new CytoscapeWindow(getParentApp(), getConfiguration(),
                            getLogger(), subGraph, getNetwork().getExpressionData(), 
                            getBioDataServer(), newNodeAttributes, newEdgeAttributes, 
                            "dataSourceName", getExpressionDataFileName(), title, 
                            requestFreshLayout);
    }
  catch (Exception e00) {
    System.err.println("exception when creating new window");
    e00.printStackTrace();
    }

} // cloneWindow
//------------------------------------------------------------------------------
public void setPopupMode(PopupMode newMode) {
    getCyWindow().setPopupMode(newMode);
}
//------------------------------------------------------------------------------
public void switchToEditMode() {
    getCyWindow().switchToEditMode();
}
//------------------------------------------------------------------------------
public void switchToReadOnlyMode() {
    getCyWindow().switchToReadOnlyMode();
}

//------------------------------------------------------------------------------
//-----------LISTENER METHODS---------------------------------------------------
//------------------------------------------------------------------------------

public void windowStateChanged (WindowEvent e)
{
  this.getLogger().info ("--- windowStateChanged: " + e);
}
//------------------------------------------------------------------------------
/**
 * This function is called as part of the Graph2DSelectionListener
 * interface.  When the selection status of the graph changes, this
 * function calls updateStatusText to reflect the change.
 *
 * There is a quirk with yFiles that causes this function to be called
 * *just before* the selection/deselection actually occurs.  That
 * means we must adjust the status text appropriately to reflect the
 * coming change.
 */
public void selectionStateChanged(Graph2DSelectionEvent e) {
	
    if (e.isEdgeSelection()) {
        updateStatusText(0,
                         (this.getGraph().isSelected((Edge)e.getSubject()) ? -1 : +1));
    } else if (e.isNodeSelection()) {
        updateStatusText((this.getGraph().isSelected((Node)e.getSubject()) ? -1 : +1),
                         0);
    }
}
//------------------------------------------------------------------------------
public void onGraph2DSelectionEvent(y.view.Graph2DSelectionEvent e) {
	updateStatusText();
}

//------------------------------------------------------------------------------
//--------HELPER METHODS  (to be removed)---------------------------------------
//------------------------------------------------------------------------------
public String getCanonicalNodeName(Node node)
{
  return this.getNodeAttributes().getCanonicalName (node);

} // getCanonicalNodeName
//------------------------------------------------------------------------------
public String getSpecies(Node node)
{
  String canonicalName = this.getNodeAttributes().getCanonicalName (node);
  String species = this.getNodeAttributes().getStringValue("species", canonicalName);
  if (species != null) {
    return species;
  } else {
    return this.getDefaultSpecies();
  }

} // getSpecies
//------------------------------------------------------------------------------
public String[] getAllSpecies()
{
  Vector list = new Vector();
  Node [] nodes = this.getGraph().getNodeArray();
  for (int i=0; i < nodes.length; i++) {
    String species = getSpecies(nodes [i]);
    if (!list.contains(species) && species != null)
      list.add(species);
    } // for i

  return (String[])list.toArray(new String[0]);

} // getAllSpecies
//------------------------------------------------------------------------------
public Node getNode (String canonicalNodeName)
{
  GraphObjAttributes nodeAttributes = this.getNodeAttributes();
  Node [] nodes = this.getGraph().getNodeArray();
  for (int i=0; i < nodes.length; i++) {
    Node node = nodes [i];
    String canonicalName = nodeAttributes.getCanonicalName(node);
    getLogger().warning (" -- checking " + canonicalNodeName + " against " + canonicalName + " " + node);
    if (canonicalNodeName.equals (canonicalName)) 
      return node;
    }

  return null;
  
} // getNode
//------------------------------------------------------------------------------
/**
 * hide every node except those explicitly named.  canonical node names must
 * be used.
 */
protected void showNodesByName (String [] nodeNames)
{
    // not sure if this is any faster. - owo 2002 10 03

    // first show all nodes
    UndoableGraphHider theHider = this.getGraphHider();
    theHider.unhideAll();
    Node [] nodes = this.getGraph().getNodeArray();
    
    // construct a hash of the nodeNames
    Hashtable namedNodes = new Hashtable();
    for (int n=0; n < nodeNames.length; n++) {
        nodeNames[n].toLowerCase();
        namedNodes.put(nodeNames[n],Boolean.TRUE);
    }
    
    // if a node in the graph isn't in the hash, hide it.
    for (int i=0; i < nodes.length; i++) {
        String graphNodeName = getCanonicalNodeName(nodes[i]);
        graphNodeName.toLowerCase();
        Boolean select = (Boolean) namedNodes.get(graphNodeName);
        if(select==null) {
            theHider.hide(nodes[i]);
        }
    }
    // no need to do a layout or apply apps
    this.redrawGraph(false,false);

    // old code follows:
    //
    //Graph2D g = graphView.getGraph2D ();
    //graphHider.unhideAll ();
    //Node [] nodes = graphView.getGraph2D().getNodeArray();
    //for (int i=0; i < nodes.length; i++) {
    //boolean matched = false;
    //String graphNodeName = getCanonicalNodeName (nodes [i]);
    //for (int n=0; n < nodeNames.length; n++) {
    //if (nodeNames [n].equalsIgnoreCase (graphNodeName)) {
    //matched = true;
    //break;
    //} // if equals
    //} // for n
    //if (!matched) 
    //graphHider.hide (nodes [i]);
    //} // for i
    //redrawGraph ();
} // showNodesByName
//------------------------------------------------------------------------------
/**
 * a Vector version of showNodesByName
 */ 
public void showNodesByName (Vector uniqueNodeNames)
{
  showNodesByName ((String []) uniqueNodeNames.toArray (new String [0]));

} // showNodesByName (Vector)
//------------------------------------------------------------------------------
/**
 * Returns an array of node names in nodeNames that were not selected.
 */
public String [] selectNodesByName (String [] nodeNames){
    boolean clearAllSelectionsFirst = true;
    return selectNodesByName (nodeNames, clearAllSelectionsFirst);
}
//------------------------------------------------------------------------------
/**
 * Returns an array of node names that were not selected.
 */
public String [] selectNodesByName (String [] nodeNames, boolean clearAllSelectionsFirst)
{
    // not sure if this is any faster. - owo 2002 10 03
  
    Graph2D theGraph = this.getGraph();
    Node [] nodes = theGraph.getNodeArray();
    int numSelectedNodes = 0;
    Hashtable selectedNodes = new Hashtable();
   
    // construct a hash of the nodeNames
    Hashtable namedNodes = new Hashtable();
    for (int n=0; n < nodeNames.length; n++) {
        nodeNames[n].toLowerCase();
        namedNodes.put(nodeNames[n],Boolean.TRUE);
        selectedNodes.put(nodeNames[n],Boolean.FALSE);
    }

    // if a node in the graph is in the hash, select it;
    // if not, and clearAllSelectionsFirst is true, unselect it.
    for (int i=0; i < nodes.length; i++) {
        String graphNodeName = getCanonicalNodeName(nodes[i]);
        NodeRealizer nodeRealizer = theGraph.getRealizer(nodes[i]);
        graphNodeName.toLowerCase();
        Boolean select = (Boolean) namedNodes.get(graphNodeName);
        if(select != null) {
          numSelectedNodes++;
          nodeRealizer.setSelected(true);
          selectedNodes.put(graphNodeName,Boolean.TRUE);
        }else{
          if (clearAllSelectionsFirst) {
            nodeRealizer.setSelected(false);
          }
        }
    }
    
    // Find out which nodes were not selected.
    ArrayList notSelectedNodes = new ArrayList();
    if(numSelectedNodes < nodeNames.length){
      // Not all nodes in the given node list were found.
      Set keys = selectedNodes.keySet();
      Iterator it = keys.iterator();
      while(it.hasNext()){
        String nodeName = (String)it.next();
        if((Boolean)selectedNodes.get(nodeName) == Boolean.FALSE){
          notSelectedNodes.add(nodeName);
        }
      }
    }
    // no need to do a layout or reapply apps.
    this.redrawGraph(false,false);
    return (String[])notSelectedNodes.toArray(new String[notSelectedNodes.size()]);
  // old code follows
  //
  //Graph2D g = graphView.getGraph2D();
  //Node [] nodes = graphView.getGraph2D().getNodeArray();
  //for (int i=0; i < nodes.length; i++) {
  //String graphNodeName = getCanonicalNodeName (nodes [i]);
  //NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
  //boolean matched = false;
  //for (int n=0; n < nodeNames.length; n++) {
  //if (nodeNames [n].equalsIgnoreCase (graphNodeName)) {
  //matched = true;
  //break;
  //} // if matched
  //} // for n
  //if (clearAllSelectionsFirst && !matched)
  //nodeRealizer.setSelected (false);
  //else if (matched)
  //nodeRealizer.setSelected (true);
  //} // for i
  //redrawGraph ();
} // selectNodesByName
//------------------------------------------------------------------------------
// added by jtwang 30 Sep 2002
public void selectEdges (Edge[] edgesToSelect, boolean clearAllSelectionsFirst) {
    Graph2D theGraph = this.getGraph();
    if (clearAllSelectionsFirst)
        theGraph.unselectEdges();
    
    for (int i = 0; i < edgesToSelect.length; i++) {
        EdgeRealizer eR = theGraph.getRealizer(edgesToSelect[i]);
        eR.setSelected(true);
    }

    // no need to apply layout or apps.
    this.redrawGraph(false,false);
}

/**
 *  quadratic (in)efficiency:  make this smarter (pshannon, 24 may 2002)

    fixed by jtwang to be linear 30 Sep 2002
 */
public void selectNodes (Node [] nodesToSelect, boolean clearAllSelectionsFirst)
{
  Graph2D theGraph = this.getGraph();
  if (clearAllSelectionsFirst)
      theGraph.unselectNodes();

  for (int i = 0; i < nodesToSelect.length; i++) {
      NodeRealizer nR = theGraph.getRealizer(nodesToSelect[i]);
      nR.setSelected(true);
  }

  /* Replaced by jtwang 30 Sep 2002
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
    boolean matched = false;
    for (int n=0; n < nodesToSelect.length; n++) {
      if (nodes [i] == nodesToSelect [n]) {
        matched = true;
        break;
        } // if matched
      } // for n
    if (clearAllSelectionsFirst && !matched)
      nodeRealizer.setSelected (false);
    else if (matched)
      nodeRealizer.setSelected (true);
    } // for i
  */

  // no need to apply layout or apps.
  this.redrawGraph(false, false);

} // selectNodesByName
//-----------------------------------------------------------------------------
public void deselectAllNodes(boolean redrawGraph){
    if(redrawGraph){
        deselectAllNodes();
    }else{
        // fixed by jtwang 30 Sep 2002
        this.getGraph().unselectNodes();
        /*
        //Graph2D g = graphView.getGraph2D();
        //Node [] nodes = graphView.getGraph2D().getNodeArray();
        Node [] nodes = graph.getNodeArray();
        for (int i=0; i < nodes.length; i++) {
            //NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
            //nodeRealizer.setSelected (false);
            this.graph.setSelected(nodes[i],false);
            } // for i */
    }
    
}
//------------------------------------------------------------------------------
public void deselectAllNodes ()
{
    // fixed by jtwang 30 Sep 2002

    this.getGraph().unselectNodes();

    /*
  Graph2D g = graphView.getGraph2D();
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
    nodeRealizer.setSelected (false);
    } // for i
    */
    // no need to layout or reapply apps
  this.redrawGraph(false, false);

} // deselectAllNodes
//------------------------------------------------------------------------------
/**
 * @deprecated Moved to CyNetworkUtilities.selectNodesStartingWith, with the
 * network and bioDataServer as arguments.
 */
public void selectNodesStartingWith (String key) {
    CyNetworkUtilities.selectNodesStartingWith(this.getNetwork(), key, this.getCytoscapeObj(), this.getCyWindow());
}
//------------------------------------------------------------------------------
/**
 * @deprecated This method is not used anywhere.
 */
protected void additionallySelectNodesMatching (String key)
{
  this.setInteractivity(false);
  key = key.toLowerCase ();
  Graph2D theGraph = this.getGraph();
  Node[] nodes = theGraph.getNodeArray();
  BioDataServer theBioDataServer = this.getBioDataServer();

  for (int i=0; i < nodes.length; i++) {
    String nodeName = theGraph.getLabelText(nodes[i]);
    boolean matched = false;
    if (nodeName.toLowerCase().equalsIgnoreCase(key))
      matched = true;
    else if (theBioDataServer != null) {
      try {
        String [] synonyms = theBioDataServer.getAllCommonNames(getSpecies(nodes[i]), nodeName);
        for (int s=0; s < synonyms.length; s++)
          if (synonyms [s].equalsIgnoreCase(key)) {
            matched = true;
            break;
         } // for s
       }
      catch (Exception ignoreForNow) {;}
      } // else if: checking synonyms
    if(matched)
        setNodeSelected(nodes [i], true);
    } // for i

  this.setInteractivity(true);
  this.redrawGraph(false, false);

} // selectDisplyToNodesStartingWith ...
//------------------------------------------------------------------------------
/**
 * @deprecated This method is no longer used, and has an awkward implementation of
 * searching for name matches. Instead, use the static methods provided by the
 * cytoscape.data.Semantics class to search for name matches.
 */
public String findCanonicalName(String key) {
    String canonicalName = key;
    BioDataServer theBioDataServer = this.getBioDataServer();
    if (theBioDataServer != null) {
        try {
            String [] synonyms = theBioDataServer.getAllCommonNames(getDefaultSpecies(), key);
            for (int s = 0; s < synonyms.length; s++) {
                String sname = synonyms[s];
                if (sname.equalsIgnoreCase(key)) {
                    canonicalName = sname;
                    break;
                }
            }
        } catch (Exception ignoreForNow) {;}
    }
    return canonicalName;
} // else if: checking synonyms
//------------------------------------------------------------------------------
/**
 * @deprecated Use yFiles built-in method graph.setSelected(node, boolean) instead.
 */
protected void setNodeSelected (Node node, boolean visible)
{
    NodeRealizer r = this.getGraph().getRealizer(node);
    r.setSelected(visible);
    
} // setNodeSelected
//------------------------------------------------------------------------------
// 
/**
 * @deprecated This method hardcodes a filename into Cytoscape, and the method
 * it delegates to has been moved. Use CyNetworkUtilities.saveVisibleNodeNames
 * instead and provide a suitable filename as an argument.
 *
 * this is public so activePaths can get at it;
 * active paths depends on saveVisibleNodeNames () to save state periodically.
 */
public boolean saveVisibleNodeNames () { 
    return saveVisibleNodeNames("visibleNodes.txt"); 
}
//------------------------------------------------------------------------------
/**
 * @deprecated Moved to CyNetworkUtilities.saveVisibleNodeNames, with the
 * network as an argument.
 */
public boolean saveVisibleNodeNames (String filename) {
    return CyNetworkUtilities.saveVisibleNodeNames(this.getNetwork(), filename);
} // saveVisibleNodeNames
//------------------------------------------------------------------------------
/**
 * @deprecated Moved to CyNetworkUtilities.saveSelectedNodeNames, with the
 * network as an argument.
 */
public boolean saveSelectedNodeNames(String filename) {
    return CyNetworkUtilities.saveSelectedNodeNames(this.getCyWindow(), this.getNetwork(), filename);
} // saveSelectedNodeNames
//------------------------------------------------------------------------------
public HashMap configureNewNode (Node node)
{
  OptionHandler options = new OptionHandler ("New Node");
  GraphObjAttributes nodeAttributes = this.getNodeAttributes();

  String [] attributeNames = nodeAttributes.getAttributeNames ();

  if (attributeNames.length == 0) {
    options.addComment ("commonName is required; canonicalName is optional and defaults to commonName");
    options.addString ("commonName", "");
    options.addString ("canonicalName", "");
    }
  else for (int i=0; i < attributeNames.length; i++) {
    String attributeName = attributeNames [i];
    Class attributeClass = nodeAttributes.getClass (attributeName);
    if (attributeClass.equals ("string".getClass ()))
      options.addString (attributeName, "");
    else if (attributeClass.equals (new Double (0.0).getClass ()))
      options.addDouble (attributeName, 0);
    else if (attributeClass.equals (new Integer (0).getClass ()))
      options.addInt (attributeName, 0);
    } // else/for i
  
  options.showEditor ();

  HashMap result = new HashMap ();

  if (attributeNames.length == 0) {
    result.put ("commonName", (String) options.get ("commonName"));
    result.put ("canonicalName", (String) options.get ("canonicalName"));
    }
  else for (int i=0; i < attributeNames.length; i++) {
    String attributeName = attributeNames [i];
    Class attributeClass = nodeAttributes.getClass (attributeName);
    if (attributeClass.equals ("string".getClass ()))
       result.put (attributeName, (String) options.get (attributeName));
    else if (attributeClass.equals (new Double (0.0).getClass ()))
       result.put (attributeName, (Double) options.get (attributeName));
    else if (attributeClass.equals (new Integer (0).getClass ()))
       result.put (attributeName, (Integer) options.get (attributeName));
    } // else/for i

  return result;

} // configureNode
//----------------------------------------------------------------------------------------
/**
 * @deprecated Moved to Semantics.getInteractionTypes, with the
 * network as an argument.
 */
public String[] getInteractionTypes() {
    return Semantics.getInteractionTypes(this.getNetwork());
}
//------------------------------------------------------------------------------
} // CytoscapeWindow

