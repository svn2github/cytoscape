/*
 File: CytoscapeAction.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * An abstract implemenation of the CyAction interface.  Instead of using this
 * class directly you should (strongly) consider implementing a 
 * {@link org.cytoscape.work.TaskFactory}/{@link org.cytoscape.work.Task} pair. Doing
 * so will allow your action to be used outside of a Swing specific application 
 * (which the CyAction interface binds you to)!
 */
public abstract class CytoscapeAction extends AbstractAction implements CyAction {
	protected String preferredMenu = null;
	protected String preferredButtonGroup = null;
	protected float menuGravity = 1.0f; 
	protected float toolbarGravity = 1.0f; 
	protected boolean acceleratorSet = false;
	protected int keyModifiers;
	protected int keyCode;
	protected String consoleName;
	protected boolean useCheckBoxMenuItem = false;
	protected boolean inToolBar = false;
	protected boolean inMenuBar = true;
	protected String enableFor = null;
	protected CyApplicationManager applicationManager;

	/**
	 * Creates a new CytoscapeAction object.
	 *
	 * @param name  The name of the action. 
	 * @param applicationManager The application manager providing context for this action.
	 */
	public CytoscapeAction(final String name, final CyApplicationManager applicationManager) {
		super(name);
		this.consoleName = name;
		this.applicationManager = applicationManager;

		consoleName = consoleName.replaceAll(":. \'", "");
	}

	/**
	 * Creates a new CytoscapeAction object.
	 *
	 * @param configProps A String-String Map of configuration metadata. This
	 * will usually be the Map provided by the Spring service configuration.
	 * @param applicationManager The application manager providing context for this action.
	 */
	public CytoscapeAction(final Map configProps, final CyApplicationManager applicationManager) {
		this((String)(configProps.get("title")), applicationManager);

		String prefMenu = (String)(configProps.get("preferredMenu"));
		if ( prefMenu != null )
			setPreferredMenu(prefMenu);

		String prefButtonGroup = (String)(configProps.get("preferredButtonGroup"));
		if ( prefButtonGroup != null ) 
			setPreferredButtonGroup(prefButtonGroup);

		String iconName = (String)(configProps.get("iconName"));
		if ( iconName != null ) 
			putValue(SMALL_ICON,new ImageIcon(getClass().getResource(iconName)));
		
		String tooltip = (String)(configProps.get("tooltip"));
		if ( tooltip != null )
			putValue(SHORT_DESCRIPTION,tooltip);
		
		String foundInToolBar = (String)(configProps.get("inToolBar"));
		if ( foundInToolBar != null )
		 	inToolBar = true;

		enableFor = (String)(configProps.get("enableFor"));

		String keyComboString = (String) configProps.get("accelerator");
		if (keyComboString != null) {
			try
			{
				KeyStroke keyStroke = AcceleratorParser.parse(keyComboString);
				super.putValue(Action.ACCELERATOR_KEY, keyStroke);
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println(String.format("WARNING: The action \'%s\' has specified the following invalid key combination: %s", consoleName, keyComboString));
				System.out.println(" => " + ex.getMessage());
			}
		}
	}

	/**
	 * @inheritdoc 
	 */
	public void setName(String name) {
		this.consoleName = name;
	}

	/**
	 * @inheritdoc 
	 */
	public String getName() {
		return consoleName;
	}

	/**
	 * By default all CytoscapeActions wish to be included in CommunityMenuBars,
	 * but you may override if you wish.
	 *
	 * @return true If this Action should be included in a CommunityMenuBar.
	 * @see #getPrefferedMenu();
	 * @beaninfo (ri)
	 */
	public boolean isInMenuBar() {
		return inMenuBar;
	}

	/**
	 * By default no CytoscapeActions wish to be included in CommunityToolBars,
	 * but you may override if you wish.
	 *
	 * @return true If this Action should be included in a CommunityMenuBar.
	 * @see #getPrefferedButtonGroup();
	 * @beaninfo (ri)
	 */
	public boolean isInToolBar() {
		return inToolBar;
	}

	/**
	 * @inheritdoc 
	 */
	public void setMenuGravity(float gravity) {
		menuGravity = gravity;
	}

	/**
	 * @inheritdoc 
	 */
	public float getMenuGravity() {
		return menuGravity;
	}

	/**
	 * @inheritdoc 
	 */
	public void setToolbarGravity(float gravity) {
		toolbarGravity = gravity;
	}

	/**
	 * @inheritdoc 
	 */
	public float getToolbarGravity() {
		return toolbarGravity;
	}

	/**
	 * @inheritdoc 
	 */
	public void setAcceleratorCombo(int key_code, int key_mods) {
		acceleratorSet = true;
		keyCode = key_code;
		keyModifiers = key_mods;
	}

	/**
	 * @inheritdoc 
	 */
	public boolean isAccelerated() {
		return acceleratorSet;
	}

	/**
	 * @inheritdoc 
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * @inheritdoc 
	 */
	public int getKeyModifiers() {
		return keyModifiers;
	}

	/**
	 * This method returns a Menu specification string. Submenus are preceeded
	 * by dots in this string, so the result "File.Import" specifies the submenu
	 * "Import" of the menu "File". If the result is null, the menu will be
	 * placed in a default location.
	 *
	 * @return a Menu specification string, or null if this Action should be
	 *         placed in a default Menu.
	 * @see #inMenuBar()
	 */
	public String getPreferredMenu() {
		return preferredMenu;
	}

	/**
	 * @inheritdoc 
	 */
	public void setPreferredMenu(String new_preferred) {
		preferredMenu = new_preferred;
	} 

	/**
	 * This method returns a ButtonGroup specification string. Subgroups are
	 * preceeded by dots in this string, so the result "Edit.Selection Modes"
	 * specifies the subgroup "Selection Modes" of the group "Edit". If the
	 * result is null, the button will be placed in a default location.
	 *
	 * @return a ButtonGroup specification string, or null if the button for
	 *         this Action should be placed in a default ButtonGroup.
	 * @see #inToolBar()
	 */
	public String getPreferredButtonGroup() {
		return preferredButtonGroup;
	}

	/**
	 * @inheritdoc 
	 */
	public void setPreferredButtonGroup(String new_preferred) {
		preferredButtonGroup = new_preferred;
	} 

	/**
	 * Indicates whether a check box menu item should be used instead of a normal one.
	 */
	public boolean useCheckBoxMenuItem() {
		return useCheckBoxMenuItem;
	}

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  It exists
	 * primarily to have access to menuSelected().
	 * @param e The triggering event.
	 */
    public void menuCanceled(MenuEvent e) {}

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  It exists
	 * primarily to have access to menuSelected().
	 * @param e The triggering event.
	 */
    public void menuDeselected(MenuEvent e) {}

	/**
	 * This method can be overridden by individual actions to set the state of menu items
	 * based on whatever unique circumstances that menu option cares about. By default it
	 * sets the state of the menu based on the "enableFor" property found in the properties used
	 * to construct the action. The valid options for "enableFor" are "network", "networkAndView",
	 * and "selectedNetworkObjs".
	 * @param e The triggering event.
	 */
    public void menuSelected(MenuEvent e) { enableMenus(); }

	/**
	 * This method can be overridden by individual actions to set the state of menu items
	 * based on whatever unique circumstances that menu option cares about. By default it
	 * sets the state of the menu based on the "enableFor" property found in the properties used
	 * to construct the action. The valid options for "enableFor" are "network", "networkAndView",
	 * and "selectedNetworkObjs".
	 * @param e The triggering event.
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) { enableMenus(); }

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  It exists
	 * primarily to have access to menuSelected().
	 * @param e The triggering event.
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  It exists
	 * primarily to have access to menuSelected().
	 * @param e The triggering event.
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {}

	private void enableMenus() {
		if ( enableFor == null || enableFor.equals("") )
			setEnabled(true);
		else if ( enableFor.equals("network") ) 
			enableForNetwork();
		else if ( enableFor.equals("networkAndView") ) 
			enableForNetworkAndView();
		else if ( enableFor.equals("selectedNetworkObjs") ) 
			enableForSelectedNetworkObjs();
	}

	//
	// The following methods are utility methods that that enable or disable 
	// the action based on the state of Cytoscape.  These methods are meant to
	// reduce duplicate code since many actions demand the same state to be
	// functional (e.g. a network and network view must exist). These methods
	// are generally called from within implementations of {@link #menuSelected}, 
	// but can be called from anywhere.
	//

	/**
	 * Enable the action if the current network exists and is not null.
	 */
	protected void enableForNetwork() {
		CyNetwork n = applicationManager.getCurrentNetwork();
		if ( n == null ) 
			setEnabled(false);
		else
			setEnabled(true);
	}

	/**
	 * Enable the action if the current network and view exist and are not null.
	 */
	protected void enableForNetworkAndView() {
		CyNetworkView v = applicationManager.getCurrentNetworkView();
		if ( v == null )
			setEnabled(false);
		else
			setEnabled(true);
	}

	/**
	 * Enable the action if more than one network object is required to execute
	 * the action.
	 */
	protected void enableForSelectedNetworkObjs() {
		CyNetwork n = applicationManager.getCurrentNetwork();

		if ( n == null ) {
			setEnabled(false);
			return;
		}

		for ( CyNode node : n.getNodeList() ) {
			if ( node.getCyRow().get("selected",Boolean.class) ) {
				setEnabled(true);
				return;
			}
		}
		for ( CyEdge edge : n.getEdgeList() ) {
			if ( edge.getCyRow().get("selected",Boolean.class) ) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}
} 
