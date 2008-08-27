/* File: GenerateRandomPanel.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package cytoscape.randomnetwork.gui;
import  cytoscape.randomnetwork.*;
import cytoscape.plugin.*;
import cytoscape.*;
import java.awt.*;
import javax.swing.*;


/**
 * GenerateRandomPanel is used for selecting which random 
 * network model to use.
 */
public class GenerateRandomPanel extends RandomNetworkPanel {
   
    /**
	 * Provides state information for the panel
	 */
	private int mode;
	
	/**
	 *
	 */
	private int nextState;

	//Title Label
	//Group together the different options
	private ButtonGroup group;

	//Checkbox for erdos-renyi model
	private JCheckBox erm;
	//Checkbox for watts-strogatz model
	private JCheckBox wsm;
	//Checkbox for barabasi-albert model
	private JCheckBox bam;

	//Label to describe erdos-renyi model
	private JLabel ermExplain;
	//Label to describe watts-strogatz model
	private JLabel wsmExplain;
	//Checkbox for barabasi-albert model
	private JLabel bamExplain;
	

	/**
	 *  Default constructor
	 */
	public GenerateRandomPanel(int pMode, RandomNetworkPanel pPrevious ){
		
		super(pPrevious); 
		mode = pMode;
		initComponents();
		nextState = -1;
	}


	/**
	 *  Default constructor
	 */
	public GenerateRandomPanel(int pMode){
		
		super(null); 
		mode = pMode;
		initComponents();
		nextState = -1;
	}



	/**
	 * Initialize the components
	 */
	private void initComponents() {

		//Create the group 
		group = new javax.swing.ButtonGroup();
		//Create the erdos-renyi checkbox
		erm = new javax.swing.JCheckBox();
		//Create the watts-strogatz checkbox
		wsm = new javax.swing.JCheckBox();
		//Create the barabasi-albert checkbox		
		bam = new javax.swing.JCheckBox();
		//Create the erdos-renyi label
		ermExplain = new javax.swing.JLabel();
		//Create the watts-strogatz label
		wsmExplain = new javax.swing.JLabel();
		//Create the barabasi-albert  label
		bamExplain = new javax.swing.JLabel();

		//Set the erdos-renyi text
		ermExplain
				.setText("<html><font size=2 face=Verdana>Generate a flat random network.</font></html>");

		//Set the watts-strogatz text
		wsmExplain
				.setText("<html><font size=2 face=Verdana>Generate a random network with <br>high clustering coefficient.</font></html>");

		//Set the barabasi-albert text
		bamExplain.setText("<html><font size=2 face=Verdana>Generate a scale-free random network.</font></html>");


		ermExplain.setPreferredSize(new Dimension(200,40));
		wsmExplain.setPreferredSize(new Dimension(200,40));
		bamExplain.setPreferredSize(new Dimension(200,40));
		ermExplain.setMinimumSize(new Dimension(200,40));
		wsmExplain.setMinimumSize(new Dimension(200,40));
		bamExplain.setMinimumSize(new Dimension(200,40));
		

		//set the labels to opaque
		ermExplain.setOpaque(true);
		wsmExplain.setOpaque(true);
		bamExplain.setOpaque(true);
		
		//Set the text for the checkboxes
		erm.setText("Erdos-Renyi Model");
		wsm.setText("Watts-Strogatz Model");
		bam.setText("Barabasi-Albert Model");



		//Make barabasi-albert the default
		erm.setSelected(true);
		
		//Add each checkbox to the group
		group.add(bam);
		group.add(wsm);
		group.add(erm);
		

		setLayout(new GridBagLayout());
		
		//Setup the titel
		GridBagConstraints c = new GridBagConstraints();
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(erm,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(15,5,15,5);		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;
		add(ermExplain,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;		
		c.insets = new Insets(5,5,5,5);				
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		add(wsm,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(15,5,15,5);				
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;
		add(wsmExplain,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);				
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 1;
		add(bam,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(15,5,15,5);				
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 5;
		add(bamExplain,c);

	}


	/**
	 *
	 */
	public String getTitle()
	{
		return new String("Generate Random Network");
	}
	
	
	
	/**
	 *
	 */
	public String getDescription()
	{
		return new String("Generate a random network according to the one of the models below.");
	}
	
	
	/**
	 *  Callback for when the "Next" button is pushed
	 */
	public RandomNetworkPanel next()
	{
		
		//See which checkbox is selected and then display the appropriate panel
		if (erm.isSelected()) {
		
			if((mNext == null) ||(nextState != 0))
			{
				mNext = null;
				mNext = new ErdosRenyiPanel(mode, this);
			}
			nextState = 0;
			
		} else if(wsm.isSelected()){
			
			if((mNext == null) ||(nextState != 1))
			{
				mNext = null;
				mNext = new WattsStrogatzPanel(mode, this);
			}
			nextState = 1;
		}else{
		
			if((mNext == null) ||(nextState != 2))
			{
				mNext = null;
				mNext = new BarabasiAlbertPanel(mode, this);
			}	
			nextState = 2;
			
		}
		
		return mNext;
	}
}