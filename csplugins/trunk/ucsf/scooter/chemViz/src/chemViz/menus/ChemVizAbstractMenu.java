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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import chemViz.ui.ChemInfoSettingsDialog;

/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
abstract public class ChemVizAbstractMenu implements ActionListener {
	
	protected Properties systemProps = null;
	protected ChemInfoSettingsDialog settingsDialog = null;

	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public ChemVizAbstractMenu(Properties sysProps, ChemInfoSettingsDialog dialog) {
		systemProps = sysProps;
		settingsDialog = dialog;
	}
		
	/**
	 * Builds a menu item in the popup menu
	 * 
	 * @param label
	 * @param command
	 * @return
	 */
	protected JMenuItem buildMenuItem(String label, String command) {
		JMenuItem item = new JMenuItem(systemProps.getProperty(label));
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	abstract public void actionPerformed(ActionEvent evt);

}
