
/*
  File: BioDataServerPanel2.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

package cytoscape.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JLabel;

import cytoscape.CytoscapeInit;


public class BioDataServerPanel2 extends JPanel {

	private javax.swing.JLabel anotherBlankSpace;

	private javax.swing.JLabel blankSpace;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;

	private javax.swing.JLabel panelTitle;

	private JPanel contentPanel;

	private JLabel iconLabel;

	private JSeparator separator;

	private JLabel textLabel;

	private JPanel titlePanel;
	
	private JFileChooser jc1;
	
	private JTextField oboFileName;
	private JTextField gaFileName;
	private JButton selectOboFile;
	private JButton selectGaFile;

	private Container jPanel3;

	private JFileChooser jc2;

	private JCheckBox flipCheck;
	
	File start;
	
	/* 
	 * This panel is for old biodataserver format.
	 * Old files always cpntains species in the file, we do not need to
	 * care about it.
	 */
	public BioDataServerPanel2() {
		super();

		// get default dir of cytoscape.
		start = CytoscapeInit.getMRUD();
		
		contentPanel = getContentPanel();
		contentPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		textLabel = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();

		setLayout(new java.awt.BorderLayout());

		titlePanel.setLayout(new java.awt.BorderLayout());
		titlePanel.setBackground(Color.gray);

		textLabel.setBackground(Color.gray);
		textLabel.setFont(new Font("Sans Serif", Font.BOLD, 15));
		textLabel.setText("Select Ontology And Annotation File");
		textLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		textLabel.setOpaque(true);

		iconLabel.setBackground(Color.gray);
		if (icon != null)
			iconLabel.setIcon(icon);

		titlePanel.add(textLabel, BorderLayout.CENTER);
		titlePanel.add(iconLabel, BorderLayout.EAST);
		titlePanel.add(separator, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.add(contentPanel, BorderLayout.NORTH);
		add(secondaryPanel, BorderLayout.WEST);

	}

	public void addButtonActionListener( ActionListener l ) {
        selectOboFile.addActionListener( l );
        selectGaFile.addActionListener( l );
    }
	
	// For checking status of the text fields.
	//
	public void addOboTextFieldActionListener( ActionListener l ) {
		oboFileName.addActionListener( l );
	}
	
	public void addGaTextFieldActionListener( ActionListener l ) {
		gaFileName.addActionListener( l );
	}
	
	public void addOboTextFieldDocumentListener( DocumentListener l ) {
		oboFileName.getDocument().addDocumentListener( l );
	}
	
	public void addGaTextFieldDocumentListener( DocumentListener l ) {
		gaFileName.getDocument().addDocumentListener( l );
	}
	
	public void addCheckBoxActionListener( ActionListener l ) {
        flipCheck.addActionListener(l);
    }
	
	private JPanel getContentPanel() {

		JPanel contentPanel1 = new JPanel();
		
		oboFileName = new JTextField(20);
		
		// Button to load the Obo file.  This becomes the command in the
		// action listner.
		selectOboFile = new JButton("Obo");
		
		gaFileName = new JTextField(20);
		
		// Button to load the Gene Assiciation file.
		selectGaFile = new JButton("Gene Association");
		
		panelTitle = new javax.swing.JLabel();
		
		blankSpace = new javax.swing.JLabel();
		
		// This is for check flip or not.
		flipCheck = new JCheckBox();
		flipCheck.setText("Flip DB Object Symbol <-> DB Object Synonym?");
		//flipCheck.setHorizontalTextPosition( JCheckBox.CENTER );
		
		
		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		jPanel3 = new JPanel();
		
		jPanel1.setLayout( new BorderLayout() );
		jPanel2.setLayout( new GridLayout(2, 0) );
		jPanel3.setLayout( new GridLayout(2, 0) );
		//jc1.addChoosableFileFilter( oboFilter );

		anotherBlankSpace = new javax.swing.JLabel();

		contentPanel1.setLayout(new java.awt.BorderLayout());

		panelTitle
				.setText("Please push the button to select ontology and annotation file:");
		jPanel2.add(oboFileName);
		jPanel3.add(selectOboFile);
		jPanel2.add(gaFileName);
		jPanel3.add(selectGaFile);
		
		jPanel1.add(jPanel2, BorderLayout.WEST );
		jPanel1.add(jPanel3, BorderLayout.CENTER );
		jPanel1.add( anotherBlankSpace, BorderLayout.SOUTH );
		
		contentPanel1.add(panelTitle, java.awt.BorderLayout.NORTH);
		contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);
		contentPanel1.add(flipCheck, java.awt.BorderLayout.SOUTH);
		//contentPanel1.add(jc1, java.awt.BorderLayout.CENTER);
		
		return contentPanel1;
	}
	
	public String getOboTextField() {
		return oboFileName.getText();
	}
	
	public String getGaTextField() {
		return gaFileName.getText();
	}

	
	
	public boolean getCheckBoxStatus() {
		return flipCheck.isSelected();
	}
	
	public void setOboFileName( String fileName ) {
		oboFileName.setText( fileName );
	}
	
	public void setGaFileName( String fileName ) {
		gaFileName.setText( fileName );
	}
	
	
	
	
	private ImageIcon getImageIcon() {

		// Icon to be placed in the upper right corner.

		return null;
	}

	

}
