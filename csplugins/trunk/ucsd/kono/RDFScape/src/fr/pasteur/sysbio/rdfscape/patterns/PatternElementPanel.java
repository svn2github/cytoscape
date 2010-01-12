/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.patterns;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.browsing.ResultTableViewer;
import fr.pasteur.sysbio.rdfscape.computing.PatternEvaluatedTable;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.RDFResourceWrapper;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.RDFWrappersFactory;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternElementPanel extends JPanel {
	
	PatternElement myPatternElement=null;
	JButton plotValueButton=null;
	JButton computeButton=null;
	JButton plotPValueButton=null;
	//TableModel myComputedTableData=null;
	JComboBox randOptionBox=null;
	//TableModel myMatchedTableData=null;
	
	
	JButton searchVariableButton=null;
	JButton addVariableButton=null;
	JButton searchPatternButton=null;
	JButton addPatternButton=null;
	JButton deletePatternButton=null;
	
	
	
	
	
	/**
	 * 
	 */
	public PatternElementPanel(PatternElement pe) {
		super();
		//myComputedTableData=new PatternEvaluatedTable(0);
		//myMatchedTableData=new PatternMatchedTable();
		System.out.println("Building pattern panel. LimitedMode= "+DefaultSettings.limitedMode);
		
		myPatternElement=pe;
		if(DefaultSettings.limitedMode==false) setLayout(new GridLayout(2,2));
		else setLayout(new GridLayout(2,1));
		JTabbedPane viewPanel=new JTabbedPane();
		JPanel graphPanel=new JPanel();
		graphPanel.add(new JLabel(new ImageIcon(myPatternElement.getImage())));
		viewPanel.add(graphPanel,"graph");
		
		/*
		final JTextArea myPatternString=new JTextArea();
		myPatternString.setText(myPatternElement.getPatternClause());
		myPatternString.setEditable(false);
		JScrollPane patternStringPanel=new JScrollPane(myPatternString);
		viewPanel.add(patternStringPanel,"Pattern");
		*/
		final JTable myPattern=new JTable(myPatternElement);
		//myPattern.setBorder(new TitledBorder("my pattern in triples"));
		JScrollPane patternStringPanel=new JScrollPane(myPattern);
		viewPanel.add(patternStringPanel,"Pattern");
		
		
		final ResultTableViewer patternMatches=new ResultTableViewer();
		
		// TODO to fix
		//patternMatches.setDefaultRenderer(String.class,new UriRenderer(myPatternElement.myPatternManager.myRDFScapeInstance.getMyRDFWrappersFactory()));
		patternMatches.setEnabled(false);
		JScrollPane patternMatchesPane=new JScrollPane(patternMatches);
		viewPanel.add(patternMatchesPane,"Matches");
		
		
		
		final JTextArea functionText=new JTextArea();
		ActionListener commitListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myPatternElement.addPatternFunction(functionText.getText());
				if(myPatternElement.hasValidRule()) computeButton.setEnabled(true);
				else computeButton.setEnabled(false);
			}
			
		};
		
		JPanel functionPanel=new JPanel();
		functionPanel.setLayout(new BorderLayout());
		functionPanel.setBorder(new TitledBorder("Evaluation function"));
		
		JButton commitButton=new JButton("Commit");
		commitButton.addActionListener(commitListener);
		functionPanel.add(functionText,BorderLayout.CENTER);
		functionPanel.add(commitButton,BorderLayout.SOUTH);
		
		
		
		JTabbedPane analysisPane=new JTabbedPane();
		JLabel chart=new JLabel("Chart");
		final JTable dataTable=new JTable(myPatternElement.getPatternEvaluatedData());
		JScrollPane scrollTablePane=new JScrollPane(dataTable);
		analysisPane.add(chart,"Chart");
		analysisPane.add(scrollTablePane,"Results");
		
		
		
		
		ActionListener searchVariableButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patternMatches.setModel(myPatternElement.searchVariable());
				/*
				patternMatches.setModel(myPatternElement.getPatternMatchedTable());
				String query =myPatternElement.myPatternManager.makeRDQLQueryFromPattern(myPatternElement);
				myPatternElement.searchPattern(true);
				//myMatchedTableData=myPatternElement.getPatternMatchedTable();
				patternMatches.setModel(myPatternElement.getPatternMatchedTable());
				*/
			}
			
		};
		ActionListener addVariableButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patternMatches.setModel(myPatternElement.addVariable());
				/*
				myPatternElement.searchPattern(false);
				//myMatchedTableData=myPatternElement.getPatternMatchedTable();
				patternMatches.setModel(myPatternElement.getPatternMatchedTable());
				*/
			}
			
		};
		ActionListener searchButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patternMatches.setModel(myPatternElement.searchPattern());
				/*
				myPatternElement.searchPattern(true);
				//myMatchedTableData=myPatternElement.getPatternMatchedTable();
				patternMatches.setModel(myPatternElement.getPatternMatchedTable());
				*/
			}
			
		};
		ActionListener addButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patternMatches.setModel(myPatternElement.addPattern());
				/*
				myPatternElement.searchPattern(false);
				//myMatchedTableData=myPatternElement.getPatternMatchedTable();
				patternMatches.setModel(myPatternElement.getPatternMatchedTable());
				*/
			}
			
		};
		ActionListener computeButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PatternEvaluatedTable myComputedTableData=myPatternElement.computeFunction(0,(String)(randOptionBox.getSelectedItem()));
				if(myComputedTableData.getRowCount()>0) {
					plotValueButton.setEnabled(true);
					//myMatchedTableData=myPatternElement.getPatternMatchedTable();
					patternMatches.setModel(myPatternElement.getPatternMatchedTable());
					
				} 
				else plotValueButton.setEnabled(false);
				dataTable.setModel(myComputedTableData);
			}
		};
		ActionListener plotValueListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myPatternElement.addValues();
				
			}
			
		};
		ActionListener plotPValueListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myPatternElement.addPValues();
				
			}
			
		};
		
		ActionListener deletePatternListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int answer=JOptionPane.showConfirmDialog(null,"Are you sure you want to delete this pattern?");
				if(answer==0) {
					myPatternElement.remove();
					
				}
				
				
			}
			
		};
		
		JPanel buttonPanel=new JPanel();
		searchVariableButton=new JButton("Search variables");
		addVariableButton=new JButton("Add variables");
		searchPatternButton=new JButton("Search patterns");
		addPatternButton=new JButton("Add patterns");
		deletePatternButton=new JButton("Delete pattern");
		deletePatternButton.setBackground(Color.RED);
		deletePatternButton.setOpaque(true);
		refreshAfterKnowledgeWrapperChange();
		
		computeButton=new JButton("Compute Function");
		computeButton.setEnabled(false);
		plotValueButton=new JButton("Plot value");
		plotValueButton.setEnabled(false);
		plotPValueButton=new JButton("Plot pvalue");
		plotPValueButton.setEnabled(false);
		searchVariableButton.addActionListener(searchVariableButtonListener);
		addVariableButton.addActionListener(addVariableButtonListener);
		searchPatternButton.addActionListener(searchButtonListener);
		addPatternButton.addActionListener(addButtonListener);
		computeButton.addActionListener(computeButtonListener);
		plotValueButton.addActionListener(plotValueListener);
		plotPValueButton.addActionListener(plotPValueListener);
		deletePatternButton.addActionListener(deletePatternListener);
		
		
		final JTextField randomSteps=new JTextField(5);
		JButton 	computeRandom=new JButton("Random");
		String[] randOptions={"total","gene"};
		randOptionBox=new JComboBox(randOptions);
		JPanel randomPanel=new JPanel();
		randomPanel.setLayout(new GridLayout(1,3));
		randomPanel.setBorder(new TitledBorder("Random eval"));
		ActionListener randomListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int cycles=0;
				try {
					cycles=(new Integer(randomSteps.getText())).intValue();
				} catch (Exception e) {cycles=0;};
				if(cycles>0) {
					TableModel myData=myPatternElement.computeFunction(cycles,(String)(randOptionBox.getSelectedItem()));
					dataTable.setModel(myData);
					plotPValueButton.setEnabled(true);
				}
				else plotPValueButton.setEnabled(false);
			}
			
		};
		computeRandom.addActionListener(randomListener);
		randomPanel.add(randomSteps);
		randomPanel.add(computeRandom);
		randomPanel.add(randOptionBox);
		
		
		JPanel addRulePanel=new JPanel();
		addRulePanel.setBorder(new TitledBorder("Add rule"));
		addRulePanel.setLayout(new BorderLayout());
		JPanel subPanel=new JPanel();
		
		
		
		String options[]={"value","pvalue"};
		final JComboBox valueType=new JComboBox(options); 
		
		String operators[]={">","<"};
		final JComboBox oper=new JComboBox(operators);
		
		final JTextField value=new JTextField(5);
		subPanel.setLayout(new GridLayout(1,5));
		
		String ruleOrPattern[]={"pattern","rule"};
		final JComboBox ruleOrPatternBox=new JComboBox(ruleOrPattern);
		
		final JTextArea rule=new JTextArea();
		JButton ruleButton=new JButton("Add Rule/Pattern");
		
		ActionListener ruleButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myPatternElement.triggerRule((String)(valueType.getSelectedItem()),
						(String)(oper.getSelectedItem()), value.getText(),
						rule.getText(),(String)(ruleOrPatternBox.getSelectedItem()));
				
			}
			
		};
		
		ruleButton.addActionListener(ruleButtonListener);
		rule.setBorder(new TitledBorder("rule"));
		
		subPanel.add(valueType);
		subPanel.add(oper);
		subPanel.add(value);
		subPanel.add(ruleOrPatternBox);
		subPanel.add(ruleButton);
		
		addRulePanel.add(subPanel,BorderLayout.NORTH);
		addRulePanel.add(rule,BorderLayout.CENTER);
		
		
		JPanel upperButtonPanel=new JPanel();
		if(DefaultSettings.limitedMode==false) upperButtonPanel.setLayout(new GridLayout(3,3));
		if(DefaultSettings.limitedMode==true) upperButtonPanel.setLayout(new GridLayout(2,3));
		upperButtonPanel.add(searchVariableButton);
		upperButtonPanel.add(addVariableButton);
		upperButtonPanel.add(new JLabel());
		upperButtonPanel.add(searchPatternButton);
		upperButtonPanel.add(addPatternButton);
		upperButtonPanel.add(deletePatternButton);
		if(DefaultSettings.limitedMode==false) upperButtonPanel.add(computeButton);
		if(DefaultSettings.limitedMode==false) upperButtonPanel.add(plotValueButton);
		if(DefaultSettings.limitedMode==false) upperButtonPanel.add(plotPValueButton);
		
		if(DefaultSettings.limitedMode==false) {
			buttonPanel.setLayout(new BorderLayout());
			buttonPanel.add(upperButtonPanel,BorderLayout.NORTH);
			buttonPanel.add(randomPanel,BorderLayout.SOUTH);
			buttonPanel.add(addRulePanel,BorderLayout.CENTER);
		}
		if(DefaultSettings.limitedMode==true) {
			buttonPanel.add(upperButtonPanel);
			
		}
		
		
		
		add(viewPanel);
		if(DefaultSettings.limitedMode==false) add(analysisPane);
		if(DefaultSettings.limitedMode==false) add(functionPanel);
		add(buttonPanel);
		
		
		
	}

	public class UriRenderer extends JLabel implements TableCellRenderer {
		RDFWrappersFactory myWrapperFactory;
		/**
		 * @param arg0
		 */
		public UriRenderer(RDFWrappersFactory rw) {
			super();
			setOpaque(true);
			myWrapperFactory=rw;
			
		}
		public Component getTableCellRendererComponent(JTable table, Object uri, boolean isSelected, boolean hasFocus, int row, int column) {
			RDFResourceWrapper myRes=myWrapperFactory.makeRDFResourceWrapper((String)uri);
			setForeground(myRes.getColor());
			setText(myRes.getDisplayText());
			//System.out.println("wrapped");
			return this;
		}
		
	}

	public class BiteRenderer extends JLabel implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			
			return null;
		}
		
	}
	
	public void refreshAfterKnowledgeWrapperChange() {
		if(myPatternElement.canAddAndSearchPatterns()) {
			searchVariableButton.setEnabled(true);
			addVariableButton.setEnabled(true);
			searchPatternButton.setEnabled(true);
			addPatternButton.setEnabled(true);
		
		}
		else {
			searchVariableButton.setEnabled(false);
			addVariableButton.setEnabled(false);
			searchPatternButton.setEnabled(false);
			addPatternButton.setEnabled(false);
		}
	}

	
	
	
}



