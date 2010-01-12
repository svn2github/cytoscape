/**
 * Copyright 2006-2007 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.query;

import java.util.ArrayList;

import fr.pasteur.sysbio.rdfscape.RDFScape;

/**
 * @author andrea@pasteur.fr
 *
 */
public class QueryManager {
	
	/**
	 * 
	 */
	public QueryManager( ) {
		super();
		
	}
	public String getDefaultQuery() {
		String query=new String();
		query=query.concat("SELECT \n WHERE (     ) \n");
		query=query.concat(getQueryNameSpacesClause());
		return query;
	}
	
	public String getQueryNameSpacesClause() {
		String nsClause=new String();
		ArrayList namespaces=RDFScape.getCommonMemory().getNamespacesList();
		if(namespaces.size()>0) {
			nsClause=nsClause.concat("USING ");
			for (int i = 0; i < namespaces.size(); i++) {
				String tempNameSpace=(String)namespaces.get(i);
				// TODO ocho!
				//nsClause=nsClause.concat(myRDFScapeInstance.getNameSpaceManager().getNameSpacePrefix(tempNameSpace));
				nsClause=nsClause.concat(" FOR <"+tempNameSpace+">,\n");
		
			}
			nsClause=nsClause.substring(0,nsClause.length()-2);
		}
		return nsClause;	
	
	}
	
	/*
	public PatternMatchedTable makeRDQLRichQuery(String textQuery) {
		QueryResults queryResults=null;
		QueryExecution qe=null;
		Query query=null;
		
		PatternMatchedTable myResultTable=new PatternMatchedTable();
		Vector result=new Vector();
		System.out.println("A");
		try {
			query=new Query(textQuery);
			//query.setSource(myRDFScapeInstance.myKnowledge); TODO to fix
			qe=new QueryEngine(query);
			queryResults=qe.exec();
		} catch (QueryException e) {
			myRDFScapeInstance.warn(e.getMessage());
			return myResultTable;
		}
		System.out.println("C");
		List queryVariables=query.getResultVars();
		System.out.println("D");
		int i=0;
		for(Iterator iter=queryResults ;iter.hasNext();) {
			ResultBinding res=(ResultBinding) iter.next();
			int j=0;
			System.out.println("E");
			for (Iterator iterator = queryVariables.iterator(); iterator
					.hasNext();) {
				System.out.println("F");
				String var = (String) iterator.next();
				
				
				RDFNode tempNode=(RDFNode) (res.get(var));
				if(tempNode.isURIResource());
				if(tempNode.isLiteral());
				if(tempNode.isAnon());
				
				String tempString="";
				if(tempNode!=null) tempString=tempNode.toString();
				
				//System.out.println("->"+res.get(var).getClass());
				
				myResultTable.addString(tempString,i,j);
				myResultTable.addVar(var,j);
				
				j++;
			}
			
			i++;
		}
		
		return myResultTable;
		
	}
	*/
		
	
}
