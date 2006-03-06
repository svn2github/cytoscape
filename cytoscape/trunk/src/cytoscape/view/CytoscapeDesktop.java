
/*
  File: CytoscapeDesktop.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.view;

import cytoscape.*;

import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.view.CyEdgeView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.cytopanels.BiModalJSplitPane;
import cytoscape.plugin.*;
import cytoscape.visual.*;
import cytoscape.visual.ui.*;

import cytoscape.giny.*;

import giny.view.GraphView;
import giny.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.beans.*;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.CSH;
import javax.help.CSH.*;

/**
 * The CytoscapeDesktop is the central Window
 * for working with Cytoscape
 */

public class CytoscapeDesktop 
  extends 
    JFrame 
  implements
    PropertyChangeListener,
    CyWindow {
   

  protected long lastPluginRegistryUpdate;
  //--------------------//
  // Static variables

  public static String NETWORK_VIEW_FOCUSED = "NETWORK_VIEW_FOCUSED";
  public static String NETWORK_VIEW_FOCUS = "NETWORK_VIEW_FOCUS";
  public static String NETWORK_VIEW_CREATED = "NETWORK_VIEW_CREATED";
  public static String NETWORK_VIEW_DESTROYED = "NETWORK_VIEW_DESTROYED";

  // state variables
  public static String VISUAL_STYLE = "VISUAL_STYLE";
  public static String VIZMAP_ENABLED = "VIZMAP_ENABLED";

  /**
   * Cytoscape UndoManager
   */
  public static cytoscape.util.UndoManager undo;

  /**
   * Displays all network views in TabbedPanes
   * ( like Mozilla )
  */
  public static int TABBED_VIEW = 0;

  /**
   * Displays all network views in JInternalFrames, using 
   * the mock desktop interface. ( like MS Office )
   */
  public static int INTERNAL_VIEW = 1;

  /**
   * Displays all network views in JFrames, so each Network
   * has its own window. ( like the GIMP )
   */
  public static int EXTERNAL_VIEW = 2;

  //--------------------//
  // Member varaibles
  
  /**
   * The type of view, should be considered final
   */
  protected int VIEW_TYPE;

  protected VisualStyle defaultVisualStyle;

  /**
   * The network panel that sends out events when 
   * a network is selected from the Tree that it contains.
   */
  protected NetworkPanel networkPanel;

  /**
   * The CyMenus object provides access to the all of the
   * menus and toolbars that will be needed.
   */
  protected CyMenus cyMenus;

  /**
   * The NetworkViewManager can support three types of interfaces.
   * Tabbed/InternalFrame/ExternalFrame
   */
  protected NetworkViewManager networkViewManager;


  /**
   * The HelpBroker provides access to JavaHelp
   */
  protected CyHelpBroker cyHelpBroker;
  
  //--------------------//
  // Event Support

  /**
   * provides support for property change events
   */
   protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  /**
   * The GraphViewController for all NetworkViews that we know about
   */
   protected GraphViewController graphViewController;

  //--------------------//
  // VizMap Variables

  /** 
   * Provides Operations for Mapping Data Attributes of CyNetworks
   * to CyNetworkViews
   */
  protected VisualMappingManager vizMapper;
  
  /** user interface to the
   *  {@link VisualMappingManager VisualMappingManager}
   *  {@link #vizMapper vizMapper}.
   */
  protected VizMapUI vizMapUI;
 
  protected String currentNetworkID;
  protected String currentNetworkViewID;

  //--------------------//
  // CytoPanel Variables
  protected CytoPanelImp cytoPanelWest;
  protected CytoPanelImp cytoPanelEast;
  protected CytoPanelImp cytoPanelSouth;

  //  Status Bar
  protected JLabel statusBar;

  //----------------------------------------//
  // Constructors
  //----------------------------------------//

  /**
   * The Default constructor uses a TabbedView
   */
  public CytoscapeDesktop () {
    this( TABBED_VIEW );
  }
  
  /**
   * Create a CytoscapeDesktop that conforms the given view type.
   * @param view_type one of the ViewTypes
   */
  public CytoscapeDesktop ( int view_type ) {
    super( "Cytoscape Desktop (New Session)" );
    this.VIEW_TYPE = view_type;
    initialize();
  }
  
  protected void initialize () {
  
    /////////////TODO: REMOVE
    this.VIEW_TYPE = INTERNAL_VIEW;

    setIconImage( Toolkit.getDefaultToolkit().getImage(
			getClass().getResource("images/c16.png") ) );

    // initialize Help system with Cytoscape help set - define context-sensitive
    // help as we create components
    cyHelpBroker = new CyHelpBroker();

    JPanel main_panel = new JPanel();

    main_panel.setLayout( new BorderLayout() );
    // enable context-sensitive help generally
    getHelpBroker().enableHelpKey(getRootPane(),"intro", null);

    // enable context-sensitive help for main panel
    getHelpBroker().enableHelp(main_panel,"intro", null);

    //------------------------------//
    // Set up the Panels, Menus, and Event Firing

    networkPanel = new NetworkPanel( this );
    // enable context-sensitive help for networkPanel
    getHelpBroker().enableHelp(networkPanel,"network-view-manager", null);

    cyMenus = new CyMenus();
    // enable context-sensitive help for menus/menubar
    getHelpBroker().enableHelp(cyMenus.getMenuBar(),"menus", null);

    networkViewManager = new NetworkViewManager( this );


    // Listener Setup
    //----------------------------------------
    //              |----------|
    //              | CyMenus  |
    //              |----------|
    //                  |
    //                  |
    //  |-----|      |---------|    |------|  |-------|
    //  | N P |------| Desktop |----| NVM  |--| Views |
    //  |-----|      |---------|    |------|  |-------|
    //                   | 
    //                   |
    //              |-----------|
    //              | Cytoscape |
    //              |-----------|

    // The CytoscapeDesktop listens to NETWORK_VIEW_CREATED events, 
    // and passes them on, The NetworkPanel listens for them
    // The Desktop also keeps Cytoscape up2date, but NOT via events
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    // The Networkviewmanager listens to the CytoscapeDesktop to know when to
    // put new NetworkViews in the userspace and to get passed focus events from 
    // the NetworkPanel. The CytoscapeDesktop also listens to the NVM
    this.getSwingPropertyChangeSupport().addPropertyChangeListener( networkViewManager );
    networkViewManager.getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    // The NetworkPanel listens to the CytoscapeDesktop for NETWORK_CREATED_EVENTS a
    // as well as for passing focused events from the Networkviewmanager. The
    // CytoscapeDesktop also listens to the NetworkPanel
    this.getSwingPropertyChangeSupport().addPropertyChangeListener( networkPanel );
    networkPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( this );


    // initialize undo manager
    undo = new cytoscape.util.UndoManager( cyMenus );
    
    // initialize Menus
    cyMenus.initializeMenus();

    // initialize Help Menu
    cyMenus.initializeHelp(cyHelpBroker.getHelpBroker());

    // create the CytoscapeDesktop
	BiModalJSplitPane masterPane = setupCytoPanels(networkPanel, networkViewManager);
	// note - proper networkViewManager has been properly selected in setupCytoPanels()
	if ( VIEW_TYPE == TABBED_VIEW ||
		 VIEW_TYPE == INTERNAL_VIEW ) {
		main_panel.add( masterPane, BorderLayout.CENTER );
		main_panel.add(cyMenus.getToolBar(), BorderLayout.NORTH);
        initStatusBar(main_panel);
        setJMenuBar(cyMenus.getMenuBar());
	}
	// not sure if this is correct
	else if ( VIEW_TYPE == EXTERNAL_VIEW ) {
		main_panel.add( masterPane );
		cyMenus.getToolBar().setOrientation( JToolBar.VERTICAL );
		main_panel.add(cyMenus.getToolBar(), BorderLayout.EAST);
		setJMenuBar(cyMenus.getMenuBar());
	}

	/* leave following code commented out for now - until CytoPanels integration is correct */
	/*
    if ( VIEW_TYPE == TABBED_VIEW ) {
      // eveything gets put into this one window
      //JScrollPane scroll_panel = new JScrollPane( networkPanel );
      JScrollPane scroll_tab = new JScrollPane( networkViewManager.getTabbedPane() );


      JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                         false,
                                         networkPanel,
                                         scroll_tab );
      split.setOneTouchExpandable( true );
    //   JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
    //                                          false,
    //                                          networkPanel,
    //                                          networkViewManager.getTabbedPane() );
      main_panel.add( split, BorderLayout.CENTER );
      main_panel.add(cyMenus.getToolBar(), BorderLayout.NORTH);
      setJMenuBar(cyMenus.getMenuBar());
    }

    else if ( VIEW_TYPE == INTERNAL_VIEW ) {
      // eveything gets put into this one window
      JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                         false,
                                         networkPanel,
                                         networkViewManager.getDesktopPane() );
     main_panel.add( split, BorderLayout.CENTER );
     main_panel.add(cyMenus.getToolBar(), BorderLayout.NORTH);
     setJMenuBar(cyMenus.getMenuBar());
    }
    
    else if ( VIEW_TYPE == EXTERNAL_VIEW ) {
      // just the NetworkPanel and the Menus get put into the Main Pane
      main_panel.add( networkPanel );
      cyMenus.getToolBar().setOrientation( JToolBar.VERTICAL );
      main_panel.add(cyMenus.getToolBar(), BorderLayout.EAST);
      

      //if ( !System.getProperty("os.name").startsWith( "Mac" ) ) {
      //  JFrame menuFrame = new JFrame("Cytoscape Menus");
      //  menuFrame.setJMenuBar(cyMenus.getMenuBar());
      //  menuFrame.setSize( 400, 60 );
      //  menuFrame.setVisible( true );
      //} else {
        setJMenuBar(cyMenus.getMenuBar());
        //}
      
    }
	*/

    //------------------------------//
    // Set up the VizMapper
    setupVizMapper( main_panel );
    
    
    final CytoscapeDesktop thisWindow = this;
    // AJK: 09/13/05 BEGIN
    //      don't automatically close window.  Let Cytoscape.exit() handle this, based upon user confirmation.
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    // AJK: 09/13/05 END
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          Cytoscape.exit();
        }
        public void windowClosed() {
          
        }
    });
    
    // show the Desktop
    setContentPane( main_panel );
    pack();
    if ( VIEW_TYPE != EXTERNAL_VIEW )
      setSize( 800, 700 );
    setVisible( true );

  }

  private void initStatusBar(JPanel main_panel) {
      statusBar = new JLabel();
      statusBar.setBorder(new EmptyBorder(0, 7, 5, 7));
      statusBar.setForeground(new Color(75, 75, 75));
      main_panel.add(statusBar, BorderLayout.SOUTH);
      setStatusBarMsg ("Welcome to Cytoscape " + CytoscapeVersion.version);
  }

  /**
  * Sets the Status Bar Message.
  * @param msg Status Bar Message.
  */
  public void setStatusBarMsg (String msg) {
      statusBar.setText(msg);
  }

  /**
   * Clears the Status Bar Message.
   */
  public void clearStatusBar() {
      //  By using mutiple white spaces, layout for the statusBar is preserved.
      statusBar.setText("   ");
  }

  public NetworkPanel getNetworkPanel () {
    return networkPanel;
  }

  public HelpBroker getHelpBroker () {
    return cyHelpBroker.getHelpBroker();
  }
  public HelpSet getHelpSet() {
    return cyHelpBroker.getHelpSet();
  }

  //----------------------------------------//
  // Common Desktop Variables

  public void addEdit ( javax.swing.undo.UndoableEdit edit) {
    // if ( undo == null )
    // undo = new cytoscape.util.UndoManager();
    undo.addEdit( edit );
  }

  /**
   * This will return the network currently under focus. however use this:
   * {@link cytoscape.Cytoscape#getCurrentNetwork}
   * @deprecated
   */
  public CyNetwork getNetwork () {
    return Cytoscape.getCurrentNetwork();
  }
 
  /**
   * Returns the UI component that renders the displayed graph.
   * {@link cytoscape.Cytoscape#getCurrentNetworkView}
   * @deprecated
   */
  public GraphView getView () {
    return ( GraphView )Cytoscape.getCurrentNetworkView();
  }

 
  /**
   * This will actually return an instance of <I>this</I> class..
  * {@link cytoscape.Cytoscape#getDesktop}
   * @deprecated
   */
  public JFrame getMainFrame () {
    return ( JFrame )this;
  }

  /**
   * Return the view type for this CytoscapeDesktop
   */
  protected int getViewType () {
    return VIEW_TYPE;
  }


  public CyMenus getCyMenus () {
    return cyMenus;
  }

  //------------------------------//
  // Deprecated CyWindow Methods

  public void setNewNetwork( CyNetwork newNetwork ) {}


  /**
   * This doesn't apply anymore as the title will never change.
   * @deprecated
   */
  public String getWindowTitle () {
    return "CD";
  }

  /**
   * {@link CyNetworkView#setTitle( String )}
   * @deprecated
   */
  public void setWindowTitle ( String newTitle ) {
  }

  /**
   * no longer used
   * @deprecated
   */
  public void setInteractivity ( boolean newState ) {}


  /**
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }
   * Redraws the graph - equivalent to redrawGraph(false, true).
   * That is, no new layout will be performed, but the visual
   * appearances will be reapplied.
   * @deprecated
  */
  public void redrawGraph() {
    Cytoscape.getCurrentNetworkView().redrawGraph( false, true );
  }

  /**
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }
   * Redraws the graph - equivalent to redrawGraph(doLayout, true).
   * That is, the visual appearances will be reapplied, and layout
   * will be done iff the argument is true.
   * @deprecated
   */
  public void redrawGraph(boolean doLayout) {
    // apply appearances by default
    Cytoscape.getCurrentNetworkView().redrawGraph( doLayout, true );
  }

  /**
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }  
   * Redraws the graph. A new layout will be performed if the first
   * argument is true, and the visual appearances will be recalculated
   * and reapplied by the visual mapper if the second argument is true
   * and the visual mapper is not disabled.
   * @deprecated
   */
  public void redrawGraph(boolean doLayout, boolean applyAppearances) {
    Cytoscape.getCurrentNetworkView().redrawGraph( doLayout, applyAppearances );
  }

  /**
   * not used
   * @deprecated
   */
  public void showWindow ( int width, int height) {
  }

  /**
   * not used
   * @deprecated
   */
  public void showWindow() {}

  /**
   * not used
   * @deprecated
   */
  public void applyLayout ( GraphView lview ) {
    //Cytoscape.getCurrentNetworkView().applyLayout( new SpringEmbeddedLayouter() );
  }

  /**
   * not used
   * @deprecated
   */
  public void applySelLayout() {
    
    //int[] selNodes = Cytoscape.getCurrentNetworkView().getSelectedNodeIndices();
    //int[] selEdges = Cytoscape.getCurrentNetworkView().getSelectedEdgeIndices();
    //Cytoscape.getCurrentNetworkView().applyLockedLayout( new SpringEmbeddedLayouter(), selNodes, selEdges );

  }

  /**
   * not used
   * @deprecated
   */
  public void applyVizmapSettings() {
    Cytoscape.getCurrentNetworkView().redrawGraph( false, true );
  }

  /**
   * not used
   * @deprecated
   */
  public void setVisualMapperEnabled(boolean newState) {
    Cytoscape.getCurrentNetworkView().setVisualMapperEnabled( newState );
  }

  /**
   * not used
   * @deprecated
   */
  public void toggleVisualMapperEnabled() {
    Cytoscape.getCurrentNetworkView().toggleVisualMapperEnabled();
  }

  /**
   * not used
   * @deprecated
   */
  public void switchToReadOnlyMode () {
  }
 
  /**
   * not used
   * @deprecated
   */
  public void switchToEditMode (){
  }


  /**
   * Returns the visual mapping manager that controls the appearance
   * of nodes and edges in this display.
   */
  public VisualMappingManager getVizMapManager() {return vizMapper;}

 

  /**
   * returns the top-level UI object for the visual mapper.
   */
  public VizMapUI getVizMapUI() {return vizMapUI;}
  


  /**
   * Create the VizMapper and the UI for it.
   */
  protected void setupVizMapper ( JPanel panel ) {
    
    //TODO:
    // why does the vizmapper care whicih network is focused

    this.vizMapper = new VisualMappingManager( Cytoscape.getCurrentNetworkView() );

    // create the VizMapUI
    this.vizMapUI = new VizMapUI( this.vizMapper, 
                                  this );
    
    // In order for the VizMapper to run when the StyleSelector is
    // run, it needs to listen to the selector.
    vizMapper.addChangeListener( vizMapUI.getStyleSelector() );

    // Add the StyleSelector to the ToolBar
    // TODO: maybe put this somewhere else to make it easier to make
    //       vertical ToolBars.

    JComboBox styleBox = vizMapUI.getStyleSelector().getToolbarComboBox();
    Dimension newSize = new Dimension( 150, (int)styleBox.getPreferredSize().getHeight());
    styleBox.setMaximumSize(newSize);
    styleBox.setPreferredSize(newSize);
    if ( VIEW_TYPE == EXTERNAL_VIEW ) {
      panel.add(styleBox , BorderLayout.SOUTH );
    } else {
      JToolBar toolBar = cyMenus.getToolBar();
      toolBar.add(styleBox);
      toolBar.addSeparator();
    }
  }


  //----------------------------------------//
  // Focus Management

  /**
   * @param style the NEW VisualStyle
   * @return the OLD VisualStyle
   */
  public VisualStyle setVisualStyle ( VisualStyle style ) {

    VisualStyle old_style = ( VisualStyle )vizMapUI.
      getStyleSelector().
      getToolbarComboBox().
      getSelectedItem();

    vizMapper.setVisualStyle( style );
    vizMapUI.getStyleSelector().getToolbarComboBox().setSelectedItem( style );

    return old_style;
  }



  protected void updateFocus ( String network_id ) {
      
    // System.out.println( "CD: setting focus to: "+network_id );


    // deal with the old Network
    VisualStyle old_style = ( VisualStyle )vizMapUI.
      getStyleSelector().
      getToolbarComboBox().
      getSelectedItem();

    CyNetworkView old_view = Cytoscape.getCurrentNetworkView();
    if ( old_view != null ) {
      old_view.putClientData( VISUAL_STYLE, old_style );
      old_view.putClientData( VIZMAP_ENABLED, new Boolean( old_view.getVisualMapperEnabled() ) );
    }

    // set the current Network/View
    Cytoscape.setCurrentNetwork( network_id );
    if ( Cytoscape.setCurrentNetworkView( network_id ) ) {

 
      // deal with the new Network
      CyNetworkView new_view = Cytoscape.getCurrentNetworkView();
      VisualStyle new_style = ( VisualStyle )new_view.getClientData( VISUAL_STYLE );
      Boolean vizmap_enabled = ( ( Boolean )new_view.getClientData( VIZMAP_ENABLED ) );
      
      if ( new_style == null ) 
        new_style = defaultVisualStyle;
      
      if ( vizmap_enabled == null )
        vizmap_enabled = new Boolean( true );
      
      vizMapper.setNetworkView( new_view );
      if ( new_style != null ) {
        vizMapper.setVisualStyle( new_style );
        vizMapUI.getStyleSelector().getToolbarComboBox().setSelectedItem( new_style );
      }     

      cyMenus.setNodesRequiredItemsEnabled();
      cyMenus.setVisualMapperItemsEnabled( vizmap_enabled.booleanValue() );
      if ( vizmap_enabled.booleanValue() ) {
        new_view.redrawGraph( false, false );
      }
    }
  }

  public void setFocus ( String network_id ) {
    pcs.firePropertyChange( new PropertyChangeEvent( this, NETWORK_VIEW_FOCUSED, null, network_id ) );
    pcs.firePropertyChange( new PropertyChangeEvent( this, NETWORK_VIEW_FOCUS, null, network_id ) );
  }

  /**
   * TO keep things clearer there is one GraphView Controller 
   * per CytoscapeDesktop
   */
  public GraphViewController getGraphViewController () {
    if ( graphViewController == null )
      graphViewController = new GraphViewController();
    
    return graphViewController;
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    

   
      if ( e.getPropertyName() == NETWORK_VIEW_CREATED ) {
        
        // add the new view to the GraphViewController
        getGraphViewController().addGraphView( ( CyNetworkView )e.getNewValue() );
        // pass on the event 
        pcs.firePropertyChange( e );
        
        networkPanel.focusNetworkNode( ( ( CyNetworkView )e.getNewValue() ).getIdentifier() );
        networkPanel.fireFocus( ( ( CyNetworkView )e.getNewValue() ).getIdentifier() );
      } 
  
      else if ( e.getPropertyName() == NETWORK_VIEW_FOCUSED ) {
        // get focus event from NetworkViewManager
        
        updateFocus( e.getNewValue().toString() );
        pcs.firePropertyChange( e );
      }

      else if ( e.getPropertyName() == NETWORK_VIEW_FOCUS ) {
        // get Focus from NetworkPanel
        
        updateFocus( e.getNewValue().toString() );
        pcs.firePropertyChange( e );
      }

      else if ( e.getPropertyName() == Cytoscape.NETWORK_CREATED ) {
        // fire the event so that the NetworkPanel can catch it
        pcs.firePropertyChange( e );
      }

      else if ( e.getPropertyName() == Cytoscape.NETWORK_DESTROYED ) {
        // fire the event so that the NetworkPanel can catch it
        pcs.firePropertyChange( e );
      }

      else if ( e.getPropertyName() == NETWORK_VIEW_DESTROYED ) {
        // remove the view from the GraphViewController
        getGraphViewController().removeGraphView( ( CyNetworkView )e.getNewValue() );
        // pass on the event 
        pcs.firePropertyChange( e );
      }
   
  }
  
  //---------------------------------------------------------------------------//
  // Cytopanels - Public and Protected methods

  /**
   * Gets a cytoPanel given a Compass direction.
   *
   * @param compassDirection Compass Direction (SwingConstants.{SOUTH,EAST,WEST}).
   * @return CytoPanel The CytoPanel that lives in the region specified by compass direction.
   */
  public CytoPanel getCytoPanel(int compassDirection){
	  
	  // return appropriate cytoPanel based on compass direction
	  switch (compassDirection){
	  case SwingConstants.SOUTH:
		  return (CytoPanel)cytoPanelSouth;
	  case SwingConstants.EAST:
		  return (CytoPanel)cytoPanelEast;
	  case SwingConstants.WEST:
		  return (CytoPanel)cytoPanelWest;
	  }

	  // houston we have a problem
	  throw new IllegalArgumentException("Illegal Argument:  "
										 + compassDirection +
										 ".  Must be one of:  SwingConstants.{SOUTH,EAST,WEST}.");
  }
  
  /**
   * Create the CytoPanels UI.
   * @param networkPanel to load on left side of right bimodal.
   * @param networkViewManager to load on left side (CytoPanel West).
   * @return BiModalJSplitPane Object.
   */
  protected BiModalJSplitPane setupCytoPanels (NetworkPanel networkPanel,
											   NetworkViewManager networkViewManager){

	  // bimodals that our Cytopanels Live within
	  BiModalJSplitPane topRightPane = createTopRightPane(networkViewManager);
	  BiModalJSplitPane rightPane = createRightPane(topRightPane);
	  BiModalJSplitPane masterPane = createMasterPane(networkPanel, rightPane);

	  return masterPane;
  } 

  /**
   * Creates the TopRight Pane.
   * @param networkViewManager to load on left side of top right bimodal.
   * @return BiModalJSplitPane Object.
   */
  protected BiModalJSplitPane createTopRightPane(NetworkViewManager networkViewManager){

	  //  create cytopanel with tabs along the top
	  cytoPanelEast = new CytoPanelImp(SwingConstants.EAST,
									   JTabbedPane.TOP,
									   CytoPanelState.HIDE);



	  //  determine proper network view manager component
	  Component networkViewComp = null;
	  if (VIEW_TYPE == TABBED_VIEW){
		  networkViewComp = (Component)networkViewManager.getTabbedPane();
	  }
	  else if (VIEW_TYPE == INTERNAL_VIEW){
		  networkViewComp = (Component)networkViewManager.getDesktopPane();
	  }
	  else if (VIEW_TYPE == EXTERNAL_VIEW){
		  // do nothing
	  }

	  //  create the split pane - we show this on startup
	  BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
														  JSplitPane.HORIZONTAL_SPLIT,
														  BiModalJSplitPane.MODE_HIDE_SPLIT,
														  networkViewComp,
														  cytoPanelEast);

	  // set the cytopanelcontainer
	  cytoPanelEast.setCytoPanelContainer(splitPane);

	  // set the resize weight - left component gets extra space
	  splitPane.setResizeWeight(1.0);

	  // outta here
	  return splitPane;
  }

  /**
   * Creates the Right Panel.
   * @param topRightPane TopRightPane Object.
   * @return BiModalJSplitPane Object
   */
  protected BiModalJSplitPane createRightPane(BiModalJSplitPane topRightPane){

	  //  create cytopanel with tabs along the bottom
	  cytoPanelSouth = new CytoPanelImp(SwingConstants.SOUTH,
									   JTabbedPane.BOTTOM,
									   CytoPanelState.HIDE);

	  //  create the split pane - hidden by default
	  BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
														  JSplitPane.VERTICAL_SPLIT,
														  BiModalJSplitPane.MODE_HIDE_SPLIT,
														  topRightPane,
														  cytoPanelSouth);

	  // set the cytopanel container
	  cytoPanelSouth.setCytoPanelContainer(splitPane);

	  //  set resize weight - top component gets all the extra space.
	  splitPane.setResizeWeight(1.0);

	  // outta here
	  return splitPane;
  }

  /**
   * Creates the Master Split Pane.
   * @param networkPanel to load on left side of CytoPanel (cytoPanelWest).
   * @param rightPane BiModalJSplitPane Object.
   * @return BiModalJSplitPane Object.
   */
  protected BiModalJSplitPane createMasterPane(NetworkPanel networkPanel, BiModalJSplitPane rightPane){

	  //  create cytopanel with tabs along the top
	  cytoPanelWest = new CytoPanelImp(SwingConstants.WEST,
									   JTabbedPane.TOP,
									   CytoPanelState.DOCK);

	  // add the network panel to our tab
	  String tab1Name = new String("Network");
	  cytoPanelWest.add(tab1Name,
						new ImageIcon(getClass().getResource("images/class_hi.gif")),
									  networkPanel,
									  "Cytoscape Network List");

	  //  create the split pane - hidden by default
	  BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
														  JSplitPane.HORIZONTAL_SPLIT,
														  BiModalJSplitPane.MODE_SHOW_SPLIT,
														  cytoPanelWest,
														  rightPane);

	  // set the cytopanel container
	  cytoPanelWest.setCytoPanelContainer(splitPane);

	  // outta here
	  return splitPane;
  }

  // End Cytopanels - Public and Protected methods
  //---------------------------------------------------------------------------//
    
  /**
   * Gets the NetworkView Manager.
   * @return NetworkViewManager Object.
  */
  public NetworkViewManager getNetworkViewManager() {
      return this.networkViewManager;
  }
}
