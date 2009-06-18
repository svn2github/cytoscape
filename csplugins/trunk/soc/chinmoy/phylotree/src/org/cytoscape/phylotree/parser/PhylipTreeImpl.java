package org.cytoscape.phylotree.parser;

import java.util.List;
import java.io.File;
import java.util.Vector;

public class PhylipTreeImpl implements Phylotree {
	
	private String treeStr;
	private List<PhylotreeNode> nodeList = null;

	
	// Constructors
	public PhylipTreeImpl(String pTreeStr){
		this.treeStr = pTreeStr;
		parse();
	}
	
	public PhylipTreeImpl(File pTreeFile){
	
		treeStr = getTreeTextFromFile(pTreeFile);
		parse();
	}

	private String getTreeTextFromFile(File pTreeFile){
		String retStr = null;
		
		//open the TreeFile, read its content
		// retStr = read the text from file
		
		return retStr;
	}
	
	private void parse(){
		//This is the core of the parser
		//Parse the treeStr, and populate the data items,
		
	}
	
	// interface methods
	public List<PhylotreeNode> getNodeList(){
		return nodeList;
	}
	
	//Get the edges for given node
	public List<PhylotreeEdge> getEdges(PhylotreeNode pNode){
		Vector<PhylotreeEdge> retValue = new Vector<PhylotreeEdge>();

		// a node could have multiple edges
		//retValue.add(connectedNode1);
		//retValue.add(connectedNode2);
		//retValue.add(connectedNodeN);
		
		return retValue;
	}
	
	public List getEdgeAttribute(PhylotreeEdge pEdge){
		return null;
	}
	
	
	// inner classes
	class PhylipNode implements PhylotreeNode {
		private String nodeName = null;
		
		public PhylipNode(String pNodeName){
			this.nodeName = pNodeName;
		}
		
		public String getName(){
			return nodeName;
		}
	}
	
	
	class PhylipEdge implements PhylotreeEdge {
		private PhylipNode sourceNode = null;
		private PhylipNode targetNode = null;
		
		public PhylipEdge(PhylipNode pSourceNode, PhylipNode pTargetNode){
			this.sourceNode = pSourceNode;
			this.targetNode = pTargetNode;
		}
		
		public PhylipNode getSourceNode() {
			return sourceNode;
		}
		
		public PhylipNode getTargetNode(){
			return targetNode;
		}
	}
}
