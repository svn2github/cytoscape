/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.cytoscape;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cytoscape.CyNode;
import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.RDFResourceWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyExtAction extends AbstractAction{
	private RDFScape myRDFScapeInstance=null;
	private RDFResourceWrapper subject;
	private RDFResourceWrapper property;
	private RDFResourceWrapper object;
	
	private CommonMemory myMemory;
	private AbstractQueryResultTable table;
	private boolean isIncoming;
	private int row;
	private CyNode node;
	
	/**
	 * @param myRDFScapeInstance
	 * @param objectWrapped
	 * @param propertyWrapped
	 * @param subjectWrapped
	 * @param tempMenuString
	 * 
	 */
	public MyExtAction(String tempMenuString, CyNode n, AbstractQueryResultTable t, boolean ii, int r,CommonMemory mm) {
		super(tempMenuString);
		table=t;
		isIncoming=ii;
		row=r;
		node=n;
		myMemory=mm;
		
	}

	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		CytoscapeDealer cd=RDFScape.getCytoscapeDealer();
		if(isIncoming)cd.addIncomingEdge(node,table,row,myMemory);
		if(!isIncoming)cd.addOutgoingEdge(node,table,row,myMemory);
		
	}
}
