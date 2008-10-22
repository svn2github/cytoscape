
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;

/**
 * A {@link CyRow} in a {@link CyDataTable} may contain either
 * values of the base types (see {@link CyDataTable}) or a
 * CyFunction. CyFunctions are evaluated when the value for the
 * row is accessed and return the newly created value.  These
 * can be used as references to rows in other columns or CyDataTables.
 */
public interface CyFunction<T> {
/**
     * @return The type of the value returned by the function.
     */
	Class<T> getBaseType();

	/**
	 * This method will evaluate the function when it is called.
	 *
	 * @return The value returned by this function.
	 */
	T getValue();

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return The string that defines the function.
	 */
	String getFunction();

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param functionDesc A string describing the function.
	 */
	void setFunction(String functionDesc);
}
