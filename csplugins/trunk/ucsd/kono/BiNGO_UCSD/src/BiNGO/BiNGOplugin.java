package BiNGO;

/**
 * * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: BiNGO is a Cytoscape plugin for the functional annotation of gene clusters.          
 **/

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

/**
 * *****************************************************************
 * BiNGOplugin.java Steven Maere & Karel Heymans (c) March 2005 ----------------
 * <p/>
 * Main class of the BiNGO plugin ; extends the CytoscapePlugin class from
 * Cytoscape.
 * <p/>
 * 
 * Refactored by Keiichiro Ono for Cytoscape 2.7.0+
 * 
 * ******************************************************************
 */

public class BiNGOplugin extends CytoscapePlugin {

	private static final String PLUGIN_VERSION = "2.40";

	private static final String MAIN_MENU = "Plugins";
	private static final String CURRENT_WORKING_DIRECTORY = "user.dir";

	private String bingoDir;

	public BiNGOplugin() {
		// create a new action to respond to menu activation
		final BiNGOpluginAction action = new BiNGOpluginAction();

		// set the preferred menu
		action.setPreferredMenu(MAIN_MENU);

		// and add it to the menus
		Cytoscape.getDesktop().getCyMenus().addAction(action);
		String cwd = System.getProperty(CURRENT_WORKING_DIRECTORY);
		bingoDir = new File(cwd, "plugins").toString();
	}

	class BiNGOpluginAction extends CytoscapeAction {

		private static final long serialVersionUID = 5389627389023778048L;

		// The constructor sets the text that should appear on the menu item.
		public BiNGOpluginAction() {
			super("Start BiNGO " + PLUGIN_VERSION);
		}

		/**
		 * This method opens the BiNGO settingspanel upon selection of the menu
		 * item and opens the settingspanel for BiNGO.
		 * 
		 * @param event
		 *            event triggered when BiNGO menu item clicked.
		 */

		public void actionPerformed(ActionEvent event) {

			final JFrame window = new JFrame("BiNGO Settings");
			final SettingsPanel settingsPanel = new SettingsPanel(bingoDir);
			// window.setJMenuBar(new
			// HelpMenuBar(settingsPanel).getHelpMenuBar());
			window.getContentPane().add(settingsPanel);
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.pack();
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			// for central position of the settingspanel.
			window.setLocation(screenSize.width / 2 - (window.getWidth() / 2),
					screenSize.height / 2 - (window.getHeight() / 2));
			window.setVisible(true);
			window.setResizable(true);
		}
	}
}
