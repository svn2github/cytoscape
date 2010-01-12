/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Dec 7, 2005
 *
 * 
 */
package fr.pasteur.sysbio.rdfscape.context;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;
/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContextManagerPanel  extends AbstractModulePanel {
	ContextManager myContextManager=null;
	JComboBox contextList=null;
	JButton selectContextButton=null;
	JButton addContextButton=null;
	JButton delContextButton=null;
	JButton	saveContextButton=null;

	/**
	 * 
	 */
	public ContextManagerPanel(ContextManager cm) {
		super();
		myContextManager=cm;
		if(myContextManager.getAvailableContextsNames()!=null)
			contextList=new JComboBox(myContextManager.getAvailableContextsNames());
		else contextList=new JComboBox();
		selectContextButton=new JButton("Activate context");
		addContextButton=new JButton("Add context");
		delContextButton=new JButton("Delete context");
		saveContextButton=new JButton("Save current context");
		contextList.setSelectedItem(myContextManager.getActiveContextName());
		
		redLightText="<html>Cannot operate an analysis context. This should not happen! You are experiencing problems here... please report!</html>";
		yellowLightText="<html>You can operate on this default context, but it is advisable to create your own context (or to use an exisiting one)</html>";
		greenLightText= "<html>You are currentyly working on the "+myContextManager.getActiveContextName()+" context</html>";
		myTabText="1) Select an analysis type!";
		myTabTooltip="<html>Create a new analysis context (or use a provided one), select it, and activate it</html>";
		
		ActionListener selectContextListener= new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				myContextManager.setActiveContext((String)contextList.getSelectedItem());
				myContextManager.loadActiveContext();
				
			}
			
		};
		ActionListener saveContextListener= new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				myContextManager.saveActiveContext();
				
			}
			
		};
		
		ActionListener addContextListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newContext=JOptionPane.showInputDialog("Context name: ");
				if(newContext!=null)
					myContextManager.addContext(newContext);
				
			}
			
		};
		
		ActionListener delContextListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] options={"Delete","Cancel"};
				if( JOptionPane.showOptionDialog(
						null,"Deleteing a context will delete all related information. Are you sure ?",
						"Warning",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,
						options,options[1]) ==0)
					if(contextList.getSelectedItem()!=null)
						myContextManager.deleteContext((String)contextList.getSelectedItem());
			}
			
		};
		//System.out.println("C");
		selectContextButton.addActionListener(selectContextListener);
		//System.out.println("1");
		addContextButton.addActionListener(addContextListener);
		//System.out.println("2");
		delContextButton.addActionListener(delContextListener);
		//System.out.println("D");
		saveContextButton.addActionListener(saveContextListener);
		
		add(contextList);
		add(selectContextButton);
		add(addContextButton);
		add(delContextButton);
		add(saveContextButton);
		
		setVisible(true);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	public void updateContextElementsList() {
		remove(contextList);
		remove(selectContextButton);
		remove(addContextButton);
		remove(delContextButton);
		remove(saveContextButton);
		
		System.out.println("Remove");
		if(myContextManager.getAvailableContextsNames()!=null)
			contextList=new JComboBox(myContextManager.getAvailableContextsNames());
		else contextList=new JComboBox();
	
		add(contextList);
		add(selectContextButton);
		add(addContextButton);
		add(delContextButton);
		add(saveContextButton);
		validate();
	}
	

	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToWorkWithAnalysisContexts";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		JLabel tempLabel=new JLabel("<html>Selects an analysis context.<br> You need to activate selected context,<br/> in order for this selection to take effect.<br> (note: the list on the left is only an example and may not reflect what is available on your system)</html>",Utilities.getHelpIcon("selectAnalysisContextButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>Adds a new context. This only init the relative data structure, doesn't activate it.<br > (The current context is not changed)</html>",Utilities.getHelpIcon("addContextButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>Deletes the context currently selected <br >(not the content currently active)</html>",Utilities.getHelpIcon("deleteContextButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>Saves all the settings of the current analysis context</html>",Utilities.getHelpIcon("saveContextButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>Changes the current analysis context to the selected one.</html>",Utilities.getHelpIcon("activateContextButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>" +"An <font color=red> analysis context</font> is grouping of all settings relative to an analysis.<br>"+
				"You can reuse an analysis context or define your own or even share it.<br>"+
				"<b>Note: the <font color=red>current analysis context</font> and the <font color=red>selected analysis context</font> are distinct. </b></html>");
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		return help;
	}

	public String getPanelName() {
		return "Analysis type selection";
	}

	public int getStatusLevel() {
		if(!myContextManager.canOperate()) return 1;
		else {
			if(myContextManager.getActiveContextName().equalsIgnoreCase("default")) return 2;
			else return 3;
		}
	}
	

	
	public void refresh() {
		
		
	}
	

	
}
