/*
 File: CloudWordInfo.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

/**
 * The CloudWordInfo class defines information pertaining to a particular
 * word in a Cloud.  In particular this class defines information that 
 * relates to how that word will be displayed in the cloud.
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudWordInfo implements Comparable<CloudWordInfo>
{
	//VARIABLES
	private String word;
	private Integer fontSize;
	private CloudParameters params;
	private Color textColor;
	private Integer cluster;
	private Integer wordNum;
	
	//String Delimeters
	private static final String FIRSTDELIMITER = "TabbedEquivalent";
	private static final String SECONDDELIMITER = "NewLineEquivalent";
	
	//CONSTRUCTORS
	
	/**
	 * Creates a blank CloudWordInfo Object.
	 */
	public CloudWordInfo()
	{
		fontSize = 0;
		textColor = Color.BLACK;
		cluster = 0;
		wordNum = 0;
	}
	
	/**
	 * Creates a blank CloudWordInfo Object for the specified word.
	 * @param String - the word for this object
	 * @param Integer - the font size for this object
	 */
	public CloudWordInfo(String aWord, Integer size)
	{
		word = aWord;
		fontSize = size;
		textColor = Color.BLACK;
		cluster = 0;
		wordNum = 0;
	}
	
	/**
	 * Constructor to create CloudWordInfo from a cytoscape property file
	 * while restoring a session.  Property file is created when the session is saved.
	 * @param propFile - the name of the property file as a String
	 */
	public CloudWordInfo(String propFile)
	{
		this();
		
		//Create a hashmap to contain all the values in the rpt file
		HashMap<String, String> props = new HashMap<String,String>();
		
		String[] lines = propFile.split(SECONDDELIMITER);
		
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			String[] tokens = line.split(FIRSTDELIMITER);
			//there should be two values in each line
			if(tokens.length == 2)
				props.put(tokens[0],tokens[1]);
		}
		
		this.word = props.get("Word");
		this.fontSize = new Integer(props.get("FontSize"));
		this.cluster = new Integer(props.get("Cluster"));
		this.wordNum = new Integer(props.get("WordNum"));
		this.textColor = new Color(new Integer(props.get("TextColor")), true);	
	}
	
	//METHODS
	
	/**
	 * Compares two CloudWordInfo objects based on their fontSize.  Then, based
	 * on cluster number, then based on wordNum, and then alphabetically.
	 * @param CloudWordInfo - object to compare
	 * @return true if 
	 */
	public int compareTo(CloudWordInfo c)
	{
		Integer first = this.getFontSize();
		Integer second = c.getFontSize();
		
		//switch order since we want to sort biggest to smallest
		int result = second.compareTo(first);
		
		if (result == 0)
		{
			first = this.getCluster();
			second = c.getCluster();
			result = first.compareTo(second);
			
			if (result == 0)
			{
				first = this.getWordNumber();
				second = c.getWordNumber();
				result = first.compareTo(second);
				
				if (result == 0)
				{
					String firstString = this.getWord();
					String secondString = c.getWord();
					result = firstString.compareTo(secondString);
				}//end string compare
			}//end word number compare
		}//end cluster compare
		
		return result;
	}
	
	/**
	 * Returns a JLabel that can be used to display this word in a cloud.
	 * @return JLabel - for display in Cloud.
	 */
	public JLabel createCloudLabel()
	{
		JLabel label = new JLabel(this.getWord());
		label.setFont(new Font("sansserif",Font.BOLD, this.getFontSize()));
		label.setForeground(textColor);
		
		//Listener stuff
		label.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent me)
			{
				JLabel clickedLabel = (JLabel)me.getComponent();
				String word = clickedLabel.getText();
				
				CloudParameters cloudParams = SemanticSummaryManager.getInstance().
				getCurCloud();
				
				//Get all nodes containing this word
				List<String> nodeNames = cloudParams.getStringNodeMapping().get(word);
				List<CyNode> nodes = new ArrayList<CyNode>();
				
				for(Iterator<String> iter = nodeNames.iterator(); iter.hasNext();)
				{
					String curNodeName = iter.next();
					CyNode curNode = Cytoscape.getCyNode(curNodeName);
					nodes.add(curNode);
				}
				
				CyNetwork network = Cytoscape.getCurrentNetwork();
				CyNetworkView view = Cytoscape.getCurrentNetworkView();
				if (view.getNetwork().equals(network))
				{
					network.unselectAllNodes();
					network.unselectAllEdges();
					network.setSelectedNodeState(nodes, true);
				
					//Redraw the graph with selected nodes
					view.redrawGraph(false, true);
					
					//Bring panels to the front
					SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
					init.loadCloudPanel();
					init.loadInputPanel();
				}
			}
			
			public void mouseEntered(MouseEvent me)
			{
				JLabel clickedLabel = (JLabel)me.getComponent();
				
				CyNetwork network = Cytoscape.getCurrentNetwork();
				CyNetworkView view = Cytoscape.getCurrentNetworkView();
				if (view.getNetwork().equals(network))
				{
					clickedLabel.setForeground(new Color(0,200,255));
					clickedLabel.repaint();
				}
				
			}
	
			public void mouseExited(MouseEvent me)
			{
				JLabel clickedLabel = (JLabel)me.getComponent();
				
				if (!Cytoscape.getCurrentNetworkView().equals(Cytoscape.getNullNetworkView()))
				{
					clickedLabel.setForeground(textColor);
					clickedLabel.repaint();
				}
			}
		});
		
		return label;
	}
	
	/**
	 * String representation of CloudWordInfo.
	 * It is used to store the persistent attributes when a session is saved.
	 * @return - String representation of this object
	 */
	public String toString()
	{
		StringBuffer paramVariables = new StringBuffer();
		
		paramVariables.append("Word" + FIRSTDELIMITER + word + SECONDDELIMITER);
		paramVariables.append("FontSize" + FIRSTDELIMITER + fontSize + SECONDDELIMITER);
		paramVariables.append("Cluster" + FIRSTDELIMITER + cluster + SECONDDELIMITER);
		paramVariables.append("WordNum" + FIRSTDELIMITER + wordNum + SECONDDELIMITER);
		paramVariables.append("TextColor" + FIRSTDELIMITER + textColor.getRGB() + SECONDDELIMITER);
		
		return paramVariables.toString();
	}
	
	//Getters and Setters
	public void setWord(String aWord)
	{
		word = aWord;
	}
	
	public String getWord()
	{
		return word;
	}
	
	public void setFontSize(Integer size)
	{
		fontSize = size;
	}
	
	public Integer getFontSize()
	{
		return fontSize;
	}
	
	public void setCloudParameters(CloudParameters curParams)
	{
		params = curParams;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	public void setTextColor(Color col)
	{
		textColor = col;
	}
	
	public Integer getCluster()
	{
		return cluster;
	}
	
	public void setCluster(Integer clusterNum)
	{
		cluster = clusterNum;
	}
	
	public Integer getWordNumber()
	{
		return wordNum;
	}
	
	public void setWordNumber(Integer num)
	{
		wordNum = num;
	}
}
