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

/**
 * Configuration class for the multilevel layout algorithm.
 * @author Pekka Salmela
 *
 */
public class MultilevelConfig {
	/**
	 * Constant multiplier used in calculation of repulsive forces.
	 */
	public static double C = 0.2;
	
	/**
	 * Constant multiplier used in calculation of attractive forces.
	 */
	public static double A = 1.0;
	
	/**
	 * Constant multiplier used in calculating the maximum movement allowed.
	 */
	public static double T = 0.9;
	
	/**
	 * Parameter used to control the tolerance below which algorithm is conidered to be converged..
	 */
	public static double tolerance = 0.01;
	
	/**
	 * Minimum clustering coefficient that has effect to the layout
	 */
	public static double minimumCC = 0.3;
	
	/**
	 * Multplier/divider to control the effect of the clustering coefficients.
	 */
	public static double ccPower = 3.0;
	
	/**
	 * Flag indicating if clustering option should be used during layout calculation.
	 */
	public static boolean clusteringEnabled = false;
}