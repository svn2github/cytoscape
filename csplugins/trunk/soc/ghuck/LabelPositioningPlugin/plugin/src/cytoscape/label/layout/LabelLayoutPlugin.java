/**
* Copyright (C) Gerardo Huck, 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published 
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*  
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*  
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package cytoscape.layout.label;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.Cytoscape;

import cytoscape.layout.CyLayouts;

import giny.view.NodeView;
import giny.view.Label;
import cytoscape.view.CyNodeView;

/** ---------------------------LabelLayoutPlugin-----------------------------
 * Takes the current network and reorganizes it so that the new network is more
 * readable.  This will be done through the repositioning of network labels,
 * and subtle repositioning of nodes.
 * @author Gerardo Huck
 *
 */
public class LabelLayoutPlugin extends CytoscapePlugin {
	
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
	public LabelLayoutPlugin() {

		// Adds LabelForce-DirectedLayout to the Layout menu under "Label Layouts".
		CyLayouts.addLayout(new LabelForceDirectedLayout(), 
				"Label Layouts");

		// Adds LabelBioLayoutFRAlgorithm to the Layout menu under "Label Layouts".
		CyLayouts.addLayout(new LabelBioLayoutFRAlgorithm(true), 
				"Label Layouts");

		// Adds LabelBioLayoutKKAlgorithm to the Layout menu under "Label Layouts".
		CyLayouts.addLayout(new LabelBioLayoutKKAlgorithm(true), 
				"Label Layouts");

		// Adds LabelNewForceDirectedLayout to the Layout menu under "Label Layouts".
		CyLayouts.addLayout(new LabelNewForceDirectedLayout(true), 
				"Label Layouts");

	}
       	
 }