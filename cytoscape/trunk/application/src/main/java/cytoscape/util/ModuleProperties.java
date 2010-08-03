
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

package cytoscape.util;

import java.util.HashMap;
import java.util.List;

import cytoscape.layout.Tunable;


/**
 * 
  */
public interface ModuleProperties {
	/**
	 * This method is used to add a new Tunable to the LayoutProperties
	 * list.  The Tunable can later be retrieved by name using the
	 * <tt>get</tt> method.
	 *
	 * @param tunable The Tunable to add to this LayoutProperties
	 */
	public void add(Tunable tunable);

	/**
	 * This method is used to get the Tunable named <tt>name</tt>
	 * from this LayoutProperties.  In general, the name of a
	 * Tunable should correspond to the last component of the
	 * property that it is associated with.
	 *
	 * @param name The name of the Tunable to retrieve.
	 * @return Tunable associated with <tt>name</tt> or null if
	 *         there is no Tunable with that name.
	 */
	public Tunable get(String name);

	/**
	  * This method is used to get the list of available Tunables for
	  * this algorithm.
	  *
	  * @return the list of the names of Tunables for this algorithm
	  */
	public List<String> getTunableList();

	/**
	 * This method is used to get the value from the Tunable
	 * named <tt>name</tt> from this LayoutProperties.  The
	 * value is always returned as a String.
	 *
	 * @param name The name of the Tunable whose value you
	 *             want to retrieve.
	 * @return String value from the Tunable or null if
	 *         there is no Tunable with that name.
	 */
	public String getValue(String name);

	/**
	 * This method calls the <tt>updateValues</tt> method of each
	 * Tunable that is part of this LayoutProperty.
	 */
	public void updateValues();

	/**
	 * These methods provide some simple convenience methods for property
	 * handling.  They are intended to be used as a mechanism to track
	 * settings and tuneables.
	 */

	/**
	 * getProperties is used to extract properties from the Cytoscape properties
	 * file.  getProperties should always be called first to initialize the property
	 * maps.
	 *
	 * @return HashMap containing the resulting properties
	 */
	public HashMap getProperties();

	/**
	 * saveProperties is used to add modified properties to the Cytoscape properties
	 * so they can be saved in the properties file.
	 *
	 */
	public void saveProperties();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param property DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void setProperty(String property, String value);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param property DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void setSavedProperty(String property, String value);

	/**
	 * revertProperties is used primarily by the settings dialog mechanism when
	 * the user does a "Cancel".
	 */
	public void revertProperties();

	/**
	 * This method is used to read the properties from the Cytoscape properties
	 * file and set the values for that property in the appropriate Tunable.  If
	 * there is no value for the property, then the default value in the Tunable
	 * is used to initialize the property.
	 */
	public void initializeProperties();

	/**
	 * This method returns list of tunables.
	 *
	 * @return JPanel that contains all of the Tunable widgets
	 */
	public List<Tunable> getTunables();
}
