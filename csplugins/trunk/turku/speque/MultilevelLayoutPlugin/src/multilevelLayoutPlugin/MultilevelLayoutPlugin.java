/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package multilevelLayoutPlugin;

import cytoscape.layout.CyLayouts;
import cytoscape.plugin.CytoscapePlugin;

/**
 * The main class of this plugin. 
 * 
 * @author Pekka Salmela
 *
 */
public class MultilevelLayoutPlugin extends CytoscapePlugin {

	/**
	 * Class constructor.
	 */
	public MultilevelLayoutPlugin() {
		CyLayouts.addLayout(new MultilevelLayoutAlgorithm(), "Multilevel Layout");
	}
}
