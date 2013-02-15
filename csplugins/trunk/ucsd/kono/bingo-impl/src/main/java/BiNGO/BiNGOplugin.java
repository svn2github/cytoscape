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

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.plugin.CyPlugin;
import org.cytoscape.plugin.CyPluginAdapter;

/**
 * *****************************************************************
 * BiNGOplugin.java Steven Maere & Karel Heymans (c) March 2005 ----------------
 * <p/>
 * Main class of the BiNGO plugin ; extends the CytoscapePlugin class from
 * Cytoscape.
 * <p/>
 * 
 * Updated by Keiichiro Ono for Cytoscape 3
 * 
 * ******************************************************************
 */

public class BiNGOplugin extends CyPlugin {

	private static final String CURRENT_WORKING_DIRECTORY = "user.dir";
	private static final String MENU_NAME = "Start BiNGO Plugin";
	private static final String MENU_CATEGORY = "Tools";
	
	private static final String WINDOW_TITLE = "BiNGO Settings";


	private String bingoDir;

	public BiNGOplugin(final CyPluginAdapter adapter) {
		super(adapter);

		adapter.getCySwingApplication().addAction(new BiNGOpluginAction(adapter));
		String cwd = System.getProperty(CURRENT_WORKING_DIRECTORY);
		bingoDir = new File(cwd, "plugins").toString();
	}

	private final class BiNGOpluginAction extends AbstractCyAction {

		private static final long serialVersionUID = 4190390703299860130L;

		// The constructor sets the text that should appear on the menu item.
		public BiNGOpluginAction(final CyPluginAdapter adapter) {
			super(MENU_NAME, adapter.getCyApplicationManager());
			setPreferredMenu(MENU_CATEGORY);
		}

		/**
		 * This method opens the BiNGO settingspanel upon selection of the menu
		 * item and opens the settingspanel for BiNGO.
		 * 
		 * @param event
		 *            event triggered when BiNGO menu item clicked.
		 */
		public void actionPerformed(ActionEvent event) {
			final JFrame window = new JFrame(WINDOW_TITLE);
			final SettingsPanel settingsPanel = new SettingsPanel(bingoDir, adapter);
			window.getContentPane().add(settingsPanel);
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.pack();

			// Cytoscape Main Window
			final JFrame desktop = adapter.getCySwingApplication().getJFrame();
			window.setLocationRelativeTo(desktop);
			window.setVisible(true);
		}
	}
}
