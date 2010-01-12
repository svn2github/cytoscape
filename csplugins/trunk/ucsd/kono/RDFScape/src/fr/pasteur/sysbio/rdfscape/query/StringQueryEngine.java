/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;


public class StringQueryEngine extends MyQueryEngine {
	//private RDQLQueryAnswerer myKnowledge=null;
	
	private StringQueryPanel myPanel=null;
	public StringQueryEngine() throws Exception {
		try {
			RDQLQueryAnswerer tempKnowledge=(RDQLQueryAnswerer) RDFScape.getKnowledgeEngine();
		} catch (Exception e) {
			throw new Exception("StringQuery not supported by this KnowledgeWrapper");
		}
		myPanel=new StringQueryPanel(this);
	}

	public JPanel getPanel() {
		
		return myPanel;
	}

	public void setQuery(String query) {
		// TODO Auto-generated method stub
		
	}

	public AbstractQueryResultTable makeQuery() {
		String myQuery="";	
		if(myPanel.getIsApprox()) {
			myQuery="SELECT ?x WHERE (?x ?y ?z) AND ( ?x =~/"+myPanel.getText()+"/ || ?z =~ /"+myPanel.getText()+"/)";
			System.out.println("Approx query");
		}
		else {
			myQuery="SELECT ?x WHERE (?x ?y ?z) AND ( ?x =/"+myPanel.getText()+"/ || ?z = /"+myPanel.getText()+"/)";
			System.out.println("Exact query");
		} 	
			try {
					return ((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(myQuery);
				} catch (Exception e) {
					RDFScape.warn("Try to make a RDQL query (you thought it was a string...) albeit I don't know how. This should not happen");
					return new JenaQueryResultTable();
				}
		
		
	}

	public String getLabel() {
		return "StringQuery";
	}
	

	public void reset() {
		// TODO Auto-generated method stub
		
	}	

	private class StringQueryPanel extends JPanel {
		private StringQueryEngine stringQueryManagerItem=null;
		private JTextField resource=null;
		private JCheckBox approx=null;
		//private JRadioButton source=null;
		//private JRadioButton target=null;
		public StringQueryPanel(StringQueryEngine sqi) {
			stringQueryManagerItem=sqi;
			setBorder(new TitledBorder("String query"));
			resource=new JTextField(30);
			approx=new JCheckBox("Approx");
			//source=new JRadioButton("Source");
			//target=new JRadioButton("Target");
			/*
			ActionListener sourceListener = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					target.setSelected(!source.isSelected());
				}
			};*/
			/*
			ActionListener targetListener =new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					source.setSelected(!target.isSelected());
				}
			};*/
			//source.addActionListener(sourceListener);
			//target.addActionListener(targetListener);
			reset();
			add(resource);
			add(approx);
			//add(source);
			//add(target);
		}

		public void reset() {
			resource.setText("");
			approx.setSelected(false);
			//source.setSelected(true);
			
		}
		public String getText() {
			return resource.getText();
		}
		public boolean getIsApprox() {
			return approx.isSelected();
		}
		
		
		
	}




}
