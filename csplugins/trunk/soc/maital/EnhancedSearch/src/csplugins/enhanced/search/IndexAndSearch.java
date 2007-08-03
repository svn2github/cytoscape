
/*
 This mosule was seperated into EnhancedSearchIndex, EnhancedSearchQuery and enhancedSearchUtils.
 It will be removed when testing is completed.
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

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import csplugins.quickfind.util.CyAttributesUtil;
import java.util.regex.*;

public class IndexAndSearch {

	public static final String SEARCH_STRING = "\\s";

	public static final String REPLACE_STRING = "_";

	public static final String INDEX_FIELD = "Identifier";

	RAMDirectory idx;

	public IndexAndSearch(String queryString) {
		// Construct a RAMDirectory to hold the in-memory representation of the
		// index.
		idx = new RAMDirectory();
		BuildIndex(idx);
		PerformSearch(idx, queryString);
	}

	public void BuildIndex(RAMDirectory idx) {
		try {
			// Make a writer to create the index
			IndexWriter writer = new IndexWriter(idx, new StandardAnalyzer(),
					true);

			// Fetch nodes and edges attributes
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

			// Define network attributes iterator
			Iterator it = null;
			final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();

			// Index node attributes
			it = currNetwork.nodesIterator();
			while (it.hasNext()) {
				CyNode currNode = (CyNode) it.next();
				String currNodeIdentifier = currNode.getIdentifier();
				writer.addDocument(createDocument(currNodeIdentifier,
						nodeAttributes));
			}

			// Index edge attributes
			it = currNetwork.edgesIterator();
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

	public void PerformSearch(RAMDirectory idx, String queryString) {
		try {

			// Define attribute fields in which the search is to be carried on
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String[] attrNameArray = nodeAttributes.getAttributeNames();

			// Handle whitespace characters in attribute names
			for (int i = 1; i < attrNameArray.length; i++) {
				attrNameArray[i] = replaceWhitespace(attrNameArray[i]);
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

				attrName = replaceWhitespace(attrName);

				for (int j = 0; j < valueList.length; j++) {
					String attrValue = valueList[j];

					// Add Document objects
					doc.add(new Field(attrName, new StringReader(attrValue)));
				}
			}

		}

		return doc;
	}

	/**
	 * Searches for the given query string. By default (without specifying
	 * attributeName), search is carried out on all attribute fields.
	 */
	private static void search(Searcher searcher, String queryString,
			String[] fields) throws ParseException, IOException {

		// Build a Query object
		QueryParser queryParser = new MultiFieldQueryParser(fields,
				new StandardAnalyzer());
		Query query = queryParser.parse(queryString);

		// Search for the query
		Hits hits = searcher.search(query);

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

	/**
	 * Replaces whitespace characters with underline
	 */
	private static String replaceWhitespace(String searchTerm) {
		String replaceTerm = "";

		Pattern searchPattern = Pattern.compile(SEARCH_STRING);
		String[] result = searchPattern.split(searchTerm);
		replaceTerm = result[0];
		for (int j = 1; j < result.length; j++) {
			replaceTerm = replaceTerm + REPLACE_STRING + result[j];
		}

		return replaceTerm;
	}

}
