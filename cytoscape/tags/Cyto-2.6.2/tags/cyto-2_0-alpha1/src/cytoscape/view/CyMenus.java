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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

import cytoscape.CytoscapeObj;
import cytoscape.plugin.AbstractPlugin;
import cytoscape.actions.*;
import cytoscape.dialogs.ShrinkExpandGraphUI;
import cytoscape.data.annotation.AnnotationGui;
import cytoscape.util.CytoscapeMenuBar;
import cytoscape.util.CytoscapeToolBar;
import cytoscape.util.CytoscapeAction;
import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;
//------------------------------------------------------------------------------
/**
 * This class creates the menu and tool bars for a Cytoscape window object. It
 * also provides access to individual menus and items.
 */
public class CyMenus  implements GraphViewChangeListener {
  CyWindow cyWindow;
  boolean menusInitialized = false;
  CytoscapeMenuBar menuBar;
  JMenu fileMenu, loadSubMenu, saveSubMenu;
  JMenu editMenu;
  //JMenuItem undoMenuItem, redoMenuItem;
  JMenuItem deleteSelectionMenuItem;
  JMenu dataMenu;
  JMenu selectMenu;
  JMenu displayNWSubMenu;
  JMenu layoutMenu;
  JMenu vizMenu;
  JMenuItem vizMenuItem, vizMapperItem;
  AbstractAction menuPrintAction;
  AbstractAction menuExportAction;
  JButton saveButton;
  JButton vizButton;
  JMenu opsMenu;
  CytoscapeToolBar toolBar;
  boolean nodesRequiredItemsEnabled;

  public CyMenus(CyWindow cyWindow){
    this.cyWindow = cyWindow;
    //the following methods construct the basic bar objects, but
    //don't fill them with menu items and associated action listeners
    createMenuBar();
    toolBar = new CytoscapeToolBar();
  }

  /**
   * Returns the main menu bar constructed by this object.
   */
  public CytoscapeMenuBar getMenuBar() {return menuBar;}

  /**
   * Returns the menu with items related to file operations.
   */
  public JMenu getFileMenu() {return fileMenu;}
  /**
   * Returns the submenu with items related to loading objects.
   */
  public JMenu getLoadSubMenu() {return loadSubMenu;}
  /**
   * Returns the submenu with items related to saving objects.
   */
  public JMenu getSaveSubMenu() {return saveSubMenu;}
  /**
   * returns the menu with items related to editing the graph.
   */
  public JMenu getEditMenu() {return editMenu;}
  /**
   * Returns the menu with items related to data operations.
   */
  public JMenu getDataMenu() {return dataMenu;}
  /**
   * Returns the menu with items related to selecting
   * nodes and edges in the graph.
   */
  public JMenu getSelectMenu() {return selectMenu;}
  /**
   * Returns the menu with items realted to layout actions.
   */
  public JMenu getLayoutMenu() {return layoutMenu;}
  /**
   * Returns the menu with items related to visualiation.
   */
  public JMenu getVizMenu() {return vizMenu;}
  /**
   * Returns the menu with items associated with plug-ins.
   * Most plug-ins grab this menu and add their menu option.
   * The plugins should then call refreshOperationsMenu to
   * update the menu.
   */
  public JMenu getOperationsMenu() {return opsMenu;}
  /**
   * @deprecated This method is no longer needed now that we don't
   * use the NO_OPERATIONS menu placeholder.
   *
   * This method does nothing.
   */
  public void refreshOperationsMenu() {
  }

  /**
   * Returns the toolbar object constructed by this class.
   */
  public CytoscapeToolBar getToolBar() {return toolBar;}


  /**
   * Takes a CytoscapeAction and will add it to the MenuBar or the
   * Toolbar as is appropriate.
   */
  public void addCytoscapeAction ( CytoscapeAction action ) {
    if ( action.isInMenuBar() ) {
      getMenuBar().addAction( action );
    }
    if ( action.isInToolBar() ) {
      getToolBar().addAction( action );
    }
  }


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
   * Enables the menu items related to the visual mapper if the argument
   * is true, else disables them. This method should only be called from
   * the window that holds this menu.
   */
  public void setVisualMapperItemsEnabled(boolean newState) {
      vizMenuItem.setEnabled(newState);
      vizButton.setEnabled(newState);
      vizMapperItem.setText(newState ?
              "Disable Visual Mapper" : "Enable Visual Mapper");
  }

  /**
   * Enables or disables save, print, and display nodes
   * in new window GUI functions, based on the number of nodes
   * in this window's graph perspective. This function should be
   * called after every operation which adds or removes nodes from
   * the current window.
   */
  public void setNodesRequiredItemsEnabled() {
      boolean newState = cyWindow.getView().getGraphPerspective().getNodeCount() > 0;
newState = true; //TODO: remove this once the GraphViewChangeListener system is working
      if (newState == nodesRequiredItemsEnabled) return;
      saveButton.setEnabled(newState);
      saveSubMenu.setEnabled(newState);
      menuPrintAction.setEnabled(newState);
      menuExportAction.setEnabled(newState);
      displayNWSubMenu.setEnabled(newState);
      nodesRequiredItemsEnabled = newState;
  }

  /**
   * Update the UI menus and buttons. When the graph view is changed,
   * this method is the listener which
   * will update the UI items, enabling or disabling items which are
   * only available when the graph view is non-empty.
   * @param e
   */
  public void graphViewChanged(GraphViewChangeEvent e) {
      // Do this in the GUI Event Dispatch thread...
      SwingUtilities.invokeLater( new Runnable() {
        public void run() {
          setNodesRequiredItemsEnabled();
      } } );
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
    dataMenu    = menuBar.getMenu( "Data" );
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
          nodesRequiredItemsEnabled = false;
          saveButton.setEnabled(false);
          saveSubMenu.setEnabled(false);
          menuPrintAction.setEnabled(false);
          menuExportAction.setEnabled(false);
          displayNWSubMenu.setEnabled(false);
          setNodesRequiredItemsEnabled();
          cyWindow.getView().addGraphViewChangeListener(this);
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
      JMenuItem mi = loadSubMenu.add(new LoadGraphFileAction(cyWindow,this));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
      //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
      //JMenuItem mi = loadSubMenu.add(new LoadInteractionFileAction(networkView));
      //mi = loadSubMenu.add(new LoadGMLFileAction(networkView));
      //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
      mi = loadSubMenu.add(new LoadNodeAttributesAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
      mi = loadSubMenu.add(new LoadEdgeAttributesAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
      mi = loadSubMenu.add(new LoadExpressionMatrixAction(networkView));
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
      mi = loadSubMenu.add(new LoadBioDataServerAction(networkView));

      //fill the Save submenu
      saveSubMenu.add(new SaveAsGMLAction(networkView));
      saveSubMenu.add(new SaveAsInteractionsAction(networkView));
      saveSubMenu.add(new SaveVisibleNodesAction(networkView));
      saveSubMenu.add(new SaveSelectedNodesAction(networkView));
      menuPrintAction = new PrintAction(networkView);
      menuExportAction = new ExportAction(networkView);
      fileMenu.add(menuPrintAction);
      fileMenu.add(menuExportAction);

      //mi = fileMenu.add(new CloseWindowAction(cyWindow)); removed 2004-03-08
      //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
      if (cytoscapeObj.getParentApp() != null) {
          mi = fileMenu.add(new ExitAction(cyWindow));
          mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
      }

    //fill the Edit menu
    //editing the graph not fully supported in Giny mode
    //deleteSelectionMenuItem = editMenu.add(new DeleteSelectedAction(networkView));
    //deleteSelectionMenuItem.setEnabled(false);
    editMenu.add( new SquiggleAction( networkView ) );

    //fill the Data menu
    mi = dataMenu.add(new DisplayBrowserAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    menuBar.addAction( new GraphObjectSelectionAction( networkView ) );
    mi = dataMenu.add(new EdgeManipulationAction(networkView));

    //fill the Select menu
    selectMenu.add( new SelectionModeAction(networkView));
    JMenu selectNodesSubMenu = new JMenu("Nodes");
    selectMenu.add(selectNodesSubMenu);
    JMenu selectEdgesSubMenu = new JMenu("Edges");
    selectMenu.add(selectEdgesSubMenu);
    displayNWSubMenu = new JMenu("To New Window");
    selectMenu.add(displayNWSubMenu);


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
    mi = selectNodesSubMenu.add(new SelectFirstNeighborsAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    selectNodesSubMenu.add(new AlphabeticalSelectionAction(networkView));
    selectNodesSubMenu.add(new ListFromFileSelectionAction(networkView));

    //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    mi = selectEdgesSubMenu.add(new InvertSelectedEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new HideSelectedEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new UnHideSelectedEdgesAction(networkView));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
    mi = selectEdgesSubMenu.add(new SelectAllEdgesAction(networkView));
    mi = selectEdgesSubMenu.add(new DeSelectAllEdgesAction(networkView));

    // RHC Added Menu Items
    //selectNodesSubMenu.add(new GraphObjectSelectionAction(networkView));
    //editMenu.add( new SquiggleAction( networkView ) );

    mi = displayNWSubMenu.add(new NewWindowSelectedNodesOnlyAction(cyWindow));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.SHIFT_MASK|ActionEvent.CTRL_MASK));
    mi = displayNWSubMenu.add(new NewWindowSelectedNodesEdgesAction(cyWindow));
    mi = displayNWSubMenu.add(new CloneGraphInNewWindowAction(cyWindow));
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

    mi = selectMenu.add(new SelectAllAction(networkView));
    mi = selectMenu.add(new DeselectAllAction(networkView));

    //fill the Layout menu
    //need to add Giny layout operations

    //layoutMenu.addSeparator();
    //mi = layoutMenu.add(new LayoutAction(networkView));
    //mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
    layoutMenu.add(new SpringEmbeddedLayoutAction(networkView));

    layoutMenu.addSeparator();
    JMenu alignSubMenu = new JMenu("Align Selected Nodes");
    //layoutMenu.add(alignSubMenu);
    //alignSubMenu.add(new AlignHorizontalAction(networkView));
    //alignSubMenu.add(new AlignVerticalAction(networkView));
    //layoutMenu.add(new RotateSelectedNodesAction(networkView));
    //layoutMenu.add(new ReduceEquivalentNodesAction(networkView));

    ShrinkExpandGraphUI.makeShrinkExpandGraphUI(cyWindow, layoutMenu);

    //fill the Visualization menu
    vizMenu.add( new BirdsEyeViewAction( networkView ) );
    //JMenu showExpressionData = new JMenu ("Show Expression Data" );

    vizMenu.add ( new BackgroundColorAction (networkView) );
    vizMenuItem = vizMenu.add(new SetVisualPropertiesAction(cyWindow));
    vizMapperItem = vizMenu.add(new ToggleVisualMapperAction(cyWindow));

    menuBar.addAction( new AnimatedLayoutAction( networkView ) );
    opsMenu.add(new LoadPluginAction (cyWindow.getCytoscapeObj()));
    opsMenu.add(new LoadPluginDirectoryAction (cyWindow.getCytoscapeObj()));
    opsMenu.addSeparator();

  }

  /**
   * Fills the toolbar for easy access to commonly used actions.
   */
  private void fillToolBar() {
    NetworkView networkView = cyWindow; //restricted interface
    JButton b;

    b = toolBar.add( new LoadGraphFileAction( cyWindow, this, null ) );
    b.setIcon( new ImageIcon(getClass().getResource("images/new/load36.gif") ) );
    b.setToolTipText("Load Graph");
    b.setBorderPainted(false);
    b.setRolloverEnabled(true);

    saveButton = toolBar.add( new SaveAsGMLAction( networkView, null ) );
    saveButton.setIcon( new ImageIcon(getClass().getResource("images/new/save36.gif") ) );
    saveButton.setToolTipText("Save Graph as GML");
    saveButton.setBorderPainted(false);
    saveButton.setRolloverEnabled(true);
    saveButton.setEnabled(false);

    toolBar.addSeparator();



    final ZoomAction zoom_in = new ZoomAction(networkView, 1.1);
    final JButton zoomInButton = new JButton();
    zoomInButton.setIcon(new ImageIcon(getClass().getResource("images/new/zoom_in36.gif")));
    zoomInButton.setToolTipText("Zoom In");
    zoomInButton.setBorderPainted(false);
    zoomInButton.setRolloverEnabled(true);
    zoomInButton.addMouseListener( new MouseListener () {
        public void 	mouseClicked(MouseEvent e) {
          zoom_in.zoom();
        }

         public void 	mouseEntered(MouseEvent e) {}

         public void 	mouseExited(MouseEvent e) {}

         public void 	mousePressed(MouseEvent e) {
           zoomInButton.setSelected( true );
        }

         public void 	mouseReleased(MouseEvent e) {
           zoomInButton.setSelected( false );
        }
      } );


    final ZoomAction zoom_out = new ZoomAction(networkView, 0.9);
    final JButton zoomOutButton = new JButton();
    zoomOutButton.setIcon(new ImageIcon(getClass().getResource("images/new/zoom_out36.gif")));
    zoomOutButton.setToolTipText("Zoom Out");
    zoomOutButton.setBorderPainted(false);
    zoomOutButton.setRolloverEnabled(true);
    zoomOutButton.addMouseListener( new MouseListener () {
         public void 	mouseClicked(MouseEvent e) {
          zoom_out.zoom();
        }

         public void 	mouseEntered(MouseEvent e) {}

         public void 	mouseExited(MouseEvent e) {}

         public void 	mousePressed(MouseEvent e) {
           zoomOutButton.setSelected( true );
        }

         public void 	mouseReleased(MouseEvent e) {
           zoomOutButton.setSelected( false );
        }
      } );


    zoomOutButton.addMouseWheelListener( new MouseWheelListener () {
        public void	mouseWheelMoved(MouseWheelEvent e) {
          if ( e.getWheelRotation() < 0 ) {
            zoom_in.zoom();
          } else {
            zoom_out.zoom();
          }

        }
      }
                                   );

     zoomInButton.addMouseWheelListener( new MouseWheelListener () {
         public void	mouseWheelMoved(MouseWheelEvent e) {
         if ( e.getWheelRotation() < 0 ) {
            zoom_in.zoom();
          } else {
            zoom_out.zoom();
          }
         }
       }
                                   );

    toolBar.add( zoomOutButton );
    toolBar.add( zoomInButton );

    b = toolBar.add(new ZoomSelectedAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/new/crop36.gif")));
    b.setToolTipText("Zoom Selected Region");
    b.setBorderPainted(false);

    b = toolBar.add(new FitContentAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/new/fit36.gif")));
    b.setToolTipText("Zoom out to display all of current Graph");
    b.setBorderPainted(false);

    // toolBar.addSeparator();

    b = toolBar.add(new ShowAllAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/new/add36.gif")));
    b.setToolTipText("Show all Nodes and Edges (unhiding as necessary)");
    b.setBorderPainted(false);


    b = toolBar.add(new HideSelectedAction(networkView));
    b.setIcon(new ImageIcon(getClass().getResource("images/new/delete36.gif")));
    b.setToolTipText("Hide Selected Region");
    b.setBorderPainted(false);

    toolBar.addSeparator();

    b = toolBar.add(new AnnotationGui(cyWindow));
    b.setIcon(new ImageIcon(getClass().getResource("images/new/ontology36.gif")));
    b.setToolTipText("Add Annotation Ontology to Nodes");
    b.setBorderPainted(false);

    toolBar.addSeparator();

    vizButton = toolBar.add(new SetVisualPropertiesAction(cyWindow, false));
    vizButton.setIcon(new ImageIcon(getClass().getResource("images/new/color_wheel36.gif")));
    vizButton.setToolTipText("Set Visual Properties");
    vizButton.setBorderPainted(false);

  }//createToolBar
}

