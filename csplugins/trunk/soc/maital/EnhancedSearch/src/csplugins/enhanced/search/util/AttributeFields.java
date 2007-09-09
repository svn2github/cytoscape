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

package csplugins.enhanced.search.util;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class AttributeFields {
	
	public static final String INDEX_FIELD = "id";

	private String[] fields = null;

	private byte[] types = null;

	public AttributeFields() {
		initFields();
	}

	public byte getType(String attrName) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(attrName)) {
				return types[i];
			}
		}
		return CyAttributes.TYPE_UNDEFINED;
	}

	public String[] getFields() {
		return fields;
	}

	private void initFields() {

		// Maital: change this.
		// Include handling of ID fields (nodes and edges).

		// Define attribute fields in which the search is to be carried on
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String[] nodeAttrNameArray = nodeAttributes.getAttributeNames();
		String[] edgeAttrNameArray = edgeAttributes.getAttributeNames();
		int numOfNodeAttributes = nodeAttrNameArray.length;
		int numOfEdgeAttributes = edgeAttrNameArray.length;

		// The last two methods didn't return ID as one of the fields.
		// To allow search on ID field, we add it manually.
		fields = new String[numOfNodeAttributes + numOfEdgeAttributes + 1];
		fields[0] = INDEX_FIELD;
		
		System.arraycopy(nodeAttrNameArray, 0, fields, 1, numOfNodeAttributes);
		System.arraycopy(edgeAttrNameArray, 0, fields, numOfNodeAttributes +1,
				numOfEdgeAttributes);

		// Define value types
		byte[] nodeAttrValueTypes = new byte[numOfNodeAttributes];
		for (int i = 0; i < numOfNodeAttributes; i++) {
			nodeAttrValueTypes[i] = nodeAttributes
					.getType(nodeAttrNameArray[i]);
		}

		byte[] edgeAttrValueTypes = new byte[numOfEdgeAttributes];
		for (int i = 0; i < numOfEdgeAttributes; i++) {
			edgeAttrValueTypes[i] = edgeAttributes
					.getType(edgeAttrNameArray[i]);
		}

		// ... And add type STRING for the ID field
		types = new byte[numOfNodeAttributes + numOfEdgeAttributes + 1];
		types[0] = CyAttributes.TYPE_STRING;
		
		System.arraycopy(nodeAttrValueTypes, 0, types, 1, numOfNodeAttributes);
		System.arraycopy(edgeAttrValueTypes, 0, types, numOfNodeAttributes + 1,
				numOfEdgeAttributes);

		// Handle whitespace characters and case in attribute names
		for (int i = 0; i < fields.length; i++) {
			fields[i] = EnhancedSearchUtils.replaceWhitespace(fields[i]);
			fields[i] = fields[i].toLowerCase();
		}

	}

}
