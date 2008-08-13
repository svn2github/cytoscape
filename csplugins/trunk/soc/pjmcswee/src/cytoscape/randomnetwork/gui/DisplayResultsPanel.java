/* File: DisplayResultsPanel.java
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

import java.util.*;
import cytoscape.plugin.*;
import cytoscape.layout.algorithms.*;
import cytoscape.*;
import java.awt.*;
import java.io.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

import java.awt.Dimension;

/**
 * RandomizeExistingPanel is used for selecting which randomizing 
 * network model to use.
 */
public class DisplayResultsPanel extends RandomNetworkPanel {

	/**
	 * Save Button
	 */
	private javax.swing.JButton saveButton;
	
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel numNodeLabel;

	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel numEdgeLabel;
	
	private javax.swing.JLabel timeLabel;
	private javax.swing.JLabel timeInfoLabel;
	
	private javax.swing.JLabel networkLabel;
	private javax.swing.JLabel roundLabel;
	
	//Treat this network as directed
	private javax.swing.JCheckBox directedCheckBox;
	
	//
	private javax.swing.JScrollPane scrollPane;
	
	
	
	private String[] columnNames =  {"Metric","Existing Network","Average","Standard Deviation"};;
	
	//1 == randomized existing network
	//0 == generated new random network
	private int mode;
	
	private RandomNetworkAnalyzer rna;

	/**
	 *  Default constructor
	 */
	public DisplayResultsPanel(int pMode, RandomNetworkAnalyzer pRNA)
	{
		
		super(null ); 
		rna = pRNA;
		mode = pMode;
		initComponents();
	}


	/**
	 *  Default constructor
	 */
	public DisplayResultsPanel(int pMode, RandomNetworkPanel pPrevious, RandomNetworkAnalyzer pRNA)
	{
		
		super(pPrevious ); 
		rna = pRNA;
		mode = pMode;
		initComponents();
	}


	/**
	*
	*/
	public String getNextText()
	{
		return new String("Save");
	
	}

	/**
	 *
	 */
	public String getTitle()
	{
		CyNetwork net = rna.getNetwork();
		return new String("Results for " + net.getTitle());
	}
	
	/**
	 *
	 */
	public String getDescription()
	{
		return new String("");
	}
	
	public String getNextTitle()
	{
		return new String("Save");
	}


	/**
	 * Initialize the components
	 */
	private void initComponents() {

		CyNetwork net = rna.getNetwork();




		nodeLabel = new javax.swing.JLabel();
		numNodeLabel = new javax.swing.JLabel();

		edgeLabel = new javax.swing.JLabel();
		numEdgeLabel = new javax.swing.JLabel();
	
		timeLabel = new javax.swing.JLabel();
		timeInfoLabel = new javax.swing.JLabel();


		nodeLabel.setText("Number of nodes: ");
		edgeLabel.setText("Number of edges: ");
		timeLabel.setText("Time elapsed: ");
		numNodeLabel.setText("" + net.getNodeCount());
		numEdgeLabel.setText("" + net.getEdgeCount());


		
	   
		//Create the column headings


		//Set up the Table for displaying 
		DefaultTableModel model = new DefaultTableModel(rna.getData(),columnNames);
		JTable table = new JTable(model) {

		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;   //Disallow the editing of any cell
		}

			public TableCellRenderer getDefaultRenderer(Class columnClass)
			{
				DefaultTableCellRenderer  tcrColumn  =  new DefaultTableCellRenderer();
				tcrColumn.setHorizontalAlignment(JTextField.RIGHT);
				return tcrColumn;
			}

			// Override this method so that it returns the preferred
			// size of the JTable instead of the default fixed size
			public Dimension getPreferredScrollableViewportSize() {
				return getPreferredSize();
        }};

		DefaultTableCellRenderer  tcrColumn  =  new DefaultTableCellRenderer();
		tcrColumn.setHorizontalAlignment(JTextField.RIGHT);
		
		//System.out.println((rna.getData()[0][0]).getClass());
		//System.out.println((rna.getData()[3][3]).getClass());
		table.setDefaultRenderer((rna.getData()[0][0]).getClass(),tcrColumn);
		table.setDefaultRenderer((rna.getData()[3][3]).getClass(),tcrColumn);



		table.getTableHeader().setReorderingAllowed( false );
		table.setGridColor(java.awt.Color.black);
		scrollPane = new JScrollPane(table);
		



		table.setPreferredScrollableViewportSize(new Dimension(500, 100)) ;
		scrollPane.setPreferredSize(new Dimension(500,100));
		scrollPane.setMinimumSize(new Dimension(500,100));
		//scrollPane.
	
	

		setLayout(new GridBagLayout());

		//Setup the titel
		GridBagConstraints c = new GridBagConstraints();
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx = 1;
		//c.weighty = 1;
		add(nodeLabel,c);
	
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx =	1;
		//c.weighty = 1;
		add(numNodeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx = 1;
		//c.weighty = 1;
		add(edgeLabel,c);
	
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx =	1;
		//c.weighty = 1;
		add(numEdgeLabel,c);



		
		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 10;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(scrollPane,c);
		

	}


	/**
	 *
	 */
	public RandomNetworkPanel next()
	{
		
		JFrame frame = (JFrame)getTopLevelAncestor();

		JFileChooser saveFile = new JFileChooser();
		saveFile.showSaveDialog(frame);

		
		File file = saveFile.getSelectedFile();
		JCheckBox saveAll = new javax.swing.JCheckBox();

		
		if(file != null)
		{
			try
			{
				DataOutputStream dout = new DataOutputStream(new FileOutputStream(file));
				Object [][]data = rna.getData();
				
				for(int i = 0; i < data.length; i++)	
				{
					dout.writeBytes(columnNames[i] + "\t\t");
				}

				for(int i = 0; i < data.length; i++)
				{
					dout.writeBytes("\n");
					for(int j = 0; j < data[0].length; j++)
					{
						dout.writeBytes(data[i][j] +"\t\t");
					}
			
				}
				
				/*
				if(saveAll.isSelected())
				{
					double res[][][] = rna.getResults();

					for(int m = 0; m < res[0][0].length; m++)
					{
						dout.writeBytes(data[m][m] + "");
						for(int i = 0; i < res.length; i++)
						{
							for(int j = 0; j < res[0].length; j++)
							{
								dout.writeBytes(res[m][i][j] +"\t");
							}
						}
						dout.writeBytes("\n");
					}
				}
				*/
				
				dout.close();
			}catch(Exception e){e.printStackTrace();}
		}

		//Nothing should change
		return this;
	
	}
	
}