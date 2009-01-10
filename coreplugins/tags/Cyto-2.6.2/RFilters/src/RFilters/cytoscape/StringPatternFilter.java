
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

import cytoscape.Cytoscape;

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
public class StringPatternFilter implements Filter {
	//----------------------------------------//
	// Filter specific properties 
	//----------------------------------------//
	protected String selectedAttribute;
	protected String searchString;
	protected Class classType;
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;

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
	public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";

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
	public static String FILTER_ID = "String Pattern Filter";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Select nodes or edges based on the value of a text attribute";

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected String identifier = "default";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new StringPatternFilter
	 */
	public StringPatternFilter(String classString, String selectedAttribute, String searchString,
	                           String identifier) {
		try {
			NODE_CLASS = Node.class;
			EDGE_CLASS = Edge.class;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.selectedAttribute = selectedAttribute;
		this.searchString = searchString;
		this.identifier = identifier;
		setClassType(classString);
	}

	/**
	 * Creates a new StringPatternFilter object.
	 *
	 * @param desc  DOCUMENT ME!
	 */
	public StringPatternFilter(String desc) {
		try {
			NODE_CLASS = Class.forName("giny.model.Node");
			EDGE_CLASS = Class.forName("giny.model.Edge");
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
		String value = "";

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

		value = data.getStringAttribute(name, selectedAttribute); //.toString();

		if (value == null) {
			return false;
		}

		// I think that * and ? are better for now....
		String[] pattern = searchString.split("\\s");

		for (int p = 0; p < pattern.length; ++p) {
			if (Strings.isLike((String) value, pattern[p], 0, true)) {
				// this is an OR function
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
		if (other_object instanceof StringPatternFilter) {
			if (((StringPatternFilter) other_object).getSearchString().equals(getSearchString())) {
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
	public Object clone() {
		return new StringPatternFilter(getClassType(), selectedAttribute, searchString,
		                               identifier + "_new");
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
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == SEARCH_STRING_EVENT) {
			//System.out.println( "Search String Changed to "+( String )e.getNewValue() );
			setSearchString((String) e.getNewValue());
		} else if (e.getPropertyName() == FILTER_NAME_EVENT) {
			setIdentifier((String) e.getNewValue());
		} else if (e.getPropertyName() == SELECTED_ATTRIBUTE_EVENT) {
			setSelectedAttribute((String) e.getNewValue());
		} else if (e.getPropertyName() == CLASS_TYPE_EVENT) {
			setClassType((String) e.getNewValue());
		}
	}

	// SearchString /////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param search_string DOCUMENT ME!
	 */
	public void setSearchString(String search_string) {
		this.searchString = search_string;
		fireSearchStringChanged();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void fireSearchStringChanged() {
		pcs.firePropertyChange(SEARCH_STRING_EVENT, null, searchString);
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
		fireSelectedAttributeModified();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void fireSelectedAttributeModified() {
		pcs.firePropertyChange(SELECTED_ATTRIBUTE_EVENT, null, selectedAttribute);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param classString DOCUMENT ME!
	 */
	public void setClassType(String classString) {
		if ((classString == NODE) || classString.equals("Node")) {
			classType = NODE_CLASS;
		} else {
			classType = EDGE_CLASS;
		}

		pcs.firePropertyChange(CLASS_TYPE_EVENT, null, classType);
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

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClassType() + ",");
		buffer.append(getSelectedAttribute() + ",");
		buffer.append(getSearchString() + ",");
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

		if (array[0].equals(NODE)) {
			setClassType(NODE);
		} else if (array[0].equals(EDGE)) {
			setClassType(EDGE);
		} else {
			throw new IllegalArgumentException(array[0] + " is not a valid type of class");
		}

		setSelectedAttribute(array[1]);
		setSearchString(array[2]);
		setIdentifier(array[3]);
	}
}
