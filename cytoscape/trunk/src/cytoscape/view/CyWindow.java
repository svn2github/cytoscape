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
 //-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.view;
//-------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import y.base.*;
import y.view.*;

import y.layout.Layouter;
import y.layout.GraphLayout;
import y.layout.circular.CircularLayouter;
import y.layout.hierarchic.*;
import y.layout.organic.OrganicLayouter;
import y.layout.random.RandomLayouter;


//imports for giny graph library support
import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.util.SpringEmbeddedLayouter;
import giny.view.GraphView;
import giny.view.EdgeView;
import giny.view.NodeView;
import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;

import phoebe.PGraphView;
//import phoebe.util.*;
//import phoebe.event.*;

import cytoscape.*;
import cytoscape.graphutil.*;
import cytoscape.data.*;
//import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.actions.SetVisualPropertiesAction;
import cytoscape.undo.UndoManager;
import cytoscape.undo.CytoscapeUndoManager;
import cytoscape.undo.EmptyUndoManager;
import cytoscape.undo.UndoableGraphHider;
import cytoscape.browsers.NodeBrowsingMode;
import cytoscape.layout.EmbeddedLayouter;
import cytoscape.layout.Subgraph;
//-------------------------------------------------------------------------
/**
 * This class represents a visible window displaying a network. It includes
 * all of the UI components and the the graph view.
 */
public class CyWindow extends JPanel implements Graph2DSelectionListener, GraphViewChangeListener,CyNetworkListener, NetworkView {
        
    protected static final int DEFAULT_WIDTH = 700;
    protected static final int DEFAULT_HEIGHT = 700;
    Paint DEFAULT_PAINT = Color.yellow;
    

    protected CytoscapeWindow cytoscapeWindow;
    protected CytoscapeObj globalInstance;
    protected CyNetwork network;
   
    protected JFrame mainFrame;
    protected CyMenus cyMenus;
    protected JLabel infoLabel;
    
    protected String defaultWindowTitle = "Cytoscape " + CytoscapeVersion.version + "     ";
    protected String windowTitle;
    
    protected Layouter layouter;
    protected Graph2DView graphView;
    protected GraphView  view;
    protected Component display;
  /**
   * An object that keeps the <code>RootGraph</code>'s <code>GraphPerspective</code>
   * synchronized with its <code>GraphView</code>.
   */
  protected GraphViewController graphViewController;
    
    protected ViewMode editGraphMode;
    protected ViewMode readOnlyGraphMode;
    protected ViewMode currentGraphMode;
    protected PopupMode nodeAttributesPopupMode;
    protected PopupMode currentPopupMode;
    protected boolean viewModesInstalled = false;
    protected boolean currentInteractivityState = false;
    protected boolean isYFiles = true;
    
    /** contains mappings from network properties and attributes to visual
     *  properties such as the sizes and colors of nodes and edges.
     */
    protected VisualMappingManager vizMapper;
    
    /** user interface to the 
     *  {@link cytoscape.visual.VisualMappingManager VisualMappingManager}
     *  {@link #vizMapper vizMapper}.
     */
    protected VizMapUI vizMapUI;
    
    //save constructor variable here to draw graph later
    protected boolean windowDisplayed = false;
    
    // added by dramage 2002-08-21
    protected CytoscapeUndoManager undoManager;
    protected UndoableGraphHider graphHider;
    
//------------------------------------------------------------------------------
/**
 * Main constructor.
 *
 * @param globalInstance  contains globally unique objects such as
 *                        the CytoscapeConfig and bioDataServer
 * @param network  the network to be displayed in this window
 * @param windowTitle  the frame title; a default value is used
 *                     if this is null
 */
public CyWindow(CytoscapeObj globalInstance, CyNetwork network, String title) {
    doInit(globalInstance, network, title);
    cytoscapeWindow = new CytoscapeWindow(this);
    //load plugins after the cytoscapeWindow constructor finishes so that the
    //plugin loader can get a valid cytoscapeWindow reference from this object
    cytoscapeWindow.loadPlugins();
}
//------------------------------------------------------------------------------
/**
 * Secondary constructor. This is only called from a CytoscapeWindow constructor,
 * when that constructor is called first. In this case this object doesn't need
 * to construct a new CytoscapeWindow.
 */
public CyWindow(CytoscapeObj globalInstance, CyNetwork network, String title,
                CytoscapeWindow cytoscapeWindow) {
    this.cytoscapeWindow = cytoscapeWindow;
    doInit(globalInstance, network, title);
}
//------------------------------------------------------------------------------
protected void doInit(CytoscapeObj globalInstance, CyNetwork network, String title) {
    this.globalInstance = globalInstance;
    this.network = network;
    this.isYFiles = globalInstance.getConfiguration().isYFiles();
     
    if (title == null) {
        this.windowTitle = defaultWindowTitle;
    } else {
        this.windowTitle = defaultWindowTitle + title;
    }
    if (isYFiles) {
	    editGraphMode = new EditGraphMode(this);
	    readOnlyGraphMode = new ReadOnlyGraphMode(this);
	    currentGraphMode = readOnlyGraphMode;
	    Properties configProps = globalInstance.getConfiguration().getProperties();
	    nodeAttributesPopupMode = new NodeBrowsingMode(configProps, this);
	    currentPopupMode = nodeAttributesPopupMode;
	    
	    initializeWidgets(); //initializes the basic window objects
	    setInitialLayouter(); //defines an initial layout algorithm
	    connectGraphAndView(); //links the graph object with it's view
	    attachGraphListeners(); //attaches various graph listeners
    }
    
    else {
	    // using giny graph library
	    //@@@@@@ what to do with graph modes in giny?
	    System.out.println ( "Using giny library, initializing" ) ;
	    initialize();
	    attachGraphViewListener();
	    
    }
	   
    //add a listener to save the visual mapping catalog on exit
    //this should eventually be replaced by a method in Cytoscape.java itself
    //to save the catalog just before exiting the program
    final CytoscapeObj theCytoscapeObj = globalInstance;
    mainFrame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
            theCytoscapeObj.saveCalculatorCatalog();
        }
    }); 
    //add the parent app as a listener, to manage the session when this window closes
    //is this strictly necessary, since cytoscape.java listens for
    //WindowOpened events? -AM 2003/06/24
    mainFrame.addWindowListener( globalInstance.getParentApp() );
}


//------------------------------------------------------------------------------
//---------INITIALIZATION METHODS-----------------------------------------------
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
/**
 * Creates the basic window objects such as the main frame and
 * the graph view object.
 */
protected void initializeWidgets() {
    setLayout( new BorderLayout() );  
    this.graphView = new Graph2DView();
    
    // added owo 2002.04.18
    DefaultBackgroundRenderer renderer = new DefaultBackgroundRenderer(graphView);
    graphView.setBackgroundRenderer(renderer);
    
    add(graphView, BorderLayout.CENTER);
    graphView.setPreferredSize(new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT));
    
    this.infoLabel = new JLabel();
    add(infoLabel, BorderLayout.SOUTH);
    
    this.mainFrame = new JFrame(windowTitle);
    
    mainFrame.setContentPane(this);
    //create the menu objects
    this.cyMenus = new CyMenus(this);
    cyMenus.initializeMenus();
    add(cyMenus.getToolBar(), BorderLayout.NORTH);
    mainFrame.setJMenuBar(cyMenus.getMenuBar());
    //this does nothing if undo is disabled
    cyMenus.updateUndoRedoMenuItemStatus();
    // load vizmapper after menus are done and graph is available
    //vizmapper is now Giny only
    //loadVizMapper();
} // initializeWidgets


//------------------------------------------------------------------------------
/** 
* Creates the basic window objects such as the main frame and
* the graph view object and initializes graph and graph view using giny
*/
protected void initialize() {
    getNetwork().addCyNetworkListener(this);
	GraphPerspective gp = network.getGraphPerspective();
	setLayout( new BorderLayout() );  
	if (gp != null) {
		updateGraphView();
		//applyLayout(); do not layout by the default, too slow
		fitGraphView();
	}
	else
	{
	    //no graph specified yet what to do with giny graph view?
	    //left null for now
	   // setLayout( new BorderLayout() );  
	    //this.view = new PGraphView();
    	   // add(graphView, BorderLayout.CENTER);
	   this.setBackground(Color.WHITE);
	   display = new JPanel();
           add(display, BorderLayout.CENTER);
		
	}	
	
	///*setLayout( new BorderLayout() );
	this.infoLabel = new JLabel();
	this.infoLabel.setBackground(Color.BLUE);
    add(infoLabel, BorderLayout.SOUTH);
    updateStatusLabel(0, 0);
    this.mainFrame = new JFrame(windowTitle);
    
    mainFrame.setContentPane(this);
    //create the menu objects
    this.cyMenus = new CyMenus(this);
    cyMenus.initializeMenus();
    add(cyMenus.getToolBar(), BorderLayout.NORTH);
    mainFrame.setJMenuBar(cyMenus.getMenuBar());
    //this does nothing if undo is disabled
    cyMenus.updateUndoRedoMenuItemStatus();
    // load vizmapper after menus are done and graph is available
    loadVizMapper();

	
}
//------------------------------------------------------------------------------
/**
* initialize the Graph View 
*/
protected void updateGraphView() {
	Component oldDisplay = null;
	 if (display != null) {
		   oldDisplay = display;
	    }
	
	GraphPerspective gp = network.getGraphPerspective();
	
	view = (GraphView) new PGraphView(network.getGraphPerspective());
	display = view.getComponent();
	add( display, BorderLayout.CENTER);

	view.setBackgroundPaint(Color.BLACK);

	Iterator i = view.getNodeViewsIterator();
	    while ( i.hasNext())
	    {
		    NodeView nv = (NodeView)i.next();
		    String label = nv.getNode().getIdentifier();
		    //System.out.println("Setting label " + label);
		    nv.setLabel(label);
		    nv.setShape( NodeView.ELLIPSE );
		    nv.setUnselectedPaint( Color.lightGray );
		    nv.setSelectedPaint( ((Color)nv.getUnselectedPaint()).darker() );
		    nv.setBorderPaint(Color.black);
	    }
	    
	    //edges
	    java.util.List edges = view.getEdgeViewsList();
	    for ( Iterator ie= edges.iterator(); ie.hasNext();)
	    {
		   EdgeView ev = (EdgeView)ie.next();
		    ev.setUnselectedPaint(Color.blue);
		    ev.setTargetEdgeEnd(EdgeView.ARROW_END);
		    ev.setTargetEdgeEndPaint(Color.CYAN);
		    ev.setSourceEdgeEndPaint(Color.CYAN);
		    //ev.setLineType(EdgeView.CURVED_LINES);
		    ev.setStroke(new BasicStroke(5f));
	    }
	    // add context menues
	    addViewContextMenues();
	    view.fitContent();
      redrawGraph(false, true);
	    if (oldDisplay != null){
		    this.remove(oldDisplay);
      }
      
      // Add the GraphViewController as a listener to the graphPerspective
      // so that it keeps is synchronized to graphView
      if(this.graphViewController == null){
        this.graphViewController = new GraphViewController();
        boolean added = this.graphViewController.addGraphView(this.view);
        if(!added){
          // This should never happen, but just in case
          System.err.println("1. In CyWindow.updateGraphView(): Could not add this.view to "
                             + " this.graphViewController.");
        }
        //TODO: Remove
        System.out.println("1. In CyWindow.updateGraphView(). Added this.view to "
                           + " this.graphViewController");
      }else{
        // The graphViewController had been instatiated before.
        // Since right now we only have one view, clear the controller, and add the
        // possibly new view. When we have more than one view, this will change.
        this.graphViewController.removeAllGraphViews();
        boolean added = this.graphViewController.addGraphView(this.view);
        if(!added){
          // Again, this should never happen, but just in case
          System.err.println("2. In CyWindow.updateGraphView(): Could not add this.view to "
                             + " this.graphViewController.");
        }
        //TODO: Remove
        System.out.println("2. In CyWindow.updateGraphView(). Added this.view to "
                           + " this.graphViewController");
      }// end of GraphViewController stuff
}

  /**
   * Returns the <code>cytoscape.view.GraphViewController</code> that keeps
   * the <code>giny.model.GraphPerspective</code> contained in <code>CyNetwork</code>
   * synchronized to the <code>giny.view.GraphView</code> in this <code>CyWindow</code>.
   *
   * @return a <code>cytoscape.view.GraphViewController</code> or null if yFiles is being
   * used or if GINY is used and a call to <code>CyWindow.updateGraphView</code> has not 
   * been made
   * @see #updateGraphView() updateGraphView
   */
  public GraphViewController getGraphViewController (){
    return this.graphViewController;
  }//getGraphViewController

/**
*
*/
public void addViewContextMenues() {
	// Add some Node Context Menu Items
      view.addContextMethod( "class edu.umd.cs.piccolo.PNode",
                                  "cytoscape.graphutil.NodeAction",
                                  "openSGD",
                                  "Color This Node White" );


	    view.addContextMethod( "class phoebe.PNodeView",
                                  "cytoscape.graphutil.NodeAction",
                                  "colorNode",
                                  "Color This Node White" );
	
	    view.addContextMethod( "class phoebe.PNodeView",
                                  "cytoscape.graphutil.NodeAction",
                                  "colorSelectNode",
                                  "Color This Node White" );
	
	    view.addContextMethod( "class phoebe.PNodeView",
                                  "cytoscape.graphutil.NodeAction",
                                  "shapeNode",
                                  "Color This Node White" );
	   
	    // Add some Edge Context Menus
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "colorEdge",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "colorSelectEdge",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeWidth",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeLineType",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeSourceEndType",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.PEdgeView",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeTargetEndType",
                                  "Color This Node White" );
	    
	    // Add some Edge-end Context menus
	    view.addContextMethod( "class phoebe.util.PEdgeEndIcon",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeEndColor",
                                  "Color This Node White" );
	    view.addContextMethod( "class phoebe.util.PEdgeEndIcon",
                                  "cytoscape.graphutil.EdgeAction",
                                  "edgeEndBorderColor",
                                  "Color This Node White" );
					
      //data menues
     // view.addContextMethod( "class phoebe.PNodeView",
                                  //"cytoscape.graphutil.NodeAction",
                                 // "showData",
                                  //"Show Data for this node" );
      view.addContextMethod( "class phoebe.PNodeView",
                                  "cytoscape.graphutil.NodeAction",
                                  "changeFirstNeighbors",
                                  "Paint First Neighbors of this node" );	
      view.addContextMethod( "edu.umd.cs.piccolo.PNode",
                                  "cytoscape.graphutil.NodeAction",
                                  "zoomToNode",
                                  "Zoom to this node" );	
}
//------------------------------------------------------------------------------
/**
 * Attempts to set an initial layouter using the information from the
 * CytoscapeConfig object. Defaults to an OrganicLayouter.
 */
protected void setInitialLayouter() {
    String defaultLayoutStrategy =
            getCytoscapeObj().getConfiguration().getDefaultLayoutStrategy();
    if (defaultLayoutStrategy.equals("hierarchical")) {
        setLayouter( new HierarchicLayouter() );
    } else if (defaultLayoutStrategy.equals("circular")) {
        setLayouter( new CircularLayouter() );
    } else if (defaultLayoutStrategy.equals("embedded")) {
        setLayouter( new EmbeddedLayouter() );
    } else if (defaultLayoutStrategy.equals("organic")) {
        OrganicLayouter ol = new OrganicLayouter();
        ol.setActivateDeterministicMode(true);
        ol.setPreferredEdgeLength(80);
        setLayouter(ol);
    } else {//use the above OrganicLayouter as a default
        OrganicLayouter ol = new OrganicLayouter();
        ol.setActivateDeterministicMode(true);
        ol.setPreferredEdgeLength(80);
        setLayouter(ol);
    }
}
//-----------------------------------------------------------------------------
/**
 * This method makes sure that the GraphView object references the current graph,
 * and the graph considers this object's GraphView as its current view. This is
 * a yFiles specific thing.
 */
protected void connectGraphAndView() {
    if (getNetwork() == null || getNetwork().getGraph() == null) {return;}
    getGraphView().setGraph2D( getNetwork().getGraph() );
    getNetwork().getGraph().setCurrentView( getGraphView() );
}
//------------------------------------------------------------------------------
/**
 * Adds this object as a selection listener to the graph, creates an appropriate
 * undo manager and adds it as a listener to the graph, and creates an
 * UndoableGraphHider object for managing hiding graph objects.
 */
protected void attachGraphListeners() {
    if (getNetwork() == null || getNetwork().getGraph() == null) {return;}
    getNetwork().addCyNetworkListener(this);
    Graph2D theGraph = getNetwork().getGraph();
    theGraph.addGraph2DSelectionListener(this);
    if (getCytoscapeObj().getConfiguration().enableUndo()) {
        undoManager = new CytoscapeUndoManager(cyMenus, theGraph);
        theGraph.addGraphListener(undoManager);
    } else {
        undoManager = new EmptyUndoManager(cyMenus, theGraph);
    }
    graphHider = new UndoableGraphHider(theGraph, undoManager);
}

//------------------------------------------------------------------------------
/**
 * Adds this object as a GraphViewChangeListener to the graph view, 
 */
protected void attachGraphViewListener() {
    if (getNetwork() == null || getView() == null) {return;}
    System.out.println( " CyWindow attaching itself as a GraphViewListener" );
    view.addGraphViewChangeListener(this);
}
//-----------------------------------------------------------------------------
/**
 * Detaches the graph listeners that were attached (by attachGraphListeners)
 * when the old graph was installed.
 */
protected void detachGraphListeners() {
    if (getNetwork() == null || getNetwork().getGraph() == null) {return;}
    getNetwork().removeCyNetworkListener(this); //no error if we're not a listener
    Graph2D currentGraph = getNetwork().getGraph();
    //remove this as a graph selection listener
    for (Iterator i = currentGraph.getGraph2DSelectionListeners(); i.hasNext(); ) {
        Object o = i.next();//no need to cast to the real type
        if (o == this) {
            currentGraph.removeGraph2DSelectionListener(this);
            break;
        }
    }
    //remove the undo manager
    if (getUndoManager() != null) {
        Iterator it = currentGraph.getGraphListeners();
        GraphListener graphL;
        // Make sure the undoManager is in the list of graph 
        // listeners for graph, otherwise we get NullPointerException
        //if we try to remove it iliana 1.16.2003
        while(it.hasNext()){
            graphL = (GraphListener)it.next();
            if(graphL == getUndoManager()){
                currentGraph.removeGraphListener(getUndoManager());
                break;
            }
        }
    }
}
//------------------------------------------------------------------------------
/**
 * Creates the vizmapper and it's UI, making sure that a visual style
 * is selected, either from the config or a default value.
 */
protected void loadVizMapper() {
  
  // BUG: vizMapper.applyAppearances() gets called twice here

  CalculatorCatalog calculatorCatalog = getCytoscapeObj().getCalculatorCatalog();
  //try to get visual style from properties
  Properties configProps = getCytoscapeObj().getConfiguration().getProperties();
  VisualStyle vs = null;
  String vsName = configProps.getProperty("visualStyle");
  if (vsName != null) {vs = calculatorCatalog.getVisualStyle(vsName);}
  if (vs == null) {//none specified, or not found; use the default
      vs = calculatorCatalog.getVisualStyle("default");
  }
  
  //create the vizMapping objects
  this.vizMapper = new VisualMappingManager(this, calculatorCatalog, vs,
                                            getCytoscapeObj().getLogger());
  this.vizMapUI = new VizMapUI(this.vizMapper, this.mainFrame);
  vizMapper.setUI(vizMapUI);

  // add vizmapper to toolbar
  JToolBar toolBar = getCyMenus().getToolBar();
  toolBar.addSeparator();
  JButton b = toolBar.add(new SetVisualPropertiesAction(this, false));
  b.setIcon(new ImageIcon(getClass().getResource("images/ColorVisual.gif")));
  b.setToolTipText("Set Visual Properties");
  b.setBorderPainted(false);

  // easy-access visual styles changer
  toolBar.add(vizMapUI.getStyleSelector().getToolbarComboBox());
  toolBar.addSeparator();
}
//------------------------------------------------------------------------------
/**
 * Helper method called when a new graph is going to be displayed.
 * Does a layout if applicable and fits the view to the size of
 * this display.
 */
protected void displayNewGraph(boolean doLayout) {
    // Apply appearances since we are displaying the graph for the first time
    if ( isYFiles) {
	    redrawGraph(doLayout,true);
	    getGraphView().fitContent();
	    getGraphView().setZoom(getGraphView().getZoom()*0.9);
    }
    else {
	    view.fitContent();
	    view.setZoom(view.getZoom()*0.9);
    }
}
/**
 *
 */
protected void fitGraphView() {
	//@@@@@ does not work for some reason, zooms out to infinity...
	//view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getGlobalFullBounds(), true, 5001 );
}

//------------------------------------------------------------------------------
/**
 * Actually displays the window. Nothing will appear on the screen until
 * this method gets called. This allows other objects to operate on this
 * object after construction but before it gets displayed to the user.
 *
 * This method does nothing on any call after the first.
 */
public void showWindow() {
    //draw the graph for the first time
    displayNewGraph( network.getNeedsLayout() );
    mainFrame.setContentPane(this);
    mainFrame.pack();
    mainFrame.setSize (new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT));
    this.setVisible(true);
    mainFrame.setVisible(true);
    //mainFrame.pack();
    //setInteractivity(true);
    windowDisplayed = true;
}

//------------------------------------------------------------------------------
//----------SET/GET METHODS-----------------------------------------------------
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
/**
 * @deprecated This method allows access to the CytoscapeWindow wrapper around
 * this class. This method will go away when support for the CytoscapeWindow
 * class is dropped.
 */
public CytoscapeWindow getCytoscapeWindow() {return cytoscapeWindow;}
//------------------------------------------------------------------------------
/**
 * Returns a reference to the global Cytoscape object.
 */
public CytoscapeObj getCytoscapeObj() {return globalInstance;}
//------------------------------------------------------------------------------
/**
 * returns the network displayed in this window.
 */
public CyNetwork getNetwork() {return network;}
//------------------------------------------------------------------------------
/**
 * Returns the UI component that renders the displayed graph.
 */
public Graph2DView getGraphView() {return graphView;}
//------------------------------------------------------------------------------

/**
 * Returns the UI component that renders the displayed graph.
 */
public GraphView getView() {return view;}



//------------------------------------------------------------------------------
/**
 * Returns the current layouter. Guaranteed to be non-null.
 * This layouter will be used for any new layout requests.
 */
public Layouter getLayouter() {return layouter;}
//------------------------------------------------------------------------------
/**
 * Sets the graph layouter. This layouter will be used for
 * new layout requests.
 * Does nothing if the argument is null.
 */
public void setLayouter(Layouter newLayouter) {
    if (newLayouter != null) {layouter = newLayouter;}
}
//------------------------------------------------------------------------------
/**
 * Returns the visual mapping manager that controls the appearance
 * of nodes and edges in this display.
 */
public VisualMappingManager getVizMapManager() {return vizMapper;}
//------------------------------------------------------------------------------
/**
 * returns the top-level UI object for the visual mapper.
 */
public VizMapUI getVizMapUI() {return vizMapUI;}
//------------------------------------------------------------------------------
/**
 * Returns the undo manager. This object handles state changes and
 * undo/redo requests.
 */
public CytoscapeUndoManager getUndoManager() {return undoManager;}
//------------------------------------------------------------------------------
/**
 * Returns a special graph hider that is synchronized with the
 * undo manager. This object should be used to hide or unhide
 * objects in the network, so that these changes are properly
 * handled by the undo manager.
 */
public UndoableGraphHider getGraphHider() {return graphHider;}
//------------------------------------------------------------------------------
/**
 * returns the main window frame. Note that this class is itself
 * an instance of JPanel, and is the content pane of the main frame.
 */
public JFrame getMainFrame() {return mainFrame;}
//------------------------------------------------------------------------------
/**
 * Returns the title of the window, guaranteed to be non-null.
 */
public String getWindowTitle() {return windowTitle;}
//------------------------------------------------------------------------------
/**
 * Sets the window title. The actual title (as displayed on the
 * frame title bar, and returned by a call to getWindowTitle)
 * is a concatenation of a default header string and the argument
 */
public void setWindowTitle(String newTitle) {
    windowTitle = defaultWindowTitle +  newTitle;
    getMainFrame().setTitle(windowTitle);
}
//------------------------------------------------------------------------------
/**
 * Returns the object that holds references to the menu bars and
 * several of the major submenus.
 */
public CyMenus getCyMenus() {return cyMenus;}
//------------------------------------------------------------------------------
/**
 * Sets the popup mode - a yFiles specific thing that deals with
 * mouse clicks in the graph view. The default responds to right
 * mouse clicks by bring up a browser window for the selected
 * nodes and edges in the graph.
 *
 * @see NodeBrowsingMode
 */
public void setPopupMode (PopupMode newMode) {
    if (currentPopupMode != null) {
        getGraphView().removeViewMode(currentPopupMode);
        currentPopupMode = newMode;
    }
    // todo (pshannon, 23 oct 2002): a terrible hack! 
    // find a way to remove this special case, perhaps by adjusting 
    // CytoscapeWindow's ctor so that modes are installed only after
    // the CW is well initialized.  but since the y-supplied nodes no
    // nothing about their CytoscapeWindow parent, this is again a special
    // case.  this problem did not arise in the previous version because our
    // cytoscape-aware mode was an inner class of CW, and thus could gain
    // access to CW data without being explicitly constructed with a CW reference.
    //
    //removed 2003-07-07; not relevant now that NodeBrowsingMode is rewritten
    //to take proper arguments in its constructor
    //if (newMode instanceof NodeBrowsingMode) {
    //    NodeBrowsingMode m = (NodeBrowsingMode) newMode;
    //    m.set(cytoscapeWindow);
    //}
    
    getGraphView().addViewMode(newMode);

} // setPopupMode

//------------------------------------------------------------------------------
//-------------ACTION METHODS---------------------------------------------------
//------------------------------------------------------------------------------

/**
 * @deprecated
 * Instead of calling this method directly, users should change the graph
 * in the network object referenced by this window, which will fire an event
 * that triggers this object to respond appropriately. This method will
 * eventually be undeprecated, but changed to protected access only.
 *
 * This method changes the graph that is displayed in the window.
 * Does nothing if the newGraph argument is null. To change the network
 * displayed by this window, use setNewNetwork.
 *
 * @param newGraph  the new graph to display
 * @param doLayout  if true, does a layout before displaying the graph
 */
public void setNewGraph(Graph2D newGraph, boolean doLayout) {
    if (newGraph == null) {return;}
    setInteractivity(false);
    detachGraphListeners();
    getNetwork().setGraph(newGraph);
    connectGraphAndView();
    attachGraphListeners();
    displayNewGraph(doLayout);
    getCyMenus().updateUndoRedoMenuItemStatus();
    setInteractivity(true);
}
//------------------------------------------------------------------------------
/**
 * Changes the network displayed in this window. This display will
 * be updated to show and reference this new network, and new listeners
 * will be attached to the network and its graph.
 *
 * Does nothing if the newNetwork argument is null, or is the same object
 * as the current network.
 * 
 * @param newNetwork  the new network to display
 * @param doLayout  if true, does a layout before displaying the graph
 */
public void setNewNetwork( CyNetwork newNetwork ) {
    if (newNetwork == null) {return;}
    if ( isYFiles ) {
	setInteractivity(false);
	detachGraphListeners();
	this.network = newNetwork;
	connectGraphAndView();
	attachGraphListeners();
	displayNewGraph( network.getNeedsLayout() );
	getCyMenus().updateUndoRedoMenuItemStatus();
	setInteractivity(true);
    } else {
	//setInteractivity(false);
	//using giny update the view
	if (view != null)
	    view.removeGraphViewChangeListener(this);
	this.network = newNetwork;
	
	updateGraphView();
	attachGraphViewListener();
	//applyLayout();
	fitGraphView();
	updateStatusLabel(0, 0);
	add(cyMenus.getToolBar(), BorderLayout.NORTH);
	
	showWindow();
	//setInteractivity(true);
    }
}
//------------------------------------------------------------------------------
/**
 *
 * NOTE: algorithms in general should not call this method directly. Instead,
 * they should call the beginActivity or endActivity methods on the CyNetwork
 * object displayed in this window. Those methods will trigger this class to
 * set the interactivity to the correct state. -AM 09-11-2003
 *
 * Sets the interactivity state of this window. If the argument is
 * false. disables user interaction with the graph view and menus;
 * removes existing edit modes, and turns off the undo manager.
 * If the argument is true, reverses all of those steps to
 * enable user interaction.
 *
 * This method is often called by methods that do major operations
 * on the graph; first turn off interactivity, then do the changes,
 * then turn interactivity back on.
 *
 * This class is initialized with interactivity off; it is turned
 * on by the showWindow method.
 */
public void setInteractivity (boolean newState) {
    if (!this.isYFiles) {return;} //not supported in Giny
    if (currentInteractivityState == newState) {return;}
    if (newState == true) { // turn interactivity ON
        if (!viewModesInstalled) {
            graphView.addViewMode(currentGraphMode);
            if (currentPopupMode != null) {
                graphView.addViewMode(currentPopupMode);
            }
            viewModesInstalled = true;
        }
        Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        graphView.setViewCursor(defaultCursor);
        setCursor(defaultCursor);
        // accept new undo entries - added by dramage 2002-08-23
        if (getUndoManager() != null) {
            getUndoManager().resume();
        }
    } else {  // turn interactivity OFF
        if (viewModesInstalled) {
            graphView.removeViewMode(currentGraphMode);
            graphView.removeViewMode(currentPopupMode); 
            viewModesInstalled = false;
        }
        Cursor busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        graphView.setViewCursor(busyCursor);
        setCursor(busyCursor);
        // deny new undo entries - added by dramage 2002-08-23
        if (getUndoManager() != null){
            getUndoManager().pause();
        }
    }

    // disable/enable UI components - added by iliana 2003-03-03
    MenuElement [] menuBarEls = getCyMenus().getMenuBar().getSubElements();
    for(int i = 0; i < menuBarEls.length; i++) {
        Component comp = menuBarEls[i].getComponent();
        comp.setEnabled(newState);
    }
    
    getCyMenus().getToolBar().setEnabled(newState);
} // setInteractivity
//------------------------------------------------------------------------------
/**
 * Redraws the graph - equivalent to redrawGraph(false, true).
 * That is, no new layout will be performed, but the visual
 * appearances will be reapplied.
 */
public void redrawGraph() {
    // Do not do a layout and apply appearances
    redrawGraph(false,true);
}


// ------------------------------------------------------------------------------

//------------------------------------------------------------------------------
/**
 * Redraws the graph - equivalent to redrawGraph(doLayout, true).
 * That is, the visual appearances will be reapplied, and layout
 * will be done iff the argument is true.
 */
public void redrawGraph(boolean doLayout) {
    // apply appearances by default
    redrawGraph(doLayout, true);
}
//------------------------------------------------------------------------------
/**
 * Redraws the graph. A new layout will be performed if the first
 * argument is true, and the visual appearances will be recalculated
 * and reapplied by the visual mapper if the second argument is true.
 *
 * This method removes any graph listeners from the graph before
 * doing the redraw, and reattaches them at the end, for performance
 * reasons - layout operations especially can trigger a large number
 * of graph events. It may make more sense to move the listener removal
 * to the applyLayout method.
 */
public void redrawGraph(boolean doLayout, boolean applyAppearances) {
    if (!isYFiles) {
        ginyRedrawGraph(doLayout, applyAppearances);
        return;
    }
    if (graphView.getGraph2D() == null) {return;}
    // added by iliana on 1.6.2003 (works with yFiles 2.01)
    // Remove graph listeners: (including undoManager)
    Graph2D theGraph = graphView.getGraph2D();
    Iterator it = theGraph.getGraphListeners();
    ArrayList gls = new ArrayList();
    GraphListener gl;
    while (it.hasNext()) {
        gl = (GraphListener)it.next();
        gls.add(gl);
    }
    for (int i = 0; i < gls.size(); i++) {
        theGraph.removeGraphListener((GraphListener)gls.get(i));
    }
    
    if (applyAppearances) {
        applyVizmapSettings();
    }
    if (doLayout) {
        applyLayout(false); //applies layout without animation
        getNetwork().setNeedsLayout(false);
    }
    
    graphView.updateView(); //forces the view to update it's contents
    /* paintImmediately() is needed because sometimes updates can be buffered */
    graphView.paintImmediately(0,0,graphView.getWidth(),graphView.getHeight());
    
    updateStatusText();
    
    // Add back graph listeners:
    for (int i = 0; i < gls.size(); i++) {
        theGraph.addGraphListener((GraphListener)gls.get(i));
    }
}
//------------------------------------------------------------------------------
/**
 * redrawGraph method used when in Giny mode. Does not do layout but does
 * call the vizmapper if requested.
 */
protected void ginyRedrawGraph(boolean doLayout, boolean applyAppearances) {
    if (view == null) {return;}
    if (doLayout) {applyLayout(view);}
    if (applyAppearances) {applyVizmapSettings();}
    view.updateView();
}
//------------------------------------------------------------------------------
/**
 * Performs a layout operation on the graph displayed in this window,
 * using the current layouter as returned by getLayouter.
 * The argument is currently ignored; animated layout is never done.
 */
public void applyLayout(boolean animated) {
    if (graphView.getGraph2D() == null) {return;}
    if (graphView.getGraph2D().getNodeArray().length == 0) return;
    
    setInteractivity(false);
    getUndoManager().saveRealizerState();
    getUndoManager().pause();
    
    getCytoscapeObj().getLogger().warning ("starting layout...");
    
    getLayouter().doLayout( graphView.getGraph2D() );
    graphView.fitContent();
    graphView.setZoom(graphView.getZoom()*0.9);
    
    getCytoscapeObj().getLogger().info(" done");
    
    getUndoManager().resume();
    setInteractivity(true);
} // applyLayout

//------------------------------------------------------------------------------
/**
 * Performs a layout operation on the giny graph displayed in this window,
 * using the default layouter for now
 */
public void applyLayout(GraphView lview) {
	SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(lview);
	lay.doLayout();
}
//------------------------------------------------------------------------------	
// applyLayoutSelection
//
// apply layout, but only on currently selected nodes
public void applyLayoutSelection() {
    
    if (graphView.getGraph2D() == null) {return;}
    Graph2D theGraph = graphView.getGraph2D();

    // special case for EmbeddedLayouter: layout whole graph,
    // holding unselected nodes in place
    // OPTIMIZE ME!
    if (getLayouter().getClass().getName().endsWith("EmbeddedLayouter")) {
        // data provider of sluggishness for each node
        NodeMap slug = theGraph.createNodeMap();
        theGraph.addDataProvider("Cytoscape:slug", slug);

        for (NodeCursor nc = theGraph.selectedNodes(); nc.ok(); nc.next())
            slug.setDouble(nc.node(), 0.5);

        Node[] nodeList = theGraph.getNodeArray();
        int nC = theGraph.nodeCount();
        for (int i = 0; i < nC; i++)
            if (slug.getDouble(nodeList[i]) != 0.5)
                slug.setDouble(nodeList[i], 0.0);

        applyLayout(false);

        theGraph.removeDataProvider("Cytoscape:slug");
        theGraph.disposeNodeMap(slug);
    }

    // special case for OrganicLayouter: layout whole graph, holding
    // unselected nodes in place
    else if (getLayouter().getClass().getName().endsWith("OrganicLayouter")) {
        OrganicLayouter ogo = (OrganicLayouter)layouter;

        // data provider of selectedness for each node
        NodeMap s = theGraph.createNodeMap();
        theGraph.addDataProvider(Layouter.SELECTED_NODES, s);

        for (NodeCursor nc = theGraph.selectedNodes(); nc.ok(); nc.next())
            s.setBool(nc.node(), true);

        Node[] nodeList = theGraph.getNodeArray();
        int nC = theGraph.nodeCount();
        for (int i = 0; i < nC; i++)
            if (s.getBool(nodeList[i]) != true)
                s.setBool(nodeList[i], false);
        
        byte oldSphere = ogo.getSphereOfAction();
        ogo.setSphereOfAction(OrganicLayouter.ONLY_SELECTION);
        applyLayout(false);
        ogo.setSphereOfAction(oldSphere);

        theGraph.removeDataProvider(Layouter.SELECTED_NODES);
        theGraph.disposeNodeMap(s);
    }

    // other layouters
    else {
        getCytoscapeObj().getLogger().warning ("starting layout..."); 
        setInteractivity(false);

        Subgraph subgraph = new Subgraph(theGraph, theGraph.selectedNodes());
        getLayouter().doLayout(subgraph);
        subgraph.reInsert();

        // remove bends
        EdgeCursor cursor = theGraph.edges();
        cursor.toFirst ();
        for (int i=0; i < cursor.size(); i++){
            Edge target = cursor.edge();
            EdgeRealizer e = theGraph.getRealizer(target);
            e.clearBends();
            cursor.cyclicNext();
        }

        setInteractivity(true);
        getCytoscapeObj().getLogger().info("  done");
    }
}

/**
*
*/
  public void applySelLayout()
 
  {
	    if (view == null) return;
	    
	    int [] selNodes = view.getSelectedNodeIndices();
	    int [] selEdges = view.getSelectedEdgeIndices();
	    PGraphView selView = new PGraphView(view.getRootGraph().createGraphPerspective(selNodes, selEdges));
	    applyLayout(selView);
  }
//------------------------------------------------------------------------------
/**
 * Uses the visual mapper to calculate and set the visual appearance
 * of nodes, edges, and certain global properties using the node and
 * edge dat aattributes.
 *
 * @see VisualMappingManager
 */
protected void applyVizmapSettings() {
    if (getVizMapManager() != null) {
        getVizMapManager().applyAppearances();
    }
}
//------------------------------------------------------------------------------
/**
 * This method responds to the given CyNetworkEvent If the source of this event
 * is the network displayed in this window. Currently, this method turns off
 * interactivity when a BEGIN event is received, and turns it back on when an
 * END event is received.
 */
public void onCyNetworkEvent(CyNetworkEvent event) {
    if (event.getNetwork() != this.getNetwork()) {return;}
    if (event.getType() == CyNetworkEvent.BEGIN) {
        setInteractivity(false);
    } else if (event.getType() == CyNetworkEvent.END) {
        setInteractivity(true);
    } else if (event.getType() == CyNetworkEvent.GRAPH_REPLACED) {
        if (this.isYFiles) {
            setNewGraph( getNetwork().getGraph(), getNetwork().getNeedsLayout() );
        } else {
            updateGraphView();
            showWindow();
        }
    }
}
//------------------------------------------------------------------------------
/**
 * Handles selection events. Currently updates the info text label to show the
 * current number of selected nodes/edges.
 */
public void onGraph2DSelectionEvent(y.view.Graph2DSelectionEvent e) {
	updateStatusText();
}

//------------------------------------------------------------------------------

/**
*
* Implementation of the GraphViewChangeListener interface
* @param event
*/
public void graphViewChanged ( GraphViewChangeEvent event)

{
	updateStatusLabel(0, 0);
}

//--------------------------------------------------------------------------------
/**
 * Equivalent to updateStatusText(0,0).
 */
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
 *
 * Note: the quirk described above appears to have been fixed in yFiles 2.0.
 * Thus we should be able to remove the hack and just directly update the text
 * when selections occur. -AM 2003-06-30
 */
public void updateStatusText(int nodeAdjust, int edgeAdjust) {
   		
    Graph2D theGraph = graphView.getGraph2D();
    int nodeCount = theGraph.nodeCount();
    int selectedNodes = theGraph.selectedNodes().size() + nodeAdjust;
    
    int edgeCount = theGraph.edgeCount();
    int selectedEdges = theGraph.selectedEdges().size() + edgeAdjust;
    infoLabel.setText("  Nodes: " + nodeCount
                      + " ("+selectedNodes+" selected)"
                      + " Edges: " + edgeCount
                      + " ("+selectedEdges+" selected)");
}

//------------------------------------------------------------------------------
/**
 * Resets the info label status bar text with the current number of
 * nodes, edges, selected nodes, and selected edges.
 *

 */
public void updateStatusLabel(int hiddenNodes, int hiddenEdges) {
	

	if (getView() == null ) 
	{ 
		infoLabel.setText("No graph specified for the display  ");
		return; }
	int nodeCount = view.getNodeViewCount();
	int edgeCount = view.getEdgeViewCount();	
	int selectedNodes = view.getSelectedNodes().size();
	int selectedEdges = view.getSelectedEdges().size();
   		
	infoLabel.setText("  Nodes: " + nodeCount
                      + " ("+selectedNodes+" selected)"
		      + " ("+hiddenNodes + " hidden)"  
                      + " Edges: " + edgeCount
                      + " ("+selectedEdges+" selected)"
		      + " (" +hiddenEdges+ " hidden)");
 
}
//------------------------------------------------------------------------------
/**
 * Sets the editing mode to allow the user to edit the graph.
 * This changes the graph mode and the menu options available.
 */
public void switchToEditMode() {
    getGraphView().removeViewMode(currentGraphMode);
    currentGraphMode = editGraphMode;
    getGraphView().addViewMode(currentGraphMode);
    getCyMenus().enableDeleteSelectionMenuItem();
}
//------------------------------------------------------------------------------
/**
 * Sets the editing mode to prevent the user from editing the graph.
 * This chagnes the graph mode and the menu options available.
 */
public void switchToReadOnlyMode() {
    getGraphView().removeViewMode(currentGraphMode);
    currentGraphMode = readOnlyGraphMode;
    getGraphView().addViewMode(currentGraphMode);
    getCyMenus().disableDeleteSelectionMenuItem();
}
//------------------------------------------------------------------------------
}

