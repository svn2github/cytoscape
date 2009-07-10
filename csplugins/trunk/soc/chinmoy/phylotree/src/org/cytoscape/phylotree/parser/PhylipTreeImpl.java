package org.cytoscape.phylotree.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;



public class PhylipTreeImpl implements Phylotree {

	private String treeStr; // The tree in string format
	private List<PhylotreeNode> nodeList = null;   // List of all nodes in the tree


	// Constructors

	/*
	 * Constructor that accepts a string as an argument
	 */ 
	public PhylipTreeImpl(String pTreeStr){
		this.treeStr = pTreeStr;
		nodeList = new LinkedList<PhylotreeNode>();
		parse();
	}

	/*
	 * Constructor that accepts a file as an argument
	 */ 
	public PhylipTreeImpl(File pTreeFile){

		treeStr = getTreeTextFromFile(pTreeFile);
		nodeList = new LinkedList<PhylotreeNode>();
		parse();
	}

	/*	
	 * Reads a PHYLIP file and returns the tree in string format
	 */
	private String getTreeTextFromFile(File pTreeFile){
		String retStr = null;

		try
		{
			// Read the file to obtain the tree
			BufferedReader reader = new BufferedReader(new FileReader(pTreeFile));
			retStr = reader.readLine();
			return retStr;
		}      
		catch(IOException l)
		{
			// Error reading file
			return null;
		}

		catch(NullPointerException l)
		{
			// File not found
			return null;
		}

	}

	private List<String> convertTreeStringToList(String treeString)
	{
		List<String> returnList = new LinkedList<String>();
		// Split the tree string into a list
		String [] substrings = treeString.split(":|,|;");


		// Parse the input into a list ignoring ',' but adding names, lengths and parentheses

		for(int i = 0; i<substrings.length; i++)
		{
			substrings[i] = substrings[i].trim();

	// For every parenthesis encountered, add it to the list
			if (substrings[i].charAt(0) == '(')
			{
				returnList.add("(");
				for (int k = 1; k<substrings[i].length(); k++)
				{
					if(substrings[i].charAt(k) == '(')
					{
						returnList.add("("); 
					}
					else
					{
						// Split the remainder of the string around the '(' if a name is encountered
						String[] tempSub = substrings[i].split("\\(+");

						// Split the remainder of the string around any ')' if encountered
						String[] tempSub3 = tempSub[1].split("\\)+");

						// Add the name to the list 

						returnList.add(tempSub3[0]);
						break;


					}
				}         
			}

			// For every name/length encountered, splice off the ')' and add it to the list
			else if(substrings[i].charAt(0) != '(' && substrings[i].charAt(0) != ')')
			{
				String[] tempSub2 = substrings[i].split("\\)+");

				returnList.add(tempSub2[0]);


			}

			// For every ')' encountered add it to the list
			if(substrings[i].charAt(substrings[i].length()-1)== ')')
			{
				for(int x = 0; x<substrings[i].length(); x++)
				{
					if(substrings[i].charAt(substrings[i].length()-1-x)==')')
						returnList.add(")");

				}
			}
		}
System.out.println(returnList);

		return returnList;
	}

	private void readListIntoStack(List<String> list)
	{

		Stack <String> stack = new Stack<String>();  
		Stack<PhylipNode> parentNodeStack = new Stack<PhylipNode>();

		List<PhylipNode> childNodeList = new LinkedList<PhylipNode>();
		// ChildNodeList records the children of a node for the purpose of creating edges.
		// It is cleared after the edges for each node are created. 

		List<Double> branchLengthList = new LinkedList<Double>(); 
		// Reflects the ChildNodeList but records the edge lengths instead of the child nodes. 


		// Iterators for lists
		Iterator<String> iterator;
		Iterator<PhylipNode> childNodeListIterator;
		Iterator<Double> branchLengthListIterator;

		Double branchLength = 0.0;

		iterator = list.iterator();
		int parentNodeIndex = 0;
		while(iterator.hasNext())
		{
			String tempStr = iterator.next(); 

			if(!tempStr.equals(")"))
			{
				stack.push(tempStr);
				// Ignore
			}
			if(tempStr.equals(")"))
			{
				// Pop off items from the stack till '(' is encountered.
				// Appropriately populate node and edge lists with names/branch lenghts

				String stackTop = stack.pop();

				while(!stackTop.equals("("))
				{

					try 
					{
						// If the item popped off is a branch length, store it in the branchLength variable
						// so that it may be associated with the edge later
						branchLength = Double.parseDouble(stackTop);
					}
					catch(NumberFormatException f)
					{
						// Failure to parseDouble() indicates that the item is a node name
						// Add a PhylipNode

						PhylipNode nodeA;
						if(!parentNodeStack.isEmpty() && stackTop.equals(parentNodeStack.peek().getName()))
						{
							// This scenario indicates that the stackTop is a parentNode which has previous associated edges
							nodeA = parentNodeStack.pop();
							nodeList.add(nodeA);	

						}
						else{
							// This scenario indicates that stackTop is a previously unencountered node
							nodeA = new PhylipNode(stackTop);
							nodeList.add(nodeA);
						}
						// Also store the node in the childNodeList and its branch length in the branchLengthList
						// for the purpose of adding edges from the parent node when it is created

						childNodeList.add(nodeA);
						branchLengthList.add(branchLength);
						branchLength = 0.0;
					}

					stackTop = stack.pop();
				}
				if(stackTop.equals("("))
				{
					// '(' indicates that a set of child nodes has been parsed
					// Add a parent node representing the common ancestor of all the child nodes

					String parentNodeLabel = "parentNode"+parentNodeIndex;
					PhylipNode parentNode = new PhylipNode(parentNodeLabel);
					// Add a parent node even when there are only two nodes in the entire tree
					if(stack.isEmpty())
					{
						nodeList.add(parentNode);
					}

					parentNodeIndex++;

					// Add edges between the parent node and the children
					// Initialize iterators
					childNodeListIterator = childNodeList.iterator();
					branchLengthListIterator = branchLengthList.iterator();

					while(childNodeListIterator.hasNext())
					{
						PhylipNode childNode = childNodeListIterator.next();

						// Create the edge
						PhylipEdge edge = new PhylipEdge(parentNode,childNode);

						// Associate the edge length with the edge
						Double edgeLength = branchLengthListIterator.next();
						branchLengthListIterator.remove();
						edge.setEdgeLength(edgeLength);

						// Record the edge in each node's respective list of edges
						//System.out.println(parentNode.nodeEdges);
						parentNode.nodeEdges.add(edge);
						childNode.nodeEdges.add(edge);

						//Used during development
						//System.out.println(edge.getSourceNode().getName()+"<-->"+edge.getTargetNode().getName()+":"+edgeLength);


					}
					childNodeList.clear();

					// Add the label of the parent node back into the stack so it is connected to the rest of the tree
					// Also add the parentNode into a separate stack so that its associated edges are preserved

					stack.push(parentNodeLabel);
					parentNodeStack.push(parentNode);

				}
			}

		}
	}
	/*
	 * parse()
	 * Traverses the tree string 'treeStr'and reads it into a list
	 * Then reads the list into a stack creating nodes and edges when required.
	 */
	private void parse()
	{

		// Parse the treeStr into a list of subelements
		List<String> list = convertTreeStringToList(treeStr);

		// Traverse the list into node and edge lists using a stack
		readListIntoStack(list);


	}




	// Interface methods
	public List<PhylotreeNode> getNodeList(){
		return nodeList;
	}

	// Get the edges for given node
	public List<PhylotreeEdge> getEdges(PhylotreeNode pNode){

		// Obtain a list of edges for the node
		PhylipNode phylipNode = (PhylipNode)pNode;
		List<PhylipEdge> edges = phylipNode.nodeEdges;

		// Add each edge to the vector to be returned
		Vector<PhylotreeEdge> retValue = new Vector<PhylotreeEdge>();
		Iterator<PhylipEdge> iterator = edges.iterator();
		while(iterator.hasNext())
		{
			// A node could have multiple edges
			PhylipEdge pEdge = iterator.next();
			retValue.add(pEdge);
		}
		return retValue;
	}

	/*	Return a list of all the edge attributes
	 *  The order of attributes is {edgeLength,}
	 */
	public List<Object> getEdgeAttribute(PhylotreeEdge pEdge){

		List<Object> edgeAttributes = new LinkedList<Object> ();

		PhylipEdge phyEdge = (PhylipEdge)pEdge;

		edgeAttributes.add(phyEdge.getEdgeLength());
		return edgeAttributes;
	}


	// Inner classes
	class PhylipNode implements PhylotreeNode {
		private String nodeName = null;
		public List<PhylipEdge> nodeEdges; // List of all edges connected to this node

		public PhylipNode(String pNodeName){
			this.nodeName = pNodeName;
			this.nodeEdges = new LinkedList<PhylipEdge>();
		}

		public String getName(){
			return nodeName;
		}
	}


	class PhylipEdge implements PhylotreeEdge {
		private PhylipNode sourceNode = null;
		private PhylipNode targetNode = null;
		private double edgeLength = 0.0;

		public PhylipEdge(PhylipNode pSourceNode, PhylipNode pTargetNode){
			this.sourceNode = pSourceNode;
			this.targetNode = pTargetNode;
		}

		public void setEdgeLength(double length){
			edgeLength = length; 
		}

		public double getEdgeLength() {
			return edgeLength;
		}
		public PhylipNode getSourceNode() {
			return sourceNode;
		} 

		public PhylipNode getTargetNode(){
			return targetNode;
		}
	}
}
