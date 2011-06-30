
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

import cytoscape.graph.layout.GraphLayout;


/**
 * This class offers a hook for layout algorithms to operate on.
 */
public interface MutableGraphLayout extends GraphLayout {
	/**
	 * Tells us whether or not the specified node
	 * can be moved by setNodePosition().
	 *
	 * @param node node whose mobility we are querying.
	 * @exception IllegalArgumentException if specified node is not a node
	 *   in this graph.
	 * @see #setNodePosition(int, double, double)
	 */
	public boolean isMovableNode(int node);

	/**
	 * Sets the X,Y position of a node.
	 * This is a hook for layout algorithms to actually set locations of
	 * nodes.  Layout algorithms should call this method.<p>
	 * X, Y values set by this method shall be reflected in the return values
	 * of getNodePosition() -- that is, if we call
	 * <blockquote><code>setNodePosition(node, x, y)</code></blockquote>
	 * then the subsequent expressions
	 * <blockquote>
	 * <nobr><code>x == getNodePosition(node, true)</code></nobr><br />
	 * <nobr><code>y == getNodePosition(node, false)</code></nobr>
	 * </blockquote>
	 * both evaluate to true.<p>
	 * Layout algorithms are encouraged to set node positions such that
	 * their X and Y values use the full range of allowable values, including
	 * the boundary values 0, getMaxWidth(), and
	 * getMaxHeight().  Any notion of node thickness, graph
	 * border on perimeter, etc. should be predetermined by the application
	 * using a layout algorithm; getMaxWidth() and
	 * getMaxHeight() should be defined accordingly by the
	 * application using a layout algorithm.
	 *
	 * @exception IllegalArgumentException if xPos or yPos are out of
	 *   allowable range [0.0, getMaxWidth()] and [0.0, getMaxHeight()].
	 *   respectively.
	 *   <nobr><code>xPos < 0.0</code></nobr>, if
	 * @exception IllegalArgumentException if specified node is not
	 *   a node in this graph.
	 * @exception UnsupportedOperationException if
	 *   isMovableNode(node) returns false.
	 *
	 * @see #getMaxWidth()
	 * @see #getMaxHeight()
	 * @see #getNodePosition(int, boolean)
	 * @see #isMovableNode(int)
	 */
	public void setNodePosition(int node, double xPos, double yPos);
}
