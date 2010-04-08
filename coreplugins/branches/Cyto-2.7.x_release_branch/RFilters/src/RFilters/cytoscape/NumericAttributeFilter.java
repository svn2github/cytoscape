
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

import cytoscape.data.CyAttributes;

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
public class NumericAttributeFilter implements Filter {
	//----------------------------------------//
	// Filter specific properties 
	//----------------------------------------//
	protected String selectedAttribute;
	protected Number searchNumber;
	protected String comparison;
	protected Class classType;
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;

	/**
	 * 
	 */
	public static String EQUAL = "=";

	/**
	 * 
	 */
	public static String LESS = "<";

	/**
	 * 
	 */
	public static String GREATER = ">";

	/**
	 * 
	 */
	public static String NODE = "Node";

	/**
	 * 
	 */
	public static String EDGE = "Edge";

	/**
	 * 
	 */
	public static String SEARCH_NUMBER_EVENT = "SEARCH_STRING_EVENT";

	/**
	 * 
	 */
	public static String SELECTED_ATTRIBUTE_EVENT = "SELECTED_ATTRIBUTE_EVENT";

	/**
	 * 
	 */
	public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

	/**
	 * 
	 */
	public static String CLASS_TYPE_EVENT = "CLASS_TYPE";

	/**
	 * 
	 */
	public static String FILTER_ID = "Numeric Attribute Filter";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Select nodes or edges based on the value of a numeric attribute";

	/**
	 * 
	 */
	public static String COMPARISON_EVENT = "COMPARISON_EVENT";

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected String identifier = "default";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new NumericAttributeFilter
	 */
	public NumericAttributeFilter(String comparison, String classString, String selectedAttribute,
	                              Number searchNumber, String identifier) {
		this.comparison = comparison;

		try {
			NODE_CLASS = Class.forName("giny.model.Node");
			EDGE_CLASS = Class.forName("giny.model.Edge");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.selectedAttribute = selectedAttribute;
		this.searchNumber = searchNumber;
		this.identifier = identifier;
		setClassType(classString);
	}

	/**
	 * Creates a new NumericAttributeFilter
	 */
	public NumericAttributeFilter(String desc) {
		try {
			NODE_CLASS = Node.class;
			EDGE_CLASS = Edge.class;
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	 * An Object Passes this Filter if its "toString" method
	 * matches any of the Text from the TextField
	 */
	public boolean passesFilter(Object object) {
		if (!classType.isInstance(object)) {
			return false;
		}

		CyAttributes data = null;

		if (classType.equals(NODE_CLASS)) {
			data = Cytoscape.getNodeAttributes();
		} else {
			data = Cytoscape.getEdgeAttributes();
		}

		String name = ((GraphObject) object).getIdentifier();

		if (name == null) {
			return false;
		}

		Number value;

		if (data.getType(selectedAttribute) == CyAttributes.TYPE_FLOATING)
			value = (Number) data.getDoubleAttribute(name, selectedAttribute);
		else
			value = (Number) data.getIntegerAttribute(name, selectedAttribute);

		if (value == null) {
			return false;
		}

		if (comparison == EQUAL) {
			return searchNumber.doubleValue() == value.doubleValue();
		} else if (comparison == LESS) {
			return searchNumber.doubleValue() > value.doubleValue();
		} else if (comparison == GREATER) {
			return searchNumber.doubleValue() < value.doubleValue();
		} else {
			//System.err.println("Comparison not identified");
			return false;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class[] getPassingTypes() {
		return new Class[] { classType };
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
		return new NumericAttributeFilter(comparison, getClassType(), selectedAttribute,
		                                  searchNumber, identifier + "_new");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	// SearchString /////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Number getSearchNumber() {
		return searchNumber;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param searchNumber DOCUMENT ME!
	 */
	public void setSearchNumber(Number searchNumber) {
		this.searchNumber = searchNumber;
		pcs.firePropertyChange(SEARCH_NUMBER_EVENT, null, searchNumber);
	}

	// Selected_Attribute ///////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSelectedAttribute() {
		return selectedAttribute;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_attr DOCUMENT ME!
	 */
	public void setSelectedAttribute(String new_attr) {
		this.selectedAttribute = new_attr;
		pcs.firePropertyChange(SELECTED_ATTRIBUTE_EVENT, null, selectedAttribute);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param classString DOCUMENT ME!
	 */
	public void setClassType(String classString) {
		if ((classString == NODE) || classString.equals("Node")) {
			this.classType = NODE_CLASS;
		} else {
			this.classType = EDGE_CLASS;
		}

		pcs.firePropertyChange(CLASS_TYPE_EVENT, null, classString);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getClassType() {
		if (classType == NODE_CLASS) {
			return NODE;
		} else {
			return EDGE;
		}
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

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getComparison() + ",");
		buffer.append(getClassType() + ",");
		buffer.append(getSelectedAttribute() + ",");
		buffer.append(getSearchNumber() + ",");
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

		if (array[0].equals(GREATER)) {
			setComparison(GREATER);
		} else if (array[0].equals(LESS)) {
			setComparison(LESS);
		} else if (array[0].equals(EQUAL)) {
			setComparison(EQUAL);
		} else {
			throw new IllegalArgumentException(array[0] + " is not a valid type of comparison");
		}

		setSelectedAttribute(array[2]);
		setSearchNumber(new Double(array[3]));
		setIdentifier(array[4]);

		if (array[1].equals(NODE)) {
			setClassType(NODE);
		} else if (array[1].equals(EDGE)) {
			setClassType(EDGE);
		} else {
			throw new IllegalArgumentException(array[0] + " is not a valid type of class");
		}
	}
}
