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

package org.cytoscape.search.util;

//import cytoscape.Cytoscape;
//import cytoscape.data.CyAttributes;
import java.util.Map;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;

/**
 * This object will serve as input to the CustomMultiFieldQueryParser.
 * It contains attribute fields names and their types.
 * This way CustomMultiFieldQueryParser can recognise numeric attribute fields.
 */
public class AttributeFields {
	
	public static final String INDEX_FIELD = "id";

	private String[] fields = null;

	private String[] types = null;
	
	private CyNetwork network;

	public AttributeFields(CyNetwork net) {
		this.network = net;
		initFields();
	}

	/**
	 * Initialize this object with attribute fields names and their type.
	 * Eventually, fields[i] will hold attribute field name and types[i] will hold its type.
	 * fields[] and types[] contain both node and edge attributes.
	 * ID (INDEX_FIELD) is treated as another attribute of type string.
	 * There are probably better ways to do this, but there you go :)
	 */
	private void initFields() {

		// Define attribute fields in which the search is to be carried on
		CyDataTable nodetable = (CyDataTable)network.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		Map<String,Class<?>> nodetypemap = nodetable.getColumnTypeMap();
		
		CyDataTable edgetable = (CyDataTable)network.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		Map<String,Class<?>> edgetypemap = edgetable.getColumnTypeMap();
		
		//CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		//CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		Object[] nodeAttrObjArray = nodetypemap.keySet().toArray();
		Object[] edgeAttrObjArray = edgetypemap.keySet().toArray();
		String[] nodeAttrNameArray = new String[nodeAttrObjArray.length];
		String[] edgeAttrNameArray = new String[edgeAttrObjArray.length];
		for(int i=0;i<nodeAttrObjArray.length;i++){
			nodeAttrNameArray[i] = nodeAttrObjArray[i].toString();
		}
		for(int i=0;i<edgeAttrObjArray.length;i++){
			edgeAttrNameArray[i] = edgeAttrObjArray[i].toString();
		}
		//String[] nodeAttrNameArray = nodeAttributes.getAttributeNames();
		//String[] edgeAttrNameArray = edgeAttributes.getAttributeNames();
		int numOfNodeAttributes = nodeAttrNameArray.length;
		int numOfEdgeAttributes = edgeAttrNameArray.length;

		// Node IDs and edge IDs are both indexed with INDEX_FIELD.
		// The last two methods didn't return INDEX_FIELD as one of the fields.
		// To allow search on ID field, we add it manually.
		fields = new String[numOfNodeAttributes + numOfEdgeAttributes + 1];
		fields[0] = INDEX_FIELD;

		// Now add node and edge attribute names to fields[]
		System.arraycopy(nodeAttrNameArray, 0, fields, 1, numOfNodeAttributes);
		System.arraycopy(edgeAttrNameArray, 0, fields, numOfNodeAttributes +1,
				numOfEdgeAttributes);

		// Define value types
		String[] nodeAttrValueTypes = new String[numOfNodeAttributes];
		for (int i = 0; i < numOfNodeAttributes; i++) {
			//nodeAttrValueTypes[i] = nodeAttributes
			//		.getType(nodeAttrNameArray[i]);
			nodeAttrValueTypes[i] = nodetypemap.get(nodeAttrNameArray[i]).getName();
		}

		String[] edgeAttrValueTypes = new String[numOfEdgeAttributes];
		for (int i = 0; i < numOfEdgeAttributes; i++) {
			//edgeAttrValueTypes[i] = edgeAttributes
				//	.getType(edgeAttrNameArray[i]);
			edgeAttrValueTypes[i] = edgetypemap.get(edgeAttrNameArray[i]).getName();
		}

		// ... And add type STRING for the INDEX_FIELD
		types = new String[numOfNodeAttributes + numOfEdgeAttributes + 1];
		types[0] = AttributeTypes.TYPE_STRING;
		
		// Now add node and edge attribute types to types[]
		System.arraycopy(nodeAttrValueTypes, 0, types, 1, numOfNodeAttributes);
		System.arraycopy(edgeAttrValueTypes, 0, types, numOfNodeAttributes + 1,
				numOfEdgeAttributes);

		// Handle whitespace characters and case in attribute names
		for (int i = 0; i < fields.length; i++) {
			fields[i] = EnhancedSearchUtils.replaceWhitespace(fields[i]);
			fields[i] = fields[i].toLowerCase();
		}

	}


	/**
	 * Get list of fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * Get the type of a given field
	 */
	public String getType(String attrName) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(attrName)) {
				return types[i];
			}
		}
		return AttributeTypes.TYPE_UNDEFINED;
	}

}
