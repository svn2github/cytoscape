package tests.org.cytoscape.phylotree.parser;

import org.cytoscape.phylotree.parser.*;

import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class PhylipTreeImplTest extends TestCase {

	private String treeStr = null;
	private PhylipTreeImpl treeParser = null;
	private PhylipTreeImpl treeParser2 = null;
	
	public void setUp() {
	
		treeStr = "(B:9,(A:8,C:7,E:6),D:5);";
		treeParser = new PhylipTreeImpl(treeStr);

		File file2 = new File("phyliptree.phy");
		treeParser2 = new PhylipTreeImpl(file2);
	}
	
	public void testGetNodeList() {
		List<PhylotreeNode> nodeList = treeParser.getNodeList();
		
		// Test number of nodes
		assertEquals(7,nodeList.size());
		
		// Test names of first and second to last nodes created
		assertEquals("E", nodeList.get(0).getName());
		assertEquals("B",nodeList.get(nodeList.size()-2).getName());
		
		nodeList = treeParser2.getNodeList();
		
		// Test number of nodes
		assertEquals(7,nodeList.size());
		
		// Test names of first and second to last nodes created
		assertEquals("E", nodeList.get(0).getName());
		assertEquals("B",nodeList.get(nodeList.size()-2).getName());
		

	 
	}
	
	public void testGetEdges() {

		// Test the number of edges the first parent node has
		List<PhylotreeNode> nodeList = treeParser.getNodeList();
		
		List<PhylotreeEdge> edgeList = treeParser.getEdges(nodeList.get(4));
		
		assertEquals(4, edgeList.size());
		
		// Test the number of edges the first parent node has
		 nodeList = treeParser2.getNodeList();
		
		 edgeList = treeParser2.getEdges(nodeList.get(4));
		
		assertEquals(4, edgeList.size());
	
		
	}

	
	public void testGetEdgeAttribute() {
		
		List<PhylotreeNode> nodeList = treeParser.getNodeList();
		List<PhylotreeEdge> edgeList = treeParser.getEdges(nodeList.get(0));
		
		// Test edge lengths of the first node
		assertEquals(6.0, treeParser.getEdgeAttribute(edgeList.get(0)).get(0));
		
		// Test edge lengths of the first parent node
		edgeList = treeParser.getEdges(nodeList.get(4));
		
		assertEquals(6.0, treeParser.getEdgeAttribute(edgeList.get(0)).get(0));
		assertEquals(7.0, treeParser.getEdgeAttribute(edgeList.get(1)).get(0));
		assertEquals(8.0, treeParser.getEdgeAttribute(edgeList.get(2)).get(0));
		assertEquals(0.0, treeParser.getEdgeAttribute(edgeList.get(3)).get(0));

		 nodeList = treeParser2.getNodeList();
		 edgeList = treeParser2.getEdges(nodeList.get(0));
		
		// Test edge lengths of the first node
		assertEquals(6.0, treeParser.getEdgeAttribute(edgeList.get(0)).get(0));
		
		// Test edge lengths of the first parent node
		edgeList = treeParser.getEdges(nodeList.get(4));
		
		assertEquals(6.0, treeParser.getEdgeAttribute(edgeList.get(0)).get(0));
		assertEquals(7.0, treeParser.getEdgeAttribute(edgeList.get(1)).get(0));
		assertEquals(8.0, treeParser.getEdgeAttribute(edgeList.get(2)).get(0));
		assertEquals(0.0, treeParser.getEdgeAttribute(edgeList.get(3)).get(0));




		
	}

}
