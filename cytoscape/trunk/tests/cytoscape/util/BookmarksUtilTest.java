
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

package cytoscape.util;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;
import cytoscape.bookmarks.DataSource;

import cytoscape.data.readers.BookmarkReader;

import cytoscape.util.BookmarksUtil;

import junit.framework.TestCase;

import java.util.List;


/**
 *
 */
public class BookmarksUtilTest extends TestCase {
	private Bookmarks bk;

	protected void setUp() throws Exception {
		super.setUp();

		BookmarkReader reader = new BookmarkReader();
		reader.readBookmarks();

		bk = reader.getBookmarks();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetCategory() {
		List<Category> rootCat = bk.getCategory();
		assertNotNull(rootCat);
		System.out.println("#### Number of bookmarks in the root category = " + rootCat.size());

		Category cat1 = BookmarksUtil.getCategory("annotation", rootCat);
		assertNotNull(cat1);
		assertEquals("annotation", cat1.getName());
		System.out.println("===============================================");

		Category cat2 = BookmarksUtil.getCategory("network", rootCat);
		assertNotNull(cat2);
		assertEquals("network", cat2.getName());
		System.out.println("===============================================");

		Category cat4 = BookmarksUtil.getCategory("aaa", rootCat);
		assertNull(cat4);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetDataSourceList() {
		List<Category> rootCat = bk.getCategory();
		List<DataSource> sources = BookmarksUtil.getDataSourceList("ontology", bk.getCategory());

		assertNotNull(sources);
		assertEquals(10, sources.size());

		List<DataSource> sources2 = BookmarksUtil.getDataSourceList("annotation", bk.getCategory());
		assertNotNull(sources2);
		assertEquals(33, sources2.size());

		List<DataSource> sources3 = BookmarksUtil.getDataSourceList("network", bk.getCategory());
		assertNotNull(sources3);
		assertEquals(6, sources3.size());
	}
}
