/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.patterns.PatternElement;

public class JenaQueryResultTable extends AbstractQueryResultTable{
	private static final long serialVersionUID = 1L;
	
	boolean hasGraph=false;
	private ArrayList resultValues=null;
	private Hashtable resultVar=null;
	private Hashtable biteToRows=null;
	public JenaQueryResultTable()  {
		super();
	
		
		
	}
	public Color getColor(int x, int y) {
		if(getValueAt(x,y)==null) {
			//System.out.println(x+","+y+" -> null value");
			return Color.LIGHT_GRAY;
		}
		String tempURI=getNamespace(x,y);
		if(tempURI==null) {
			//System.out.println(x+","+y+" -> null URI");
			return Color.LIGHT_GRAY;
		}
		Color returnColor=commonMemory.getNamespaceColor(tempURI);
		if(returnColor==null) {
			System.out.println(x+","+y+" -> null Color");
			return Color.LIGHT_GRAY;
		}
		return returnColor;
	}
	
	public String getLabel(int x, int y) {
		if(getValueAt(x,y)==null) return "?";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return "";
		if(tempNode.isAnon()) {	
			return "BLANK: "+tempNode.toString();
		}
		else if (tempNode.isLiteral()) {
			return ((Literal)tempNode).getValue().toString();
		}
		else if (tempNode.isResource()) {
			if(commonMemory.showRDFSLabels && KnowledgeWrapper.hasGraphAccessSupport(RDFScape.getKnowledgeEngine())) {
				String label =((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).getRDFLabelForURI(getURI(x,y));
				if(label!=null) return label;
			}
			String ns=commonMemory.getNamespacePrefix(((Resource)tempNode).getNameSpace());
			String ln=((Resource)tempNode).getLocalName();
			if(ns==null) return ln;
			else if(ns.equals("")) return ln;
			else return ns+":"+ln;
		}
		else return "";
	}
	public boolean isURI(int x, int y) {
		if(getValueAt(x,y)==null) return false;
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode!=null) return tempNode.isURIResource();
		else return false;
	}
	public boolean isLiteral(int x, int y) {
		if(getValueAt(x,y)==null) return false;
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode!=null) return tempNode.isLiteral();
		else return false;
		
	}
	public boolean isBlank(int x, int y) {
		if(getValueAt(x,y)==null) return false;
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode!=null) return tempNode.isAnon();
		else return false;
		
	}
	public String getNamespace(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode.isURIResource()) {
			return ((Resource)tempNode).getNameSpace();
		}
		else if(tempNode.isLiteral()) {
			return ResourceFactory.createResource(((Literal)tempNode).getDatatypeURI()).getNameSpace();
			
		}
		else {
			return null;
		}
	}
	public String getID(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		else return tempNode.toString();
	}
	public String getURI(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		if(!tempNode.isURIResource()) return null;
		return((Resource)tempNode).getURI();
	
	}
	public String getDatatypeValue(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		if(!tempNode.isLiteral()) return null;
		return((Literal)tempNode).getValue().toString();
		
	}
	/**
	 * TODO
	 * we really are not handling this now...
	 */
	public String getDatatypeType(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		if(!tempNode.isLiteral()) return null;
		RDFDatatype datatype=((Literal)tempNode).getDatatype();
		if(datatype!=null) return datatype.getURI();
		else return "";
	}
	public String getDatatypeLanguage(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		if(!tempNode.isLiteral()) return null;
		return((Literal)tempNode).getLanguage();
	}
	public String getLocalName(int x, int y) {
		if(getValueAt(x,y)==null) return "";
		RDFNode tempNode=(RDFNode)getValueAt(x,y);
		if(tempNode==null) return null;
		if(!tempNode.isURIResource()) return null;
		return((Resource)tempNode).getLocalName();
	}
	public boolean isNamespaceSelected(int x, int y) {
		if(getValueAt(x,y)==null) return false;
		String tempURI=getNamespace(x,y);
		if(tempURI!=null) return commonMemory.getIsActiveFromNs(tempURI).booleanValue();
		else return false;
		
	}
	/**
	 * Jena Query Result Table Only holds a Table, for now...
	 */
	public boolean hasGraph() {
		return hasGraph;
	}
	public boolean hasTable() {
		return !hasGraph;
	}
	public AbstractQueryResultTable getSubsetByRows(int[] selectedRows) {
		JenaQueryResultTable newTable=new JenaQueryResultTable();
		for (int i = 0; i < selectedRows.length; i++) {
			for (int j = 0; j < this.getColumnCount(); j++) {
				System.out.println("("+selectedRows[i]+","+j+")");
				newTable.addObject(getValueAt(selectedRows[i],j),i,j);
			}
		}
		System.out.println("Dimensions "+newTable.getRowCount()+" x "+newTable.getColumnCount());
		return newTable;
	}
	
	public AbstractQueryResultTable getExpandedInPattern(PatternElement myPattern) {
		JenaQueryResultTable myResultTable=new JenaQueryResultTable();
		String[][] oldPattern=myPattern.getTriples();
		int [][] newPatternVarCol=new int[oldPattern.length][oldPattern[0].length];
		Resource[][] newPatternRes=new Resource[oldPattern.length][oldPattern[0].length];
		for (int i = 0; i < oldPattern.length; i++) {
			for (int j = 0; j < oldPattern[i].length; j++) {
				System.out.println("Eval:"+oldPattern[i][j]);
				if(oldPattern[i][j].indexOf("?")==0) {
					newPatternVarCol[i][j]=getVarColumn(oldPattern[i][j].substring(1,oldPattern[i][j].length()));
					
				}
				else {
					newPatternRes[i][j]=ResourceFactory.createResource(oldPattern[i][j]);
					newPatternVarCol[i][j]=-1;
				}
			}
		}
		/*
		System.out.println("Correspondence");
		for (int i = 0; i < oldPattern.length; i++) {
			for (int j = 0; j < oldPattern[i].length; j++) {
				System.out.println(i+","+j+"->"+newPatternVarCol[i][j]+"("+newPatternVarCol[i][j]+")");
			}
		}
		*/
		
		System.out.println("Making the pattern");
		int currentLine=0;
		for (int match = 0; match < this.getRowCount(); match++) {
			for (int i = 0; i < oldPattern.length; i++) {
				for (int j = 0; j < oldPattern[i].length; j++) {
					if(newPatternVarCol[i][j]>=0) {
						myResultTable.addObject(this.getValueAt(match,newPatternVarCol[i][j]),currentLine,j);
					}
					else {
						myResultTable.addObject(newPatternRes[i][j],currentLine,j);
					}
				}
				currentLine++;
			}
		}
		myResultTable.addVar("Source",0);
		myResultTable.addVar("Property",1);
		myResultTable.addVar("Object",2);
		System.out.println("Dump");
		
		for (int i = 0; i < myResultTable.getRowCount(); i++) {
			for (int j = 0; j < myResultTable.getColumnCount(); j++) {
				System.out.print(myResultTable.getLabel(i,j)+"   ");
			}
			System.out.println();
		}
		
		return myResultTable;
		
	}
		
		
	
	
	
}
