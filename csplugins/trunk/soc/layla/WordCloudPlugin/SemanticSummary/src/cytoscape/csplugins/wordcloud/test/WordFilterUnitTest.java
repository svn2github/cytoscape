/*
 File: WordFilterUnitTest.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.wordcloud.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import cytoscape.csplugins.wordcloud.WordFilter;

/**
 * This class tests the functionality of the WordFilter class.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class WordFilterUnitTest extends TestCase {
	
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
