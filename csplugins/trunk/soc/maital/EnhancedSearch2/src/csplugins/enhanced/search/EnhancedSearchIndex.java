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

package csplugins.enhanced.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;

import csplugins.enhanced.search.util.EnhancedSearchUtils;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class EnhancedSearchIndex {

	public static final String INDEX_FIELD = "id";
	public static final int MAX_FIELD_LENGTH = 50000;
    private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

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
			IndexWriter writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

			// Set the number of terms to be indexed for a field.
			// writer.setMaxFieldLength(MAX_FIELD_LENGTH);

			// Fetch nodes and edges attributes
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

			// Define network attributes iterator
			Iterator it = null;

			// Index node attributes
			it = network.nodesIterator();
			while (it.hasNext()) {
				CyNode currNode = (CyNode) it.next();
				String currNodeIdentifier = currNode.getIdentifier();
				writer.addDocument(createDocument(currNodeIdentifier,
						nodeAttributes));
			}

			// Index edge attributes
			it = network.edgesIterator();
			while (it.hasNext()) {
				CyEdge currEdge = (CyEdge) it.next();
				String currEdgeIdentifier = currEdge.getIdentifier();
				writer.addDocument(createDocument(currEdgeIdentifier,
						edgeAttributes));
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
	private static Document createDocument(String identifier,
			CyAttributes attributes) {

		Document doc = new Document();
		doc.add(new Field(INDEX_FIELD, identifier, Field.Store.YES,
				Field.Index.ANALYZED));

		String[] attrNameArray = attributes.getAttributeNames();
		for (int i = 0; i < attrNameArray.length; i++) {
			String attrName = attrNameArray[i];

			boolean hasAttribute = attributes.hasAttribute(identifier, attrName);
			if (hasAttribute) {

				// Handle whitespace characters and case in attribute names
				String attrIndexingName = EnhancedSearchUtils
						.replaceWhitespace(attrName);
				attrIndexingName = attrIndexingName.toLowerCase();
				
				byte valueType = attributes.getType(attrName);

				if (valueType == CyAttributes.TYPE_BOOLEAN) {
					String attrValue = attributes.getBooleanAttribute(identifier, attrName).toString();
					doc.add(new Field(attrIndexingName, attrValue,
							Field.Store.NO, Field.Index.NOT_ANALYZED));

				} else if (valueType == CyAttributes.TYPE_INTEGER) {
					Integer attrValue = attributes.getIntegerAttribute(identifier, attrName);
					doc.add(new NumericField(attrIndexingName).setIntValue(attrValue));
				
				} else if (valueType == CyAttributes.TYPE_FLOATING) {
					Double attrValue = attributes.getDoubleAttribute(identifier, attrName);
					doc.add(new NumericField(attrIndexingName).setDoubleValue(attrValue));
				
				} else if (valueType == CyAttributes.TYPE_STRING) {
					String attrValue = attributes.getStringAttribute(
							identifier, attrName);
					doc.add(new Field(attrIndexingName, attrValue,
							Field.Store.NO, Field.Index.ANALYZED));

				// Attributes of type TYPE_SIMPLE_LIST may have several values.
				// Create a document for each value.
				} else if (valueType == CyAttributes.TYPE_SIMPLE_LIST) {
					List list = attributes.getListAttribute(identifier, attrName);

					//  Iterate through all elements in the list
					if ((list != null) && (list.size() > 0)) {
						for (int index = 0; index < list.size(); index++) {
							Object o = list.get(index);
							String attrValue = o.toString();

							doc.add(new Field(attrIndexingName, attrValue,
									Field.Store.NO, Field.Index.ANALYZED));
						}
					}

				// Attributes of type TYPE_SIMPLE_MAP may have several values.
				// Create a document for each value.
				} else if (valueType == CyAttributes.TYPE_SIMPLE_MAP) {
					Map map = attributes.getMapAttribute(identifier, attrName);

					//  Iterate through all values in the map
					if ((map != null) && (map.size() > 0)) {
						Iterator mapIterator = map.values().iterator();
						while (mapIterator.hasNext()) {
							Object o = mapIterator.next();
							String attrValue = o.toString();

							doc.add(new Field(attrIndexingName, attrValue,
									Field.Store.NO, Field.Index.ANALYZED));
						}
					}

				} else if (valueType == CyAttributes.TYPE_COMPLEX) {
					// Do not index this field
				}
			}

		}

		return doc;
	}

	public RAMDirectory getIndex() {
		return idx;
	}

}
