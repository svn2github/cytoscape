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
package org.cytoscape.vizmap;

import org.cytoscape.model.CyNetwork;

import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;


/**
 *
  */
public interface VisualMappingManager extends SubjectBase {
	/**
	 * Name of the default Visual Style
	 */
	public static final String DEFAULT_VS_NAME = "default";

	/**
	 *
	 */
	public static final String VIZMAP_RESTORED = "VIZMAP_RESTORED";

	/**
	 *
	 */
	public static final String SAVE_VIZMAP_PROPS = "SAVE_VIZMAP_PROPS";

	/**
	 *
	 */
	public static final String VIZMAP_LOADED = "VIZMAP_LOADED";

	/**
	 * DOCUMENT ME!
	 *
	 * @param new_view DOCUMENT ME!
	 */
	public void setNetworkView(final GraphView new_view);

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getNetworkView();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyNetwork getNetwork();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CalculatorCatalog getCalculatorCatalog();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle(String name);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle getVisualStyleForView(GraphView g);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 * @param vs DOCUMENT ME!
	 */
	public void setVisualStyleForView(GraphView g, VisualStyle vs);

	/**
	 * Sets a new visual style, and returns the old style. Also fires an event
	 * to attached listeners only if the visual style changes.
	 *
	 * If the argument is null, the previous visual style is simply returned.
	 */
	public VisualStyle setVisualStyle(final VisualStyle vs);

	/**
	 * Sets a new visual style. Attempts to get the style with the given name
	 * from the catalog and pass that to setVisualStyle(VisualStyle). The return
	 * value is the old style.
	 *
	 * If no visual style with the given name is found, no change is made, an
	 * error message is passed to the logger, and null is returned.
	 */
	public VisualStyle setVisualStyle(final String newVSName);

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances();

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances(final CyNetwork network, final GraphView network_view);

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances();

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances(final CyNetwork network, final GraphView network_view);

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyGlobalAppearances();

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 *
	 * @param network
	 *            the network to apply to
	 * @param network_view
	 *            the view to apply to
	 */
	public void applyGlobalAppearances(CyNetwork network, GraphView network_view);

	/**
	 * Recalculates and reapplies all of the node, edge, and global visual
	 * attributes. This method delegates to, in order, applyNodeAppearances,
	 * applyEdgeAppearances, and applyGlobalAppearances.
	 */
	public void applyAppearances();

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeView DOCUMENT ME!
	 * @param network_view DOCUMENT ME!
	 */
	public void vizmapNode(NodeView nodeView, GraphView network_view);

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeView DOCUMENT ME!
	 * @param network_view DOCUMENT ME!
	 */
	public void vizmapEdge(EdgeView edgeView, GraphView network_view);
}
