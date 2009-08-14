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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;

/**
 * This custom MultiFieldQueryParser is used to parse queries containing
 * numerical values. Lucene treats all attribute field values as strings. During
 * indexing, numerical values were transformed into structured strings
 * preserving their numerical sorting order. Now, numerical values in query
 * should also be transformed so they can be properly compared to the values
 * stored in the index.
 */
public class CustomMultiFieldQueryParser extends MultiFieldQueryParser {

	private AttributeFields attrFields;

	public CustomMultiFieldQueryParser(AttributeFields attrFields,
			Analyzer analyzer) {
		super(attrFields.getFields(), analyzer);
		this.attrFields = attrFields;
	}

	protected Query getFieldQuery(String field, String queryText)
			throws ParseException {
		// System.out.println("I am in field Query :" + field+ ":"+ queryText );
		/*
		 * if (field == null) { Vector<BooleanClause> clauses = new
		 * Vector<BooleanClause>(); for (int i = 0; i < fields.length; i++) {
		 * Query q = getFieldQuery(fields[i], queryText); if (q != null) { // If
		 * the user passes a map of boosts if (boosts != null) { // Get the
		 * boost from the map and apply them Float boost = (Float)
		 * boosts.get(fields[i]); if (boost != null) {
		 * q.setBoost(boost.floatValue()); } } applySlop(q, slop);
		 * clauses.add(new BooleanClause(q,BooleanClause.Occur.SHOULD)); } } if
		 * (clauses.size() == 0) // happens for stopwords return null; return
		 * getBooleanQuery(clauses, true); }
		 */
		if (attrFields.getType(field) == AttributeTypes.TYPE_INTEGER) {
			try {
				int num1 = Integer.parseInt(queryText);
				return super.getFieldQuery(field, NumberTools
						.longToString(num1), 0);
			} catch (NumberFormatException e) {
				// Do nothing. When using a MultiFieldQueryParser, queryText is
				// searched in each one of the fields. This exception occurs
				// when trying to convert non-numeric queryText into numeric.
				// throw new ParseException(e.getMessage());
			}

		} else if (attrFields.getType(field) == AttributeTypes.TYPE_DOUBLE) {
			try {
				double num1 = Double.parseDouble(queryText);
				// Workaround: The commented statement below won't return the
				// desired
				// search result, but inclusive range query does.
				// return super.getFieldQuery(field, NumberUtils
				// .double2sortableStr(num1));
				return new RangeQuery(new Term(field, NumberUtils
						.double2sortableStr(num1)), new Term(field, NumberUtils
						.double2sortableStr(num1)), true);
			} catch (NumberFormatException e) {
				// Do nothing. When using a MultiFieldQueryParser, queryText is
				// searched in each one of the fields. This exception occurs
				// when trying to format String to numerical.
				// throw new ParseException(e.getMessage());
			}
		}

		return super.getFieldQuery(field, queryText);
	}

	protected Query getRangeQuery(String field, String part1, String part2,
			boolean inclusive) throws ParseException {

		// a workaround to avoid a TooManyClauses exception.
		// Temporary until RangeFilter is implemented.
		BooleanQuery.setMaxClauseCount(5120); // 5 * 1024

		if (attrFields.getType(field) == AttributeTypes.TYPE_INTEGER) {
			try {
				int num1 = Integer.parseInt(part1);
				int num2 = Integer.parseInt(part2);
				return new RangeQuery(new Term(field, NumberTools
						.longToString(num1)), new Term(field, NumberTools
						.longToString(num2)), inclusive);
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage());
			}
		}
		if (attrFields.getType(field) == AttributeTypes.TYPE_DOUBLE) {
			try {
				double num1 = Double.parseDouble(part1);
				double num2 = Double.parseDouble(part2);
				return new RangeQuery(new Term(field, NumberUtils
						.double2sortableStr(num1)), new Term(field, NumberUtils
						.double2sortableStr(num2)), inclusive);
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage());
			}
		}
		return super.getRangeQuery(field, part1, part2, inclusive);
	}
/*
	private void applySlop(Query q, int slop) {
		if (q instanceof PhraseQuery) {
			((PhraseQuery) q).setSlop(slop);
		} else if (q instanceof MultiPhraseQuery) {
			((MultiPhraseQuery) q).setSlop(slop);
		}
	}
*/
}
