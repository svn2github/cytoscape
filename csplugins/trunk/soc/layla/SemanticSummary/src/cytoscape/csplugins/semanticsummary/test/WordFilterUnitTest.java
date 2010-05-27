/*
 File: WordFilterUnitTest.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.csplugins.semanticsummary.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cytoscape.csplugins.semanticsummary.WordFilter;

/**
 * This class tests the functionality of the WordFilter class.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class WordFilterUnitTest {
	
	WordFilter curFilter = new WordFilter();

	@Test
	public void testContains() {
		assertTrue(curFilter.contains("a")); // First in Stop list
		assertTrue(curFilter.contains("zero")); //Last in Stop list
		assertTrue(curFilter.contains("kegg")); //First in Flagged list
		assertTrue(curFilter.contains("nci")); //Last in Flagged
		assertTrue(curFilter.contains("react"));//Other
		assertFalse(curFilter.contains("layla"));//not in list
		assertFalse(curFilter.contains("A"));//capitalization
	}

	@Test
	public void testAdd() {
		assertFalse(curFilter.contains("layla"));
		curFilter.add("layla");
		assertTrue(curFilter.contains("layla"));
	}

	@Test
	public void testRemove() {
		assertTrue(curFilter.contains("a"));
		curFilter.remove("a");
		assertFalse(curFilter.contains("a"));
		curFilter.remove("not_Contained");
		assertFalse(curFilter.contains("not_contained"));
	}

}
