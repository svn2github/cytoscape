/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;

public class ClassQueryEngine extends MyQueryEngine {
	//private RDQLQueryAnswerer myKnowledge=null;
	private ClassQueryPanel myPanel=null;
	String[] classesURIs=null;
	String RDQLquery=null;
	public ClassQueryEngine() throws Exception {
		try {
			RDQLQueryAnswerer tempKnowledge=(RDQLQueryAnswerer) RDFScape.getKnowledgeEngine();
		} catch (Exception e) {
			throw new Exception("Class query not supported by this KnowledgeWrapper");
		}
		classesURIs=new String[0];
		myPanel=new ClassQueryPanel(this);
		
	}

	public JPanel getPanel() {
		myPanel=new ClassQueryPanel(this);
		return myPanel;
	}

	public void setQuery(String query) {
		// TODO Auto-generated method stub

	}

	public AbstractQueryResultTable makeQuery() {
		if(myPanel.getSelectedURI()==null) {
			System.out.println("No URI");
			return new JenaQueryResultTable();
		}
		RDQLquery="SELECT ?x \n WHERE (?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+myPanel.getSelectedURI()+">)\n";
		System.out.println("query:\n"+RDQLquery);
		try {
		return ((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(RDQLquery);
		} catch (Exception e) {
			RDFScape.warn("Trying to make an RDQL query (you thought it was a class query) albeit I don-t know how. This should not happen...");
			return new JenaQueryResultTable();
		}
	}

	public String getLabel() {
		return "ClassQuery";
	}

	public void reset() {
		try {
			if(KnowledgeWrapper.hasGraphAccessSupport(RDFScape.getKnowledgeEngine())) {
				classesURIs=((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).getClassURIList();
			}
			else System.out.println("Unable to get class list");
		} catch (ClassCastException e) {
			System.out.println("Unable to get class list");
			e.printStackTrace();
		}
		System.out.println("Class # "+classesURIs.length);
		myPanel.reset();
		
	}

	
	private class ClassQueryPanel extends JPanel  {
		ClassQueryEngine classQueryManagerItem=null;
		JComboBox classes=null;
		
		public ClassQueryPanel(ClassQueryEngine qi) {
		
			classQueryManagerItem=qi;
			setBorder(new TitledBorder("OWL class query"));
			classes=new JComboBox(classesURIs);
			add(classes);
			
		}
		/* (non-Javadoc)
		 * @see fr.pasteur.sysbio.rdfscape.browsing.QueryPanel#reset()
		 */
		public void reset() {
			
			System.out.println("R");
			remove(classes);
			classes=new JComboBox(classesURIs);
			add(classes);
			validate();
			
		}
		public String getSelectedURI() {
			return (String)classes.getSelectedItem();
		}
	}	
	
	
	
}
