/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.namespacemanagement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;



/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NameSpaceManagerPanel extends AbstractModulePanel{
	NamespaceManager nameSpaceManager=null;
	JTable nameSpaceTable=null;
	/**
	 * 
	 */
	public NameSpaceManagerPanel(NamespaceManager ns) {
		super();
		nameSpaceManager=ns;
		nameSpaceTable=new JTable(nameSpaceManager);
		
		redLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		yellowLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		greenLightText= "<html>You can always edit namespace settings</html>";
		myTabText="Color and visilibily by namespace";
		myTabTooltip="<html>Select which color associate to which namespaces, prefixes, and whether resources should bevisible or not (selection by namespace)</html>";
		
		JScrollPane myScrollPane=new JScrollPane(nameSpaceTable);
		setLayout(new BorderLayout());
		
		TableColumn colorColumn=nameSpaceTable.getColumnModel().getColumn(2);
		JComboBox comboBox=new JComboBox();
		comboBox.setEditable(false);
		Color[] colors=nameSpaceManager.getPossibleNameSpaceColors();
		for (int i = 0; i < colors.length; i++) {
			comboBox.addItem(colors[i]);
		}
		
		
		comboBox.setRenderer(new ColorListRenderer());
		
		colorColumn.setCellEditor(new DefaultCellEditor(comboBox));
		colorColumn.setCellRenderer(new ColorRenderer());
		
		ActionListener addListener =new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String myNameSpace=JOptionPane.showInputDialog("Insert name space: ");
				nameSpaceManager.addNewNameSpaceToMemory(myNameSpace);
			}
			
		};
		ActionListener delListener =new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(nameSpaceTable.getSelectedRowCount()>1 || nameSpaceTable.getSelectedRowCount()==0) {
					JOptionPane.showMessageDialog(null,"please select only one name space");
				}
				else {
					String nameSpaceToDelete=(String) nameSpaceTable.getValueAt(nameSpaceTable.getSelectedRow(),0);
					nameSpaceManager.removeNameSpace(nameSpaceToDelete);
				}
				
			}
			
		};
		JButton addButton=new JButton("Add namespace");
		JButton delButton=new JButton("Remove namespace");
		JPanel buttonPanel=new JPanel();
		addButton.addActionListener(addListener);
		delButton.addActionListener(delListener);
		
		buttonPanel.add(addButton);
		buttonPanel.add(new JLabel("Warning"));
		buttonPanel.add(delButton);
		add(myScrollPane,BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
		
	}
	public class ColorRenderer extends JLabel implements TableCellRenderer {
		public ColorRenderer() {
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table, Object color,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
					
				setBackground((Color)nameSpaceManager.getValueAt(row,column));
			return this;

		}
	}
	class ColorListRenderer extends JLabel implements ListCellRenderer {
	     public ColorListRenderer() {
	         setOpaque(true);
	     }
	     public Component getListCellRendererComponent(
	         JList list, Object value,
	         int index, boolean isSelected, boolean cellHasFocus) {
	     		//System.out.println("Attempt at rendering..."+(Color)value);
	     		setText("choose this");
	     		setBackground((Color)value);
	     		return this;
	     }
	 }
	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/SelectingNamespaces";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		JLabel tempLabel=new JLabel(
				"<html>This is the list of namespaces defined in the system. They can be either loaded from a configuration file or can be derived from ontologies." +
				" In the second case color and other attributes have default values. You can alter this list with the " +
				"<b>Add namespace</b> and <b>Remove namespace</b> buttons</html>",
				Utilities.getHelpIcon("nameSpaceList.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>These are prefixes for namespaces. They are used in sevarl visualization and query panels in RDFScape. You can change a prefix simply by editing its fields. If two prefixes have the same string, one of the two will be changed into a default value.</html>",
				Utilities.getHelpIcon("nameSpacePrefixSelection.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>These are colors associated to namespaces. Select a color field to edit this association.</html>",
				Utilities.getHelpIcon("nameSpaceColorSelection.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		
		
		tempLabel=new JLabel(
				"<html>Select which namespace should be visible or not. All resources of namespaces not visible will not appear in browsing ontologies or in the result of queries.</html>",
				Utilities.getHelpIcon("nameSpaceSelection.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>Add a new namespace URL (this is either http:// for web accessible ontologies or <br> file:// for ontologies on the local filesystem) </html>",
				Utilities.getHelpIcon("addNameSpaceButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>Remove the selected namespace.</html>",
				Utilities.getHelpIcon("removeNameSpaceButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
	
		
		return help;
	}

	public String getPanelName() {
		return "Namespace settings";
	}

	public int getStatusLevel() {
		return 3;
	}
	/**
	 * 
	 */

	
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
}
