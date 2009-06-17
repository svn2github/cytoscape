package org.cytoscape.phylotree.parser;

import org.cytoscape.phylotree.parser.PhylipTree;

import junit.framework.TestCase;
import java.util.List;

public class PhylipTreeTest extends TestCase {

	private String treeStr = null;
	private PhylipTree treeParser = null;
	
	public void setUp() {
	
		String treeStr = "XXXX";
		PhylipTree treeParser = new PhylipTree(treeStr);
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
