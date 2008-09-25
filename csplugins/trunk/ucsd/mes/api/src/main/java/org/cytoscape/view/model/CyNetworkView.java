
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.view.model;

import org.cytoscape.model.CyNetwork;

import java.util.List;


/**
 * Contains the visual representation of a Network.
 */
public interface CyNetworkView extends View {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNetwork getNetwork();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyNodeView> getNodeViewList();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyEdgeView> getEdgeViewList();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeView DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNodeView addNodeView(CyNodeView nodeView);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeView DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEdgeView addEdgeView(CyEdgeView edgeView);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeView DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNodeView removeNodeView(CyNodeView nodeView);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeView DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEdgeView removeEdgeView(CyEdgeView edgeView);

	/**
	 * I wonder if this should be here. Shouldn't updating be a function of the renderer?
	 * Doesn't the view just contain the state of the view?  I think a better approach is for the
	 * view to fire an event when something is modified and the renderer will listen for that
	 * event and redraw/update accordingly.
	 */
	public void updateView();
}
