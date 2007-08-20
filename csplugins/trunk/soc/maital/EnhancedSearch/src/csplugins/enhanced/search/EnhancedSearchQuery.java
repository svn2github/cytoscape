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
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import csplugins.enhanced.search.util.EnhancedSearchUtils;

public class EnhancedSearchQuery {

	private IdentifiersCollector hitCollector = null;

	private RAMDirectory idx;

	private Searcher searcher;

	public EnhancedSearchQuery(RAMDirectory index) {
		idx = index;
	}

	public IdentifiersCollector ExecuteQuery(String queryString) {
		try {

			// Define attribute fields in which the search is to be carried on
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String[] attrNameArray = nodeAttributes.getAttributeNames();

			// Handle whitespace characters in attribute names
			for (int i = 1; i < attrNameArray.length; i++) {
				attrNameArray[i] = EnhancedSearchUtils
						.replaceWhitespace(attrNameArray[i]);
			}

			// Build an IndexSearcher using the in-memory index
			searcher = new IndexSearcher(idx);
			search(searcher, queryString, attrNameArray);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return hitCollector;

	}

	/**
	 * Searches for the given query string. By default (without specifying
	 * attributeName), search is carried out on all attribute fields.
	 */
	private void search(Searcher searcher, String queryString, String[] fields)
			throws IOException {

		// Build a Query object
		QueryParser queryParser = new MultiFieldQueryParser(fields,
				new StandardAnalyzer());
		try {
			// Execute query
			Query query = queryParser.parse(queryString);
			hitCollector = new IdentifiersCollector(searcher);
			searcher.search(query, hitCollector);
		} catch (ParseException pe) {
			// Parse exceptions occure when colon appear in the query in an
			// unexpected location, e.g. when attribute or value are
			// missing in the query. In such case, the returned hits
			// variable will be null.
			System.out.println("Invalid query '" + queryString + "'");
			String message = pe.getMessage();
			System.out.println(message);
		}
	}

	public void close() {
		try {
			searcher.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public class IdentifiersCollector extends HitCollector {

		public static final String INDEX_FIELD = "Identifier";

		private Searcher searcher;

		public ArrayList<String> hitsIdentifiers = new ArrayList<String>();

		public IdentifiersCollector(Searcher searcher) {
			this.searcher = searcher;
		}

		public void collect(int id, float score) {
			try {
				Document doc = searcher.doc(id);
				String currID = doc.get(INDEX_FIELD);
				hitsIdentifiers.add(currID);
			} catch (IOException e) {
				// ignored
			}
		}

	}

}
