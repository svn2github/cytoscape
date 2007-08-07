package cytoscape.view;

import cytoscape.*;
import giny.view.GraphView;
import cytoscape.visual.*;
import cytoscape.visual.ui.*;
import javax.swing.JFrame;


/**
 * @deprecated
 * All of the functionalitiy is now split between:
 * {@link CytoscapeDesktop} and {@link CyNetworkView} 
 * <hr>
 * This class represents a visible window displaying a network. It includes
 * all of the UI components and the the graph view.
 */
public interface CyWindow {



  public void showWindow ( int width, int height) ;


  public void showWindow() ;



  /**
   * returns the network displayed in this window.
   */
  public CyNetwork getNetwork ();


  /**
   * Returns the UI component that renders the displayed graph.
   */
  public GraphView getView();
  //------------------------------------------------------------------------------
  /**
   * Returns the <code>cytoscape.view.GraphViewController</code> that keeps
   * the <code>giny.model.GraphPerspective</code> contained in <code>CyNetwork</code>
   * synchronized to the <code>giny.view.GraphView</code> in this <code>CyWindow</code>.
   *
   * @return a <code>cytoscape.view.GraphViewController</code>
   */
  public GraphViewController getGraphViewController ();
  //------------------------------------------------------------------------------
  /**
   * Returns the visual mapping manager that controls the appearance
   * of nodes and edges in this display.
   */
  public VisualMappingManager getVizMapManager();
  //------------------------------------------------------------------------------
  /**
   * returns the top-level UI object for the visual mapper.
   */
  public VizMapUI getVizMapUI();
  //------------------------------------------------------------------------------
  /**
   * returns the main window frame. Note that this class is itself
   * an instance of JPanel, and is the content pane of the main frame.
   */
  public JFrame getMainFrame();
  //------------------------------------------------------------------------------
  /**
   * Returns the title of the window, guaranteed to be non-null.
   */
  public String getWindowTitle();
  //------------------------------------------------------------------------------
  /**
   * Sets the window title. The actual title (as displayed on the
   * frame title bar, and returned by a call to getWindowTitle)
   * is a concatenation of a default header string and the argument
   */
  public void setWindowTitle(String newTitle);
  //------------------------------------------------------------------------------
  /**
   * Returns the object that holds references to the menu bars and
   * several of the major submenus.
   */
  public CyMenus getCyMenus() ;

 

 
  /**
   * @deprecated 
   * Since networks are now managed this should never be used.  For 
   * now it will assume that you meant to create a NetworkView for it
   * and display that view.
   *
   * Changes the network displayed in this window. This display will
   * be updated to show and reference this new network, and new listeners
   * will be attached to the network and its graph. Note that this switch
   * will take place even if the new network is the same object as the
   * current network (this case is useful when a new graph is loaded into
   * an existing network object).
   *
   * If the newNetwork argument is null, this method does nothing.
   *
   * @param newNetwork  the new network to display
   */
  public void setNewNetwork( CyNetwork newNetwork ) ;
 

  //------------------------------------------------------------------------------
  /**
   * Sets the interactivity state of this window. If the argument is
   * false. disables user interaction with the graph view and menus.
   * If the argument is true, user interaction is enabled.
   *
   * Currently, interaction with the view is never disabled since
   * Giny doesn't have this feature available yet.
   *
   * This class is initialized with interactivity off; it is turned
   * on by the showWindow method.
   */
  public void setInteractivity (boolean newState) ; // setInteractivity
  //------------------------------------------------------------------------------
  /**
   * Redraws the graph - equivalent to redrawGraph(false, true).
   * That is, no new layout will be performed, but the visual
   * appearances will be reapplied.
   */
  public void redrawGraph() ;
  //------------------------------------------------------------------------------
  /**
   * Redraws the graph - equivalent to redrawGraph(doLayout, true).
   * That is, the visual appearances will be reapplied, and layout
   * will be done iff the argument is true.
   */
  public void redrawGraph(boolean doLayout) ;
  //------------------------------------------------------------------------------
  /**
   * Redraws the graph. A new layout will be performed if the first
   * argument is true, and the visual appearances will be recalculated
   * and reapplied by the visual mapper if the second argument is true
   * and the visual mapper is not disabled.
   */
  public void redrawGraph(boolean doLayout, boolean applyAppearances) ;
  //------------------------------------------------------------------------------
  /**
   * Performs a layout operation on the giny graph displayed in this window,
   * using the default layouter for now
   */
  public void applyLayout(GraphView lview);

  /**
   * Applies a layout to only the selected nodes or edges. Does this work?
   */
  public void applySelLayout();
 
 

  /**
   * Enables the visual mapper if the argument is true, otherwise disables the
   * visual mapper. When the vsual mapper is disabled, the corresponding menu items
   * are disabled and the visual mappings will not be reapplied when the graph
   * is redrawn, even if requested.
   *
   * When enabling the visual mapper, this method forces a redraw of the graph
   * to reapply the current visual style.
   */
  public void setVisualMapperEnabled(boolean newState) ;

  /**
   * If enabled, disable the visual mapper (and vice versa). Menu items and
   * redraw behavior follow the semantics of setVisualMapperEnabled()
   */
  public void toggleVisualMapperEnabled() ;

 
  public void switchToReadOnlyMode ();

  public void switchToEditMode ();


}

