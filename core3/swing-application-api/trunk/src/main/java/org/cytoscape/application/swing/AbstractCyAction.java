/*
 File: AbstractCyAction.java

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
package org.cytoscape.application.swing;


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
 * An abstract implementation of the CyAction interface.  Instead of using this
 * class directly you should (strongly) consider implementing a 
 * org.cytoscape.work.TaskFactory/org.cytoscape.work.Task pair. Doing
 * so will allow your action to be used outside of a Swing specific application 
 * (which the CyAction interface binds you to)!
 */
public abstract class AbstractCyAction extends AbstractAction implements CyAction {
	protected String preferredMenu = null;
	protected String preferredButtonGroup = null;
	protected float menuGravity = 1.0f; 
	protected float toolbarGravity = 1.0f; 
	protected boolean acceleratorSet = false;
	protected KeyStroke acceleratorKeyStroke = null;
	protected String name;
	protected boolean useCheckBoxMenuItem = false;
	protected boolean inToolBar = false;
	protected boolean inMenuBar = true;
	protected String enableFor = null;
	protected CyApplicationManager applicationManager;

	/**
	 * Creates a new AbstractCyAction object.
	 *
	 * @param name  The name of the action. 
	 * @param applicationManager The application manager providing context for this action.
	 */
	public AbstractCyAction(final String name, final CyApplicationManager applicationManager) {
		super(name);
		this.name = name;
		this.applicationManager = applicationManager;
	}

	/**
	 * Creates a new AbstractCyAction object.
	 *
	 * @param configProps A String-String Map of configuration metadata. This
	 * will usually be the Map provided by the Spring service configuration. 
	 * Available configuration keys include:
	 * <ul>
	 * <li>title</li>
	 * <li>preferredMenu</li>
	 * <li>preferredButtonGroup</li>
	 * <li>iconName</li>
	 * <li>tooltip</li>
	 * <li>inToolBar</li>
	 * <li>inMenuBar</li>
	 * <li>enableFor</li>
	 * <li>accelerator</li>
	 * </ul>
	 * @param applicationManager The application manager providing context for this action.
	 */
	public AbstractCyAction(final Map configProps, final CyApplicationManager applicationManager) {
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

		String foundInMenuBar = (String)(configProps.get("inMenuBar"));
		if ( foundInMenuBar != null )
		 	inMenuBar = true;

		enableFor = (String)(configProps.get("enableFor"));

		String keyComboString = (String) configProps.get("accelerator");
		if (keyComboString != null) 
			setAcceleratorKeyStroke( AcceleratorParser.parse(keyComboString) );
	}

	/**
	 * Sets the name of the action.
	 * @param name The name of the action. 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc} 
	 */
	public String getName() {
		return name;
	}

	/**
	 * By default all CytoscapeActions wish to be included in the menu bar
	 * at the 'preferredMenuName' location is specified and the 'Tools' menu
	 * not.
	 * @return true if this CyAction should be included in menu bar. 
	 */
	public boolean isInMenuBar() {
		return inMenuBar;
	}

	/**
	 * By default no CytoscapeActions will be included in the toolbar. 
	 * @return true if this Action should be included in the toolbar. 
	 */
	public boolean isInToolBar() {
		return inToolBar;
	}

	/**
	 * Sets the gravity used to order this action in the menu bar.
	 * @param gravity The gravity for ordering menu bar actions.
	 */
	public void setMenuGravity(float gravity) {
		menuGravity = gravity;
	}

	/**
	 * {@inheritDoc} 
	 */
	public float getMenuGravity() {
		return menuGravity;
	}

	/**
	 * Sets the gravity used to order this action in the toolbar.
	 * @param gravity The gravity for ordering toolbar actions.
	 */
	public void setToolbarGravity(float gravity) {
		toolbarGravity = gravity;
	}

	/**
	 * {@inheritDoc} 
	 */
	public float getToolbarGravity() {
		return toolbarGravity;
	}

	/**
     * Sets the accelerator KeyStroke for this action. 
     * @param ks The KeyStroke to be used as an accelerator for this action. 
     * This parameter may be null, in which case no accelerator is defined.
	 */
	public void setAcceleratorKeyStroke(KeyStroke ks) {
		acceleratorKeyStroke = ks;	
	}

	/**
	 * {@inheritDoc}
	 */
	public KeyStroke getAcceleratorKeyStroke() {
		return acceleratorKeyStroke;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreferredMenu() {
		return preferredMenu;
	}

	/**
	 * Sets the preferredMenuString.  See the {@link #getPreferredMenu} 
	 * description for formatting description.
	 * @param new_preferred The string describing the preferred menu name.
	 */
	public void setPreferredMenu(String new_preferred) {
		preferredMenu = new_preferred;
	} 

	/** 
	 * {@inheritDoc}
	 */
	public String getPreferredButtonGroup() {
		return preferredButtonGroup;
	}

	/**
	 * Sets the preferred button group.
	 * @param new_preferred The preferred button group for this action. 
	 */
	public void setPreferredButtonGroup(String new_preferred) {
		preferredButtonGroup = new_preferred;
	} 

	/**
	 * {@inheritDoc}
	 */
	public boolean useCheckBoxMenuItem() {
		return useCheckBoxMenuItem;
	}

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  
	 * @param e The triggering event.
	 */
	public void menuCanceled(MenuEvent e) { }

	/**
	 * This method can be used at your discretion, but otherwise does nothing.  
	 * @param e The triggering event.
	 */
	public void menuDeselected(MenuEvent e) { }

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
	 * This method can be used at your discretion, but otherwise does nothing.  
	 * @param e The triggering event.
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	/**
	 * This method can be used at your discretion, but otherwise does nothing.
	 * @param e The triggering event.
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {}

	//
	// The following methods are utility methods that that enable or disable 
	// the action based on the state of Cytoscape.  These methods are meant to
	// reduce duplicate code since many actions demand the same state to be
	// functional (e.g. a network and network view must exist). These methods
	// are generally called from within implementations of {@link #menuSelected}, 
	// but can be called from anywhere.
	//

	private void enableMenus() {
		if (enableFor == null || enableFor.equals(""))
			setEnabled(true);
		else if (enableFor.equals("network")) 
			enableForNetwork();
		else if (enableFor.equals("networkAndView")) 
			enableForNetworkAndView();
		else if (enableFor.equals("selectedNodesOrEdges")) 
			enableForSelectedNodesOrEdges();
		else if (enableFor.equals("selectedNodes")) 
			enableForSelectedNodes();
		else if (enableFor.equals("selectedEdges")) 
			enableForSelectedEdges();
	}

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
	 * Enable the action if at least one selected node or edge is required to perform the action.
	 */
	protected void enableForSelectedNodesOrEdges() {
		CyNetwork n = applicationManager.getCurrentNetwork();

		if (n == null) {
			setEnabled(false);
			return;
		}

		for (CyNode node : n.getNodeList()) {
			if (node.getCyRow().get("selected", Boolean.class)) {
				setEnabled(true);
				return;
			}
		}
		for (CyEdge edge : n.getEdgeList()) {
			if (edge.getCyRow().get("selected", Boolean.class)) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}

	/**
	 * Enable the action if at least one selected node is required to perform the action.
	 */
	protected void enableForSelectedNodes() {
		CyNetwork n = applicationManager.getCurrentNetwork();

		if (n == null) {
			setEnabled(false);
			return;
		}

		for (CyNode node : n.getNodeList()) {
			if (node.getCyRow().get("selected", Boolean.class)) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}
	/**
	 * Enable the action if at least one selected edge is required to perform the action.
	 */
	protected void enableForSelectedEdges() {
		CyNetwork n = applicationManager.getCurrentNetwork();

		if (n == null) {
			setEnabled(false);
			return;
		}

		for (CyEdge edge : n.getEdgeList()) {
			if (edge.getCyRow().get("selected", Boolean.class)) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}
} 
