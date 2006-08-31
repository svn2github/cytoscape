package cytoscape.data.ontology.unitTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.biojava.ontology.AlreadyExistsException;
import org.biojava.ontology.Term;
import org.biojava.ontology.Triple;
import org.biojava.utils.ChangeVetoException;

import junit.framework.TestCase;
import cytoscape.Cytoscape;
import cytoscape.data.ontology.Ontology;
import cytoscape.data.ontology.OntologyTerm;

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

	public void testGetName() {
		assertNotNull(onto.getName());
		assertEquals("Sample Ontology", onto.getName());
	}

	public void testGetCurator() {
		assertNotNull(onto.getCurator());
		assertEquals("GO", onto.getCurator());
	}

	public void testAdd() {
		
		OntologyTerm newTerm3 = new OntologyTerm("GO Term3", "Sample Ontology", "This is a fake description 3.");
		onto.add(newTerm3);
		
		OntologyTerm newTerm4 = new OntologyTerm("GO Term4", "Sample Ontology", "This is a fake description 4.");
		onto.add(newTerm4);
		assertEquals(4, onto.size());
	}

	public void testSize() {
		assertEquals(2, onto.size());
	}

	public void testGetTerms() {
		Set<Term> terms = onto.getTerms();
		List<String> termNames = new ArrayList<String>();
		
		assertNotNull(terms);
		assertEquals(2, terms.size());
		for(Term term:terms) {
			System.out.println("Term in Set = " + term.getName());
			termNames.add(term.getName());
		}
		
		assertTrue(termNames.contains("GO Term2"));
		assertTrue(termNames.contains("GO Term1"));
		assertFalse(terms.contains(new OntologyTerm("foo", "Sample Ontology", null)));
	}

	public void testContainsTerm() {
		assertTrue(onto.containsTerm("GO Term1"));
		assertTrue(onto.containsTerm("GO Term2"));
		assertFalse(onto.containsTerm("foo"));
	}

	public void testGetTerm() {
		final Term target = onto.getTerm("GO Term1");
		assertNotNull(target);
		assertEquals(newTerm1.getName(), target.getName());
		assertEquals(newTerm1.getDescription(), target.getDescription());
		assertEquals(newTerm1.getOntology().getName(), target.getOntology().getName());
	}

	public void testToString() {
		String message = onto.toString();
		System.out.println("# Ontology toString() method = " + message);
		assertEquals("Ontology Name: Sample Ontology, Curator: GO, Description: Fake ontology", message);	
	}

	public void testContainsTriple() {
		//fail("Not yet implemented");
	}

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

	public void testCreateTermStringStringObjectArray() {
		//fail("Not yet implemented");
	}

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
			Triple triple = onto.createTriple(subject, object, predicate, "Sample Triple", "Test triple creation.");
			
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

	public void testGetDescription() {
		String desc = onto.getDescription();
		assertEquals("Fake ontology", desc);
	}

	public void testGetTriples() {
		//fail("Not yet implemented");
	}
}
