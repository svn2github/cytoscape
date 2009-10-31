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

package org.cytoscape.enhanced_search.internal;

import java.io.IOException;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyDataTable; 

import org.cytoscape.enhanced_search.internal.util.EnhancedSearchUtils;
import org.cytoscape.enhanced_search.internal.util.NumberUtils;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.NumberTools;
import java.util.Set;

import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyRow;
import java.util.List;
import java.util.Map;

public class EnhancedSearchIndex {

	public static final int MAX_FIELD_LENGTH = 50000;

	RAMDirectory idx;

	// Index the given network
	public EnhancedSearchIndex(CyNetwork network) {
		// Construct a RAMDirectory to hold the in-memory representation of the
		// index.
		idx = new RAMDirectory();
		BuildIndex(idx, network);
	}

	private void BuildIndex(RAMDirectory idx, CyNetwork network) {
		try {
			// Make a writer to create the index
			IndexWriter writer = new IndexWriter(idx, new StandardAnalyzer(), true);

			// Set the number of terms to be indexed for a field.
			// writer.setMaxFieldLength(MAX_FIELD_LENGTH);

			// Add a document for each graph object - node and edge
			for (CyNode cyNode : network.getNodeList()) {
				writer.addDocument(createDocument(cyNode, EnhancedSearch.NODE_TYPE, cyNode.getIndex()));
			}
			for (CyEdge cyEdge : network.getEdgeList()) {
				writer.addDocument(createDocument(cyEdge, EnhancedSearch.EDGE_TYPE, cyEdge.getIndex()));
			}

			// Optimize and close the writer to finish building the index
			writer.optimize();
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Make a Document object with an un-indexed identifier field and indexed
	 * attribute fields
	 */
	private static Document createDocument(GraphObject graphObject, String graphObjectType, int index) {

		Document doc = new Document();
		String identifier = Integer.toString(index);
		
		doc.add(new Field(EnhancedSearch.INDEX_FIELD, identifier, Field.Store.YES,
				Field.Index.TOKENIZED));
		doc.add(new Field(EnhancedSearch.TYPE_FIELD, graphObjectType, Field.Store.YES,
				Field.Index.TOKENIZED));
		
		CyRow cyRow = graphObject.attrs();
		CyDataTable cyDataTable = cyRow.getDataTable();
		Map<String,Class<?>> columnTypeMap = cyDataTable.getColumnTypeMap();
		Set<String> attributeNames = columnTypeMap.keySet();

		for (String attrName : attributeNames) {

			// Handle whitespace characters and case in attribute names
			String attrIndexingName = EnhancedSearchUtils.replaceWhitespace(attrName);
			attrIndexingName = attrIndexingName.toLowerCase();

			// Determine type
			Class<?> valueType =columnTypeMap.get(attrName);
			
			if (valueType == String.class) {
				String attrValue = graphObject.attrs().get(attrName, String.class);
				doc.add(new Field(attrIndexingName, attrValue, Field.Store.NO, Field.Index.TOKENIZED));
			} else if (valueType == Integer.class) {
				String attrValue = NumberTools.longToString(graphObject.attrs().get(attrName, Integer.class));
				doc.add(new Field(attrIndexingName, attrValue, Field.Store.NO, Field.Index.TOKENIZED));
			} else if (valueType == Double.class) {
				String attrValue = NumberUtils.double2sortableStr(graphObject.attrs().get(attrName, Double.class));
				doc.add(new Field(attrIndexingName, attrValue, Field.Store.NO, Field.Index.TOKENIZED));
			} else if (valueType == Boolean.class) {
				String attrValue = graphObject.attrs().get(attrName, Boolean.class).toString();
				doc.add(new Field(attrIndexingName, attrValue, Field.Store.NO, Field.Index.TOKENIZED));
			} else if (valueType == List.class) {
				List attrValueList = graphObject.attrs().get(attrName, List.class);
				for (int j = 0; j < attrValueList.size(); j++) {
					String attrValue = attrValueList.get(j).toString();
					doc.add(new Field(attrIndexingName, attrValue, Field.Store.NO, Field.Index.TOKENIZED));
				}
			}

		}

		return doc;
	}

	public RAMDirectory getIndex() {
		return idx;
	}

}
