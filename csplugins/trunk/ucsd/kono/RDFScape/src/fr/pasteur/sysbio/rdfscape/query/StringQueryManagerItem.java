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

import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;


public class StringQueryManagerItem extends QueryManagerItem {
	RDQLQueryAnswerer myKnowledge=null;
	JPanel myPanel=null;
	public StringQueryManagerItem(RDQLQueryAnswerer kw) {
		myKnowledge=kw;
	}

	public JPanel getPanel() {
		if(myPanel==null) myPanel=new StringQueryPanel(this);
		return myPanel;
	}

	public void setQuery(String query) {
		// TODO Auto-generated method stub
		
	}

	public QueryResultTable makeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		return "StringQuery";
	}
	

	public void reset() {
		// TODO Auto-generated method stub
		
	}	

	private class StringQueryPanel extends JPanel {
		StringQueryManagerItem stringQueryManagerItem=null;
		JTextField resource=null;
		JCheckBox approx=null;
		JRadioButton source=null;
		JRadioButton target=null;
		public StringQueryPanel(StringQueryManagerItem sqi) {
			stringQueryManagerItem=sqi;
			setBorder(new TitledBorder("String query"));
			resource=new JTextField(30);
			approx=new JCheckBox("Approx");
			source=new JRadioButton("Source");
			target=new JRadioButton("Target");
			
			ActionListener sourceListener = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					target.setSelected(!source.isSelected());
				}
			};
			ActionListener targetListener =new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					source.setSelected(!target.isSelected());
				}
			};
			source.addActionListener(sourceListener);
			target.addActionListener(targetListener);
			reset();
			add(resource);
			add(approx);
			add(source);
			add(target);
		}

		public void reset() {
			resource.setText("");
			approx.setSelected(false);
			source.setSelected(true);
			
		}
		
		
		public void execQuery() {
			/*
			TableModel myResult=null;
			if(approx.isSelected()) {
				String myQuery = null;
				if(source.isSelected()) {
					myQuery="SELECT ?x WHERE (?x ?y ?z) AND ?x =~/"+resource.getText()+"/";
				}
				if(target.isSelected()) {
					myQuery="SELECT ?z WHERE (?x ?y ?z) AND ?z =~/"+resource.getText()+"/";
				}
				myResult=browserManager.makeRDQLRichQuery(myQuery);
				rdqlQueryResult.display(myResult);
			}
			else {
				myResult=browserManager.getResourceInModelAsTable(resource.getText());
				rdqlQueryResult.display(myResult);
			}*/
		}
		
	}




}
