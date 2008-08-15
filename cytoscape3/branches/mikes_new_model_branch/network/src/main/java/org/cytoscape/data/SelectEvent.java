/*
  File: SelectEvent.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.data;


/**
 * Events that are fired when the selected state of a Node or Edge, or a group
 * of Nodes or Edges, is changed.
 */
public interface SelectEvent {

	/**
	 * Static constant indicating a change to a single Node.
	 */
	public static final int SINGLE_NODE = 0;

	/**
	 * Static constant indicating a change to a single Edge.
	 */
	public static final int SINGLE_EDGE = 1;

	/**
	 * Static constant indicating a change to a group of Nodes.
	 */
	public static final int NODE_SET = 2;

	/**
	 * Static constant indicating a change to a group of Edges.
	 */
	public static final int EDGE_SET = 3;


	/**
	 * Returns the source of this event.
	 */
	public SelectFilter getSource(); 

	/**
	 * Returns an object reference to the target that was changed. This should
	 * be a Node, an Edge, a Set of Nodes, or a Set of Edges. The return value
	 * of getTargetType determines which of the four cases applies.
	 */
	public Object getTarget(); 

	/**
	 * Returns a static constant identifying the type of object; either SINGLE_NODE
	 * for a Node, SINGLE_EDGE for an Edge, NODE_SET for a Set of Nodes, or
	 * EDGE_SET for a Set of Edges.
	 */
	public int getTargetType(); 

	/**
	 * Returns a boolean identifying the type of event, true the selectes state was set to true, false if it was set to false
	 */
	public boolean getEventType(); 

	/**
	 * Returns a String representation of this object's data.
	 */
	public String toString();
}
