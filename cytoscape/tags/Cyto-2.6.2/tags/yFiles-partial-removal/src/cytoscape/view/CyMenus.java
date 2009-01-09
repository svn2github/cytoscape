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
package cytoscape.view;
//------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cytoscape.CytoscapeObj;
import cytoscape.actions.*;
import cytoscape.dialogs.ShrinkExpandGraphUI;
import cytoscape.data.annotation.AnnotationGui;
import cytoscape.util.CytoscapeMenuBar;

//------------------------------------------------------------------------------
/**
 * This class creates the menu and tool bars for a Cytoscape window object. It
 * also provides access to individual menus and items.
 */
public class CyMenus {
  CyWindow cyWindow;
  boolean menusInitialized = false;
  CytoscapeMenuBar menuBar;
  JMenu fileMenu, loadSubMenu, saveSubMenu;
  JMenu editMenu;
  //JMenuItem undoMenuItem, redoMenuItem;
  JMenuItem deleteSelectionMenuItem;
  JMenu selectMenu;
  JMenu layoutMenu;
  JMenu vizMenu;
  JMenu opsMenu;
  JMenuItem NO_OPERATIONS;
  JToolBar toolBar;
    

  public CyMenus(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    //the following methods construct the basic bar objects, but
    //don't fill them with menu items and associated action listeners
    createMenuBar();
    toolBar = new JToolBar();
    //default menu item used when the operations menu is empty
    NO_OPERATIONS = new JMenuItem("No operations available");
    NO_OPERATIONS.setEnabled(false);
  }
    
  /**
   * Returns the main menu bar constructed by this object.
   */
  public JMenuBar getMenuBar() {return menuBar;}
    
  /**
   * Returns the submenu that holds menu items such as
   * loading and saving.
   */
  public JMenu getLoadSubMenu() {return loadSubMenu;}
  /**
   * Returns the submenu holding menu items for actions that
   * select nodes and edges in the graph.
   */
  public JMenu getSelectMenu() {return selectMenu;}
  /**
   * Returns the submenu holding menu items for layout actions.
   */
  public JMenu getLayoutMenu() {return layoutMenu;}
  /**
   * Returns the submenu holding menu items associated with
   * the visual mapper.
   */
  public JMenu getVizMenu() {return vizMenu;}
  /**
   * Returns the submenu holding menu items associated with
   * plug-ins. Most plug-ins grab this submenu and add their
   * menu option.
   */
  public JMenu getOperationsMenu() {return opsMenu;}
  /**
   * Checks the operations menu for existing items. If there
   * are none, adds a default item indicating that no plugin
   * menu operations are available. If there is more than one
   * menu item, then this method removes the default item if
   * it exists.
   */
  public void refreshOperationsMenu() {
      if (opsMenu.getItemCount() == 0) {//no real items exist
          opsMenu.add(NO_OPERATIONS);   //so add the default item
      } else if (opsMenu.getItemCount() > 1) {//one real item exists
          //the default item will always be first if it's there
          if (opsMenu.getItem(0) == NO_OPERATIONS) {
              opsMenu.remove(0);  //remove the default item
          }
      }
  }
    
  /**
   * Returns the toolbar object constructed by this class.
   */
  public JToolBar getToolBar() {return toolBar;}
    
  /**
   * @deprecated This method is no longer needed now that the undo
   * manager has been removed. It will soon be removed, because
   * there are better ways to manage the menu items. -AM 12-30-03<P>
   *
   * This helper method enables or disables the menu items
   * associated with the undo manager. The undo menu option
   * is enabled only if there is a previous state to undo to,
   * and similarly for the redo menu option.
   *
   * It may make more sense to give the menu item objects to
   * the undo maanger and let it handle the activation state.
   */
  public void updateUndoRedoMenuItemStatus() {
  }
    
  /**
   * Called when the window switches to edit mode, enabling
   * the menu option for deleting selected objects.
   *
   * Again, the keeper of the edit modes should probably get
   * a reference to the menu item and manage its state.
   */
  public void enableDeleteSelectionMenuItem() {
      if (deleteSelectionMenuItem != null) {
          deleteSelectionMenuItem.setEnabled(true);
      }
  }
    
  /**
   * Called when the window switches to read-only mode, disabling
   * the menu option for deleting selected objects.
   *
   * Again, the keeper of the edit modes should probably get
   * a reference to the menu item and manage its state.
   */
  public void disableDeleteSelectionMenuItem() {
      if (deleteSelectionMenuItem != null) {
          deleteSelectionMenuItem.setEnabled(false);
      }
  }

  /**
   * Creates the menu bar and the various menus and submenus, but
   * defers filling those menus with items until later.
   */
  private void createMenuBar() {
    menuBar = new CytoscapeMenuBar();
    fileMenu    = menuBar.getMenu( "File" );
    loadSubMenu = menuBar.getMenu( "File.Load" );
    saveSubMenu = menuBar.getMenu( "File.Save" );
    editMenu    = menuBar.getMenu( "Edit" );
    selectMenu  = menuBar.getMenu( "Select" );
    layoutMenu  = menuBar.getMenu( "Layout" );
    vizMenu     = menuBar.getMenu( "Visualization" );
    opsMenu     = menuBar.getMenu( "Plugins" );
  }
    
  /**
   * This method should be called by the creator of this object after
   * the constructor has finished. It fills the previously created
   * menu and tool bars with items and action listeners that respond
   * when those items are activated. This needs to come after the
   * constructor is done, because some of the listeners try to access
   * this object in their constructors.
   *
   * Any calls to this method after the first will do nothing.
   */
  public void initializeMenus() {
      if (!menusInitialized) {
	  menusInitialized = true;
	  fillMenuBar();
	  fillToolBar();
      }
  }
  /**
   * Fills the previously created menu bar with a large number of
   * items with attached action listener objects.
   */
  private void fillMenuBar() {
      NetworkView networkView = cyWindow;  //restricted interface
      CytoscapeObj cytoscapeObj = cyWindow.getCytoscapeObj();

      //fill the Load submenu
      JMenuItem mi = loadSubMenu.add(new LoadGraphFileAction(networkView));
      //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
      //JMenuItem mi = loadSubMenu.add(new LoadInteractionFileAction(networkView));
      //mi = loadSubMenu.add(new LoadGMLFileAction(networkView));
      //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
      mi = loadSubMenu.add(new LoadExpressionMatrixAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
      mi = loadSubMenu.add(new LoadBioDataServerAction(networkView));
      mi = loadSubMenu.add(new LoadNodeAttributesAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
      mi = loadSubMenu.add(new LoadEdgeAttributesAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
      
      //fill the Save submenu
      //saveAsGML not supported yet
      //saveSubMenu.add(new SaveAsGMLAction(networkView));
      saveSubMenu.add(new SaveAsInteractionsAction(networkView));
      saveSubMenu.add(new SaveVisibleNodesAction(networkView));
      saveSubMenu.add(new SaveSelectedNodesAction(networkView));
      
      fileMenu.add(new PrintAction(networkView));
        
        mi = fileMenu.add(new CloseWindowAction(cyWindow));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        if (cytoscapeObj.getParentApp() != null) {
            mi = fileMenu.add(new ExitAction(cyWindow));
            mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        }
        
    //fill the Edit menu
    //editing the graph not fully supported in Giny mode
    //deleteSelectionMenuItem = editMenu.add(new DeleteSelectedAction(networkView));
    //deleteSelectionMenuItem.setEnabled(false);

    //fill the Select menu
    JMenu selectNodesSubMenu = new JMenu("Nodes");
    selectMenu.add(selectNodesSubMenu);
    JMenu selectEdgesSubMenu = new JMenu("Edges");
    selectMenu.add(selectEdgesSubMenu);
    JMenu displayNWSubMenu = new JMenu("To New Window");
    selectMenu.add(displayNWSubMenu);
	
	
    // added by larissa 10/09/03
    mi = selectMenu.add(new SelectAllAction(networkView));
    mi = selectMenu.add(new DeselectAllAction(networkView));
    mi = selectMenu.add(new DisplayBrowserAction(networkView));
        
    // mi = selectEdgesSubMenu.add(new EdgeTypeDialogAction());
        
    mi = selectNodesSubMenu.add(new InvertSelectedNodesAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    mi = selectNodesSubMenu.add(new HideSelectedNodesAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
	
    // added by larissa 10/09/03
    mi = selectNodesSubMenu.add(new UnHideSelectedNodesAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
    mi = selectNodesSubMenu.add(new SelectAllNodesAction(networkView));
    mi = selectNodesSubMenu.add(new DeSelectAllNodesAction(networkView));
        
    //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    mi = selectEdgesSubMenu.add(new InvertSelectedEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new HideSelectedEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new UnHideSelectedEdgesAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
    mi = selectEdgesSubMenu.add(new EdgeManipulationAction(networkView));
    mi = selectEdgesSubMenu.add(new SelectAllEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new DeSelectAllEdgesAction(networkView));
        
        
    mi = selectNodesSubMenu.add(new SelectFirstNeighborsAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));

    // RHC Added Menu Items
    //selectNodesSubMenu.add(new GraphObjectSelectionAction(networkView));
    menuBar.addAction( new GraphObjectSelectionAction( networkView ) );
    editMenu.add( new SquiggleAction( networkView ) ); 
    vizMenu.add( new BirdsEyeViewAction( networkView ) );
    
    menuBar.addAction( new AnimatedLayoutAction( networkView ) );
    
    //added by larissa 10/03
    JMenu showExpressionData = new JMenu ("Show Expression Data" );
    vizMenu.add(showExpressionData);
    mi = showExpressionData.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.STAR_PLOT, "... as Star Plots" ) );
    mi = showExpressionData.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.GRID_NODE, "... as Grid Nodes" ) );
    mi = showExpressionData.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.PETAL_NODE, "... as Petal Nodes" ) );
    mi = showExpressionData.add( new ShowExpressionDataAction( networkView, ShowExpressionDataAction.RADAR_NODE, "... as Radar Nodes" ) );
    
    vizMenu.add ( new BackgroundColorAction (networkView) );


    selectNodesSubMenu.add(new AlphabeticalSelectionAction(networkView));
    selectNodesSubMenu.add(new ListFromFileSelectionAction(networkView));
  
    mi = displayNWSubMenu.add(new NewWindowSelectedNodesOnlyAction(cyWindow));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    mi = displayNWSubMenu.add(new NewWindowSelectedNodesEdgesAction(cyWindow));
    mi = displayNWSubMenu.add(new CloneGraphInNewWindowAction(cyWindow));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK));
        
    //fill the Layout menu
    //need to add Giny layout operations
        
    layoutMenu.addSeparator();
    mi = layoutMenu.add(new LayoutAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        
    layoutMenu.addSeparator();
    JMenu alignSubMenu = new JMenu("Align Selected Nodes");
    layoutMenu.add(alignSubMenu);
    alignSubMenu.add(new AlignHorizontalAction(networkView));
    alignSubMenu.add(new AlignVerticalAction(networkView));
    layoutMenu.add(new RotateSelectedNodesAction(networkView));
    //layoutMenu.add(new ReduceEquivalentNodesAction(networkView));
        
    ShrinkExpandGraphUI shrinkExpand =
      new ShrinkExpandGraphUI(cyWindow, layoutMenu);  

    //fill the Visualization menu
    vizMenu.add(new SetVisualPropertiesAction(cyWindow));
  }
    
  /**
   * Fills the toolbar for easy access to commonly used actions.
   */
  private void fillToolBar() {
    NetworkView networkView = cyWindow; //restricted interface
    JButton b;
        
    b = toolBar.add(new ZoomAction(networkView, 0.9));
    b.setIcon(new ImageIcon(getClass().getResource("images/ZoomOut24.gif")));
    b.setToolTipText("Zoom Out");
    b.setBorderPainted(false);
    b.setRolloverEnabled(true);
        
    b = toolBar.add(new ZoomAction(networkView, 1.1));
    b.setIcon(new ImageIcon(getClass().getResource("images/ZoomIn24.gif")));
    b.setToolTipText("Zoom In");
    b.setBorderPainted(false);
        
    b = toolBar.add(new ZoomSelectedAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/ZoomArea24.gif")));
    b.setToolTipText("Zoom Selected Region");
    b.setBorderPainted(false);
        
    b = toolBar.add(new FitContentAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/overview.gif")));
    b.setToolTipText("Zoom out to display all of current graph");
    b.setBorderPainted(false);
        
    // toolBar.addSeparator();
        
    b = toolBar.add(new ShowAllAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/overall.gif")));
    b.setToolTipText("Show all nodes and edges (unhiding as necessary)");
    b.setBorderPainted(false);
        
        
    b = toolBar.add(new HideSelectedAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/Zoom24.gif")));
    b.setToolTipText("Hide Selected Region");
    b.setBorderPainted(false);
        
    toolBar.addSeparator();
        
    b = toolBar.add(new AnnotationGui(cyWindow));
    b.setIcon(new ImageIcon(getClass().getResource("images/AnnotationGui.gif")));
    b.setToolTipText("add annotation to nodes");
    b.setBorderPainted(false);
  }//createToolBar
}

