package cytoscape.view;

import cytoscape.CytoscapeObj;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;

import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.view.CyEdgeView;
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
import javax.swing.event.*;
import java.beans.*;

/**
 * The CytoscapeDesktop is the central Window
 * for working with Cytoscape
 */

public class CytoscapeDesktop 
  extends 
    JFrame 
  implements
    PluginListener,
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
    super( "Cytoscape Desktop" );
    this.VIEW_TYPE = view_type;
    initialize();
  }
  
  protected void initialize () {
  
    setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getResource("images/c16.png") ) );

    JPanel main_panel = new JPanel();

    main_panel.setLayout( new BorderLayout() );

    //------------------------------//
    // Set up the Panels, Menus, and Event Firing

    networkPanel = new NetworkPanel( this );
    cyMenus = new CyMenus();
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


    cyMenus.initializeMenus();

    // create the CytoscapeDesktop
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
      

      if ( !System.getProperty("os.name").startsWith( "Mac" ) ) {
        JFrame menuFrame = new JFrame("Cytoscape Menus");
        menuFrame.setJMenuBar(cyMenus.getMenuBar());
        menuFrame.setSize( 400, 60 );
        menuFrame.setVisible( true );
      } else {
        setJMenuBar(cyMenus.getMenuBar());
      }
      
    }

    //------------------------------//
    // Set up the VizMapper
    setupVizMapper( main_panel );
    
    //------------------------------//
    // Window Closing, Program Shutdown

    
    Cytoscape.getCytoscapeObj().getPluginRegistry().addPluginListener( this );

    //add a listener to save the visual mapping catalog on exit
    //this should eventually be replaced by a method in Cytoscape.java itself
    //to save the catalog just before exiting the program
    //TODO: Allow other things to be notified if the
    //      Program is exiting.
    final CytoscapeObj theCytoscapeObj = Cytoscape.getCytoscapeObj();
    final CytoscapeDesktop thisWindow = this;
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          Cytoscape.exit();
        }
        public void windowClosed() {
          
        }
    });
    
    //add the parent app as a listener, to manage the session when this window closes
    //is this strictly necessary, since cytoscape.java listens for
    //WindowOpened events? -AM 2003/06/24
    addWindowListener( Cytoscape.getCytoscapeObj().getParentApp() );
   


    // show the Desktop
    setContentPane( main_panel );
    pack();
    if ( VIEW_TYPE != EXTERNAL_VIEW )
      setSize( 700, 700 );
    setVisible( true );

  }

  public NetworkPanel getNetworkPanel () {
    return networkPanel;
  }

  //----------------------------------------//
  // Common Desktop Variables


  /**
   * @deprecated
   * Returns a reference to the global Cytoscape object.
   * {@link cytoscape.Cytoscape#getCytoscapeObj} 
   */
  public CytoscapeObj getCytoscapeObj () {
    return Cytoscape.getCytoscapeObj();
  }

  /**
   * @deprecated
   * returns the network displayed in this window.
   * { @link cytoscape.Cytoscape#getCurrentNetwork}
   */
  public CyNetwork getNetwork () {
    return Cytoscape.getCurrentNetwork();
  }
 
  /**
   * @deprecated
   * Returns the UI component that renders the displayed graph.
   * {@link cytoscape.Cytoscape#getCurrentNetworkView}
   */
  public GraphView getView () {
    return ( GraphView )Cytoscape.getCurrentNetworkView();
  }

 
  /**
   * @deprecated
   * {@link cytoscape.Cytoscape#getDesktop}
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
   * @deprecated
   */
  public String getWindowTitle () {
    return "CD";
  }

  /**
   * @deprecated
   * { @link CyNetworkView#setTitle( String ) }
   */
  public void setWindowTitle ( String newTitle ) {
  }

  /**
   * @deprecated
   */
  public void setInteractivity ( boolean newState ) {}


  /**
   * @deprecated
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }
   * Redraws the graph - equivalent to redrawGraph(false, true).
   * That is, no new layout will be performed, but the visual
   * appearances will be reapplied.
   */
  public void redrawGraph() {
    Cytoscape.getCurrentNetworkView().redrawGraph( false, true );
  }

  /**
   * @deprecated
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }
   * Redraws the graph - equivalent to redrawGraph(doLayout, true).
   * That is, the visual appearances will be reapplied, and layout
   * will be done iff the argument is true.
   */
  public void redrawGraph(boolean doLayout) {
    // apply appearances by default
    Cytoscape.getCurrentNetworkView().redrawGraph( doLayout, true );
  }

  /**
   * @deprecated
   * {@link CyNetworkView#redrawGraph( boolean, boolean ) }  
   * Redraws the graph. A new layout will be performed if the first
   * argument is true, and the visual appearances will be recalculated
   * and reapplied by the visual mapper if the second argument is true
   * and the visual mapper is not disabled.
   */
  public void redrawGraph(boolean doLayout, boolean applyAppearances) {
    Cytoscape.getCurrentNetworkView().redrawGraph( doLayout, applyAppearances );
  }

  /**
   * @deprecated
   */
  public void showWindow ( int width, int height) {
  }

  /**
   * @deprecated
   */
  public void showWindow() {}

  /**
   * @deprecated
   */
  public void applyLayout ( GraphView lview ) {
    //Cytoscape.getCurrentNetworkView().applyLayout( new SpringEmbeddedLayouter() );
  }

  /**
   * @deprecated
   */
  public void applySelLayout() {
    
    //int[] selNodes = Cytoscape.getCurrentNetworkView().getSelectedNodeIndices();
    //int[] selEdges = Cytoscape.getCurrentNetworkView().getSelectedEdgeIndices();
    //Cytoscape.getCurrentNetworkView().applyLockedLayout( new SpringEmbeddedLayouter(), selNodes, selEdges );

  }

  /**
   * @deprecated
   */
  public void applyVizmapSettings() {
    Cytoscape.getCurrentNetworkView().redrawGraph( false, true );
  }

  /**
   * @deprecated
   */
  public void setVisualMapperEnabled(boolean newState) {
    Cytoscape.getCurrentNetworkView().setVisualMapperEnabled( newState );
  }

  /**
   * @deprecated
   */
  public void toggleVisualMapperEnabled() {
    Cytoscape.getCurrentNetworkView().toggleVisualMapperEnabled();
  }

  /**
   * @deprecated
   */
  public void switchToReadOnlyMode () {
  }
 
  /**
   * @deprecated
   */
  public void switchToEditMode (){
  }


   /**
   * Load in the Plugins
   */
  public void setupPlugins () {
    updatePlugins();
  }
  /**
   * Implemenation of the PluginListener interface. Triggers update of
   * currently loaded plugins.
   */
  public void pluginRegistryChanged(PluginEvent event) {
    updatePlugins();
  }
  protected void updatePlugins () {

    //poll Plugin Registry for new plugins since last update
    PluginUpdateList pul = Cytoscape.getCytoscapeObj().getPluginRegistry().getPluginsLoadedSince(lastPluginRegistryUpdate);
    Class neededPlugin[] = pul.getPluginArray();
    for (int i = 0; i < neededPlugin.length; i++) {

      if ( AbstractPlugin.class .isAssignableFrom( neededPlugin[i] ) ) {
        // System.out.println( "AbstractPlugin Loaded" );
        AbstractPlugin.loadPlugin( neededPlugin[i], 
                                   Cytoscape.getCytoscapeObj(),
                                   ( cytoscape.view.CyWindow ) Cytoscape.getDesktop() );
      } 

      else if ( CytoscapePlugin.class.isAssignableFrom( neededPlugin[i] ) ) {
        // System.out.println( "CytoscapePlugin Loaded" );
        CytoscapePlugin.loadPlugin( neededPlugin[i] );
      }
    }
    lastPluginRegistryUpdate = pul.getTimestamp();
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
    
    // BUG: vizMapper.applyAppearances() gets called twice here
    
    CalculatorCatalog calculatorCatalog = Cytoscape.getCytoscapeObj().getCalculatorCatalog();

    //try to get visual style from properties
    Properties configProps = Cytoscape.getCytoscapeObj().getConfiguration().getProperties();
    VisualStyle vs = null;
    String vsName = configProps.getProperty("visualStyle");
    if (vsName != null) {vs = calculatorCatalog.getVisualStyle(vsName);}
    if (vs == null) {//none specified, or not found; use the default
        vs = calculatorCatalog.getVisualStyle("default");
    }

    // create the VisualMappingManager using default values for now
    // TODO: as CyNetworkViews get the focus the VizMapper will update
    //       and the constructor should reflect this.
    this.vizMapper = new VisualMappingManager( Cytoscape.getCurrentNetworkView(),
                                               calculatorCatalog, 
                                               vs,
                                               Cytoscape.getCytoscapeObj().getLogger());
    
    defaultVisualStyle = calculatorCatalog.getVisualStyle("default");

    // craete the VizMapUI
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
      vizMapper.setVisualStyle( new_style );
      vizMapUI.getStyleSelector().getToolbarComboBox().setSelectedItem( new_style );
     
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
  
 



}
