
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

package cytoscape.filter.model;

import javax.swing.event.SwingPropertyChangeSupport;


/**
 * 
  */
public interface Filter {
	/**
	 * 
	 */
	public static String FILTER_MODIFIED = "FILTER_MODIFIED";

	/**
	 * Note that to truly pass this Filter an object must <b>both</b> be an
	 * instance of at least one of the passingTypes (unless {@link
	 * #getPassingTypes()} returns null) <i>and</i> passesFilter( object ).
	 * @see FilterUtilities#passesFilter( Filter, Object )
	 * @return true iff the given Object passes the filter.
	 */
	public boolean passesFilter(Object object);

	/**
	 * @return an array of Classes; all passers of this filter must be an
	 * instance of at least one of these classes; null if all object types could
	 * potentially pass this filter.
	 */
	public Class[] getPassingTypes();

	/**
	 * @return the name of this Filter.
	 */
	public String toString();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterID();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription();

	/**
	 * Every filter needs to know how to output itself in such a way that
	 * it knows how to re-create itself from a flat file.<BR>
	 * The required format is: <BR>
	 * Filter <I>class name</I> <B><small>whatever a filter wants</small></B>
	 */
	public String output();

	/**
	 * By passing a String that was ouput by this Filter it will create and return
	 * a new Filter that is equivalent to the instance that was output. Note that
	 * the output is modifiable, so that one could change how a Filter behaves if
	 * they wanted to.
	 */
	public void input(String desc);

	/**
	 * All filters should override the Object equals(..) method to return true
	 * when compared to an equivalent filter.  This method should <b>not</b> call
	 * passesFilter(..) on this or on the given object.
	 * @see #hashCode()
	 */
	public boolean equals(Object other_object);

	/**
	 * All filters should implement the clone() method, as Filters will be
	 * cloned.
	 */
	public Object clone();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport();
} // interface Filter
