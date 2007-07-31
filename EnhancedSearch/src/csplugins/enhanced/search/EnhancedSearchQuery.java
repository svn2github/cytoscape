
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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.RAMDirectory;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import csplugins.enhanced.search.util.EnhancedSearchUtils;

public class EnhancedSearchQuery {

	public static final String INDEX_FIELD = "Identifier";

	private Hits hits;

	public EnhancedSearchQuery() {
	}

	public void ExecuteQuery(RAMDirectory idx, String queryString) {
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
			Searcher searcher = new IndexSearcher(idx);
			search(searcher, queryString, attrNameArray);

			searcher.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
	}

	/**
	 * Searches for the given query string. By default (without specifying
	 * attributeName), search is carried out on all attribute fields.
	 */
	private void search(Searcher searcher, String queryString, String[] fields)
			throws ParseException, IOException {

		// Build a Query object
		QueryParser queryParser = new MultiFieldQueryParser(fields,
				new StandardAnalyzer());
		Query query = queryParser.parse(queryString);

		// Search for the query
		hits = null;
		hits = searcher.search(query);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();
		if (hitCount == 0) {
			System.out.println("No matches were found for \"" + queryString
					+ "\"");
		} else {
			System.out.println("Hits for \"" + queryString
					+ "\" were found in:");
			// Iterate over the Documents in the Hits object
			for (int i = 0; i < hitCount; i++) {
				Document doc = hits.doc(i);
				// Print the value that we stored in the INDEX_FIELD field.
				// Note that this Field was not indexed, but (unlike other
				// attribute fields)
				// was stored verbatim and can be retrieved.
				System.out
						.println("  " + (i + 1) + ". " + doc.get(INDEX_FIELD));
			}
		}
	}

	public Hits getHits() {
		return hits;
	}

}
