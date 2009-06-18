package org.cytoscape.phylotree.parser;

import org.cytoscape.phylotree.parser.PhylipTreeImpl;

import junit.framework.TestCase;
import java.util.List;

public class PhylipTreeImplTest extends TestCase {

	private String treeStr = null;
	private PhylipTreeImpl treeParser = null;
	
	public void setUp() {
	
		String treeStr = "XXXX";
		PhylipTreeImpl treeParser = new PhylipTreeImpl(treeStr);
	}
	
	public void testGetNodeList() {
		List nodeList = treeParser.getNodeList();
		
		assertEquals("","");
	}
	
	public void testGetEdges() {
		
	}

	
	public void testGetEdgeAttribute() {
		
	}

}
