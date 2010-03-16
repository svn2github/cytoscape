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
package cytoscape.data.ontology;

import cytoscape.Cytoscape;

import cytoscape.data.ontology.Ontology;
import cytoscape.data.ontology.OntologyTerm;

import junit.framework.TestCase;

import org.biojava.ontology.AlreadyExistsException;
import org.biojava.ontology.Term;
import org.biojava.ontology.Triple;

import org.biojava.utils.ChangeVetoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 *
 */
public class OntologyTest extends TestCase {
	private Ontology onto;
	private OntologyTerm newTerm1;
	private OntologyTerm newTerm2;

	protected void setUp() throws Exception {
		super.setUp();
		onto = new Ontology("Sample Ontology", "GO", "Fake ontology", null);

		Cytoscape.buildOntologyServer();
		Cytoscape.getOntologyServer().addOntology(onto);

		newTerm1 = new OntologyTerm("GO Term1", "Sample Ontology", "This is a fake description 1.");
		onto.add(newTerm1);

		newTerm2 = new OntologyTerm("GO Term2", "Sample Ontology", "This is a fake description 2.");
		onto.add(newTerm2);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		onto = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetName() {
		assertNotNull(onto.getName());
		assertEquals("Sample Ontology", onto.getName());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetCurator() {
		assertNotNull(onto.getCurator());
		assertEquals("GO", onto.getCurator());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAdd() {
		OntologyTerm newTerm3 = new OntologyTerm("GO Term3", "Sample Ontology",
		                                         "This is a fake description 3.");
		onto.add(newTerm3);

		OntologyTerm newTerm4 = new OntologyTerm("GO Term4", "Sample Ontology",
		                                         "This is a fake description 4.");
		onto.add(newTerm4);
		assertEquals(4, onto.size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSize() {
		assertEquals(2, onto.size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetTerms() {
		Set<Term> terms = onto.getTerms();
		List<String> termNames = new ArrayList<String>();

		assertNotNull(terms);
		assertEquals(2, terms.size());

		for (Term term : terms) {
			System.out.println("Term in Set = " + term.getName());
			termNames.add(term.getName());
		}

		assertTrue(termNames.contains("GO Term2"));
		assertTrue(termNames.contains("GO Term1"));
		assertFalse(terms.contains(new OntologyTerm("foo", "Sample Ontology", null)));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testContainsTerm() {
		assertTrue(onto.containsTerm("GO Term1"));
		assertTrue(onto.containsTerm("GO Term2"));
		assertFalse(onto.containsTerm("foo"));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetTerm() {
		final Term target = onto.getTerm("GO Term1");
		assertNotNull(target);
		assertEquals(newTerm1.getName(), target.getName());
		assertEquals(newTerm1.getDescription(), target.getDescription());
		assertEquals(newTerm1.getOntology().getName(), target.getOntology().getName());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testToString() {
		String message = onto.toString();
		System.out.println("# Ontology toString() method = " + message);
		assertEquals("Ontology Name: Sample Ontology, Curator: GO, Description: Fake ontology",
		             message);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testContainsTriple() {
		//fail("Not yet implemented");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCreateTermStringString() {
		try {
			Term testTerm = onto.createTerm("TestTerm1", "Test Term description1.");

			assertNotNull(testTerm);
			assertEquals("TestTerm1", testTerm.getName());
			assertEquals("Test Term description1.", testTerm.getDescription());
			assertEquals(onto.getName(), testTerm.getOntology().getName());
		} catch (AlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChangeVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCreateTermStringStringObjectArray() {
		//fail("Not yet implemented");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCreateTriple() {
		Term object = null;
		Term subject = null;
		Term predicate = null;

		try {
			object = onto.createTerm("object", "Fro triple test");
			subject = onto.createTerm("subject", "test subject.");
			predicate = new OntologyTerm("pred", onto.getName(), "pred test");
		} catch (AlreadyExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ChangeVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Triple triple = onto.createTriple(subject, object, predicate, "Sample Triple",
			                                  "Test triple creation.");

			assertNotNull(triple);
			assertEquals("Sample Triple", triple.getName());
			assertEquals("Test triple creation.", triple.getDescription());
		} catch (AlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChangeVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testDeleteTerm() {
		Term toBeDeleted = onto.getTerm("GO Term1");

		try {
			onto.deleteTerm(toBeDeleted);
			assertEquals(1, onto.size());
		} catch (ChangeVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetDescription() {
		String desc = onto.getDescription();
		assertEquals("Fake ontology", desc);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetTriples() {
		//fail("Not yet implemented");
	}
}
