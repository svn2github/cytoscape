package parser;


import java.awt.event.ActionEvent;
import java.util.*;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
/**
 * Plugin to parse a phylogenetic tree into a network
 * 
 */

public class ParserPlugin extends CytoscapePlugin {

	/**
	 * Parser a string representation of a PHYLIP phylogenetic tree
	 * into a Cytoscape network.
	 * Edit the String treeString to try different phylogenetic trees
	 * 
	 */
	public ParserPlugin() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(ParserPlugin myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("ParserPlugin");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			// Pick a string represenation of a phylogenetic tree
			// These have all been obtained from the PHYLIP site
			
			// String treeString= "";
			
			 String treeString =  "(Bovine:0.69395,(Gibbon:0.36079,(Orang:0.33636,(Gorilla:0.17147,(Chimp:0.19268, Human:0.11927):0.08386):0.06124):0.15057):0.54939,Mouse:1.21460)";
			
			// String treeString = "(Bovine:0.69395,(Hylobates:0.36079,(Pongo:0.33636,(G._Gorilla:0.17147, (P._paniscus:0.19268,H._sapiens:0.11927):0.08386):0.06124):0.15057):0.54939, Rodent:1.21460)";
			// String treeString = "((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700,seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201,weasel:18.87953):2.09460):3.87382,dog:25.46154)";
			
			
			//String treeString = "(B,(A,C,E),D)";
			
			 	Stack <String> stack = new Stack<String>();	
			    LinkedList<String> list = new LinkedList<String>();
			    LinkedList<String> edgeList = new LinkedList<String>();
			    
			    Iterator<String> iterator;
			    Iterator<String> edgeListIterator;
			
			    CyNetwork cyNetwork = Cytoscape.createNetwork("network", false);
			
				
			    // Split the input string into a list
			    String [] substrings = treeString.split(":|,");
			    for(int i =0; i<substrings.length;i++)
			    {
			      substrings[i] = substrings[i].trim();
			    }
			    
			    // Parse the input into a list
			    for(int i = 0; i<substrings.length; i++)
			    {
			      substrings[i].trim();
			      if (substrings[i].charAt(0) == '(')
			      {
			        list.add("(");
			        String[] tempSub = substrings[i].split("\\(");
			        list.add(tempSub[1]);
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
			    
			    
			    // Parse the list into a CyNetwork using a stack
			    
			    iterator = list.iterator();
			    int tempNodeIndex = 0;
			    while(iterator.hasNext())
			    {
			      Object tempObj = iterator.next();
			      String tempStr = (String) tempObj;
			      
			      if(!tempStr.equals(")"))
			      {
			        stack.push(tempStr);
			        // Ignore (
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
			    			cyNetwork.addNode(nodeA);
			    			
			    			
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
			        	 cyNetwork.addNode(tempNode);
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
			    				cyNetwork.addEdge(edgeA);
			    			
			    			
			    			}
			        	 edgeList.clear();
			        	 
			        	 // Add only the tempNode back to the edgeList so that its parent can make an edge to it.
			        	 edgeList.add(tempNodeLabel);
			         }
			      }
			     
			    }
			 
		  
			// remove a node
			//cyNetwork.removeNode(node1.getRootGraphIndex(), true);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);
			
			// destroy the network
			//Cytoscape.destroyNetwork(cyNetwork);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_DESTROYED, cyNetwork, null);
		}
	}
}
