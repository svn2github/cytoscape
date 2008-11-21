
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

package cytoscape.filter.cytoscape;


import cytoscape.Cytoscape;
import cytoscape.filter.model.Filter;
import cytoscape.filter.model.FilterManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import javax.swing.event.SwingPropertyChangeSupport;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class NodeTopologyFilter implements Filter {
	//----------------------------------------//
	// Filter specific properties 
	//----------------------------------------//
	protected int filter;
	protected Integer count;
	protected Integer distance;
	protected HashSet seenNodes;
	protected CyNetwork myPerspective;

	/**
	 * 
	 */
	public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

	/**
	 * 
	 */
	public static String FILTER_ID = "Node Topology Filter";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Select nodes based on the attributes of surrounding nodes";

	/**
	 * 
	 */
	public static String FILTER_EVENT = "FILTER_EVENT";

	/**
	 * 
	 */
	public static String FILTER_BOX_EVENT = "FILTER_BOX";

	/**
	 * 
	 */
	public static String COUNT_EVENT = "COUNT";

	/**
	 * 
	 */
	public static String DISTANCE_EVENT = "DISTANCE";

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected String identifier = "default";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new NodeTopologyFilter
	 */
	public NodeTopologyFilter(Integer count, Integer distance, int filter, String identifier) {
		this.count = count;
		this.distance = distance;
		this.filter = filter;
		this.identifier = identifier;
	}

	/**
	 * Creates a new NodeTopologyFilter
	 */
	public NodeTopologyFilter(String desc) {
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
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return FILTER_DESCRIPTION;
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
	 * An Object Passes this Filter if its "toString" method
	 * matches any of the Text from the TextField
	 */
	public boolean passesFilter(Object object) {
		if (object instanceof CyNode) {
			seenNodes = new HashSet();
			seenNodes.add(object);
			myPerspective = Cytoscape.getCurrentNetwork();

			int totalSum = countNeighbors((CyNode) object, 0);
			Filter filter = FilterManager.defaultManager().getFilter(this.filter);

			if (filter.passesFilter((CyNode) object)) {
				totalSum -= 1;
			}

			return totalSum >= count.intValue();
		} else {
			return false;
		}
	}

	private int countNeighbors(CyNode currentNode, int currentDistance) {
		Filter filter = FilterManager.defaultManager().getFilter(this.filter);
		int sum = 0;

		if (filter == null) {
			return sum;
		}

		if (sum >= count.intValue()) {
			return sum;
		}

		if (currentDistance == distance.intValue()) {
			if (filter.passesFilter(currentNode)) {
				return 1;
			} else {
				return 0;
			}
		} else {
			java.util.List neighbors = myPerspective.neighborsList(currentNode);
			Iterator nodeIt = neighbors.iterator();

			while (nodeIt.hasNext() && (sum < count.intValue())) {
				CyNode nextNode = (CyNode) nodeIt.next();

				if (!seenNodes.contains(nextNode)) {
					seenNodes.add(nextNode);
					sum += countNeighbors(nextNode, currentDistance + 1);
				}

				if (sum > count.intValue()) {
					return sum;
				}
			}

			if (filter.passesFilter(currentNode)) {
				return sum + 1;
			} else {
				return sum;
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class[] getPassingTypes() {
		return new Class[] { CyNode.class };
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
		return new NodeTopologyFilter(count, distance, filter, identifier + "_new");
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param count DOCUMENT ME!
	 */
	public void setCount(Integer count) {
		this.count = count;
		pcs.firePropertyChange(COUNT_EVENT, null, count);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param distance DOCUMENT ME!
	 */
	public void setDistance(Integer distance) {
		this.distance = distance;
		pcs.firePropertyChange(DISTANCE_EVENT, null, distance);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Integer getDistance() {
		return distance;
	}

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getCount() + ",");
		buffer.append(getDistance() + ",");
		buffer.append(getFilter() + ",");
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
		setCount(Integer.valueOf(array[0]));
		setDistance(Integer.valueOf(array[1]));
		setFilter((Integer.valueOf(array[2])).intValue());
		setIdentifier(array[3]);
	}
}
