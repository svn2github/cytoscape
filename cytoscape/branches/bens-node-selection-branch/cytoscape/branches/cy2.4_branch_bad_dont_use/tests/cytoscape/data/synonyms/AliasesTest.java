package cytoscape.data.synonyms;

import giny.model.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import cytoscape.Cytoscape;
import cytoscape.data.synonyms.AliasType;
import cytoscape.data.synonyms.Aliases;

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
