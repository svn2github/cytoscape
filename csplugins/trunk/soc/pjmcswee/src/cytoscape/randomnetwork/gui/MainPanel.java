/*  File: MainPanel.java
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
 import cytoscape.randomnetwork.*;
 import javax.swing.*;
 import java.awt.*;
 
 /**
  *  This provides the buttons and a main panel 
  */
 public class MainPanel extends JPanel
 {
 
	private JDialog mFrame;
	private RandomNetworkPanel mAlgorithmPanel;
	private JButton mNextButton;
	private JButton mBackButton;
	private JButton mCancelButton;
	
	private JLabel mTitle;
	private JLabel mDescription;
	
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 40;

	/**
	 * Main Constructor
	 */
	public MainPanel(RandomNetworkPanel pPanel, JDialog pDialog)//JFrame pFrame)
	{
		super();
		mFrame = pDialog; //must be done first
		mAlgorithmPanel = pPanel;
		initComponents();
		algorithmPanelChange();

	}
	
	/**
	 * Initialize all swing components
	 */
	private void initComponents()
	{
	
		mTitle = new JLabel();
		mTitle.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		mDescription = new JLabel();
	
	
		mDescription.setPreferredSize(new Dimension(380,80));
		mDescription.setMinimumSize(new Dimension(380,80));
	

	
		//Set up the run button
		mNextButton = new JButton();
		mNextButton.setText("Next");
		mNextButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nextButtonActionPerformed(evt);
			}
		});
		
		//Set up the cancel button
		mBackButton = new JButton();
		mBackButton.setText("Back");
		mBackButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});
		
		mBackButton.setVisible(false);

		//Set up the cancel button
		mCancelButton = new JButton();
		mCancelButton.setText("Close");
		mCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
				
					
		
		//Set the preferred and minimum size of each button
		//Do not let button sizes change!
	/*	Dimension dim = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
		mNextButton.setPreferredSize(dim);
		mCancelButton.setPreferredSize(dim);
		mBackButton.setPreferredSize(dim);
		mNextButton.setMinimumSize(dim);
		mCancelButton.setMinimumSize(dim);
		mBackButton.setMinimumSize(dim);
		mNextButton.setMaximumSize(dim);
		mCancelButton.setMaximumSize(dim);
		mBackButton.setMaximumSize(dim);
		mNextButton.setSize(dim);
		mCancelButton.setSize(dim);
		mBackButton.setSize(dim);			
		*/
		makeLayout();
		
	}
	
	/**
	 * backButtonActionPerformed call back when the cancel button is pushed
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
		mAlgorithmPanel = mAlgorithmPanel.getPrevious();
		algorithmPanelChange();
	}
	
	
	/**
	 *  Call back when the cancel button is pushed
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		///JFrame frame = (JFrame)getTopLevelAncestor();
		//frame.dispose();
		mFrame.dispose();
	}
	
	/**
	 *  Callback for when the "Next" button is pushed
	 */
	private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {
		mAlgorithmPanel = mAlgorithmPanel.next();
		algorithmPanelChange();

	}

	/**
	 * Update parameters when the algorithmPanel has been changed
	 */
	private void algorithmPanelChange()
	{
		mNextButton.setText(mAlgorithmPanel.getNextText());
		if(mAlgorithmPanel.getPrevious() == null)
		{
			mBackButton.setVisible(false);
		}
		else
		{
			mBackButton.setVisible(true);
		}
		
		mNextButton.setText(mAlgorithmPanel.getNextText());
		mTitle.setText(mAlgorithmPanel.getTitle());
		mDescription.setText("<html><font size=2 face=Verdana>" +mAlgorithmPanel.getDescription() + "</font></html>");
		removeAll();
		makeLayout();

	
		repaint();
	
		mFrame.toFront();
	
	}

	/**
 	 *
	 */
	private void makeLayout()
	{
	
		setLayout(new GridBagLayout());
	
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20,10,0,0);		
		c.anchor = GridBagConstraints.NORTHWEST;
		add(mTitle,c);
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		add(mDescription,c);
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0,10,0,10);
		c.anchor = GridBagConstraints.LINE_START;		
		c.gridwidth = 3; 
		add(mAlgorithmPanel,c);

		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.anchor =GridBagConstraints.SOUTHWEST;
		c.weightx = 1;
		c.weighty = 1;
		add(mCancelButton,c);
		
		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.SOUTH;
		add(mBackButton,c);
		
		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 2;
		c.gridy = 3;
		c.anchor =	GridBagConstraints.SOUTHEAST;
		add(mNextButton,c);

	}
	
 }