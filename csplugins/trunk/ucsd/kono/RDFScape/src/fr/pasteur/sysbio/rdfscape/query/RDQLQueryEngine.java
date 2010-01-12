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

public class RDQLQueryEngine extends MyQueryEngine {
	
	private RDQLQueryPanel myPanel=null;
	private String query=null;
	
	public RDQLQueryEngine() throws Exception {
		try{
			RDQLQueryAnswerer tempEngine=(RDQLQueryAnswerer)RDFScape.getKnowledgeEngine();
		} catch (Exception e) {
			throw new Exception("RDQL query not supported by this KnowledgeWrapper");
		}	
		query=getDefaultQuery();
	}

	public JPanel getPanel() {
		if(myPanel==null) myPanel=new RDQLQueryPanel(this);
		return myPanel;
	}
	
	public void setQuery(String query) {
		this.query=query;
		if(myPanel!=null) myPanel.refresh();

	}
	
	public AbstractQueryResultTable makeQuery() {
		System.out.println("Querying my knowledge...");
		if(myPanel!=null) query=myPanel.getQueryText();
		try{
			return ((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(query);
		} catch (Exception e) {
			RDFScape.warn("trying to make an RDQL query, but I can't. This should not happen.\n"+e.getMessage());
			return new JenaQueryResultTable();
		}	
		
	}

	public String getLabel() {
		return "RDQLQuery";
	}

	public void reset() {
		query=getDefaultQuery();
		if(myPanel!=null) myPanel.refresh();
		
	}
	public String getDefaultQuery() {
		String query=new String();
		query=query.concat("SELECT \n WHERE (     ) \n");
		query=query.concat(getQueryNameSpacesClause());
		return query;
				
	}
	
	public String getQueryNameSpacesClause() {
		CommonMemory commonMemory=RDFScape.getCommonMemory();
		String nsClause=new String();
		ArrayList namespaces=commonMemory.getNamespacesList();
		if(namespaces.size()>0) {
			nsClause=nsClause.concat("USING ");
			for (int i = 0; i < namespaces.size(); i++) {
				String tempNameSpace=(String)namespaces.get(i);
				System.out.println("Considering tempNameSpace "+tempNameSpace);
				if(commonMemory.getNamespacePrefix(tempNameSpace)!=null) {
					nsClause=nsClause.concat(commonMemory.getNamespacePrefix(tempNameSpace));
					nsClause=nsClause.concat(" FOR <"+tempNameSpace+">,\n");
				}
			}
			nsClause=nsClause.substring(0,nsClause.length()-2);
		}
		return nsClause;	
	
	}
	
	
	
	private class RDQLQueryPanel extends JPanel {
		private RDQLQueryEngine rdqlQueryManager=null;
		private JTextArea rdqlQueryTextArea=null;
		
		//JButton clearButton=null;
		//OntoHintBox rdqlHelperComboBox=null;
		
		public RDQLQueryPanel(RDQLQueryEngine rdqlqi) {
			rdqlQueryManager=rdqlqi;
			setBorder(new TitledBorder("RDQL query"));
			setLayout(new BorderLayout());
			
			rdqlQueryTextArea=new JTextArea(rdqlQueryManager.query,8,60);
			add(rdqlQueryTextArea,BorderLayout.CENTER);
			
			
			//rdqlHelperComboBox=new OntoHintBox(inspect.myRDFScapeInstance);
			//rdqlHelperComboBox.registerListener(this);
			//JPanel buttonPanel=new JPanel();
			//clearButton=new JButton("Delete Query");
			
		}
		
		
		public String getQueryText() {
			return rdqlQueryTextArea.getText();
		}
		
		/*
		public void setQueryText(String text) {
			rdqlQueryTextArea.setText(text);
			
		}
		*/
		public void refresh() {
			rdqlQueryTextArea.setText(rdqlQueryManager.query);
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
