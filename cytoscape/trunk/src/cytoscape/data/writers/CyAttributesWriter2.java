
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

package cytoscape.data.writers;

import cytoscape.data.CyAttributes;

import cytoscape.data.attr.MultiHashMap;

import java.io.IOException;
import java.io.Writer;

import java.util.Collection;
import java.util.Iterator;


/**
 * CyAttributeWriter extracted from AttributeSaverDialog.
 *
 */
public class CyAttributesWriter2 {
	/**
	 * 
	 */
	public static String newline = System.getProperty("line.separator");
	private final CyAttributes cyAttributes;
	private final String attributeName;
	private final Writer fileWriter;

	/**
	 * Creates a new CyAttributesWriter2 object.
	 *
	 * @param attributes  DOCUMENT ME!
	 * @param attributeName  DOCUMENT ME!
	 * @param fileWriter  DOCUMENT ME!
	 */
	public CyAttributesWriter2(final CyAttributes attributes, final String attributeName,
	                           final Writer fileWriter) {
		this.cyAttributes = attributes;
		this.attributeName = attributeName;
		this.fileWriter = fileWriter;
	}

	/**
	 * Write out the state for the given attributes
	 *
	 * @param selectedRows
	 *
	 * @return number of files successfully saved, the better way to do this
	 *         would just be to throw the error and display a specific message
	 *         for each failure, but oh well.
	 * @throws IOException
	 *
	 */
	public void writeAttributes() throws IOException {
		final MultiHashMap attributeMap = cyAttributes.getMultiHashMap();

		if (attributeMap != null) {
			final Iterator keys = cyAttributes.getMultiHashMap().getObjectKeys(attributeName);

			while (keys.hasNext()) {
				final String key = (String) keys.next();
				Object value = attributeMap.getAttributeValue(key, attributeName, null);

				if (value != null) {
					if (value instanceof Collection) {
						String result = key + " = ";
						Collection collection = (Collection) value;

						if (collection.size() > 0) {
							Iterator objIt = collection.iterator();
							result += ("(" + objIt.next());

							while (objIt.hasNext()) {
								result += ("::" + objIt.next());
							}

							result += (")" + newline);
							fileWriter.write(result);
						}
					} else {
						fileWriter.write(key + " = " + value + newline);
					}
				}
			}

			fileWriter.flush();
		}
	}
}
