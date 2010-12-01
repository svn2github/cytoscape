/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.vizmap;

import java.util.Properties;
import java.util.Collection;

/**
 * This is a utility interface used for converting collections of 
 * VisualStyle objects into serializable Properties objects and vice 
 * versa. 
 */
public interface VisualStyleSerializer {

	/**
	 * This method creates a serializable Properties object based
	 * on the provided collection of visual styles.
	 * @param styles The collection of VisualStyles that you wish to
	 * convert into a serializable Properties object.
	 * @return A Properties object that contains a representation
	 * of the collection of visual styles.
	 */
	Properties createProperties(Collection<VisualStyle> styles);

	/**
	 * This method creates a collection of VisualStyle objects based
	 * on the provided Properties object.
	 * @param props A properties object containing a representation
	 * of VisualStyles.
	 * @return A collection of VisualStyle objects created from the
	 * provided Properties object.
	 */
	Collection<VisualStyle> createVisualStyles(Properties props);
}
