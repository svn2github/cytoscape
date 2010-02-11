/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.menus;

import java.awt.Component;

import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;

import chemViz.ui.ChemInfoSettingsDialog;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class ChemVizMenu implements MenuListener {
	
	private Properties systemProps = null;
	private ChemInfoSettingsDialog settingsDialog = null;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public ChemVizMenu(Properties systemProperties, ChemInfoSettingsDialog settingsDialog) {
		this.systemProps = systemProperties;
		this.settingsDialog = settingsDialog;
	}


	/**
 	 * MenuListener: menuCanceled (not used)
 	 */
	public void menuCanceled (MenuEvent e) {};

	/**
 	 * MenuListener: menuDeselected (not used)
 	 */
	public void menuDeselected (MenuEvent e) {};

	/**
 	 * MenuListener: menuSelected is called when the user
 	 * selects our main menu. This method will populate the submenu
 	 * depending on what is selected, the presence of attributes
 	 * that contain compound descriptors, and the type of object
 	 * that has been selected.
 	 *
 	 * @param e the menu event
 	 */
	public void menuSelected (MenuEvent e) {
		JMenu m = (JMenu)e.getSource();

		// remove the current entries
		Component[] subMenus = m.getMenuComponents();
		for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }
		new DepictionMenus(m, systemProps, settingsDialog, null);
		new SimilarityMenu(m, systemProps, settingsDialog);
		new AttributesMenu(m, systemProps, settingsDialog, null);
		new SettingsMenu(m, systemProps, settingsDialog);
	}

}
