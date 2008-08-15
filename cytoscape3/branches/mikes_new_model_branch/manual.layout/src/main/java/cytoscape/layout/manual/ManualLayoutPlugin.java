
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

package cytoscape.layout.manual;

import cytoscape.Cytoscape;
import cytoscape.layout.manual.control.ControlPanel;
import cytoscape.layout.manual.control.ControlPanelAction;
import cytoscape.layout.manual.rotate.RotatePanel;
import cytoscape.layout.manual.rotate.RotatePanelAction;
import cytoscape.layout.manual.scale.ScalePanel;
import cytoscape.layout.manual.scale.ScalePanelAction;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;


/**
 *
 */
public class ManualLayoutPlugin implements BundleActivator {

	/**
	 * Creates a new ManualLayoutPlugin object.
	 */
	public void start(BundleContext bc) {

		// create the panels 
		RotatePanel rotatePanel = new RotatePanel();
		ScalePanel scalePanel = new ScalePanel();
		ControlPanel controlPanel = new ControlPanel();

		// add them to the cytopanel
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Rotate", rotatePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).add("Scale", scalePanel);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
		         .add("Align and Distribute", controlPanel);

		// create the actions
		Cytoscape.getDesktop().getCyMenus().addAction( new RotatePanelAction(), 0);
		Cytoscape.getDesktop().getCyMenus().addAction( new ScalePanelAction(), 1);
		Cytoscape.getDesktop().getCyMenus().addAction( new ControlPanelAction(), 2);

	}

	public void stop(BundleContext bc) {
	}
}
