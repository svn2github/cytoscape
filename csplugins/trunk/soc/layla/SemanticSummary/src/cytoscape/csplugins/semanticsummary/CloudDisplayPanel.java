/*
 File: SemanticSummaryInputPanel.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.csplugins.semanticsummary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;

/**
 * The CloudDisplayPanel class defines the panel that displays a Semantic 
 * Summary tag cloud in the South data panel.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudDisplayPanel extends JPanel
{
	
	//VARIABLES
	//TODO
	JPanel tagCloudFlowPanel;//add JLabels here for words
	
	
	//CONSTRUCTORS
	public CloudDisplayPanel()
	{
		setLayout(new BorderLayout());
		
		//Create JPanel containing tag words
		tagCloudFlowPanel = initializeTagCloud();
		JScrollPane cloudScroll = new JScrollPane(tagCloudFlowPanel);
		cloudScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(cloudScroll, BorderLayout.CENTER);
		
		
		//TESTING DATA
		//TODO - Remove this
		JLabel first = new JLabel("First" + " ");
		first.setFont(new Font("sansserif",Font.BOLD, 72));
		tagCloudFlowPanel.add(first);
		
		JLabel second = new JLabel("Second" + " ");
		second.setFont(new Font("sansserif",Font.BOLD, 32));
		tagCloudFlowPanel.add(second);
		
		JLabel third = new JLabel("Third" + " ");
		third.setFont(new Font("sansserif",Font.BOLD, 12));
		tagCloudFlowPanel.add(third);
		
		JLabel fourth = new JLabel("Fourth" + " ");
		fourth.setFont(new Font("sansserif",Font.PLAIN, 72));
		tagCloudFlowPanel.add(fourth);
		
		JLabel fifth = new JLabel("Fifth" + " ");
		fifth.setFont(new Font("sansserif",Font.PLAIN, 32));
		tagCloudFlowPanel.add(fifth);
		
		JLabel sixth = new JLabel("Sixth" + " ");
		sixth.setFont(new Font("sansserif",Font.PLAIN, 12));
		tagCloudFlowPanel.add(sixth);
		
		JLabel seventh = new JLabel("First" + " ");
		seventh.setFont(new Font("sansserif",Font.BOLD, 72));
		tagCloudFlowPanel.add(seventh);
		
		JLabel eighth = new JLabel("Second" + " ");
		eighth.setFont(new Font("sansserif",Font.BOLD, 32));
		tagCloudFlowPanel.add(eighth);
		
		JLabel ninth = new JLabel("Third" + " ");
		ninth.setFont(new Font("sansserif",Font.BOLD, 12));
		tagCloudFlowPanel.add(ninth);
		
		JLabel tenth = new JLabel("Fourth" + " ");
		tenth.setFont(new Font("sansserif",Font.PLAIN, 72));
		tagCloudFlowPanel.add(tenth);
		
		JLabel eleventh = new JLabel("Fifth" + " ");
		eleventh.setFont(new Font("sansserif",Font.PLAIN, 32));
		tagCloudFlowPanel.add(eleventh);
		
		JLabel twelfth = new JLabel("Sixth" + " ");
		twelfth.setFont(new Font("sansserif",Font.PLAIN, 12));
		tagCloudFlowPanel.add(twelfth);
		
	}
	
	//METHODS
	//TODO
	
	/**
	 * Initialized a blank tag cloud JPanel object.
	 * @return JPanel
	 */
	private JPanel initializeTagCloud()
	{
		JPanel panel = new JPanel(new ModifiedFlowLayout());
		return panel;
	}
	
	/**
	 * Clears all words from the CloudDisplay.
	 */
	public void clearCloud()
	{
		tagCloudFlowPanel = initializeTagCloud();
	}
	
	

	//Getters and Setters
	
	public JPanel getTagCloudFlowPanel()
	{
		return tagCloudFlowPanel;
	}
	
	public void setTagCloudFlowPanel(JPanel aPanel)
	{
		tagCloudFlowPanel = aPanel;
	}

}
