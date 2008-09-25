
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

package org.cytoscape.view.model;

/**
 * The interface used to provide a common interface that allows
 * String to be parsed into ViewModel objects.
 */
public interface Saveable {
	/**
	 * Any class that implements this method or interface that extends it <b>must</b>
	 * provide a description of the format of the string that will be written.  The goal is to
	 * provide as much information as an implementer will need to create a definition string that
	 * will be work seamlessly with other implementions of this interface. You might even consider
	 * defining a regular expression that defines the string.
	 *
	 * @return A String that defines the state of this object that is suitable for serialization.
	 */
	public String getStateDefinition();

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param def A String that is defined according state definition found in
	 *        Saveable#getStateDefinition().
	 */
	public void parseStateDefinition(String def);

	/**
	 * This meant to return a human readable string representation of this implementation
	 * that would be suitable for presentation in a user interface.  This can be an arbitrary
	 * string but shouldn't be more than a few words long.<p><b>This String is <i>NOT</i>
	 * meant to be parsed or otherwise interpreted by a computer. It is <i>STRICTLY</i> for humans
	 * to read!</b></p>
	 *
	 * @return A human readable string identifying this object.
	 */
	public String getName();
}
