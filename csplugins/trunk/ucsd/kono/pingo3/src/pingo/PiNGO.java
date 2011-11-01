package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.plugin.CyPlugin;
import org.cytoscape.plugin.CyPluginAdapter;

/**
 * PiNGOplugin.java Steven Maere(c) 2010
 * 
 * <p/>
 * Main class of the PiNGO plugin. Extends the CyPlugin class from Cytoscape.
 * <p/>
 * 
 * @author Steven Maere
 */
public class PiNGO extends CyPlugin {
	private String pingoDir;

	public PiNGO(final CyPluginAdapter adapter) {
		super(adapter);

		// create a new action to respond to menu activation
		final PiNGOAction action = new PiNGOAction();
		adapter.getCySwingApplication().addAction(action);
		String homeDir = System.getProperty("user.home");
		pingoDir = homeDir + File.separator + ".cytoscape";
		File dir = new File(pingoDir);
		if (!dir.exists())
			dir.mkdir();
	}

	private final class PiNGOAction extends AbstractCyAction {

		private static final long serialVersionUID = 4295013622487021987L;
		
		private static final String MENU_NAME = "Start PiNGO";
		private static final String MENU_CATEGORY = "Tools";
		private static final String WINDOW_TITLE = "PiNGO Settings";

		public PiNGOAction() {
			super(MENU_NAME, adapter.getCyApplicationManager());
			setPreferredMenu(MENU_CATEGORY);
		}

		/**
		 * This method opens the PiNGO settingspanel upon selection of the menu
		 * item and opens the settingspanel for PiNGO.
		 * 
		 * @param event
		 *            event triggered when PiNGO menu item clicked.
		 */

		public void actionPerformed(ActionEvent event) {
			final JFrame window = new JFrame(WINDOW_TITLE);
			final SettingsPanel settingsPanel = new SettingsPanel(window, pingoDir, adapter);
			final JScrollPane scrollPane = new JScrollPane(settingsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			window.getContentPane().add(scrollPane);
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.pack();

			// Cytoscape Main Window
			final JFrame desktop = adapter.getCySwingApplication().getJFrame();
			window.setLocationRelativeTo(desktop);
			window.setVisible(true);
		}
	}
}
