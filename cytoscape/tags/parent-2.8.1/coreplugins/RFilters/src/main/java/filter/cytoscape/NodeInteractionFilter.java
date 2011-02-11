
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

package filter.cytoscape;

import cytoscape.*;
import cytoscape.CyNetwork;

import cytoscape.data.*;

import filter.model.*;

import giny.model.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 * This filter will pass nodes based on the edges that
 * they have.
 */
public class NodeInteractionFilter implements Filter {
	//----------------------------------------//
	// Filter specific properties 
	//----------------------------------------//
	protected int filter;
	protected String target;

	/**
	 * 
	 */
	public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

	/**
	 * 
	 */
	public static String FILTER_BOX_EVENT = "FILTER_BOX_EVENT";

	/**
	 * 
	 */
	public static String TARGET_BOX_EVENT = "TARGET_BOX_EVENT";

	/**
	 * 
	 */
	public static String FILTER_ID = "NodeInteractionFilter";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Select nodes based on adjacent edges";

	/**
	 * 
	 */
	public static String SOURCE = "source";

	/**
	 * 
	 */
	public static String TARGET = "target";

	/**
	 * 
	 */
	public static String EITHER = "source or target";

	//----------------------------------------//
	// Cytoscape specific Variables
	//----------------------------------------//

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected String identifier = "NodeInteractionFilter";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new InteractionFilter
	 */
	public NodeInteractionFilter(int filter, String target, String identifier) {
		this.filter = filter;
		this.target = target;
		this.identifier = identifier;
	}

	/**
	 * Creates a new NodeInteractionFilter object.
	 *
	 * @param desc  DOCUMENT ME!
	 */
	public NodeInteractionFilter(String desc) {
		input(desc);
	}

	//----------------------------------------//
	// Implements Filter
	//----------------------------------------//

	/**
	 * Returns the name for this Filter
	 */
	public String toString() {
		return identifier;
	}

	/**
	 * sets a new name for this filter
	 */
	public void setIdentifier(String new_id) {
		this.identifier = new_id;
		pcs.firePropertyChange(FILTER_NAME_EVENT, null, new_id);
	}

	/**
	 * This is usually the same as the class name
	 */
	public String getFilterID() {
		return FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return FILTER_DESCRIPTION;
	}

	/**
	 * An object passes this Filter if it is the source/target
	 * node for an edge that has a matching property for
	 * the given Edge atttribute.
	 */
	public boolean passesFilter(Object object) {
		Filter filter = FilterManager.defaultManager().getFilter(this.filter);

		if (filter == null) {
			return false;
		}

		if (!(object instanceof Node)) {
			return false;
		}

		Node node = (Node) object;

		//get the list of all relevant edges
		List adjacentEdges;
		GraphPerspective myPerspective = Cytoscape.getCurrentNetwork();

		if (target == SOURCE) {
			adjacentEdges = myPerspective.getAdjacentEdgesList(node, true, false, true);
		} else if (target == TARGET) {
			adjacentEdges = myPerspective.getAdjacentEdgesList(node, true, true, false);
		} else {
			adjacentEdges = adjacentEdges = myPerspective.getAdjacentEdgesList(node, true, true,
			                                                                   true);
		}

		Iterator edgeIt = adjacentEdges.iterator();

		while (edgeIt.hasNext()) {
			if (filter.passesFilter(edgeIt.next())) {
				return true;
			}
		}

		return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class[] getPassingTypes() {
		return new Class[] { Node.class };
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param other_object DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean equals(Object other_object) {
		return super.equals(other_object);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object clone() {
		return new NodeInteractionFilter(filter, target, identifier + "_new");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getTarget() {
		return target;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param target DOCUMENT ME!
	 */
	public void setTarget(String target) {
		this.target = target;
		pcs.firePropertyChange(TARGET_BOX_EVENT, null, target);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param filter DOCUMENT ME!
	 */
	public void setFilter(int filter) {
		int oldvalue = this.filter;
		this.filter = filter;
		pcs.firePropertyChange(FILTER_BOX_EVENT, oldvalue, filter);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getFilter() {
		return filter;
	}

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFilter() + ",");
		buffer.append(getTarget() + ",");
		buffer.append(toString());

		return buffer.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param desc DOCUMENT ME!
	 */
	public void input(String desc) {
		String[] array = desc.split(",");
		setFilter((new Integer(array[0])).intValue());

		if (array[1].equals(TARGET)) {
			setTarget(TARGET);
		} else if (array[1].equals(SOURCE)) {
			setTarget(SOURCE);
		} else if (array[1].equals(EITHER)) {
			setTarget(EITHER);
		} else {
			throw new IllegalArgumentException(array[1] + " is not a valid interaction type");
		}

		setIdentifier(array[2]);
	}
}
