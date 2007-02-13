
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

import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;

import cytoscape.util.GMLFileFilter;

import junit.framework.TestCase;

import java.io.File;


/**
 * Tests Implementation of CyFileFilter.
 *
 * TODO:  Add to DataSuite
 */
public class GMLFileFilterTest extends TestCase {
	GMLFileFilter test;
	File DUMMY_GML_FILE;
	GraphReader reader;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
		test = new GMLFileFilter();
		DUMMY_GML_FILE = File.createTempFile("inputGmlTest", ".gml");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
		test = null;
		DUMMY_GML_FILE.delete();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetReader() throws Exception {
		reader = test.getReader(DUMMY_GML_FILE.toString());
		assertEquals(reader.getClass(), GMLReader.class);
	}
}
