

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
	int degree;
	double beta;
	int numNodes;
	boolean directed;
	boolean allowSelfEdge;
	
	
	
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField betaTextField;
	private javax.swing.JTextField degreeTextField;

	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel betaLabel;	
	private javax.swing.JLabel degreeLabel;
	
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
		betaTextField = new javax.swing.JTextField();	
		degreeTextField = new javax.swing.JTextField();
		
		directedRadioButton = new javax.swing.JRadioButton();
		selfEdgeRadioButton = new javax.swing.JRadioButton();
		
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		
		titleLabel = new javax.swing.JLabel();
		
		nodeLabel = new javax.swing.JLabel();
		degreeLabel = new javax.swing.JLabel();
		betaLabel = new javax.swing.JLabel();

		
		
		nodeLabel.setText("Number of Nodes:");
		betaLabel.setText("<html> &#x3B2; :</html>");

		degreeLabel.setText("Node Degree:");

		selfEdgeRadioButton.setText("Allow reflexive Edges (u,u)");
		directedRadioButton.setText("Undirected");
		
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
																			  20,170) 
		                                                                 //.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(nodeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, 50))
																		.add(layout.createSequentialGroup()
																		 .add(betaLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, 170)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(betaTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, 50))
																.add(layout.createSequentialGroup()
																		 .add(degreeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20,170)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(degreeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, 50))																				  
																.add(layout.createSequentialGroup()
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
													  .add(betaLabel)
												      .add(betaTextField))
											      .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  .add(degreeLabel)
												      .add(degreeTextField))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)
												.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
												
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
		String betaString = betaTextField.getText();
		String degreeString = degreeTextField.getText();

try{
		numNodes = Integer.parseInt(numNodeString);
		}
catch(Exception e)
{
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.RED);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return;
		}


	try
	{
		beta = Double.parseDouble(betaString);
		}catch(Exception e)
		{
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.RED);
			return;
		}

		try
		{
			degree = Integer.parseInt(degreeString);
			}catch(Exception e)
		{
			degreeLabel.setForeground(java.awt.Color.RED);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return;
		}
		
		
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
	
	
	
		WattsStrogatzModel wsm = new WattsStrogatzModel(numNodes,allowSelfEdge,!directed,beta,degree);
		wsm.Generate();
		
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(0);

		//status = false;
		this.dispose();
	}
		
			
					
	
	

}
