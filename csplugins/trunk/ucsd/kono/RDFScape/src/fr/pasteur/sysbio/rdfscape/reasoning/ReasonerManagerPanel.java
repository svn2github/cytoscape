/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.reasoning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;
import fr.pasteur.sysbio.rdfscape.knowledge.InfRuleObject;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReasonerManagerPanel extends AbstractModulePanel implements ListSelectionListener{
	private ReasonerManager reasonerManager=null;
	private JList rulesList=null;
	private JTextArea ruleBody=null;
	private JButton addButton=null;
	private JButton commitButton=null;
	private JButton deleteButton=null;
	private JButton reasonButton=null;
	private JComboBox enginesBox=null;
	
	
	private JPanel reasonOptionPanel=null;
	private JPanel ruleOptionPanel=null;
	
	
	private JPanel buttonPanel=null;
	private JPanel selectionPanel=null;
	//private JComboBox ruleTypeButton=null;
	private JButton saveButton=null;
	
	private String[][] optionsToShow;
	private String[] defaultOptions;
	//private String[] selectedOptions; // TODO this should be in reasonerManager
	private JComboBox[] optionBoxes;
	
	private String[][] ruleOptionsToShow;
	private String[] ruleDefaultOptions;
	private String[] ruleOptionParams;
	private JComboBox[] ruleOptionBoxes;
	


	public ReasonerManagerPanel(ReasonerManager rs) {
		super();
		
		redLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		yellowLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		greenLightText= "<html>You can always edit reasoning settings</html>";
		myTabText="Resoner and inference rules";
		myTabTooltip="<html>Configure reasoneings options and inference rules</html>";
		
		reasonerManager=rs;
		setLayout(new BorderLayout());
		rulesList=new JList(reasonerManager);
		rulesList.setCellRenderer(new RulesListCellRenderer());
		rulesList.addListSelectionListener(this);
		ruleBody=new JTextArea(80,10);
		
		
		/**
		 * Rule management buttons
		 */
		addButton=new JButton("Add");
		commitButton=new JButton("Commit");
		deleteButton=new JButton("Delete");
		reasonButton=new JButton("Reason");

		
		/**
		 * Engine Box
		 */
		ActionListener enginesListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reasonerManager.setActiveEngine((String) enginesBox.getSelectedItem());
				
			}
		};
		enginesBox=new JComboBox(reasonerManager.getEngines());
		enginesBox.addActionListener(enginesListener);
		enginesBox.setSelectedItem(reasonerManager.getActiveEngine());
		enginesBox.setEnabled(true);			
		
		selectionPanel=new JPanel();
		selectionPanel.add(enginesBox);
		selectionPanel.add(makeReasonOptionPanel());
		
		
		addButton.addActionListener(new AddButtonListener());
		commitButton.addActionListener(new CommitButtonListener());
		deleteButton.addActionListener(new DeleteButtonListener());
		reasonButton.addActionListener(new ReasonButtonListener());
		
		
		
		JPanel centralPanel=new JPanel();
		centralPanel=new JPanel();
		centralPanel.setLayout(new BorderLayout());
		centralPanel.add(ruleBody,BorderLayout.CENTER);
		
		buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		//buttonPanel.add(ruleTypeButton);
		buttonPanel.add(addButton);
		buttonPanel.add(commitButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(makeRuleOptionPanel());
		
		
		
		//buttonPanel.add(saveButton);
		centralPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		JScrollPane myScrollPane=new JScrollPane(rulesList);
		myScrollPane.setPreferredSize(new Dimension(150,200));
		add(myScrollPane,BorderLayout.WEST);
		add(centralPanel,BorderLayout.CENTER);
		add(reasonButton,BorderLayout.SOUTH);
		add(selectionPanel,BorderLayout.NORTH);
		
		rulesList.addMouseListener(new CheckBoxSelectionListener());
		
		checkButtonsEnabled();
	}
	
	ActionListener multiOptionsListener=new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			String[] tempSelection=new String[optionBoxes.length];
			for (int i = 0; i < optionBoxes.length; i++) {
				tempSelection[i]=(String) optionBoxes[i].getSelectedItem();
			}
			reasonerManager.setSelectedOptions(tempSelection);
	}
	
};
	private void checkButtonsEnabled() {
		if(reasonerManager.rulesEnabled()) {
			ruleBody.setEditable(true);
			addButton.setEnabled(true);
			commitButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
		else {
			ruleBody.setEditable(false);
			addButton.setEnabled(false);
			commitButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
		
	}
	
	private JPanel makeReasonOptionPanel() {
		reasonOptionPanel=new JPanel();
		
		if(reasonerManager.hasReasoningOptions()) {
			optionsToShow=reasonerManager.getOptions();
			defaultOptions=reasonerManager.getDefaultOptions();
			if(optionsToShow==null) return reasonOptionPanel;
			optionBoxes=new JComboBox[optionsToShow.length];
			for (int i = 0; i < optionBoxes.length; i++) {
				optionBoxes[i]=new JComboBox(optionsToShow[i]);
				optionBoxes[i].setSelectedItem(defaultOptions[i]);
				optionBoxes[i].addActionListener(multiOptionsListener);
				reasonOptionPanel.add(optionBoxes[i]);
			}
			
		}
		return reasonOptionPanel;
		
	}
	private JPanel makeRuleOptionPanel() {
		ruleOptionPanel=new JPanel();
		if(reasonerManager.rulesEnabled()) {
			ruleOptionsToShow=reasonerManager.getRuleOptions();
			ruleDefaultOptions=reasonerManager.getRuleDefaultOptions();
			ruleOptionParams =reasonerManager.getRuleOptionParams();
			ruleOptionBoxes=new JComboBox[ruleOptionsToShow.length];
			for (int i = 0; i < ruleOptionBoxes.length; i++) {
				ruleOptionBoxes[i]=new JComboBox(ruleOptionsToShow[i]);
				ruleOptionBoxes[i].setSelectedItem(ruleDefaultOptions[i]);
				//ruleOptionBoxes[i].addActionListener(multiRuleOptionsListener);
				ruleOptionPanel.add(ruleOptionBoxes[i]);
			}
		}
		return ruleOptionPanel;
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent arg0) {
		int selectedIndex=rulesList.getSelectedIndex();
		if(selectedIndex>=0) {
			InfRuleObject myRule=(InfRuleObject)reasonerManager.getElementAt(selectedIndex);
			ruleBody.setText(myRule.getRule());
			
			for (int i = 0; i < ruleOptionBoxes.length; i++) {
				ruleOptionBoxes[i].setSelectedItem(myRule.getParam(ruleOptionParams[i]));
				System.out.println( ruleOptionParams[i]+"<-"+myRule.getParam(ruleOptionParams[i]));
			}
			
			//reasonerManager.setSelectedOptions(selectedOptions);

		}
		else {
			ruleBody.setText("");
			for (int i = 0; i < ruleOptionBoxes.length; i++) {
				optionBoxes[i].setSelectedItem(ruleDefaultOptions[i]);
			}
			
		}
		rulesList.repaint();
		
	}

	
	/*
	 * Allow select/unselect of single rules
	 */
	public class CheckBoxSelectionListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getX() < 20) {
				System.out.println("GOT IT NOW!");
				int index = rulesList.getSelectedIndex();
				if (index < 0)
					return;
				InfRuleObject myRuleObject = (InfRuleObject)rulesList.getModel().
					getElementAt(index);
				System.out.println("Index: "+index+" "+myRuleObject.getName());
				myRuleObject.invertSelected();
				rulesList.repaint();
				
			}
			
		}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}	
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
		
	}
	
	/*
	 * Rendering of rule names as checkboxes. Value=isActive
	 */
	class RulesListCellRenderer  implements ListCellRenderer {
		Hashtable cells;
		public RulesListCellRenderer() {
			cells=new Hashtable();
			setOpaque(true);
		}
		public Component getListCellRendererComponent(
	         JList list, Object value,
	         int index, boolean isSelected, boolean cellHasFocus) {
				if(cells.get(value)==null) {
					cells.put(value, new JCheckBox(((InfRuleObject)value).getName(),((InfRuleObject)value).isActive()));
					((JCheckBox)(cells.get(value))).setEnabled(true);
					//((JCheckBox)(cells.get(value))).addItemListener(new RuleCheckboxListener((InfRuleObject)value));
				}
				((JCheckBox)(cells.get(value))).setOpaque(true);
				((JCheckBox)(cells.get(value))).setBackground(Color.WHITE);
				if(cellHasFocus) ((JCheckBox)(cells.get(value))).setBackground(Color.CYAN);
				if(isSelected) ((JCheckBox)(cells.get(value))).setBackground(Color.BLUE);
				((JCheckBox)(cells.get(value))).setText(((InfRuleObject)value).getName());
				((JCheckBox)(cells.get(value))).setSelected(((InfRuleObject)value).isActive());
	     		
	     		return ((JCheckBox)(cells.get(value)));
	   }
		
		
	}
	
	
	class AddButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			InfRuleObject newObject=new InfRuleObject("[new:]");
			for (int i = 0; i < ruleOptionBoxes.length; i++) {
				newObject.setParam(ruleOptionParams[i],ruleDefaultOptions[i]);
			}
			
			reasonerManager.addRuleObject(newObject);
			rulesList.setSelectedIndex(reasonerManager.getIndexOfRuleObject(newObject));
			
		}
	
	}
	
	class DeleteButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			InfRuleObject toDelete=(InfRuleObject) rulesList.getSelectedValue();
			int selectedIndex=rulesList.getSelectedIndex();
			rulesList.removeSelectionInterval(selectedIndex,selectedIndex);
			reasonerManager.deleteRule(toDelete);
		}
		
	}

	class CommitButtonListener implements ActionListener {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			int index=rulesList.getSelectedIndex();
			if(index>=0) {
				String text=ruleBody.getText();
				String[][] myParams=new String[ruleDefaultOptions.length][2];
				for (int i = 0; i < myParams.length; i++) {
					myParams[i][0]=ruleOptionParams[i];
					myParams[i][1]=(String)ruleOptionBoxes[i].getSelectedObjects()[0];
				}
				reasonerManager.updateRule(index,text,myParams);
			}
		}
	}
	
	class ReasonButtonListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			reasonerManager.forwardRules();
			reasonerManager.reason();
			
			
		}

	
	}
	
	public void updateAfterEngineChange() {
		selectionPanel.remove(reasonOptionPanel);
		selectionPanel.add(makeReasonOptionPanel());
		
		buttonPanel.remove(ruleOptionPanel);
		buttonPanel.add(makeRuleOptionPanel());
		enginesBox.setSelectedItem(reasonerManager.getActiveEngine());
		checkButtonsEnabled();
		validate();
		
	}

	

	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToUseInference";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		JLabel tempLabel=new JLabel("" +
				"<html>Select the \"knowledge engine\" you want to use (the only choice available at this time is Jena) and the level of reasoining that you wish to be performed.<br>" +
				"Note that custom inference rules are added to this step.<br>" +
				"Current options are: <ul>" +
				"<li><b>None</b> No entailments are computed (apart from rules, if selected)</li>" +
				"<li><b>RDFS-1</b> based on the Jena rule engine (see help on the web for details)</li>" +
				"<li><b>RDFS-2</b> as above</li>" +
				"<li><b>OWL-1</b> as above</li>" +
				"<li><b>OWL-2</li> as above</b>" +
				"<li><b>Pellet-fast</b> Use pellet. This attempts to compute a subset of entailments. In this case only the subsumption computation from Pellet is used, and its result (classes and properties) is added to the original model and passed to an RDFS model (toghether with custom inference rules) for transitive types resolution</li>" +
				"<li><b>Pellet</b> Use pellet</li>" +
				"<li><b>DIG</b> Connects to an external DIG reasoner</li></ul>" +
				"Notes: for Jena based models, custom inference rules are computed at same time as standard inference rules.<br>" +
				"For Pellet and DIG, custom rules are applied <i>on top</i> of the total knowledge entailed by the previous reasoning step.<br>" +
				"<font color=red> It is advisable to use Pellet model only for very small ontologies, or to use Pellet-fast</font><br>" +
				"The perfomance of <b>Pellet-fast</b> is similar to that of Pellet used command line. <b>Pellet</b> is considerably slower.</html>",
				Utilities.getHelpIcon("reasonerAndLevel.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("" +
				"<html>Reasoning (of standard enatilments and inference rules) is issued through this button. This must be selected after changes in reasoning settings in order from them to take place. <br>" +
				"Note that after loading ontologies, the reasoning step is automatically computed with settings from the current analysis context.</html>",
				Utilities.getHelpIcon("reasonButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				
				Utilities.getHelpIcon("rulePanel.png"),SwingConstants.CENTER);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("" +
				"<html>The list of rules in the current analysis context is presented here. Each rule can be activated or not. Only activated rules are taken into consoderation in the reasoning step." +
				"The syntax of rules follows that of Jena. See <b>Help on the web</b> for more details.</html>"
		);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		tempLabel=new JLabel("" +
				"<html>Selecting a rule and selecting <b>Delete</b> deletes the rule. To add a rule select <b>Add</b>. At this point an empty rule panel is added to the list." +
				"Go to the rule panel and enter the text ofg the rule (this includes the name, that will be parsed as a rule index).<br>" +
				"In order for the rule to be recorder, select <b>Commit</b><br>" +
				"<font color=red>Note: <b>Add</b>  does not save the rule currently displayed. You must select <b>Commit</b> before.</html>",
				Utilities.getHelpIcon("addCommitDeleteRules.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("" +
				"<html>Currently no alternatives are present. The only syntax supported is Jena and all the rules are tentatively evaluated after standard entailments.</html>",
				Utilities.getHelpIcon("ruleOptionPanel.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
	
		
		
		
		return help;
	}

	public String getPanelName() {
		return "Resoner Settings";
	}

	public int getStatusLevel() {
		return 3;
	}

	
	public void refresh() {
		String[] names=reasonerManager.getReasonerOptionNames();
		for(int i=0;i<names.length;i++) {
			System.out.println("Option: "+names[i]);
			if(names[i].equalsIgnoreCase("Level")) {
				System.out.println("Was selected :"+optionBoxes[i].getSelectedItem());
				System.out.println("Going to be :"+reasonerManager.getReasonerActualOptions()[i]);
				optionBoxes[i].setSelectedItem(reasonerManager.getReasonerActualOptions()[i]);
			}
		}
			
	
			
		
	}
	
		
	

	
	
}
