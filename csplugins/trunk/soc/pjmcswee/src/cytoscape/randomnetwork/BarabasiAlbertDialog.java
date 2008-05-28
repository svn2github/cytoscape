

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
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class BarabasiAlbertDialog extends JDialog
{
	int initNumNodes;
	double power;
	int numNodes;
	boolean directed;
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField powerTextField;
	private javax.swing.JTextField initTextField;
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel powerLabel;	
	private javax.swing.JLabel initLabel;	

	public BarabasiAlbertDialog(java.awt.Frame parent)
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
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titleLabel = new javax.swing.JLabel();
		powerLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		initLabel = new javax.swing.JLabel();
		
		
		nodeLabel.setText("Number of Nodes:");
		powerLabel.setText("Set Power:");
		initLabel.setText("Initial Number of Nodes:");
		
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
																.add(layout.createSequentialGroup()
																		 .add(powerLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE) 
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		 .add(powerTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE) )
																.add(layout.createSequentialGroup()
																		 .add(initLabel,
																			  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			  20, Short.MAX_VALUE)
																		 // .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED) 
																		  .add(initTextField,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      10, Short.MAX_VALUE))		                                                               //  .add(selectButton))
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
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												      .add(powerLabel)
												      .add(powerTextField))
												 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)												 
													  .add(initLabel)
												      .add(initTextField))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          3, Short.MAX_VALUE)
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
		String powerString = powerTextField.getText();
		String initString = initTextField.getText();
		
		numNodes = Integer.parseInt(numNodeString);
		power = Double.parseDouble(powerString);
		initNumNodes = Integer.parseInt(initString);
	
		BarabasiAlbertModel bam = new BarabasiAlbertModel(numNodes,directed,initNumNodes,power);
		bam.Generate();

		//status = false;
		this.dispose();
	}
		
			
					
	
	

}