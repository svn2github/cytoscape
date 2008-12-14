
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

import ViolinStrings.Strings;

import cytoscape.*;
import cytoscape.CyNetwork;

import cytoscape.data.*;

import filter.model.*;

import giny.model.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class BooleanMetaFilter implements Filter {
	//----------------------------------------//
	// Filter specific properties 
	//----------------------------------------//
	protected int[] filters;
	protected String comparison;
	protected boolean negation;

	/**
	 * 
	 */
	public static String AND = "ALL";

	/**
	 * 
	 */
	public static String OR = "AT LEAST ONE";

	/**
	 * 
	 */
	public static String XOR = "ONLY ONE";

	/**
	 * 
	 */
	public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

	/**
	 * 
	 */
	public static String FILTER_ID = "Boolean Meta-Filter";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Select nodes based on a combination of other filters";

	/**
	 * 
	 */
	public static String COMPARISON_EVENT = "COMPARISON_EVENT";

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
	public static String NEGATION_EVENT = "NEGATION_EVENT";

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected String identifier = "default";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new BooleanMetaFilter
	 */
	public BooleanMetaFilter(int[] filters, String comparison, String identifier, boolean negation) {
		this.filters = filters;
		this.comparison = comparison;
		this.identifier = identifier;
		this.negation = negation;
	}

	/**
	 * Creates a new BooleanMetaFilter object.
	 *
	 * @param desc  DOCUMENT ME!
	 */
	public BooleanMetaFilter(String desc) {
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
		if (filters.length == 0) {
			return false ^ negation;
		}

		int count = 0;

		for (int idx = 0; idx < filters.length; idx++) {
			//System.err.println(""+filters[idx]);
			Filter f = FilterManager.defaultManager().getFilter(filters[idx]);
			boolean filterResult = false;

			if (f != null) {
				filterResult = f.passesFilter(object);
			} // end of if ()

			if ((comparison == AND) && !filterResult) {
				return false ^ negation;
			}

			if ((comparison == OR) && filterResult) {
				return true ^ negation;
			}

			if ((comparison == XOR) && filterResult) {
				if (++count > 1) {
					return false ^ negation;
				}
			}
		}

		if ((comparison == XOR) && (count == 1)) {
			return true ^ negation;
		}

		if (comparison == AND) {
			return true ^ negation;
		} else {
			return false ^ negation;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class[] getPassingTypes() {
		Vector passingTypes = new Vector(2);

		for (int idx = 0; idx < filters.length; idx++) {
			Filter filter = FilterManager.defaultManager().getFilter(filters[idx]);
			Class[] filterPassingTypes = filter.getPassingTypes();

			for (int idy = 0; idy < filterPassingTypes.length; idy++) {
				if (!passingTypes.contains(filterPassingTypes[idy])) {
					passingTypes.add(filterPassingTypes[idy]);
				}
			}
		}

		return (Class[]) passingTypes.toArray(new Class[] {  });
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
		return new BooleanMetaFilter(filters, comparison, identifier + "_new", negation);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param comparison DOCUMENT ME!
	 */
	public void setComparison(String comparison) {
		this.comparison = comparison;
		pcs.firePropertyChange(COMPARISON_EVENT, null, comparison);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getComparison() {
		return comparison;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param negation DOCUMENT ME!
	 */
	public void setNegation(boolean negation) {
		this.negation = negation;
		pcs.firePropertyChange(NEGATION_EVENT, null, null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean getNegation() {
		return negation;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param filters DOCUMENT ME!
	 */
	public void setFilters(int[] filters) {
		this.filters = filters;
		pcs.firePropertyChange(FILTER_BOX_EVENT, null, filters);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getFilters() {
		return filters;
	}

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < filters.length; ++i) {
			buffer.append(filters[i]);

			if (i != (filters.length - 1))
				buffer.append(":");
		}

		buffer.append("," + getComparison());
		buffer.append("," + negation + ",");
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
		String[] filterStrings = array[0].split(":");
		int[] selectedFilters = null;

		if (filterStrings[0].equals("")) {
			selectedFilters = new int[0];
		} else {
			selectedFilters = new int[filterStrings.length];
		}

		for (int idx = 0; idx < selectedFilters.length; idx++) {
			selectedFilters[idx] = (new Integer(filterStrings[idx])).intValue();
		} // end of for ()

		setFilters(selectedFilters);

		if (array[1].equals(AND)) {
			setComparison(AND);
		} else if (array[1].equals(OR)) {
			setComparison(OR);
		} else if (array[1].equals(XOR)) {
			setComparison(XOR);
		} else {
			throw new IllegalArgumentException(array[1] + " is not a valid type of comparison");
		}

		setIdentifier(array[3]);
		setNegation((new Boolean(array[2])).booleanValue());
	}
}
