/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: KnnModel.java,v $
 * $Revision: 1.14 $
 * $Date: 2006/03/20 05:59:52 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview.model;
import java.awt.Menu;
import java.util.Vector;

import edu.stanford.genetics.treeview.*;

public class KnnModel extends TVModel implements DataModel {
	/**
	 * This not-so-object-oriented hack is in those rare instances
	 * where it is not enough to know that we've got a DataModel.
	 */
	public String getType() {
		return "KnnModel";
	}
	
	// accessor methods
	public int getNumArrayClusters() {
		return aClusterMembers.length;
	}
	public int getNumGeneClusters() {
		return gClusterMembers.length;
	}
	
	public int [] getArrayClusters() {
		if (aClusterMembers==null) return null;
		int n = aClusterMembers.length;
		int[] clusters = new int[n];
		for (int i = 0; i < n; i++)
			clusters[i] = aClusterMembers[i].length;
		return clusters;
	};
	public int [] getGeneClusters() {
		if (gClusterMembers==null) return null;
		int n = gClusterMembers.length;
		int[] clusters = new int[n];
		for (int i = 0; i < n; i++)
			clusters[i] = gClusterMembers[i].length;
		return clusters;
	};
	
	public KnnModel() {
		super();
		/* build KnnModel, initially empty... */	
	}
	
	/**
	 *
	 *
	 * @param fileSet fileset to load
	 *
	 */
	public void loadNew(FileSet fileSet) 
	throws LoadException {
		resetState();
		setSource(fileSet);
		final KnnModelLoader loader = new KnnModelLoader(this);
		loader.loadInto(); 
	}
	
	/**
	 * Don't open a loading window...
	 */
	public void loadNewNW(FileSet fileSet) throws LoadException {
		resetState();
		setSource(fileSet);
		final KnnModelLoader loader = new KnnModelLoader(this);
		loader.loadIntoNW(); 
	}
	
	public String[] toStrings() {
		String[] msg = {"Selected KnnModel Stats",
				"Source = " + source.getCdt(),
				"Nexpr   = " + nExpr(),
				"NGeneHeader = " + getGeneHeaderInfo().getNumNames(),
				"Ngene   = " + nGene(),
				"eweight  = " + eweightFound,
				"gweight  = " + gweightFound,
				"aid  = " + aidFound,
				"gid  = " + gidFound
				};
		
		/*
		 Enumeration e = genePrefix.elements();
		 msg += "GPREFIX: " + e.nextElement();
		 for (; e.hasMoreElements() ;) {
		 msg += " " + e.nextElement();
		 }
		 
		 e = aHeaders.elements();
		 msg += "\naHeaders: " + e.nextElement();
		 for (; e.hasMoreElements() ;) {
		 msg += ":" + e.nextElement();
		 }
		 */
		
		return msg;
	}
	private Menu menu; // holds menu to access model stuff...
	static final int gap = 1;
	/**
	 * This method adds a GROUP column to the CDT
	 * 
	 * @param tempTable - RectData object with two columns, the first of gene names and the second of group membership
	 * @param ptype the parse type for error reporting.
	 */
	public void setGClusters(RectData tempTable, int ptype) {
		HeaderInfo geneHeader = getGeneHeaderInfo();
		boolean result = checkCorrespondence(tempTable, geneHeader, ptype);
		if (result) {
			geneHeader.addName("GROUP", geneHeader.getNumNames()-1);
			for (int row = 0; row < geneHeader.getNumHeaders(); row++)
				geneHeader.setHeader(row, "GROUP", tempTable.getString(row, 1));
		}
	}
	public void setAClusters(RectData tempTable, int kagparse) {
		HeaderInfo arrayHeader = getArrayHeaderInfo();
		boolean result = checkCorrespondence(tempTable, arrayHeader, kagparse);
		if (result) {
			arrayHeader.addName("GROUP", arrayHeader.getNumNames()-1);
			for (int row = 0; row < arrayHeader.getNumHeaders(); row++)
				arrayHeader.setHeader(row, "GROUP", tempTable.getString(row, 1));
		}
	}
	public void parseClusters() throws LoadException {
		gClusterMembers =calculateMembership
		(getGeneHeaderInfo(), "GROUP");
		aClusterMembers =calculateMembership
		(getArrayHeaderInfo(), "GROUP");
	}
	public int [][] calculateMembership(HeaderInfo headerInfo, String column) {
		int groupIndex = headerInfo.getIndex(column);
		if (groupIndex < 0) return null;
		int [] counts = getCountVector(headerInfo, groupIndex);
		int [][]members = new int [counts.length][]; 
		for (int i = 0 ; i < counts.length; i++) {
			members[i] = new int[counts[i]];
		}
		populateMembers(members, headerInfo, groupIndex);
		return members;
	}
	private void populateMembers(int[][] members, HeaderInfo headerInfo, int index) {
		int [] counts = new int[members.length];
		for (int i = 0; i < counts.length; i++)
			counts[i] = 0;
		for (int i = 0; i < headerInfo.getNumHeaders(); i++) {
			Integer group = new Integer(headerInfo.getHeader(i,index));
			int g = group.intValue();
			members[g][counts[g]] = i;
			counts[g]++;
		}
	}

	/**
	 * For a column of ints, returns the number of occurences of 
	 * each int in the column.
	 * 
	 * @param headerInfo
	 * @param columnIndex
	 * @return
	 */
	private int [] getCountVector(HeaderInfo headerInfo, int columnIndex) {
		Vector counts = new Vector();
		for (int i = 0; i < headerInfo.getNumHeaders(); i++) {
			Integer group = new Integer(headerInfo.getHeader(i,columnIndex));
			Integer current = (Integer) counts.elementAt(group.intValue());
			Integer insertElement = new Integer(1);
			if (current != null)
				insertElement = new Integer(current.intValue() + 1);
			counts.insertElementAt(insertElement, group.intValue());
		}
		int [] cv = new int [counts.size()];
		for (int i =0; i < cv.length; i++) {
			cv[i] = ((Integer)counts.elementAt(i)).intValue();
		}
		return cv;
	}
	/**
	 * check to see that the order of names in the first column of the temptable
	 * matches the headerinfo.
	 * @param tempTable
	 * @param headerInfo
	 * @param ptype
	 * @return true if it matches
	 */
	private boolean checkCorrespondence(RectData tempTable, HeaderInfo headerInfo, int ptype) {
		// TODO Auto-generated method stub
		return true;
	}
	/** 
	 * averages of gene clusters
	 * should be nGeneCluster() by nExpr()
	 */
	private double [] gClusterData = null;

	/**
	 * holds membership of the gene clusters
	 */
	private int [] gClusterMembers[];
	/** 
	 * holds averages for array clusters
	 * should be nArrayCluster() by nGene()
	 */
	private double [] aClusterData = null;
	/**
	 * holds membership of the array clusters
	 */
	private int [] aClusterMembers[];
	
}

