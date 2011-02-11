
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

package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.PolyEdgeGraphLayout;


/**
 * This class extends MutableGraphLayout to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 **/
public interface MutablePolyEdgeGraphLayout extends PolyEdgeGraphLayout, MutableGraphLayout {
	/**
	 * Deletes an edge anchor point.<p>
	 * The deletion of an anchor point is accomplished such that the ordering of
	 * remaining anchor points stays the same.  An anchor point [belonging
	 * to specified edge] with index greater than
	 * anchorIndex will be assigned a new index equal to its
	 * previous index minus one; an anchor point with index less than
	 * anchorIndex will keep its index.
	 *
	 * @param edge the edge to which the anchor point to be
	 *   deleted belongs.
	 * @param anchorIndex the index of anchor point, within specified edge,
	 *   which we're trying to delete.
	 * @exception IllegalArgumentException if specified edge is not
	 *   an edge in this graph.
	 * @exception IndexOutOfBoundsException if anchorIndex is not
	 *   in the interval [0, getNumAnchors(edge) - 1].
	 * @exception UnsupportedOperationException if specified edge
	 *   has source and target nodes that are both
	 *   non-movable.
	 **/
	public void deleteAnchor(int edge, int anchorIndex);

	/**
	 * Creates a new edge anchor point.<p>
	 * The creation of an anchor point is accomplished such that the ordering
	 * of existing anchor points stays the same.  An existing anchor point
	 * [belonging to specified edge] with index greater
	 * than or equal to anchorIndex will be assigned a new index
	 * equal to its previous index plus one; an existing anchor point with index
	 * less than anchorIndex will keep its index.<p>
	 * A new anchor point P's X,Y position is the midpoint along the segment
	 * whose end points are P's neighbors in the edge poly-line definition;
	 * X,Y positions of existing anchor points and nodes are unchanged.
	 *
	 * @param edge new anchor point will be created on specified edge.
	 * @param anchorIndex new anchor point will have index
	 *   anchorIndex within specified edge.
	 * @exception IllegalArgumentException if specified edge is not
	 *   an edge in this graph.
	 * @exception IndexOutOfBoundsException if anchorIndex is not
	 *   in the interval [0, getNumAnchors(edge)].
	 * @exception UnsupportedOperationException if specified edge
	 *   source and target nodes that are both non-movable.
	 **/
	public void createAnchor(int edge, int anchorIndex);

	/**
	 * Sets the X,Y position of an edge anchor point.<p>
	 * X, Y values set by this method shall be reflected in the return values
	 * of getAnchorPosition() - that is, if we call
	 * <blockquote><code>setAnchorPosition(edge, aInx, x, y)</code></blockquote>
	 * then the subsequent expressions
	 * <blockquote>
	 * <nobr><code>x == getAnchorPosition(edge, aInx, true)</code></nobr><br />
	 * <nobr><code>y == getAnchorPosition(edge, aInx, false)</code></nobr>
	 * </blockquote>
	 * both evaluate to true.
	 *
	 * @param edge the edge to which the anchor point to be
	 *   positioned belongs.
	 * @param anchorIndex the index of anchor point, within specified edge,
	 *   which we're trying to position.
	 * @param xPosition the desired X position of specified edge anchor point.
	 * @param yPosition the desired Y position of specified edge anchor point.
	 * @exception IllegalArgumentException if specified edge is not
	 *   an edge in this graph.
	 * @exception IndexOutOfBoundsException if anchorIndex is not
	 *   in the interval [0, getNumAnchors(edge) - 1].
	 * @exception IllegalArgumentException if specified X position or
	 *   specified Y position falls outside of [0.0, getMaxWidth()] and
	 *   [0.0, getMaxHeight()], respectively.
	 * @exception UnsupportedOperationException if specified edge
	 *   has source and target nodes that are both non-movable.
	 **/
	public void setAnchorPosition(int edge, int anchorIndex, double xPosition, double yPosition);
}
