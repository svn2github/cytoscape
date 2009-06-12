package parser;
import java.util.*;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class Parser {


	String treeString;
	LinkedList<CyNode> allNodes;
	LinkedList<CyEdge> allEdges;

	public Parser(String tree)
	{
		treeString = tree;
		allNodes = new LinkedList<CyNode>();
		allEdges = new LinkedList<CyEdge>();

	}

	public void parse()
	{
		Stack <String> stack = new Stack<String>();	
		LinkedList<String> list = new LinkedList<String>();
		LinkedList<String> edgeList = new LinkedList<String>();

		Iterator<String> iterator;
		Iterator<String> edgeListIterator;

		// Split the input string into a list
		String [] substrings = treeString.split(":|,|;");
		for(int i =0; i<substrings.length;i++)
		{
			substrings[i] = substrings[i].trim();
		}

		// Parse the input into a list
		for(int i = 0; i<substrings.length; i++)
		{

			if (substrings[i].charAt(0) == '(')
			{
				list.add("(");
				for (int k = 1; k<substrings[i].length(); k++)
				{
					if(substrings[i].charAt(k) == '(')
					{
						list.add("("); 
					}
					else
					{
						String[] tempSub = substrings[i].split("\\(+");

						list.add(tempSub[1]);
						break;


					}
				}         
			}

			else if(substrings[i].charAt(0) != '(' && substrings[i].charAt(0) != ')')
			{
				String[] tempSub2 = substrings[i].split("\\)");
				list.add(tempSub2[0]);
			}
			if(substrings[i].charAt(substrings[i].length()-1)== ')')
			{
				list.add(")");
			}


		}


		// Parse the list into a node and edge lists using a stack

		iterator = list.iterator();
		int tempNodeIndex = 0;
		while(iterator.hasNext())
		{
			Object tempObj = iterator.next();
			String tempStr = (String) tempObj;
			
			if(!tempStr.equals(")"))
				{
				stack.push(tempStr);
				// Ignore
				}
			if(tempStr.equals(")"))
			{
				String stackTop = stack.pop();

				while(!stackTop.equals("("))
				{

					try
					{
						Double branchLength = Double.parseDouble(stackTop);
						// @DEVELOP_ME
						// Find a way to store the branch length with the node
						// so that the layout is actually representative of the 
						// edge distances
					}
					catch(NumberFormatException f)
					{
						// Add a node

						CyNode nodeA = Cytoscape.getCyNode(stackTop, true);
						allNodes.add(nodeA);

						// Store each node label into a list

						edgeList.add(stackTop);
					}

					stackTop = stack.pop();
				}
				if(stackTop.equals("("))
				{
					// Add a temporary parent node
					String tempNodeLabel = "tempNode"+tempNodeIndex;
					CyNode tempNode = Cytoscape.getCyNode(tempNodeLabel, true);
					allNodes.add(tempNode);
					tempNodeIndex++;

					// Add edges between the temporary parent and the children
					edgeListIterator = edgeList.iterator();
					int tempEdgeIndex = 0;
					while(edgeListIterator.hasNext())
					{
						Object tempEdgeListObj = edgeListIterator.next();
						String tempEdgeListStr = (String) tempEdgeListObj;
						String tempEdgeLabel = "edge"+tempEdgeIndex;
						CyEdge edgeA = Cytoscape.getCyEdge(tempNodeLabel, tempEdgeLabel, tempEdgeListStr, "pp");
						tempEdgeIndex++;
						allEdges.add(edgeA);


					}
					edgeList.clear();

					// Add the temporary Parent node back into the stack
					stack.push(tempNodeLabel);
					

				}
			}

		}	

	}

	public LinkedList<CyNode> getNodeList(){
		return allNodes;

	}

	public LinkedList<CyEdge> getEdgeList(){
		return allEdges;
	}
}
