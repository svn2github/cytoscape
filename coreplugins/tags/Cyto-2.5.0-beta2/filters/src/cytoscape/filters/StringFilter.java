
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

package cytoscape.filters;


import java.beans.*;

import java.util.*;



/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class StringFilter extends AtomicFilter {

	public String getSearchStr() {
		if (searchValues == null) return null;
		return searchValues[0];
	}
	
	public void setSearchStr(String pSearchStr) {
		searchValues = new String[2];
		searchValues[0] = pSearchStr;
	}
	
	/**
	 * @return the name of this Filter and the search string (keyword).
	 */
	public String toString() {
		return attributeName + ","+searchValues[0];
	}

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new StringPatternFilter object.
	 *
	 * @param desc  DOCUMENT ME!
	 */
	public StringFilter(String pAttributeName) {
		super(pAttributeName,null);
	}

	public StringFilter(String pAttributeName, String[] pSearchValues) {
		super(pAttributeName,pSearchValues);
	}

	public StringFilter(String pAttributeName, String pSearchString) {
		super(pAttributeName,null);
		searchValues = new String[2];
		searchValues[0] = pSearchString;
	}
	
	public StringFilter clone() {
		return new StringFilter(attributeName, searchValues);
	}
}
