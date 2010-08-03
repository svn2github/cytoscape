/*
  File: AnnotationDescriptionTest.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

// AnnotationDescriptionTest
package cytoscape.data.annotation;

import cytoscape.AllTests;

import cytoscape.data.annotation.*;

import junit.framework.*;

import java.io.*;


/**
 *  test the AnnotationDescription class, especially with regard to
 *  the 'equals' and 'hashCode' member functions
 */
public class AnnotationDescriptionTest extends TestCase {
	/**
	 * Creates a new AnnotationDescriptionTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public AnnotationDescriptionTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * make sure that the ctor properly initializes all relevant data structures
	 * as seen through the standard getter methods
	 */
	public void testCtor() throws Exception {
		AllTests.standardOut("testCtor");

		String species = "Halobacterium sp.";
		String curator = "KEGG";
		String type = "Metabolic Pathway";

		AnnotationDescription desc = new AnnotationDescription(species, curator, type);

		assertTrue(desc.getSpecies().equals(species));
		assertTrue(desc.getCurator().equals(curator));
		assertTrue(desc.getType().equals(type));
	} // testCtor

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testEquals() throws Exception {
		AllTests.standardOut("testEquals");

		String species = "Halobacterium sp.";
		String curator = "KEGG";
		String type = "Metabolic Pathway";

		AnnotationDescription desc0 = new AnnotationDescription(species, curator, type);

		AnnotationDescription desc1 = new AnnotationDescription(species, curator, type);

		AnnotationDescription desc2 = new AnnotationDescription("home sapiens", curator, type);

		assertTrue(desc0.equals(desc1));
		assertTrue(!desc0.equals(desc2));
		assertTrue(!desc0.equals(new Integer(99)));
	} // testEquals

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(AnnotationDescriptionTest.class));
	}
} // AnnotationDescriptionTest
