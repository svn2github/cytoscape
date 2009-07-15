/*
 File: CytoscapeMenus.java

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

package cytoscape.internal.view;

import cytoscape.util.CyAction;
import cytoscape.util.CyMenuBar;
import cytoscape.util.CyToolBar;

import org.cytoscape.session.CyNetworkManager;

import cytoscape.internal.task.TaskFactoryTunableAction;
import cytoscape.internal.task.NetworkTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkViewTaskFactoryTunableAction;

import cytoscape.view.CyMenus;

import cytoscape.internal.util.CytoscapeMenuBar;
import cytoscape.internal.util.CytoscapeToolBar;

import javax.swing.JMenu;

import java.util.Map;
import java.util.HashMap;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;


/**
 * Creates the menu and tool bars for a Cytoscape window object. It
 * also provides access to individual menus and items.<BR>
 * <p>
 * AddAction takes one more optional argument to specify index. Plugin
 * writers can use this function to specify the location of the menu item.
 * </p>
 */
public class CytoscapeMenus implements CyMenus {

	CytoscapeMenuBar menuBar;
	CytoscapeToolBar toolBar;

	JMenu fileMenu;
	JMenu loadSubMenu;
	JMenu saveSubMenu;
	JMenu newSubMenu;
	JMenu newSubMenu2;
	JMenu editMenu;
	JMenu viewMenu;
	JMenu viewSubMenu;
	JMenu selectMenu;
	JMenu layoutMenu;
	JMenu vizMenu;
	JMenu helpMenu;
	JMenu opsMenu;

	Map<TaskFactory,CyAction> taskMap;

	TaskManager taskManager;
	TunableInterceptor interceptor;
	CyNetworkManager netManager;

	/**
	 * Creates a new CytoscapeMenus object. This will construct the basic bar objects, 
	 * but won't fill them with menu items and associated action listeners.
	 */
	public CytoscapeMenus(TaskManager taskManager, TunableInterceptor interceptor, CyNetworkManager netManager) {
		this.taskManager = taskManager;
		this.interceptor = interceptor;
		this.netManager = netManager;

		toolBar = new CytoscapeToolBar();

		menuBar = new CytoscapeMenuBar();

		fileMenu = menuBar.getMenu("File");
		newSubMenu = menuBar.getMenu("File.New", 0);
		newSubMenu2 = menuBar.getMenu("File.New.Network");
		loadSubMenu = menuBar.getMenu("File.Import", 1);
		saveSubMenu = menuBar.getMenu("File.Export", 2);
		editMenu = menuBar.getMenu("Edit");
		viewMenu = menuBar.getMenu("View");
		selectMenu = menuBar.getMenu("Select");
		layoutMenu = menuBar.getMenu("Layout");
		opsMenu = menuBar.getMenu("Plugins");
		helpMenu = menuBar.getMenu("Help");

		taskMap = new HashMap<TaskFactory,CyAction>();
	}

	/**
	 * Returns the main menu bar constructed by this object.
	 */
	public CyMenuBar getMenuBar() {
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
	 * Returns the menu with items associated with plugins. Most plugins grab
	 * this menu and add their menu option. 
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
	public CyToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * Add the menu item. 
	 *
	 * @param action The action to be added
	 * @param props A map of properties that will be ignored
	 */
	public void addAction(CyAction action, Map props) {
		addAction( action );
	}

	/**
	 * Remove the menu item. 
	 *
	 * @param action The action to be removed
	 * @param props A map of properties that will be ignored
	 */
	public void removeAction(CyAction action, Map props) {
		if (action.isInMenuBar()) {
			getMenuBar().removeAction(action);
		}

		if (action.isInToolBar()) {
			getToolBar().removeAction(action);
		}
	}

	public void addTaskFactory(TaskFactory factory, Map props) {
		addFactory( new TaskFactoryTunableAction<TaskFactory>(taskManager, interceptor, factory, props, netManager), factory, props );
	}

	public void addNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		addFactory( new NetworkTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props );
	}

	public void addNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		addFactory( new NetworkViewTaskFactoryTunableAction(taskManager, interceptor, factory, props, netManager), factory, props );
	}

	public void removeTaskFactory(TaskFactory factory, Map props) {
		removeFactory(factory,props);
	}

	public void removeNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		removeFactory(factory,props);
	}

	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		removeFactory(factory,props);
	}
	
	private <F extends TaskFactory> void addFactory(CyAction action, F factory, Map props) {
		taskMap.put(factory,action);
		addAction( action );
	}

	private void removeFactory(TaskFactory factory, Map props) {
		//System.out.println("removeTaskFactory called");
		CyAction action = taskMap.remove(factory);
		if ( action != null ) {
			if (action.isInMenuBar()) {
				getMenuBar().removeAction(action);
			}

			if (action.isInToolBar()) {
				getToolBar().removeAction(action);
			}
		}
	}


	/**
	 * Takes a CyAction and will add it to the MenuBar or the Toolbar as
	 * is appropriate.
	 */
	public void addAction(CyAction action) {
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
	public void addAction(CyAction action, int index) {
		if (action.isInMenuBar()) {
			getMenuBar().addAction(action, index);
		}

		if (action.isInToolBar()) {
			getToolBar().addAction(action);
		}
	}
}
