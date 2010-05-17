///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  ImplementationTaskPlugin.java
// Embedded Classes:   CountWords, ImplementationTaskPluginAction
//
// Author:           Layla Oesper layla.oesper@gmail.com
//
//////////////////////////// 80 columns wide //////////////////////////////////

/**
 * This program creates a plugin that will analyze titles from nodes in a 
 * network, count the frequencies of the words appearing, and output a 
 * a readout with a list of the words appearing and their relative 
 * frequencies.
 */

import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.CyNetwork;
import cytoscape.CyNode;


public class BasicCountPlugin extends CytoscapePlugin  
{
	
	/**
	 * This constructor creates an action and adds it to the Plugins menu.
	 */
	public BasicCountPlugin()
	{
		//new action for response to menu activation
		BasicCountPluginAction action = new BasicCountPluginAction();
		
		//set to Plugin menu
		action.setPreferredMenu("Plugins");
		
		//add to the menu
		Cytoscape.getDesktop().getCyMenus().addAction(action);
	}
	
	/**
	 * Provides a description of this plugin.
	 */
	public String describe()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("For every node in the current network, this plugin ");
		sb.append("counts the number of occurences of each unique word ");
		sb.append("that appears.  It also counts the occurences of each ");
		sb.append("word pair that occurs next to each other.  These counts ");
		sb.append("are displayed to the user");
		return sb.toString();
	}
	
	
	
	/**
	 * Class to store a particular word and its count.
	 */
	public class WordInfo
	{
		//Instance Variables
		String word;
		int count;
		
		//Constructors
		/**
		 * Constructs a new WordInfo object with a count of 1.
		 * @param w - the word we are creating an WordInfo object for.
		 */
		WordInfo(String w)
		{
			word = w;
			count = 1;
		}
		
		
	} 
	
	/**
	 * This is the class that is attached to the menu item when selected.
	 */
	public class BasicCountPluginAction extends CytoscapeAction
	{
		//Variables
		CountPanel curPanel;
		boolean panelAdded;
		
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public BasicCountPluginAction()
		{
			super("BasicCount");
			
			curPanel = new CountPanel();
		}
		
		/**
		 * Method called when menu item is selected.
		 */
		public void actionPerformed(ActionEvent ae)
		{
			TreeMap WordCounts = new TreeMap();
			TreeMap WordPairCounts = new TreeMap(); 
			
			retrieveCounts(WordCounts);
			retrievePairCounts(WordPairCounts); 
			
			if(!panelAdded)
			{
				addPanel(curPanel);
				panelAdded = true;
			}
			
			curPanel.clearText();
			printWords(curPanel, WordCounts,1); 
			printWords(curPanel, WordPairCounts,2);
		
		}
	
		
		/**
		 * This method reads in all words from the title of all nodes in the
		 * current Cytoscape network, and stores them
		 * along with their counts in the specified Map (a TreeMap in this case).
		 * @param words - the TreeMap object that we are adding our words and 
		 * counts to.
		 */
		public void retrieveCounts(TreeMap words)
		{
			
			//get the network object
			CyNetwork network = Cytoscape.getCurrentNetwork();
			
			//Check that network is not null - if it is just return
			if (network == null)
			{
				return;
			}
			
			List allNodes = network.nodesList();
			
			//Iterate to retrieve nodes
			Iterator iter = allNodes.iterator();
			while(iter.hasNext())
			{
				CyNode curNode = (CyNode)iter.next();
				String nodeName = curNode.toString();
				
				//Tokenize nodeName to retrieve individual words
				StringTokenizer token = new StringTokenizer(nodeName);
				while(token.hasMoreTokens())
				{
					String curWord = token.nextToken();
					curWord = curWord.toLowerCase();
					
					//Check if this word is already in Tree.
					//If not, add it.
					//If it is, update count.
					WordInfo wordData = (WordInfo)words.get(curWord);
					if (wordData == null)
					{
						words.put(curWord, new WordInfo(curWord));
					}
					else
					{
						wordData.count = wordData.count + 1;
					}
				} // end StringTokenizer while loop
			} // of node iterator
		}//end of retrieveCounts method
		
		
		/**
		 * This method reads in all paired words from the titles of all nodes 
		 * in the current Cytoscape network and stores them along with their 
		 * counts in the specified Map (a TreeMap in this case).
		 * @param words - the TreeMap object that we are adding our words and 
		 * counts to.
		 */
		public void retrievePairCounts(TreeMap words)
		{
			
			//get the network object
			CyNetwork network = Cytoscape.getCurrentNetwork();
			
			//Check that network is not null - if it is just return
			if (network == null)
			{
				return;
			}
			
			List allNodes = network.nodesList();
			
			
			//Iterate to retrieve nodes
			Iterator iter = allNodes.iterator();
			while(iter.hasNext())
			{
				CyNode curNode = (CyNode)iter.next();
				String nodeName = curNode.toString();
				
				//Tokenize nodeName to retrieve individual words
				StringTokenizer token = new StringTokenizer(nodeName);
				String prevWord = "";
					
				if (token.hasMoreTokens())
				{
					prevWord = token.nextToken(); 
				}
					
				while (token.hasMoreTokens())
				{
					String curWord = token.nextToken();
					curWord = curWord.toLowerCase();
					prevWord = prevWord.toLowerCase();
					String combinedWord = prevWord + " " + curWord;
						
						
					//Check if this word combination is already in Tree.
					//If not, add it.
					//If it is, update count.
					WordInfo wordData = (WordInfo)words.get(combinedWord);
					if (wordData == null)
					{
						words.put(combinedWord, new WordInfo(combinedWord));
					}
					else
					{
						wordData.count = wordData.count + 1;
					}
						
					prevWord = curWord;
				} // end StringTokenizer while loop
			} //end of node iterator
		}//end of readFile method
		
		/**
		 * This method displays in a CountPanel words and their frequency
		 * in the specified network.  It prints lower case version of words in
		 * alphabetical order.
		 * @param the CountPanel to display the words in
		 * @param words - the TreeMap object that we are printing information from.
		 * @param num - the number of times that a value must appear in order to 
		 * be printed.
		 */
		public void printWords(CountPanel aPanel, TreeMap words, int num)
		{	
			String message = "";
			List wordsByAlpha = new ArrayList(words.values());
			Iterator iter = wordsByAlpha.iterator();
			while(iter.hasNext())
			{
				WordInfo curData = (WordInfo)iter.next();
				int count = curData.count;
				
				if (count >= num)
				{
					message = message + curData.word + " " + curData.count + "\n";
				}
			}
			
			//Display with a new workspace
			System.out.println(message);
			aPanel.addText(message);
		}
	
		/**
		 * This method adds a new tabbed panel to the data display.  
		 */
		public void addPanel(CountPanel myPanel)
		{
			//Retrieve handler
			CytoPanelImp dataPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
			
			
			//Add the panel to SOUTH
			dataPanel.add("Word Counts", myPanel);
			
			//Select panel once plugin is initialized
			int indexInCytoPanel = dataPanel.indexOfComponent("Word Counts");
			dataPanel.setSelectedIndex(indexInCytoPanel);
			
		}
	}
	
}



