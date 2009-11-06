package org.cytoscape.phylotree.parser;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import cytoscape.logger.CyLogger;



public class PhylipTreeImpl implements Phylotree {

	private String treeStr; // The tree in string format
	private List<PhylotreeNode> nodeList = null;   // List of all nodes in the tree
	private CyLogger logger = CyLogger.getLogger("phylotree.parser");


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
	public String getTreeTextFromFile(File pTreeFile){
		String retStr = "";

		try
		{
			// Read the file to obtain the tree
			BufferedReader reader = new BufferedReader(new FileReader(pTreeFile));
			String line = null;
			while ((line = reader.readLine()) != null) retStr += line;
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

	private List<String> convertTreeStringToList(String str)
	{
		if(str!=null)
		{String word = "";
		int unIndex = 0;
		int ancestorNumber = 0;
		List<String> list = new LinkedList<String>();
		for(int i = 0; i<str.length();)
		{
			// Get the node name and length
			if(str.charAt(i) != '(' && str.charAt(i) != ')' && str.charAt(i)!=',' && str.charAt(i)!=';')
			{
				while(str.charAt(i)!='(' && str.charAt(i)!=')'&& str.charAt(i)!=';')
				{ 
					// Create multiple words for those separated by a ','
					if(str.charAt(i)==',')
					{
						break; 
					}
					word = word+str.charAt(i);
					i++;
					if(i>=str.length())
						break;
				}

				// Add the word to the list
				if(!word.equals(""))
				{
					// System.out.println("See word: "+word);
					if(word.charAt(0)!=':'){
						// OK, we've got a word, but it doesn't start with a colon.  If
						// we see a colon, then we have a name:length pair
						String [] subWords = word.split(":");
						if (subWords.length > 0) {
							// Do we have a name?
							if (!subWords[0].equals("")) {
								list.add(subWords[0].trim());		// Yes, add it as a string
								// System.out.println("Added: "+subWords[0].trim());
							}

							// Do we have a number?
							if (subWords.length == 2 && !subWords[1].equals("")) {
								list.add(":");
								list.add(subWords[1].trim());
								// System.out.println("Added: "+": + "+subWords[1].trim());
							}
						}
					}
					// If its only a length with no name specified, mark it with ":"
					else
					{
						String [] lengths = word.split(":");
						list.add(":");
						list.add(lengths[1].trim());
					}
				}
				word = "";
			}

			// End of the tree string
			else if(i<str.length() && str.charAt(i)==';')
				break;

			// Otherwise add the special character to the list
			else  
			{
				list.add(""+str.charAt(i));
				i++;
				if(i>=str.length())
					break;
			}
		}

		// For debugging
		// System.out.println("Node List before refinement: ");
		// for (String s: list)
		// 	System.out.println("Node: "+s);

		// Refine the list

		ListIterator<String> ite = list.listIterator();
		List<Integer> replaceList = new LinkedList<Integer>();
		List<Integer> ancestorList = new LinkedList<Integer>();

		// Get indices of unnamed nodes
		while(ite.hasNext())
		{
			int previousIndex = 0;
			int index = 0;

			if(ite.hasPrevious())
				previousIndex = ite.previousIndex();

			String listEle = ite.next();
			// System.out.println("List refine - 1.  Element = "+listEle+", previous element = "+list.get(previousIndex));

			if((listEle.equals(",") && list.get(previousIndex).equals("(")) ||
			   (listEle.equals(":") && list.get(previousIndex).equals("(")))
			{
				index = ite.nextIndex();
				replaceList.add(index);
				// System.out.println("Adding "+list.get(index)+" to replaceList");
			}
			else if ((listEle.equals(")")||listEle.equals(":")) && list.get(previousIndex).equals(","))
			{ 
				index = ite.nextIndex();
				replaceList.add(index);
				// System.out.println("Adding "+list.get(index)+" to replaceList");
			}
			// And the unnamed parents as well
			else if((listEle.equals(",") && list.get(previousIndex).equals(")"))
					||(listEle.equals(")") && list.get(previousIndex).equals(")"))
					||(listEle.equals(":") && list.get(previousIndex).equals(")")))
			{
				index = ite.nextIndex();
				ancestorList.add(index-1);
				// System.out.println("Adding "+list.get(index-1)+" to ancestor list");
			}
		}

		// Add unnamed nodes to marked indices
		ListIterator<Integer> ite2 = replaceList.listIterator();

		while(ite2.hasNext())
		{
			int addIndex = ite2.next();
			list.add(addIndex+unIndex+ancestorNumber, "Unnamed Node"+unIndex);
			// System.out.println("Adding Unnamed Node"+unIndex+" to list");
			unIndex++;
		}

		ite2 = ancestorList.listIterator();
		while(ite2.hasNext())
		{
			int ancestorIndex = ite2.next();
			list.add(ancestorIndex+ancestorNumber+unIndex, "Ancestor"+ancestorNumber);
			// System.out.println("Adding Ancestor"+ancestorNumber+" to list");
			ancestorNumber++;
		}

		// Remove placeholder ',' and ':'
		ite = list.listIterator();
		while(ite.hasNext())
		{
			String ele = ite.next();

			if(ele.equals(","))
				ite.remove();
		}
		// list.add(";");

		// For debugging
		// System.out.println("Node List after refinement: ");
		// for (String s: list)
		// 	System.out.println("Node: "+s);

		return list;
		}
		else
		{
			return null;
		}
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
		ListIterator<String> iterator;
		Iterator<PhylipNode> childNodeListIterator;
		Iterator<Double> branchLengthListIterator;

		Double branchLength = 0.0;

		iterator = list.listIterator();

		// System.out.println("Creating stack");

		//stack.push("Root Node"); // Something for the highest ancestor to connect to
		while(iterator.hasNext())
		{
			String tempStr = iterator.next(); 

			//if(!tempStr.equals(")")&&!tempStr.equals(";"))
			if(!tempStr.equals(")"))
			{
				// System.out.println("Push "+tempStr);
				stack.push(tempStr);
				// Ignore
			}

			//if(tempStr.equals(")") || tempStr.equals(";"))
			if(tempStr.equals(")"))
			{
				// Pop off items from the stack till '(' is encountered.
				// Appropriately populate node and edge lists with names/branch lenghts

				String stackTop = stack.pop();


				while(!stackTop.equals("("))
				{
					// System.out.println("stackTop "+stackTop);

					// See if this is a branch length
					if (stack.peek().equals(":")) {
						// System.out.println("BranchLength = "+stackTop);
						try 
						{
							// If the item popped off is a branch length, store it in the branchLength variable
							// so that it may be associated with the edge later
							branchLength = Double.parseDouble(stackTop);
						}
						catch(NumberFormatException f)
						{
							logger.warning("Can't parse branch length: "+f.getMessage(), f);
						}
						// Skip over the ":" and get it
						if(!stack.isEmpty()) {
							stackTop = stack.pop();
						}
					} else {
						// Add a PhylipNode

						PhylipNode nodeA;
						if(!parentNodeStack.isEmpty() && stackTop.equals(parentNodeStack.peek().getName()))
						{
							// This scenario indicates that the stackTop is a parentNode which has previous associated edges
							nodeA = parentNodeStack.pop();
							nodeList.add(nodeA); 
							// System.out.println("Added parent node "+nodeA.getName());
						}
						else{
							// This scenario indicates that stackTop is a previously unencountered node
							nodeA = new PhylipNode(stackTop);
							nodeList.add(nodeA);
							// System.out.println("Added new node "+nodeA.getName());
						}
						// Also store the node in the childNodeList and its branch length in the branchLengthList
						// for the purpose of adding edges from the parent node when it is created

						childNodeList.add(nodeA);
						branchLengthList.add(branchLength);
						branchLength = 0.0;
					}

					if(!stack.isEmpty())
						stackTop = stack.pop();
				}
				if(stackTop.equals("("))
				{
					// '(' indicates that a set of child nodes has been parsed
					// Add a parent node representing the common ancestor of all the child nodes


					// Look ahead and get the name of the parent node
					String ancestor = "";
					if(iterator.hasNext())
					{
						ancestor = iterator.next();

					}

					if(ancestor.equals(""))
					{
						ancestor = "Root Node";
					}
					PhylipNode parentNode = new PhylipNode(ancestor);
					// System.out.println("Added new parent node "+parentNode.getName());
					// Add a parent node even when there are only two nodes in the entire tree
					if(stack.isEmpty())
					{
						nodeList.add(parentNode);
					}

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

						parentNode.nodeEdges.add(edge);
						childNode.nodeEdges.add(edge);

						//Used during development
						// System.out.println(edge.getSourceNode().getName()+"<-->"+edge.getTargetNode().getName()+":"+edgeLength);
					}
					childNodeList.clear();

					// Add the label of the parent node back into the stack so it is connected to the rest of the tree
					// Also add the parentNode into a separate stack so that its associated edges are preserved

					stack.push(ancestor);
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

		if(list!=null)
			// Traverse the list into node and edge lists using a stack
			readListIntoStack(list);
	}


	// Interface methods
	public List<PhylotreeNode> getNodeList(){
		return nodeList;
	}

	// Get all the outgoingedges for given node
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

	// Get the outgoingedges for given node
	public List<PhylotreeEdge> getOutgoingEdges(PhylotreeNode pNode){

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
			if(pEdge.getSourceNode().getID().equals(pNode.getID()))
				retValue.add(pEdge);
		}
		return retValue;
	}

	/* Return a list of all the edge attributes
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
		private String nodeID = null;
		public List<PhylipEdge> nodeEdges; // List of all edges connected to this node

		public PhylipNode(String pNodeName){
			this.nodeName = pNodeName;
			this.nodeEdges = new LinkedList<PhylipEdge>();
			this.nodeID = pNodeName;
		}

		public String getName(){
			return nodeName;
		}

		public String getID(){
			return nodeID;
		}
		public void setID(String newID){
			nodeID = newID;
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
