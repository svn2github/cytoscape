
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

package csplugins.test.widgets.test.unitTests.text;

import csplugins.quickfind.util.QuickFind;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.IndexFactory;
import csplugins.widgets.autocomplete.index.TextIndex;
import junit.framework.TestCase;


/**
 * Unit Tests for TextIndexImpl.
 *
 * @author Ethan Cerami.
 */
public class TestTextIndex extends TestCase {
	/**
	 * First Round of Test Cases.
	 */
	public void testTextIndex0() {
		TextIndex textIndex = IndexFactory.createDefaultTextIndex(QuickFind.INDEX_NODES);
		textIndex.addToIndex("rain", Integer.valueOf(1));
		textIndex.addToIndex("rain", Integer.valueOf(2));
		textIndex.addToIndex("rainbow", Integer.valueOf(3));
		textIndex.addToIndex("rainbow trout", Integer.valueOf(4));
		textIndex.addToIndex("RABBIT", Integer.valueOf(5));

		assertEquals(4, textIndex.getNumKeys());

		//  Test with prefix:  "ra"
		Hit[] hits = textIndex.getHits("ra", Integer.MAX_VALUE);
		assertEquals(4, hits.length);
		assertEquals("rabbit", hits[0].getKeyword());
		assertEquals("rain", hits[1].getKeyword());
		assertEquals("rainbow", hits[2].getKeyword());
		assertEquals("rainbow trout", hits[3].getKeyword());

		//  Test with prefix "rain"
		hits = textIndex.getHits("rain", Integer.MAX_VALUE);
		assertEquals(3, hits.length);
		assertEquals("rain", hits[0].getKeyword());
		assertEquals("rainbow", hits[1].getKeyword());
		assertEquals("rainbow trout", hits[2].getKeyword());

		//  Test with prefix "RAIN".  Verifies that search is
		// case *insensitive*.
		hits = textIndex.getHits("RAIN", Integer.MAX_VALUE);
		assertEquals(3, hits.length);
		assertEquals("rain", hits[0].getKeyword());
		assertEquals("rainbow", hits[1].getKeyword());
		assertEquals("rainbow trout", hits[2].getKeyword());

		//  Test Existence of Embedded Objects
		hits = textIndex.getHits("rain", Integer.MAX_VALUE);
		assertEquals(2, hits[0].getAssociatedObjects().length);
		assertEquals("1", hits[0].getAssociatedObjects()[0].toString());
		assertEquals("2", hits[0].getAssociatedObjects()[1].toString());

		hits = textIndex.getHits("rainbow", Integer.MAX_VALUE);
		assertEquals(1, hits[0].getAssociatedObjects().length);
		assertEquals("3", hits[0].getAssociatedObjects()[0].toString());

		//  Try getting hits for an undefined key
		hits = textIndex.getHits("cytoscape", Integer.MAX_VALUE);
		assertEquals(0, hits.length);

		//  Now, try resetting the index
		textIndex.resetIndex();

		//  Verify that keys are no longer available
		hits = textIndex.getHits("rain", Integer.MAX_VALUE);
		assertEquals(0, hits.length);

		//  Verify toString() makes sense
		assertEquals("Text Index:  [Total number of keys:  0]", textIndex.toString());
	}

	/**
	 * Tests wild card searches.
	 */
	public void testWildCardSearches() {
		TextIndex textIndex = IndexFactory.createDefaultTextIndex(QuickFind.INDEX_NODES);
		textIndex.addToIndex("rain", Integer.valueOf(1));
		textIndex.addToIndex("rain", Integer.valueOf(2));
		textIndex.addToIndex("rainbow", Integer.valueOf(3));
		textIndex.addToIndex("rainbow trout", Integer.valueOf(4));
		textIndex.addToIndex("RABBIT", Integer.valueOf(5));

		Hit[] hits = textIndex.getHits("ra*", Integer.MAX_VALUE);
		assertEquals(1, hits.length);
		assertEquals("ra*", hits[0].getKeyword());
		assertEquals(5, hits[0].getAssociatedObjects().length);
	}

	/**
	 * Test max key length.
	 */
	public void testMaxKeyLength() {
		TextIndex textIndex = IndexFactory.createDefaultTextIndex(QuickFind.INDEX_NODES);
		assertEquals(TextIndex.DEFAULT_MAX_KEY_LENGTH, textIndex.getMaxKeyLength());
		textIndex.addToIndex("The Associated Press and the New York Times "
		                     + "are now reporting that Atlantis will not launch Sunday.",
		                     Integer.valueOf(1));

		Hit[] hits = textIndex.getHits("the", Integer.MAX_VALUE);
		assertEquals(1, hits.length);
		assertEquals("the associated press and the new york times are now "
		             + "reporting that atlantis will not launch sunday.", hits[0].getKeyword());
		assertEquals(1, hits[0].getAssociatedObjects().length);
	}

	/**
	 * Test modified sort order.
	 * @throws Exception All Errors.
	 */
	public void testSortOrder() throws Exception {
		TextIndex textIndex = IndexFactory.createDefaultTextIndex(QuickFind.INDEX_NODES);
		textIndex.addToIndex("?", Integer.valueOf(1));
		textIndex.addToIndex("1rain", Integer.valueOf(2));
		textIndex.addToIndex("rainbow", Integer.valueOf(3));
		textIndex.addToIndex("rainbow trout", Integer.valueOf(4));
		textIndex.addToIndex("RABBIT", Integer.valueOf(5));

		//  Verify that strings starting beginning with letters appear
		//  at beginning of list.
		Hit[] hits = textIndex.getHits("", Integer.MAX_VALUE);
		assertEquals("rabbit", hits[0].getKeyword());
		assertEquals("rainbow", hits[1].getKeyword());
		assertEquals("rainbow trout", hits[2].getKeyword());
		assertEquals("1rain", hits[3].getKeyword());
		assertEquals("?", hits[4].getKeyword());
	}
}
