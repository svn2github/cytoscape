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
package cytoscape.data.synonyms;

import cytoscape.Cytoscape;

import cytoscape.data.synonyms.AliasType;
import cytoscape.data.synonyms.Aliases;

import giny.model.Node;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *
 */
public class AliasesTest extends TestCase {
	Aliases al;
	List<String> sampleData;

	protected void setUp() throws Exception {
		super.setUp();

		sampleData = new ArrayList<String>();
		sampleData.add("alias1");
		sampleData.add("alias2");
		sampleData.add("alias3");

		al = new Aliases(AliasType.NODE);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		al = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAdd() {
		Iterator it = Cytoscape.getRootGraph().nodesIterator();

		while (it.hasNext()) {
			Node node = (Node) it.next();
			String name = node.getIdentifier();
			al.add(name, sampleData);
			al.add(name, "testAlias");
			assertTrue(al.getAliases(name).contains("alias2"));

			assertEquals(4, al.getAliases(name).size());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testRemove() {
		Iterator it = Cytoscape.getRootGraph().nodesIterator();

		while (it.hasNext()) {
			Node node = (Node) it.next();
			String name = node.getIdentifier();
			al.add(name, sampleData);
			assertEquals(3, al.getAliases(name).size());
			al.remove(name, "alias3");

			assertEquals(2, al.getAliases(name).size());
			assertTrue(al.getAliases(name).contains("alias2"));
			assertFalse(al.getAliases(name).contains("alias3"));
		}
	}
}
