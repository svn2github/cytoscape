/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

//import obsolete.OntoHintBox;

import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;

public class RDQLQueryManagerItem extends QueryManagerItem {
	RDQLQueryAnswerer myKnowledge=null;
	JPanel myPanel=null;
	public RDQLQueryManagerItem(RDQLQueryAnswerer kw) {
		myKnowledge=kw;
	}

	public JPanel getPanel() {
		if(myPanel==null) myPanel=new RDQLQueryPanel(this);
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
		return "RDQLQuery";
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}
	public String getDefaultQuery() {
		return("Nothing yet");
		
	}
	
	private class RDQLQueryPanel extends JPanel {
		private RDQLQueryManagerItem rdqlQueryManager=null;
		private JTextArea rdqlQueryTextArea=null;
		
		//JButton clearButton=null;
		//OntoHintBox rdqlHelperComboBox=null;
		
		public RDQLQueryPanel(RDQLQueryManagerItem rdqlqi) {
			rdqlQueryManager=rdqlqi;
			setBorder(new TitledBorder("RDQL query"));
			setLayout(new BorderLayout());
			
			rdqlQueryTextArea=new JTextArea(getDefaultQuery(),8,60);
			add(rdqlQueryTextArea,BorderLayout.CENTER);
			
			
			//rdqlHelperComboBox=new OntoHintBox(inspect.myRDFScapeInstance);
			//rdqlHelperComboBox.registerListener(this);
			//JPanel buttonPanel=new JPanel();
			//clearButton=new JButton("Delete Query");
			
		}
		
		public String getQuery() {
			return rdqlQueryTextArea.getText();
		}


		public void reset() {
			rdqlQueryTextArea.setText(getDefaultQuery());
			
		}
		/*
		public void execQuery() {
			String textQuery=rdqlBrowsePanel.getQuery();
			TableModel myresult=browserManager.makeRDQLRichQuery(textQuery);
			
			System.out.println("TableModel");
			for(int i=0;i<myresult.getRowCount();i++) {
				System.out.print(i+": ");
				for(int j=0;j<myresult.getColumnCount();j++) {
				 	
					System.out.print(" "+myresult.getValueAt(i,j));
				}
				System.out.println("");
			}
			
			rdqlQueryResult.display(myresult);
		}
		*/
	}	

}
