/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

//import obsolete.OntoHintBox;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;

public class SPARQLQueryEngine extends MyQueryEngine {
	//private SPARQLQueryAnswerer myKnowledge=null;
	
	private SPQRLQueryPanel myPanel=null;
	private String query=null;
	
	public SPARQLQueryEngine() throws Exception {
		try {
			SPARQLQueryAnswerer tempEngine=(SPARQLQueryAnswerer)RDFScape.getKnowledgeEngine();
		} catch (Exception e) {
			throw new Exception("SPARQL query not supported by this KnowledgeWrapper");
		}
		query=getDefaultQuery();
	}

	public JPanel getPanel() {
		if(myPanel==null) myPanel=new SPQRLQueryPanel(this);
		return myPanel;
	}
	
	public void setQuery(String query) {
		this.query=query;
		if(myPanel!=null) myPanel.refresh();

	}
	
	public AbstractQueryResultTable makeQuery() {
		System.out.println("Querying my knowledge...");
		if(myPanel!=null) query=myPanel.getQueryText();
		try {
		return ((SPARQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeSPAQRLQuery(query);
		} catch (Exception e) {
			RDFScape.warn("Trying to make a SPARQL query when i can't. This should not happen\n"+e.getMessage());
			return new JenaQueryResultTable();
		}
	}

	public String getLabel() {
		return "SPARQLQuery";
	}

	public void reset() {
		query=getDefaultQuery();
		if(myPanel!=null) myPanel.refresh();
		
	}
	public String getDefaultQuery() {
		String query=new String();
		query=query.concat(getQueryNameSpacesClause());
		query=query.concat("SELECT \n WHERE {     } \n");
		
		return query;
				
	}
	
	public String getQueryNameSpacesClause() {
		CommonMemory commonMemory=RDFScape.getCommonMemory();
		String nsClause=new String();
		ArrayList namespaces=commonMemory.getNamespacesList();
		if(namespaces.size()>0) {
			
			for (int i = 0; i < namespaces.size(); i++) {
				String tempNameSpace=(String)namespaces.get(i);
				// TODO ocho!
				if(commonMemory.getNamespacePrefix(tempNameSpace)!=null) {
					nsClause=nsClause.concat("PREFIX \t"+commonMemory.getNamespacePrefix(tempNameSpace)+":");
					nsClause=nsClause.concat("\t <"+tempNameSpace+">\n");
				}
		
			}
			nsClause=nsClause+"\n";
		}
		return nsClause;	
	
	}
	
	
	
	private class SPQRLQueryPanel extends JPanel {
		private SPARQLQueryEngine spqrlQueryManager=null;
		private JTextArea spqrlQueryTextArea=null;
		
		//JButton clearButton=null;
		//OntoHintBox rdqlHelperComboBox=null;
		
		public SPQRLQueryPanel(SPARQLQueryEngine rdqlqi) {
			spqrlQueryManager=rdqlqi;
			setBorder(new TitledBorder("SPQRL query"));
			setLayout(new BorderLayout());
			
			spqrlQueryTextArea=new JTextArea(spqrlQueryManager.query,8,60);
			add(spqrlQueryTextArea,BorderLayout.CENTER);
			
			
			//rdqlHelperComboBox=new OntoHintBox(inspect.myRDFScapeInstance);
			//rdqlHelperComboBox.registerListener(this);
			//JPanel buttonPanel=new JPanel();
			//clearButton=new JButton("Delete Query");
			
		}
		
		
		public String getQueryText() {
			return spqrlQueryTextArea.getText();
		}
		
		/*
		public void setQueryText(String text) {
			rdqlQueryTextArea.setText(text);
			
		}
		*/
		public void refresh() {
			spqrlQueryTextArea.setText(spqrlQueryManager.query);
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
