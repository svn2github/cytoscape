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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

public class ErdosRenyiDialog extends JDialog  
							  implements ActionListener 
{


	int numNodes;   //number of nodes 
	double probability;   
	int numEdges;
	boolean directed;
	boolean allowSelfEdge;
	
	
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField probabilityTextField;
	private javax.swing.JTextField edgeTextField;
	private javax.swing.JRadioButton directedRadioButton;
	private javax.swing.JRadioButton selfEdgeRadioButton;
	
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel probabilityLabel;	
	private javax.swing.JLabel edgeLabel;	
	//private javax.swing.JLabel directedLabel;
//	private javax.swing.JLabel selfEdgeLabel;
	private javax.swing.ButtonGroup group;
	private javax.swing.JRadioButton gnp;	
	private javax.swing.JRadioButton gnm;
	
	private javax.swing.JLabel gnpExplain;
	private javax.swing.JLabel gnmExplain;
	
	public ErdosRenyiDialog(java.awt.Frame parent)
	{
		super(parent, true);
		initComponents();
		pack();
	}
	private void initComponents() 
	{		
		nodeTextField = new javax.swing.JTextField();
		probabilityTextField = new javax.swing.JTextField();	
		edgeTextField = new javax.swing.JTextField();
		directedRadioButton = new javax.swing.JRadioButton();
		selfEdgeRadioButton = new javax.swing.JRadioButton();
		
		group = new javax.swing.ButtonGroup();
		gnm = new javax.swing.JRadioButton();
		gnp = new javax.swing.JRadioButton();
		gnpExplain = new javax.swing.JLabel();
		gnmExplain = new javax.swing.JLabel();
		
		
		//<font color=#C0C0C0>
		gnmExplain.setText("<html><font size=2 face=Verdana>Uniformly generate a random <br> graph with n nodes and m edges.</font></html>");
		gnpExplain.setText("<html><font size=2 face=Verdana>Generate a random graph with n <br> nodes and each edge has probability p to be included.</font></html>");

		
		gnmExplain.setOpaque(true);
		gnpExplain.setOpaque(true);
		gnp.setText("G(n,p)");
		gnm.setText("G(n,m)");
		gnp.setSelected(true);
		group.add(gnm);
		group.add(gnp);
		
	
		directedRadioButton.setText("Undirected");
		selfEdgeRadioButton.setText("Allow Reflexive Edges (u,u)");
		
		
		
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titleLabel = new javax.swing.JLabel();
		probabilityLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();
		//directedLabel = new javax.swing.JLabel();
//		selfEdgeLabel = new javax.swing.JLabel();
		edgeTextField.setEnabled(false);
		edgeTextField.setBackground(Color.LIGHT_GRAY);
		gnm.addActionListener(this);
		gnp.addActionListener(this);
		
		nodeLabel.setText("Number of Nodes (n):");
		edgeLabel.setText("Number of Edges (m):");
		probabilityLabel.setText("Edge Probability (p):   ");
	//	directedLabel.setText("Directed");
	//	selfEdgeLabel.setText("Allow reflexive edges" );
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Erdos-Renyi Model");
		


		
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
																     .add(gnm,
																	      1,//org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		  1, 80)
																	 .add(gnmExplain,
																	      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																	      10, Short.MAX_VALUE))
																.add(layout.createSequentialGroup()			  
																  .add(gnp,
																	   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                               10, 70)
																  .addPreferredGap(1)
																  .add(gnpExplain,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		  10, Short.MAX_VALUE))
																.add(layout.createSequentialGroup()
																		 .add(nodeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, 150) 
		                                                                 //.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(nodeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))
																.add(layout.createSequentialGroup()
																		 .add(probabilityLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, 150) 
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(probabilityTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE) )
																.add(layout.createSequentialGroup()
																		 .add(edgeLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20,150)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(edgeTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))	
																.add(layout.createSequentialGroup()
																		 //.add(directedLabel,
																		//	  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		//	  20, Short.MAX_VALUE)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(directedRadioButton,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))
																	.add(layout.createSequentialGroup()
																		// .add(selfEdgeLabel,
																		//	  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		//	  20, Short.MAX_VALUE)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(selfEdgeRadioButton,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))				  								  				  																																			   	                                                               //  .add(selectButton))
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
												 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
/*
												.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												     .add(gnm)
													 .add(gnmExplain))
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
													  .add(gnp)
													  .add(gnpExplain))*/

												//.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
											
													 .add(gnm)
													  .add(gnmExplain))
													  
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)	 
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
													 .add(gnp)
													  .add(gnpExplain))
													  													
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												     .add(nodeLabel)
												     .add(nodeTextField))
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												      .add(probabilityLabel)
												      .add(probabilityTextField))
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  .add(edgeLabel)
												      .add(edgeTextField))
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  //.add(directedLabel)
												      .add(directedRadioButton))
												.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													//  .add(selfEdgeLabel)
												      .add(selfEdgeRadioButton))												 	  
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(cancelButton).add(runButton))
		                                         .addContainerGap()));
		pack();
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
		
		try
		{
			numNodes = Integer.parseInt(numNodeString);
		}catch(Exception e)
		{
			nodeLabel.setForeground(java.awt.Color.RED);
			pack();
			return;
		}
		
		nodeLabel.setForeground(java.awt.Color.BLACK);
		
		
		
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
		
		
		System.out.println(directed);
		
		if(gnm.isSelected())
		{
			String edgeString = edgeTextField.getText();
			
			try
			{
				numEdges = Integer.parseInt(edgeString);
			}catch(Exception e)
			{
			probabilityLabel.setForeground(java.awt.Color.BLACK);
				edgeLabel.setForeground(java.awt.Color.RED);
				pack();
				return;
			}
			
			
			ErdosRenyiModel erm = new ErdosRenyiModel(numNodes, numEdges, allowSelfEdge, directed);
			erm.Generate();
		}
		else
		{
			String probabilityString = probabilityTextField.getText();
		
			try
			{
				probability = Double.parseDouble(probabilityString);
			}catch(Exception e)
			{
				probabilityLabel.setForeground(java.awt.Color.RED);
				edgeLabel.setForeground(java.awt.Color.BLACK);
				pack();
				return;
			}
		
		
		
			
			ErdosRenyiModel erm = new ErdosRenyiModel(numNodes, allowSelfEdge, !directed, probability);
			erm.Generate();
		}
		
		
				
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(0);

		
		
		this.dispose();
	}
	
	/*
	
	*/
	public void actionPerformed(ActionEvent e) 
	{
		
	
		if("G(n,m)".equals(e.getActionCommand()))
		{
			probabilityTextField.setEnabled(false);
			probabilityTextField.setBackground(Color.LIGHT_GRAY);
			edgeTextField.setBackground(Color.WHITE);
			edgeTextField.setEnabled(true);
		} 
		else if("G(n,p)".equals(e.getActionCommand()))
		{
			probabilityTextField.setEnabled(true);
			probabilityTextField.setBackground(Color.WHITE);
			edgeTextField.setBackground(Color.LIGHT_GRAY);
			edgeTextField.setEnabled(false);		
		}
		
		pack();
	} 
			
					
	
	

}