package parser;


import java.awt.event.ActionEvent;
import java.util.*;
import java.io.*;
import javax.swing.*;
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
			
			  String treeString; // The variable that will contain the string read from the file
		        
			JFileChooser chooser = new JFileChooser();
	        int option = chooser.showOpenDialog(null);
	        if (option == JFileChooser.APPROVE_OPTION) {
	          File file = chooser.getSelectedFile();
	          
	          
              try{
                
	                BufferedReader in = new BufferedReader(new FileReader(file));
	                treeString = in.readLine();
	                parser.Parser p = new Parser(treeString); 
	    			p.parse();
	    			LinkedList<CyNode> nodes = p.getNodeList();
	    			LinkedList<CyEdge> edges = p.getEdgeList();
	    			
	
	    		    CyNetwork cyNetwork = Cytoscape.createNetwork("network", false);
	    			Iterator<CyNode> iterator1 = nodes.iterator(); 
	    			while(iterator1.hasNext())
	    			{
	    				cyNetwork.addNode(iterator1.next());
	    				
	    			}
	    						
	    			Iterator<CyEdge> iterator2 = edges.iterator(); 
	    			while(iterator2.hasNext())
	    			{
	    				cyNetwork.addEdge(iterator2.next());
	    				
	    			} 	
    			 
              }
              
              
              catch(IOException l)
              {
               JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Error reading file: " + file.getName()); 
              }
	          
              catch(NullPointerException l)
              {
               JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Error reading file: " + file.getName()); 
              }
	          
	        }
			
	        
			
		  
		}
	}
}
