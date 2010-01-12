/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;

public class ClassQueryManagerItem extends QueryManagerItem {
	RDQLQueryAnswerer myKnowledge=null;
	ClassQueryPanel myPanel=null;
	
	public ClassQueryManagerItem(RDQLQueryAnswerer rdqa) {
		myKnowledge=rdqa;
	}

	public JPanel getPanel() {
		if(myPanel==null) myPanel=new ClassQueryPanel(this);
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
		return "ClassQuery";
	}

	public void reset() {
		
		
	}

	
	private class ClassQueryPanel extends JPanel  {
		ClassQueryManagerItem classQueryManagerItem=null;
		JComboBox classes=null;
		
		public ClassQueryPanel(ClassQueryManagerItem qi) {
			/*
			classQueryManagerItem=qi;
			setBorder(new TitledBorder("owl query"));
			classes=new JComboBox(browserManager.getClassesList());
			add(classes);
			*/
		}
		/* (non-Javadoc)
		 * @see fr.pasteur.sysbio.rdfscape.browsing.QueryPanel#reset()
		 */
		public void reset() {
			/*
			System.out.println("R");
			remove(classes);
			classes=new JComboBox(browserManager.getClassesList());
			add(classes);
			validate();
			*/
		}
		/* (non-Javadoc)
		 * @see fr.pasteur.sysbio.rdfscape.browsing.QueryPanel#getQuery()
		 */
		public void execQuery() {
			/*
			String currentClass=(String)classes.getSelectedItem();
			TableModel myResult=browserManager.getInstancesForClassAsTable(currentClass);
			rdqlQueryResult.display(myResult);
			*/
		}
	}	
	
	
	
}
