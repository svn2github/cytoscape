/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingRule {
	String patternText=null;
	String uriVar=null;
	String idVar=null;
	String name=null;
	MappingRulePanel myPanel=null;
	/**
	 * 
	 */
	public MappingRule() {
		super();
		
		
	}

	/**
	 * @return
	 */
	public MappingRulePanel getPanel() {
		if(myPanel==null) myPanel=new MappingRulePanel(this);
		return myPanel;
	}
	
	private class MappingRulePanel extends JPanel {
		JTextArea myTextArea=null;
		MappingRule myRule=null;
		JTextField myIDVar=null;
		JTextField myURIVar=null;
		/**
		 * @param rule
		 */
		public MappingRulePanel(MappingRule rule) {
			myRule=rule;
			setLayout(new BorderLayout());
			myTextArea=new JTextArea(patternText);
			myIDVar=new JTextField(idVar);
			myURIVar=new JTextField(uriVar);
			JButton commitButton=new JButton("Commit");
			
			commitButton.addActionListener(new CommitListener());
			JPanel extPanel=new JPanel(new GridLayout(1,5));
			extPanel.add(new JLabel("URI:"));
			extPanel.add(myURIVar);
			extPanel.add(new JLabel("ID:"));
			extPanel.add(myIDVar);
			extPanel.add(commitButton);
			add(myTextArea,BorderLayout.CENTER);
			add(extPanel,BorderLayout.SOUTH);
			
			
		}
		public class CommitListener implements ActionListener {
			public void actionPerformed(ActionEvent arg0) {
				myRule.idVar=myIDVar.getText();
				myRule.uriVar=myURIVar.getText();
				myRule.patternText=myTextArea.getText();
			
			}
		}
		
	}

	/**
	 * @return
	 */
	public String getRuleString() {
		return patternText;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getURI() {
		return uriVar;
	}
	
	public String getID() {
		return idVar;
	}

	/**
	 * @return
	 */
	public boolean isValid() {
		if(name!=null && patternText!=null && uriVar!=null && idVar!=null) return true;
		else return false;
	}
}
