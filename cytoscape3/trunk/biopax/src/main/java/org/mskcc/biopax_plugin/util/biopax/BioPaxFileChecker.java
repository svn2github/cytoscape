// $Id: BioPaxFileChecker.java,v 1.3 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.biopax;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;


/**
 * Tries to ascertain whether the given file is a BioPAX document.
 *
 * @author Ethan Cerami.
 */
public class BioPaxFileChecker {
	private boolean bioPaxFlag = false;

	/**
	 * Constructor.
	 *
	 * @param reader Reader reader.
	 * @throws IOException File Reading Error.
	 */
	public BioPaxFileChecker(Reader reader) throws IOException {
		try {
			//  Read in File via JDOM SAX Builder
			SAXBuilder builder = new SAXBuilder();
			Document bioPaxDoc = builder.build(reader);

			//  Get Root Element
			Element root = bioPaxDoc.getRootElement();

			String name = root.getName();

			//  If the Root Element is RDF, and at least one of the children
			//  is in the BioPAX Namespace, we probably have a BioPAX Document.
			if (name.equalsIgnoreCase("RDF")) {
				List children = root.getChildren();

				for (int i = 0; i < children.size(); i++) {
					Element child = (Element) children.get(i);
					String namespaceUri = child.getNamespaceURI();

					if (namespaceUri.equals(BioPaxConstants.BIOPAX_LEVEL_1_NAMESPACE_URI)
					    || namespaceUri.equals(BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI)) {
						bioPaxFlag = true;

						break;
					}
				}
			}
		} catch (JDOMException e) {
			bioPaxFlag = false;
		}
	}

	/**
	 * Indicates whether the File is probably a BioPAX Document.
	 *
	 * @return true or false.
	 */
	public boolean isProbablyBioPaxFile() {
		return bioPaxFlag;
	}
}
