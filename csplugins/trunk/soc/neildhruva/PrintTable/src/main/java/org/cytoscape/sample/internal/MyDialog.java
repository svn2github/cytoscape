package org.cytoscape.sample.internal;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MyDialog extends JDialog{
	
	private static final long serialVersionUID = -265391392725279259L;
	private JTable table;
	private static boolean dialogAlreadyExists = false;
	
	public MyDialog(JTable table){
		
		if(!dialogAlreadyExists){
		
			this.table = table;
		
			createDialog();
			addTable();
		
			this.pack();
			this.setVisible(true);
			
			dialogAlreadyExists=true;
		}
		
		this.addWindowListener(new WindowListener() {
		
			@Override
			public void windowClosing(WindowEvent e) {
				dialogAlreadyExists = false;
			}			
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowOpened(WindowEvent e) {}				
		});
		

	}
	
	/**
	 * Creates the JDialog UI
	 */
	public void createDialog(){
		
		this.setTitle("Table View");
		this.setBounds(200, 200, 500, 500);
	}
	
	/**
	 * Adds the JTable to the JDialog
	 */
	public void addTable(){
		
		this.add(new JScrollPane(table), BorderLayout.NORTH);
	}
	
	public void windowClosed(WindowEvent e)
    {
    	System.out.println("jdialog window closed event received");
    }
}
