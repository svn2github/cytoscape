/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 5, 2005
 * Rev 0 dirty code
 * 
 */
package fr.pasteur.sysbio.rdfscape.ontologyhandling;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RDFScapeModuleInterface;
import fr.pasteur.sysbio.rdfscape.context.ContextManager;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.namespacemanagement.NamespaceManager;

/**
 * @author andrea@pasteur.fr
 * manages the Ontologies' URI list. Issues ontology loading.
 */
public class OntologyLoaderManager extends AbstractTableModel implements RDFScapeModuleInterface,Contextualizable {
	private ArrayList myOntologies=null;				//untologies URIs
	private OntologyManagerLoaderPanel myPanel=null;	//my Panel
	
	/**
	 * Creates OntologyLoaderManager and initializes it.
	 * @throws Exception 
	 */
	public OntologyLoaderManager() throws Exception {
		super();
		System.out.print("\tOntologyLoaderManager... ");
		if(RDFScape.getContextManager()==null) {
			throw new Exception("Cannot build OntologyLoaderManager : missing Context Manager");
		}
		RDFScape.getContextManager().addContextualizableElement(this);
		initialize();
		System.out.println("Ok");
	}
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#initialize()
	 */
	public boolean initialize() {
		myOntologies=new ArrayList();
		fireTableDataChanged();
		return true;
	}
	

	public void reset() {
		initialize();
		
	}
	
	/**
	 * @param knowledge
	 */
	/*
	public void setKnowledge(KnowledgeWrapper mk) {
		myKnowledge=mk;

		
	}
	*/
	/** 
	 * Add an URL ontology to the list of ontologies to be loaded.
	 */
	public void addURL(String ontology) {
		if(ontology==null) return;
		if(ontology.equals("")) return;
		myOntologies.add(new OntologyItem(ontology));
		update();
	}
	/**
	 * Only updates the values in the table
	 */
	public void update() {
		fireTableDataChanged();
	}
	
	
	public int getColumnCount() { return 2;}
	public int getRowCount() {return myOntologies.size();}

	public Object getValueAt(int arg0, int arg1) {
		if(myOntologies.get(arg0)!=null) {
				if(arg1==0) return ((OntologyItem)myOntologies.get(arg0)).url;
				else if(arg1==1) {
					OntologyItem tempItem=(OntologyItem)myOntologies.get(arg0);
					if(tempItem.message==null) return "ERROR";
					if(tempItem.message.equals("")) return "";
					return tempItem.message;
					
				}
				else return "?";
		}
		else return "?";
	}	
	
	public String getColumnName(int col) {
        if(col==0) return "URL";
        else if(col==1) return "Status";
        else return "";
    }

	public AbstractModulePanel getOntologyManagerPanel() {
		if(myPanel==null) myPanel=new OntologyManagerLoaderPanel(this);
		return myPanel;
	}

	public void deleteOntology(int i) {
		if(((OntologyItem)(myOntologies.get(i))).canDelete()==true) myOntologies.remove(i);
		fireTableDataChanged();
		
	}
	//TODO maybe we should add a method for multiple deletions
	
	
	private class OntologyItem {
		public String url="";
		public String message=""; // note: message=null means error.
		public String errorMessage="";
	
		public OntologyItem(String s) {
			url=s;
		}
		
		public boolean canDelete() {
			if(message==null) return true; //an error can be deleted
			if(message.equals("")) return true; // not loaded yet
			return false;
		}
		
	}

	
	
	/**
	 * 
	 */
	public void loadOntologiesFromScratch() {
		//myKnowledge.clear();
		//if(!checkPreconditions()) myRDFScapeInstance.warn("Context undefined");
		for (Iterator currentOntology = myOntologies.iterator(); currentOntology.hasNext();) {
			OntologyItem ontologyItem = (OntologyItem) currentOntology.next();
			System.out.println("Loading "+ontologyItem.url);
			ontologyItem.message=RDFScape.getKnowledgeEngine().addOntology(ontologyItem.url);
			int statn=0;
			try {
				statn=Integer.parseInt(ontologyItem.message);
			} catch (NumberFormatException e) {
				statn=0;
			}
			if(statn==0) ontologyItem.message=null;
			else ontologyItem.message="Read "+ontologyItem.message+" statements";
			//fireTableDataChanged();
			//myPanel.validate();
		}
		fireTableDataChanged();
		if(ontologiesPresentAndCorrectlyLoaded()) RDFScape.ontologiesLoaded();
		
	}
	
	
	

	

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#validate()
	 */
	/*
	public boolean isInValidState() {
		if(myOntologies.size()==0) return false;
		for (Iterator iter = myOntologies.iterator(); iter.hasNext();) {
			OntologyItem element = (OntologyItem) iter.next();
			if(element.message==null) return false;
		}
		return true;
		
		
	};
*/
	public boolean ontologiesPresentAndCorrectlyLoaded() {
	if(myOntologies.size()==0) return false;
	for (Iterator iter = myOntologies.iterator(); iter.hasNext();) {
		OntologyItem element = (OntologyItem) iter.next();
		if(element.canDelete()!=false) return false;
	}
	return true;
	
	
	};
	public boolean ontologiesPresentAndNeverLoaded() {
		if(myOntologies.size()==0) return false;
		for (Iterator iter = myOntologies.iterator(); iter.hasNext();) {
			OntologyItem element = (OntologyItem) iter.next();
			if(element.message!="") return false;
		}
		return true;
		
		
		};
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#loadFromContext()
	 */
	public boolean loadFromActiveContext() {
		System.out.println("Loading ontologies");
		//if(!checkPreconditions()) myRDFScapeInstance.warn("Context undefined");
		reset(); // TODO maybe we should avoid doing this twice at startup with a flag
		RDFScape.getContextManager().getActiveContext().getOntologiesListFileName();
		int counter = 0;
		String line;
		try {
			BufferedReader preferredOntologiesReader=new BufferedReader(new FileReader(RDFScape.getContextManager().getActiveContext().getOntologiesListFileName()));
			while((line = preferredOntologiesReader.readLine()) != null) {
				counter++; 
				addURL(line.trim());
			}
		}
		catch(IOException ioe)
		{
			 System.out.println("Unable to load ontology list file");
			  
		}
		fireTableDataChanged();
		return false;
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#saveToContext()
	 */
	public boolean saveToContext() {
		System.out.println("Saving ontology list");
		int counter = 0;
		String line;
		try {
			FileWriter ontologyWriter=new FileWriter(RDFScape.getContextManager().getActiveContext().getOntologiesListFileName());
			for (Iterator iter = myOntologies.iterator(); iter.hasNext();) {
				OntologyItem tempOntology = (OntologyItem) iter.next();
				ontologyWriter.write(tempOntology.url+"\n");
			}
			ontologyWriter.close();
			return true;
		}
		catch(IOException ioe)
		{
			 System.out.println("Unable to write ontology list to file");
			 return false;
		}
		
		
		
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#checkPreconditions()
	 */
	public boolean canOperate() {
		return true;
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#touch()
	 */
	public void touch() {
		fireTableDataChanged();
		
	}
	
	
	
}
