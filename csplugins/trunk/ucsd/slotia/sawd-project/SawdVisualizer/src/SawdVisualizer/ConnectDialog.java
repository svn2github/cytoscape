package SawdVisualizer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ConnectDialog extends JDialog
{
	public ConnectDialog(Window owner)
	{
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("SawdVisualizer: Connect");
		setResizable(false);

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jTextField2 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1.setText("Server:");

		jLabel2.setText("Port:");

		jTextField1.setText("localhost");

		jTextField2.setText("2626");

		jButton1.setText("Connect");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
		    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		    .add(layout.createSequentialGroup()
			.addContainerGap()
			.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			    .add(jLabel1)
			    .add(jLabel2))
			.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
			.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
			    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
				.add(jButton1)
				.add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
			.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
		    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		    .add(layout.createSequentialGroup()
			.addContainerGap()
			.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
			    .add(jLabel1)
			    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
			.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
			    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
			    .add(jLabel2))
			.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
			.add(jButton1)
			.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		pack();	
	}

	public void addActionListener(ActionListener l)
	{
		jButton1.addActionListener(l);
	}

	public String getServer()
	{
		return jTextField1.getText();
	}

	public int getPort()
	{
		return Integer.parseInt(jTextField2.getText());
	}

    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
}
