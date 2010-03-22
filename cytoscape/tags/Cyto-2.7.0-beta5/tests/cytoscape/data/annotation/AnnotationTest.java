/*
  File: AnnotationTest.java

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

// AnnotationTest.java

// todo (pshannon, 19 jan 2003): test the new ctor (with receives no Ontology)
//                               both with and without an separately assigned ontology
package cytoscape.data.annotation;

import cytoscape.AllTests;

import cytoscape.data.annotation.*;

import junit.framework.*;

import java.io.*;


/**
 * test the Annotation class
 */
public class AnnotationTest extends TestCase {
	/**
	 * Creates a new AnnotationTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public AnnotationTest(String name) {
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

		Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology();
		String species = "Saccharomyces cerevisiae";
		String type = "pathways";
		Annotation annotation = new Annotation(species, type, ontology);

		assertTrue(annotation.size() == 0);
		assertTrue(annotation.count() == 0);
		assertTrue(annotation.getCurator().equals(ontology.getCurator()));
		assertTrue(annotation.getType().equals(type));
		assertTrue(annotation.getOntologyType().equals(ontology.getType()));
		assertTrue(annotation.getSpecies().equals(species));
	} // testCtor

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testAdd() throws Exception {
		AllTests.standardOut("testAdd");

		Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology();
		Annotation annotation = new Annotation("Halobacterium Sp.", "pathways", ontology);

		assertTrue(annotation.size() == 0);
		annotation.add("VNG0006G", 251);
		annotation.add("VNG0006G", 530);
		annotation.add("VNG0008G", 520);
		annotation.add("VNG0008G", 522);
		annotation.add("VNG0009G", 520);
		annotation.add("VNG0009G", 522);
		annotation.add("VNG0046G", 40);
		annotation.add("VNG0046G", 500);
		annotation.add("VNG0046G", 520);

		assertTrue(annotation.size() == 9);
		assertTrue(annotation.count() == 4);
	} // testAdd

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGet() throws Exception {
		AllTests.standardOut("testGet");

		Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology();
		Annotation annotation = new Annotation("Halobacterium Sp.", "pathways", ontology);

		annotation.add("VNG0006G", 251);
		annotation.add("VNG0006G", 530);
		annotation.add("VNG0008G", 520);
		annotation.add("VNG0008G", 522);
		annotation.add("VNG0009G", 520);
		annotation.add("VNG0046G", 40);
		annotation.add("VNG0046G", 500);
		annotation.add("VNG0046G", 520);

		String[] names = annotation.getNames();
		assertTrue(names.length == 4);

		int[] classifications = annotation.getClassifications("VNG0006G");
		assertTrue(classifications.length == 2);
		assertTrue(classifications[0] == 251);
		assertTrue(classifications[1] == 530);

		classifications = annotation.getClassifications("VNG0008G");
		assertTrue(classifications.length == 2);
		assertTrue(classifications[0] == 520);
		assertTrue(classifications[1] == 522);

		classifications = annotation.getClassifications("VNG0009G");
		assertTrue(classifications.length == 1);
		assertTrue(classifications[0] == 520);

		classifications = annotation.getClassifications("VNG0046G");
		assertTrue(classifications.length == 3);
		assertTrue(classifications[0] == 40);
		assertTrue(classifications[1] == 500);
		assertTrue(classifications[2] == 520);
	} // testGet

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testMaxDepth() throws Exception {
		AllTests.standardOut("testMaxDepth");

		Ontology ontology = Utils.createMinimalKeggMetabolicPathwayOntology();
		Annotation annotation = new Annotation("Halobacterium Sp.", "pathways", ontology);

		annotation.add("VNG0009G", 520);
		assertTrue(annotation.maxDepth() == 3);
	} // testMaxDepth

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(AnnotationTest.class));
	}
} // AnnotationTest
