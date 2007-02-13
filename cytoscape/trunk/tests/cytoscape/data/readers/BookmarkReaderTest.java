
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

package cytoscape.data.readers;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;

import cytoscape.data.readers.BookmarkReader;

import junit.framework.TestCase;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;


/**
 * Unit test fopr the bookmark reader.
 *
 * @author kono
 *
 */
public class BookmarkReaderTest extends TestCase {
	private BookmarkReader reader;

	protected void setUp() throws Exception {
		super.setUp();
		reader = new BookmarkReader();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		reader = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testReadBookmarks() {
		try {
			reader.readBookmarks();

			Bookmarks bkm = reader.getBookmarks();
			List cat = bkm.getCategory();
			List<String> nameList = new ArrayList<String>();

			for (Object item : cat) {
				if (item.getClass() == Category.class) {
					nameList.add(((Category) item).getName());
				}
			}

			assertTrue(nameList.contains("ontology"));
			assertTrue(nameList.contains("network"));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
