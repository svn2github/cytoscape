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

package org.cytoscape.search.internal;

import java.io.IOException;
import java.util.*;
import java.lang.String; //import cytoscape.Cytoscape;
import org.cytoscape.model.*; //import org.cytoscape.attributes.CyAttributes;

//import org.cytoscape.search.util.CyAttributesUtil;
import org.cytoscape.search.util.EnhancedSearchUtils;
import org.cytoscape.search.util.NumberUtils;
import org.cytoscape.search.EnhancedSearchIndex;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory; ///import org.apache.lucene.store.instantiated.InstantiatedIndex;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.NumberTools;

public class EnhancedSearchIndexImpl extends EnhancedSearchIndex {

	RAMDirectory idx;

	// Index the given network
	public EnhancedSearchIndexImpl(CyNetwork network) {
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

			// Set the number of terms to be indexed for a field.
			// writer.setMaxFieldLength(MAX_FIELD_LENGTH);

			// Fetch nodes and edges attributes
			// CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			// CyDataTable nodetable =
			// (CyDataTable)network.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
			CyDataTable nodetable = (CyDataTable) network.getCyDataTables(
					"NODE").get(CyNetwork.DEFAULT_ATTRS);
			List<CyNode> nl = network.getNodeList();

			// List<String> nodes =
			// nodetable.getColumnValues(CyDataTable.PRIMARY_KEY , String.class
			// );
			Iterator<CyNode> nodeit = nl.iterator();
			// Define network attributes iterator
			// Iterator it = null;
			Map<String, Class<?>> nodetypemap = nodetable.getColumnTypeMap();
			// Index node attributes
			// it = network.nodesIterator();
			while (nodeit.hasNext()) {
				CyNode n1 = nodeit.next();
				long currid = n1.getSUID();
				CyRow currow = nodetable.getRow(currid);

				// String currNodeIdentifier = currNode.getIdentifier();
				// writer.addDocument(createDocument(new
				// Long(currid).toString(),
				// currow.getAllValues(),nodetypemap));
				writer
						.addDocument(createDocument(new Integer(n1.getIndex())
								.toString(), currow.getAllValues(),
								nodetypemap, "node"));
				// System.out.println();
			}

			// Index edge attributes
			// CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			// CyDataTable edgetable =
			// (CyDataTable)network.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
			CyDataTable edgetable = (CyDataTable) network.getCyDataTables(
					"EDGE").get(CyNetwork.DEFAULT_ATTRS);
			List<CyEdge> el = network.getEdgeList();
			Iterator<CyEdge> edgeit = el.iterator();
			Map<String, Class<?>> edgetypemap = edgetable.getColumnTypeMap();

			while (edgeit.hasNext()) {
				CyEdge e1 = edgeit.next();
				long currid = e1.getSUID();
				CyRow currow = edgetable.getRow(currid);

				// String currNodeIdentifier = currNode.getIdentifier();
				// writer.addDocument(createDocument(new
				// Long(currid).toString(),
				// currow.getAllValues(),edgetypemap));
				writer
						.addDocument(createDocument(new Integer(e1.getIndex())
								.toString(), currow.getAllValues(),
								edgetypemap, "edge"));
				// System.out.println();
			}
			// it = network.edgesIterator();
			/*
			 * while (it.hasNext()) { CyEdge currEdge = (CyEdge) it.next();
			 * String currEdgeIdentifier = currEdge.getIdentifier();
			 * writer.addDocument(createDocument(currEdgeIdentifier,
			 * edgeAttributes)); }
			 */
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
			Map<String, Object> map, Map<String, Class<?>> typemap,
			String indextype) {
		Document doc = new Document();
		doc.add(new Field(INDEX_FIELD, identifier, Field.Store.YES,
				Field.Index.TOKENIZED));
		doc.add(new Field("doctype", indextype, Field.Store.YES,
				Field.Index.UN_TOKENIZED));

		Set<Map.Entry<String, Object>> keys = map.entrySet();
		Iterator<Map.Entry<String, Object>> it = keys.iterator();

		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			String attrName = me.getKey();
			String attrIndexingName = EnhancedSearchUtils
					.replaceWhitespace(attrName);
			attrIndexingName = attrIndexingName.toLowerCase();
			// System.out.println("Attribute Indexing Name:"+attrIndexingName+";");
			// System.out.println(identifier + ":" + attrIndexingName + ":" +
			// me.getValue().toString());
			if (me.getValue() != null) {
				String cname = typemap.get(attrName).getName();
				if (cname.equals("java.lang.Boolean")) {
					doc.add(new Field(attrIndexingName, me.getValue()
							.toString(), Field.Store.NO,
							Field.Index.UN_TOKENIZED));
				} else if (cname.equals("java.lang.Integer")) {
					String attrValue = NumberTools.longToString((Integer) me
							.getValue());
					doc.add(new Field(attrIndexingName, attrValue,
							Field.Store.NO, Field.Index.UN_TOKENIZED));
				} else if (cname.equals("java.lang.Double")) {
					String attrValue = NumberUtils
							.double2sortableStr((Double) me.getValue());
					doc.add(new Field(attrIndexingName, attrValue,
							Field.Store.NO, Field.Index.UN_TOKENIZED));
				} else if (cname.equals("java.lang.String")) {
					String attrValue = (String) me.getValue();
					doc.add(new Field(attrIndexingName, attrValue,
							Field.Store.NO, Field.Index.TOKENIZED));
				} else if (cname.equals("java.util.List")) {
					List<?> l = (List<?>) me.getValue();
					for (int i = 0; i < l.size(); i++) {
						Object o = l.get(i);
						doc.add(new Field(attrIndexingName, o.toString(),
								Field.Store.NO, Field.Index.TOKENIZED));
					}
				} else if (cname.equals("java.util.Map")) {
					Iterator<?> mit = ((Map<?, ?>) me.getValue()).values()
							.iterator();
					while (mit.hasNext()) {
						doc.add(new Field(attrIndexingName, mit.next()
								.toString(), Field.Store.NO,
								Field.Index.TOKENIZED));
					}
				}
			}
		}
		return doc;
	}

	public RAMDirectory getIndex() {
		return idx;
	}

}
