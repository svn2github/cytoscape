/*
 File: CyMenus.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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

//------------------------------------------------------------------------------
package cytoscape.view;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.actions.AlphabeticalSelectionAction;
import cytoscape.actions.BendSelectedEdgesAction;
import cytoscape.actions.BirdsEyeViewAction;
import cytoscape.actions.BookmarkAction;
import cytoscape.actions.CloneGraphInNewWindowAction;
import cytoscape.actions.CreateNetworkViewAction;
import cytoscape.actions.CytoPanelAction;
import cytoscape.actions.DeSelectAllEdgesAction;
import cytoscape.actions.DeSelectAllNodesAction;
import cytoscape.actions.DeselectAllAction;
import cytoscape.actions.DestroyNetworkAction;
import cytoscape.actions.DestroyNetworkViewAction;
import cytoscape.actions.ExitAction;
import cytoscape.actions.ExportAsGMLAction;
import cytoscape.actions.ExportAsGraphicsAction;
import cytoscape.actions.ExportAsInteractionsAction;
import cytoscape.actions.ExportAsXGMMLAction;
import cytoscape.actions.ExportEdgeAttributesAction;
import cytoscape.actions.ExportNodeAttributesAction;
import cytoscape.actions.ExportVizmapAction;
import cytoscape.actions.FitContentAction;
import cytoscape.actions.HelpAboutAction;
import cytoscape.actions.HelpContactHelpDeskAction;
import cytoscape.actions.HideSelectedEdgesAction;
import cytoscape.actions.HideSelectedNodesAction;
import cytoscape.actions.ImportEdgeAttributesAction;
import cytoscape.actions.ImportExpressionMatrixAction;
import cytoscape.actions.ImportGraphFileAction;
import cytoscape.actions.ImportNodeAttributesAction;
import cytoscape.actions.ImportVizmapAction;
import cytoscape.actions.InvertSelectedEdgesAction;
import cytoscape.actions.InvertSelectedNodesAction;
import cytoscape.actions.ListFromFileSelectionAction;
import cytoscape.actions.NewSessionAction;
import cytoscape.actions.NewWindowSelectedNodesEdgesAction;
import cytoscape.actions.NewWindowSelectedNodesOnlyAction;
import cytoscape.actions.OpenSessionAction;
import cytoscape.actions.PluginManagerAction;
import cytoscape.actions.PreferenceAction;
import cytoscape.actions.PrintAction;
import cytoscape.actions.ProxyServerAction;
import cytoscape.actions.SaveSessionAction;
import cytoscape.actions.SaveSessionAsAction;
import cytoscape.actions.SelectAllAction;
import cytoscape.actions.SelectAllEdgesAction;
import cytoscape.actions.SelectAllNodesAction;
import cytoscape.actions.SelectFirstNeighborsAction;
import cytoscape.actions.SelectionModeAction;
import cytoscape.actions.SetVisualPropertiesAction;
import cytoscape.actions.StraightenSelectedEdgesAction;
import cytoscape.actions.ToggleVisualMapperAction;
import cytoscape.actions.UnHideSelectedEdgesAction;
import cytoscape.actions.UnHideSelectedNodesAction;
import cytoscape.actions.ZoomAction;
import cytoscape.actions.ZoomSelectedAction;

import cytoscape.util.undo.UndoAction;
import cytoscape.util.undo.RedoAction;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.CytoscapeMenuBar;
import cytoscape.util.CytoscapeToolBar;

import cytoscape.view.cytopanels.CytoPanel;

//------------------------------------------------------------------------------
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.help.CSH;
import javax.help.HelpBroker;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


//------------------------------------------------------------------------------
/**
 * Creates the menu and tool bars for a Cytoscape window object. It
 * also provides access to individual menus and items.<BR>
 * <p>
 * AddAction takes one more optional argument to specify index. Plugin
 * writers can use this function to specify the location of the menu item.
 * </p>
 */
public class CyMenus implements GraphViewChangeListener {
	boolean menusInitialized = false;
	CytoscapeMenuBar menuBar;
	JMenu fileMenu;
	JMenu loadSubMenu;
	JMenu saveSubMenu;
	JMenu newSubMenu;
	JMenu newSubMenu2;
	JMenu editMenu;
	JMenu viewMenu;
	JMenu viewSubMenu;
	JMenu selectMenu;
	JMenu displayNWSubMenu;
	JMenu layoutMenu;
	JMenu vizMenu;
	JMenu helpMenu;
	JMenu cytoPanelMenu;
	CytoscapeAction menuPrintAction;
	CytoscapeAction menuExportAction;
	CytoscapeAction menuSaveSessionAction;
	CytoscapeAction menuSaveSessionAsAction;
	CytoscapeAction menuOpenSessionAction;
	CytoscapeAction networkOverviewAction;
	JMenuItem vizMenuItem;
	JMenuItem vizMapperItem;
	JCheckBoxMenuItem cytoPanelWestItem;
	JCheckBoxMenuItem cytoPanelEastItem;
	JCheckBoxMenuItem cytoPanelSouthItem;
	JCheckBoxMenuItem networkOverviewItem;
	JMenuItem helpContentsMenuItem;
	JMenuItem helpAboutMenuItem;
	JMenuItem helpContactHelpDeskMenuItem;
	JButton openSessionButton;
	JButton saveButton;
	JButton zoomInButton;
	JButton zoomOutButton;
	JButton zoomSelectedButton;
	JButton zoomDisplayAllButton;
	JButton showAllButton;
	JButton hideSelectedButton;
	JButton annotationButton;
	JButton vizButton;
	JMenu opsMenu;
	CytoscapeToolBar toolBar;
	boolean nodesRequiredItemsEnabled;

	/**
	 * Creates a new CyMenus object.
	 */
	public CyMenus() {
		// the following methods construct the basic bar objects, but
		// don't fill them with menu items and associated action listeners
		createMenuBar();
		toolBar = new CytoscapeToolBar();
	}

	/**
	 * Returns the main menu bar constructed by this object.
	 */
	public CytoscapeMenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * Returns the menu with items related to file operations.
	 */
	public JMenu getFileMenu() {
		return fileMenu;
	}

	/**
	 * Returns the submenu with items related to loading objects.
	 */
	public JMenu getLoadSubMenu() {
		return loadSubMenu;
	}

	/**
	 * Returns the submenu with items related to saving objects.
	 */
	public JMenu getSaveSubMenu() {
		return saveSubMenu;
	}

	/**
	 * returns the menu with items related to editing the graph.
	 */
	public JMenu getEditMenu() {
		return editMenu;
	}

	/**
	 * Returns the menu with items related to data operations.
	 */
	public JMenu getViewMenu() {
		return viewMenu;
	}

	/**
	 * Returns the menu with items related to selecting nodes and edges in the
	 * graph.
	 */
	public JMenu getSelectMenu() {
		return selectMenu;
	}

	/**
	 * Returns the menu with items realted to layout actions.
	 */
	public JMenu getLayoutMenu() {
		return layoutMenu;
	}

	/**
	 * Returns the menu with items related to visualiation.
	 */
	public JMenu getVizMenu() {
		return vizMenu;
	}

	/**
	 * Returns the help menu.
	 */
	public JMenu getHelpMenu() {
		return helpMenu;
	}

	/**
	 * Returns the menu with items associated with plug-ins. Most plug-ins grab
	 * this menu and add their menu option. The plugins should then call
	 * refreshOperationsMenu to update the menu.
	 */
	public JMenu getOperationsMenu() {
		return opsMenu;
	}

	/**
	 * Returns the File.New.Network menu.
	 */
	public JMenu getNewNetworkMenu() {
		return newSubMenu2;
	}

	/**
	 * Returns the toolbar object constructed by this class.
	 */
	public CytoscapeToolBar getToolBar() {
		return toolBar;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param action DOCUMENT ME!
	 */
	public void addAction(CytoscapeAction action) {
		addCytoscapeAction(action);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param action DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 */
	public void addAction(CytoscapeAction action, int index) {
		addCytoscapeAction(action, index);
	}

	/**
	 * Takes a CytoscapeAction and will add it to the MenuBar or the Toolbar as
	 * is appropriate.
	 */
	public void addCytoscapeAction(CytoscapeAction action) {
		if (action.isInMenuBar()) {
			getMenuBar().addAction(action);
		}

		if (action.isInToolBar()) {
			getToolBar().addAction(action);
		}
	}

	/**
	 * Add the menu item in a specific position
	 *
	 * @param action
	 * @param index
	 */
	public void addCytoscapeAction(CytoscapeAction action, int index) {
		if (action.isInMenuBar()) {
			getMenuBar().addAction(action, index);
		}

		if (action.isInToolBar()) {
			getToolBar().addAction(action);
		}
	}

	/**
	 * Enables the menu items related to the visual mapper if the argument is
	 * true, else disables them. This method should only be called from the
	 * window that holds this menu.
	 */
	public void setVisualMapperItemsEnabled(boolean newState) {
	//	vizMenuItem.setEnabled(newState);
	//	vizButton.setEnabled(newState);
	//	vizMapperItem.setText(newState ? "Lock VizMapper\u2122" : "Unlock VizMapper\u2122");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newState DOCUMENT ME!
	 */
	public void setOverviewEnabled(boolean newState) {
		networkOverviewItem.setSelected(newState);
	}

	/**
	 * Enables or disables save, print, and display nodes in new window GUI
	 * functions, based on the number of nodes in this window's graph
	 * perspective. This function should be called after every operation which
	 * adds or removes nodes from the current window.
	 */
	public void setNodesRequiredItemsEnabled() {
		boolean newState = Cytoscape.getCurrentNetwork().getNodeCount() > 0;
		newState = true; // TODO: remove this once the
		                 // GraphViewChangeListener system is working

		if (newState == nodesRequiredItemsEnabled)
			return;

		saveButton.setEnabled(newState);
		saveSubMenu.setEnabled(newState);
		//menuPrintAction.setEnabled(newState);
		nodesRequiredItemsEnabled = newState;
	}

	/**
	 * Update the UI menus and buttons. When the graph view is changed, this
	 * method is the listener which will update the UI items, enabling or
	 * disabling items which are only available when the graph view is
	 * non-empty.
	 *
	 * @param e
	 */
	public void graphViewChanged(GraphViewChangeEvent e) {
		// Do this in the GUI Event Dispatch thread...
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setNodesRequiredItemsEnabled();
				}
			});
	}

	/**
	 * Returns the cytopanels menu.
	 */
	public JMenu getCytoPanelMenu() {
		return cytoPanelMenu;
	}

	/*
	 * Sets up the CytoPanelMenu items. This is put into its own public method
	 * so it can be called from CytoscapeInit.Init(), because we need the
	 * CytoscapeDesktop to be instantiated first.
	 */
	public void initCytoPanelMenus() {
		CytoPanel cytoPanel;

		// setup cytopanel west (enabled/shown by default)
		cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		cytoPanelWestItem = new JCheckBoxMenuItem(cytoPanel.getTitle());
		initCytoPanelMenuItem(cytoPanel, cytoPanelWestItem, true, true, "cytoPanelWest",
		                      KeyEvent.VK_1);

		// setup cytopanel east (disabled/hidden by default)
		cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanelEastItem = new JCheckBoxMenuItem(cytoPanel.getTitle());
		initCytoPanelMenuItem(cytoPanel, cytoPanelEastItem, false, false, "cytoPanelEast",
		                      KeyEvent.VK_3);

		// setup cytopanel south (disabled/hidden by default)
		cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
		cytoPanelSouthItem = new JCheckBoxMenuItem(cytoPanel.getTitle());
		initCytoPanelMenuItem(cytoPanel, cytoPanelSouthItem, false, false, "cytoPanelSouth",
		                      KeyEvent.VK_2);

		// add cytopanel menu items to CytoPanels Menu
		menuBar.getMenu("View").add(new JSeparator());
		menuBar.getMenu("View.Desktop").add(cytoPanelWestItem);
		menuBar.getMenu("View.Desktop").add(cytoPanelSouthItem);
		menuBar.getMenu("View.Desktop").add(cytoPanelEastItem);

	}

	private void initCytoPanelMenuItem(CytoPanel cytoPanel, JCheckBoxMenuItem menuItem,
	                                   boolean selected, boolean enabled, String mapObject,
	                                   int keyCode) {
		// setup action
		CytoPanelAction cytoPanelAction = new CytoPanelAction(menuItem, cytoPanel);
		menuItem.addActionListener(cytoPanelAction);
		// enabled/disabled - shown/hidden
		menuItem.setSelected(selected);
		menuItem.setEnabled(enabled);

		// setup menu item key accel
		KeyStroke accel = KeyStroke.getKeyStroke(keyCode, java.awt.event.InputEvent.CTRL_MASK);
		menuItem.setAccelerator(accel);
		// setup global key accel
		menuItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(accel, mapObject);
		menuItem.getActionMap().put(mapObject, cytoPanelAction);
	}

	/**
	 * Creates the menu bar and the various menus and submenus, but defers
	 * filling those menus with items until later.
	 */
	private void createMenuBar() {
		menuBar = new CytoscapeMenuBar();
		fileMenu = menuBar.getMenu("File");

		// Create submenues for "File" menu item
		newSubMenu = menuBar.getMenu("File.New", 0);
		newSubMenu2 = menuBar.getMenu("File.New.Network");
		loadSubMenu = menuBar.getMenu("File.Import", 1);
		saveSubMenu = menuBar.getMenu("File.Export", 2);

		editMenu = menuBar.getMenu("Edit");

		viewMenu = menuBar.getMenu("View");
		viewMenu.setEnabled(true);

		selectMenu = menuBar.getMenu("Select");

		final JMenu f_selectMenu = selectMenu;
		selectMenu.addMenuListener(new MenuListener() {
				public void menuCanceled(MenuEvent e) {
				}

				public void menuDeselected(MenuEvent e) {
				}

				public void menuSelected(MenuEvent e) {
					CyNetwork graph = Cytoscape.getCurrentNetwork();
					boolean inactive = false;

					if ((graph == null) || (graph.getNodeCount() == 0))
						inactive = true;

					MenuElement[] popup = f_selectMenu.getSubElements();

					if (popup[0] instanceof JPopupMenu) {
						MenuElement[] submenus = ((JPopupMenu) popup[0]).getSubElements();

						for (int i = 0; i < submenus.length; i++) {
							if (submenus[i] instanceof JMenuItem) {
								if (inactive)
									((JMenuItem) submenus[i]).setEnabled(false);
								else
									((JMenuItem) submenus[i]).setEnabled(true);
							}
						}
					}
				}
			});
		layoutMenu = menuBar.getMenu("Layout");

		final JMenu f_layoutMenu = layoutMenu;
		layoutMenu.addMenuListener(new MenuListener() {
				public void menuCanceled(MenuEvent e) {
				}

				public void menuDeselected(MenuEvent e) {
				}

				public void menuSelected(MenuEvent e) {
					CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
					boolean inactive = false;

					if ((graphView == null) || (graphView.nodeCount() == 0))
						inactive = true;

					MenuElement[] popup = f_layoutMenu.getSubElements();

					if (popup[0] instanceof JPopupMenu) {
						MenuElement[] submenus = ((JPopupMenu) popup[0]).getSubElements();

						for (int i = 0; i < submenus.length; i++) {
							if (submenus[i] instanceof JMenuItem) {
								if (inactive)
									((JMenuItem) submenus[i]).setEnabled(false);
								else
									((JMenuItem) submenus[i]).setEnabled(true);
							}
						}
					}
				}
			});
		// vizMenu = menuBar.getMenu("Visualization");
		opsMenu = menuBar.getMenu("Plugins");
		// cytoPanelMenu = menuBar.getMenu("CytoPanels");
		helpMenu = menuBar.getMenu("Help");
	}

	/**
	 * This method should be called by the creator of this object after the
	 * constructor has finished. It fills the previously created menu and tool
	 * bars with items and action listeners that respond when those items are
	 * activated. This needs to come after the constructor is done, because some
	 * of the listeners try to access this object in their constructors.
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
			//menuPrintAction.setEnabled(false);
			setNodesRequiredItemsEnabled();
		}
	}

	/**
	 * Fills the previously created menu bar with a large number of items with
	 * attached action listener objects.
	 */
	private void fillMenuBar() {

		//
		// File menu
		// 

		// fill the New submenu
		addAction(new PluginManagerAction());
		addAction(new NewSessionAction());
		addAction(new NewWindowSelectedNodesOnlyAction());
		addAction(new NewWindowSelectedNodesEdgesAction());

		// Session Save/Open
		addAction(new OpenSessionAction(),1);
		addAction(new SaveSessionAction("Save"),2);
		addAction(new SaveSessionAsAction("Save As..."),3);
		fileMenu.add(new JSeparator(), 4);

		// fill the Import submenu
		addAction(new ImportGraphFileAction(this));
		loadSubMenu.add(new JSeparator());
		addAction(new ImportNodeAttributesAction());
		addAction(new ImportEdgeAttributesAction());
		addAction(new ImportExpressionMatrixAction());
		loadSubMenu.add(new JSeparator());
		addAction(new ImportVizmapAction());

		// fill the Save submenu
		addAction(new ExportAsXGMMLAction());
		addAction(new ExportAsGMLAction());
		addAction(new ExportAsInteractionsAction());
		addAction(new ExportNodeAttributesAction());
		addAction(new ExportEdgeAttributesAction());
		addAction(new ExportVizmapAction());
		addAction(new ExportAsGraphicsAction());

		fileMenu.add(new JSeparator());

		// Print Actions
		addAction(new PrintAction());

		// Exit
		addAction(new ExitAction());

		//
		// Edit menu
		//

		// fill the Edit menu
		addAction(new UndoAction());
		addAction(new RedoAction());
		editMenu.add(new JSeparator());

		addAction(new CreateNetworkViewAction());
		addAction(new DestroyNetworkViewAction());
		addAction(new DestroyNetworkAction());
		// add Preferences...
		editMenu.add(new JSeparator());
		addAction(new PreferenceAction());
		addAction(new BookmarkAction());
		addAction(new ProxyServerAction());

		//
		// Select menu
		//

		// fill the Select menu
		selectMenu.add(new SelectionModeAction());

		addAction(new InvertSelectedNodesAction());
		addAction(new HideSelectedNodesAction());
		addAction(new UnHideSelectedNodesAction());

		addAction(new SelectAllNodesAction());
		addAction(new DeSelectAllNodesAction());
		addAction(new SelectFirstNeighborsAction());
		addAction(new AlphabeticalSelectionAction());
		addAction(new ListFromFileSelectionAction());

		addAction(new InvertSelectedEdgesAction());
		addAction(new HideSelectedEdgesAction());
		addAction(new UnHideSelectedEdgesAction());
		addAction(new SelectAllEdgesAction());
		addAction(new DeSelectAllEdgesAction());
		addAction(new BendSelectedEdgesAction());
		addAction(new StraightenSelectedEdgesAction());

		selectMenu.addSeparator();

		addAction(new CloneGraphInNewWindowAction());
		addAction(new SelectAllAction());
		addAction(new DeselectAllAction());

		selectMenu.addSeparator();

		//
		// Layout menu
		//

		layoutMenu.addSeparator();

		//
		// View menu
		// 

		// fill View Menu
		networkOverviewAction = new BirdsEyeViewAction();
		networkOverviewItem = new JCheckBoxMenuItem(networkOverviewAction);
		menuBar.getMenu("View").add(networkOverviewItem);
		//addAction(new BirdsEyeViewAction());

		menuBar.getMenu("View").add(new JSeparator());

		ImageIcon vizmapperIcon = new ImageIcon(getClass()
		                                  .getResource("images/ximian/stock_file-with-objects-16.png"));
		addAction(new SetVisualPropertiesAction(vizmapperIcon));
		addAction(new ToggleVisualMapperAction());

		// 
		// Help menu
		//
		helpAboutMenuItem = new JMenuItem(new HelpAboutAction());

		// for Contents and Context Sensitive help, don't use *Action class
		// since actions encapsulated by HelpBroker and need run-time data
		helpContentsMenuItem = new JMenuItem("Contents...", KeyEvent.VK_C);
		helpContentsMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));

		helpContactHelpDeskMenuItem = new JMenuItem(new HelpContactHelpDeskAction());
		helpMenu.add(helpContentsMenuItem);
		helpMenu.add(helpContactHelpDeskMenuItem);
		helpMenu.addSeparator();
		helpMenu.add(helpAboutMenuItem);
	}

	/**
	 * Fills the toolbar for easy access to commonly used actions.
	 */
	private void fillToolBar() {
		openSessionButton = toolBar.add(new OpenSessionAction(this, false));
		openSessionButton.setToolTipText("Open Session File...");
		openSessionButton.setIcon(new ImageIcon(getClass()
		                                            .getResource("images/ximian/stock_open.png")));
		openSessionButton.setBorderPainted(false);
		openSessionButton.setRolloverEnabled(true);

		saveButton = toolBar.add(new SaveSessionAction());
		saveButton.setToolTipText("Save Current Session As...");
		saveButton.setIcon(new ImageIcon(getClass().getResource("images/ximian/stock_save.png")));

		saveButton.setBorderPainted(false);
		saveButton.setRolloverEnabled(true);
		saveButton.setEnabled(false);

		toolBar.addSeparator();

		final ZoomAction zoom_in = new ZoomAction(1.1);
		zoomInButton = new JButton();
		zoomInButton.setIcon(new ImageIcon(getClass().getResource("images/ximian/stock_zoom-in.png")));
		zoomInButton.setToolTipText("Zoom In");
		zoomInButton.setBorderPainted(false);
		zoomInButton.setRolloverEnabled(true);
		zoomInButton.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					zoom_in.zoom();
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
					zoomInButton.setSelected(true);
				}

				public void mouseReleased(MouseEvent e) {
					zoomInButton.setSelected(false);
				}
			});

		final ZoomAction zoom_out = new ZoomAction(0.9);
		zoomOutButton = new JButton();
		zoomOutButton.setIcon(new ImageIcon(getClass()
		                                        .getResource("images/ximian/stock_zoom-out.png")));
		zoomOutButton.setToolTipText("Zoom Out");
		zoomOutButton.setBorderPainted(false);
		zoomOutButton.setRolloverEnabled(true);
		zoomOutButton.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					zoom_out.zoom();
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
					zoomOutButton.setSelected(true);
				}

				public void mouseReleased(MouseEvent e) {
					zoomOutButton.setSelected(false);
				}
			});

		zoomOutButton.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (e.getWheelRotation() < 0) {
						zoom_in.zoom();
					} else {
						zoom_out.zoom();
					}
				}
			});
		zoomInButton.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (e.getWheelRotation() < 0) {
						zoom_in.zoom();
					} else {
						zoom_out.zoom();
					}
				}
			});

		toolBar.add(zoomOutButton);
		toolBar.add(zoomInButton);

		zoomSelectedButton = toolBar.add(new ZoomSelectedAction());
		zoomSelectedButton.setIcon(new ImageIcon(getClass()
		                                             .getResource("images/ximian/stock_zoom-object.png")));
		zoomSelectedButton.setToolTipText("Zoom Selected Region");
		zoomSelectedButton.setBorderPainted(false);

		zoomDisplayAllButton = toolBar.add(new FitContentAction());
		zoomDisplayAllButton.setIcon(new ImageIcon(getClass()
		                                               .getResource("images/ximian/stock_zoom-1.png")));
		zoomDisplayAllButton.setToolTipText("Zoom out to display all of current Network");
		zoomDisplayAllButton.setBorderPainted(false);

		toolBar.addSeparator();

		toolBar.addSeparator();

		vizButton = toolBar.add(new SetVisualPropertiesAction(false));
		vizButton.setIcon(new ImageIcon(getClass()
		                                    .getResource("images/ximian/stock_file-with-objects.png")));
		vizButton.setToolTipText("Set Visual Style");
		vizButton.setBorderPainted(false);
	} 

	/**
	 * Register the help set and help broker with the various components
	 */
	void initializeHelp(HelpBroker hb) {
		hb.enableHelp(helpContentsMenuItem, "d0e1", null); // comes from jhelptoc.xml
		helpContentsMenuItem.addActionListener(new CSH.DisplayHelpFromSource(hb));

		// Add Help Button to main tool bar
		JButton helpButton = new JButton();
		helpButton.addActionListener(new CSH.DisplayHelpFromSource(hb));
		helpButton.setIcon(new ImageIcon(getClass().getResource("images/ximian/stock_help.png")));
		helpButton.setToolTipText("Help");
		helpButton.setBorderPainted(false);

		// Add Help Button before VizMapper button
		int numComponents = toolBar.getComponentCount();
		toolBar.add(helpButton, numComponents - 1);

	}
}
