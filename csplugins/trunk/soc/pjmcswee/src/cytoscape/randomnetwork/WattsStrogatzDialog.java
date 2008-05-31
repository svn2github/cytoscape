

/*
File: BarabasiAlbertDialog

References:




Author: Patrick J. McSweeney
Creation Date: 5/27/08

*/

package cytoscape.randomnetwork;

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import giny.view.*;

import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class WattsStrogatzDialog extends JDialog
{
	int initNumNodes;
	double power;
	int numNodes;
	boolean directed;
	int edgesToAdd;
	boolean allowSelfEdge;
	
	
	
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField powerTextField;
	private javax.swing.JTextField initTextField;
	private javax.swing.JTextField edgeTextField;
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel directedLabel;
	private javax.swing.JLabel selfEdgeLabel;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel powerLabel;	
	private javax.swing.JLabel initLabel;	
	private javax.swing.JLabel edgeLabel;
	private javax.swing.JRadioButton directedRadioButton;
	private javax.swing.JRadioButton selfEdgeRadioButton;
	public WattsStrogatzDialog(java.awt.Frame parent)
	{
		super(parent, true);
		initComponents();
		pack();
	}
	private void initComponents() 
	{		
		nodeTextField = new javax.swing.JTextField();
		powerTextField = new javax.swing.JTextField();	
		initTextField = new javax.swing.JTextField();
		edgeTextField = new javax.swing.JTextField();
		
		directedRadioButton = new javax.swing.JRadioButton();
		selfEdgeRadioButton = new javax.swing.JRadioButton();
		
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		
		titleLabel = new javax.swing.JLabel();
		powerLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		initLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();
		directedLabel = new javax.swing.JLabel();
		selfEdgeLabel = new javax.swing.JLabel();
		
		
		nodeLabel.setText("Number of Nodes:");
		powerLabel.setText("Set Power:");
		initLabel.setText("Initial Number of Nodes:");
		edgeLabel.setText("Minimum Edges per node:");
		directedLabel.setText("Directed");
		selfEdgeLabel.setText("Allow reflexive edge");
		
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Barabasi-Albert Model");
		


		
		runButton.setText("Generate");
		runButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					runButtonActionPerformed(evt);
				}
			});



		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelButtonActionPerformed(evt);
				}
			});
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);		
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(titleLabel,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                           350,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(layout.createSequentialGroup()
																		 .add(nodeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE) 
		                                                                 //.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(nodeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))
															/*	.add(layout.createSequentialGroup()
																		 .add(powerLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE) 
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(powerTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE) )*/
																.add(layout.createSequentialGroup()
																		 .add(initLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(initTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))
																.add(layout.createSequentialGroup()
																		 .add(edgeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(edgeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))																				  
																.add(layout.createSequentialGroup()
																	 .add(directedLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE)
																  	.add(directedRadioButton))
																	
															/*	.add(layout.createSequentialGroup()
																	 .add(selfEdgeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE)
																  	.add(selfEdgeRadioButton))		      */
															.add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(runButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(cancelButton)))
		                                           .addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel).add(8, 8, 8)
		                                     
		                                         .add(7, 7, 7)
		                                      /*   .add(radioButtonPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
											*/
											
											
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												     .add(nodeLabel)
												     .add(nodeTextField))
												/* .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												      .add(powerLabel)
												      .add(powerTextField))*/
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  .add(initLabel)
												      .add(initTextField))
											      .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  .add(edgeLabel)
												      .add(edgeTextField))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)
												.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													.add(directedLabel)
													.add(directedRadioButton))
												/*.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													.add(selfEdgeLabel)
													.add(selfEdgeRadioButton))*/
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(cancelButton).add(runButton))
		                                         .addContainerGap()));
		pack();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
	
		//status = false;
		this.dispose();
	}
	
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{	
		String numNodeString = nodeTextField.getText();
		//String powerString = powerTextField.getText();
		String initString = initTextField.getText();
		String edgeString = edgeTextField.getText();
		numNodes = Integer.parseInt(numNodeString);
		//power = Double.parseDouble(powerString);
		initNumNodes = Integer.parseInt(initString);
		edgesToAdd = Integer.parseInt(edgeString);
		
		directed = false;
		if(directedRadioButton.isSelected())
		{
			directed = true;
		}
	
		allowSelfEdge = false;
		if(selfEdgeRadioButton.isSelected())
		{
			allowSelfEdge = true;
		}
	
	
	
		BarabasiAlbertModel bam = new BarabasiAlbertModel(numNodes,allowSelfEdge,directed,initNumNodes,power,edgesToAdd);
		bam.Generate();
		
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(0);

		//status = false;
		this.dispose();
	}
		
			
					
	
	

}
