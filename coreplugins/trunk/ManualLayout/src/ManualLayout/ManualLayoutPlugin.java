
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package ManualLayout;

import ManualLayout.control.ControlPanel;

import ManualLayout.rotate.RotatePanel;

import ManualLayout.scale.ScalePanel;

import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;

import java.awt.Dimension;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;


/**
 *
 */
public class ManualLayoutPlugin extends CytoscapePlugin {
	/**
	 * Creates a new ManualLayoutPlugin object.
	 */
	public ManualLayoutPlugin() {
		init();

		ManualLayoutAction manualLayoutActionListener = new ManualLayoutAction();
		JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout");
		JCheckBoxMenuItem rotateMenuItem = new JCheckBoxMenuItem("Rotate");
		JCheckBoxMenuItem scaleMenuItem = new JCheckBoxMenuItem("Scale");
		JCheckBoxMenuItem controlMenuItem = new JCheckBoxMenuItem("Align and Distribute");

		layoutMenu.add(rotateMenuItem, 0);
		layoutMenu.add(scaleMenuItem, 1);
		layoutMenu.add(controlMenuItem, 2);

		rotateMenuItem.setSelected(false);
		scaleMenuItem.setSelected(false);
		controlMenuItem.setSelected(false);

		rotateMenuItem.addActionListener(manualLayoutActionListener);
		scaleMenuItem.addActionListener(manualLayoutActionListener);
		controlMenuItem.addActionListener(manualLayoutActionListener);
	}

	private void init() {
		RotatePanel rotatePanel = new RotatePanel();
		ScalePanel scalePanel = new ScalePanel();
		ControlPanel controlPanel = new ControlPanel();

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Rotate", rotatePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Scale", scalePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
		         .add("Align and Distribute", controlPanel);
	}
}
