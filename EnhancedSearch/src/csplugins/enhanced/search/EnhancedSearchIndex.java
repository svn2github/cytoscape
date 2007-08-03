
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

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import csplugins.quickfind.util.CyAttributesUtil;
import csplugins.enhanced.search.util.EnhancedSearchUtils;

public class EnhancedSearchIndex {

	public static final String INDEX_FIELD = "Identifier";

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
			IndexWriter writer = new IndexWriter(idx, new StandardAnalyzer(),
					true);

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
				Field.Index.NO));

		String[] attrNameArray = attributes.getAttributeNames();
		for (int i = 0; i < attrNameArray.length; i++) {
			String attrName = attrNameArray[i];
			String[] valueList = CyAttributesUtil.getAttributeValues(
					attributes, identifier, attrName);

			// Some attributes don't have values at all; Some attributes have
			// several values.
			// Create a document for each value.
			if (valueList != null) {

				attrName = EnhancedSearchUtils.replaceWhitespace(attrName);

				for (int j = 0; j < valueList.length; j++) {
					String attrValue = valueList[j];

					// Add Document objects
					doc.add(new Field(attrName, new StringReader(attrValue)));
				}
			}

		}

		return doc;
	}

	public RAMDirectory getIndex() {
		return idx;
	}

}
