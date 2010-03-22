
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
public class SelectAllFilter implements Filter {
	//----------------------------------------//
	/**
	 * 
	 */
	public static String FILTER_ID = "[ No Filter ]";

	/**
	 * 
	 */
	public static String FILTER_DESCRIPTION = "Selects all nodes and edges";

	//----------------------------------------//
	// Needed Variables
	//----------------------------------------//
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new SelectAllFilter
	 */
	public SelectAllFilter() {
	}

	//----------------------------------------//
	// Implements Filter
	//----------------------------------------//

	/**
	 * Returns the name for this Filter
	 */
	public String toString() {
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
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterID() {
		return FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param object DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean passesFilter(Object object) {
		if (object instanceof Node || object instanceof Edge) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class[] getPassingTypes() {
		return new Class[] { Node.class, Edge.class };
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
		return new SelectAllFilter();
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
	 * @return  DOCUMENT ME!
	 */
	public String output() {
		return FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param desc DOCUMENT ME!
	 */
	public void input(String desc) {
	}
}
