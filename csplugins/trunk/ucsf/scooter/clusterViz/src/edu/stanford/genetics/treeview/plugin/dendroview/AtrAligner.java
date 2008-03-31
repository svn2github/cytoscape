 /* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: avsegal
 * $RCSfile: AtrAligner.java
 * $Revision: 
 * $Date: Jun 25, 2004
 * $Name:  
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
 
package edu.stanford.genetics.treeview.plugin.dendroview;

/**
 * @author avsegal
 *
 * Aligns the array ordering to match a different array tree. Used statically two
 * align one fileset to another.
 */

import edu.stanford.genetics.treeview.*;
import java.util.*;

public class AtrAligner {
	
    /**
     * 
     * @param atrHeader1 the atr header to be aligned
     * @param arrayHeader1 the array header to be aligned
     * @param atrHeader2 the atr header to align to
     * @param arrayHeader2 the array header to align to
     * @return a new ordering of arrayHeader1
     * @throws DendroException
     */
	public static int [] align(HeaderInfo atrHeader1, HeaderInfo arrayHeader1, HeaderInfo atrHeader2, HeaderInfo arrayHeader2)
		throws DendroException
	{
		int numArrays = arrayHeader1.getNumHeaders();
		int [] newOrder = new int[numArrays];
		AtrAnalysisNode root1;
		
		for(int i = 0; i < numArrays; i++)
		{
			newOrder[i] = i;
		}
		
		root1 = createAnalysisTree(atrHeader1, arrayHeader1);
		
	
		
		
		alignTree(root1, arrayHeader1, arrayHeader2, newOrder);
		
		return newOrder;
	}
	
	/**
	 * Creates an AtrAnalysis tree based on the atr and array headers.
	 * @param atrHeader ATR header
	 * @param arrayHeader array header
	 * @return the root node of the tree
	 * @throws DendroException
	 */
	private static AtrAnalysisNode createAnalysisTree(HeaderInfo atrHeader, HeaderInfo arrayHeader)
		throws DendroException
	{
		int numArrays = arrayHeader.getNumHeaders();
		
		AtrAnalysisNode [] leafNodes = new AtrAnalysisNode[numArrays];
		Hashtable id2node = new Hashtable(((atrHeader.getNumHeaders() * 4) /3)/2, .75f);
		
		String newId, leftId, rightId;
		
		AtrAnalysisNode newN, leftN, rightN;
		
		for(int i = 0; i < atrHeader.getNumHeaders(); i++)
		{
			newId = atrHeader.getHeader(i, "NODEID");
			leftId = atrHeader.getHeader(i, "LEFT");
			rightId = atrHeader.getHeader(i, "RIGHT");
			
			newN = (AtrAnalysisNode)id2node.get(newId);
			leftN = (AtrAnalysisNode)id2node.get(leftId);
			rightN = (AtrAnalysisNode)id2node.get(rightId);
			
			
			if (newN != null) {
				System.out.println("Symbol '" + newId +
				"' appeared twice, building weird tree");
			}
			else
			{
				newN = new AtrAnalysisNode(newId, null);
				id2node.put(newId, newN);
			}				
			
			if (leftN == null) { // this means that the identifier for leftn is a new leaf
				int val; // stores index (y location)
				val = arrayHeader.getHeaderIndex(leftId);
				if (val == -1) {
					throw new DendroException("Identifier " + leftId + " from tree file not found in CDT");
				}
				leftN = new AtrAnalysisNode(leftId, newN);
				leftN.setIndex(val);
				leftN.setName(arrayHeader.getHeader(val, "GID"));
				
				leafNodes[val] = leftN;
				id2node.put(leftId, leftN);
			}
			
			if (rightN == null) { // this means that the identifier for rightn is a new leaf
				//		System.out.println("Looking up " + rightId);
				int val; // stores index (y location)
				val = arrayHeader.getHeaderIndex(rightId);
				if (val == -1) {
					throw new DendroException("Identifier " + rightId + " from tree file not found in CDT.");
				}
				rightN = new AtrAnalysisNode(rightId, newN);
				rightN.setIndex(val);
				rightN.setName(arrayHeader.getHeader(val, "GID"));
				leafNodes[val] = rightN;
				id2node.put(rightId, rightN);
			}
			
			if(leftN.getIndex() > rightN.getIndex())
			{
				AtrAnalysisNode temp = leftN;
				leftN = rightN;
				rightN = temp;	
			}
			
			rightN.setParent(newN);
			leftN.setParent(newN);
			
			newN.setLeft(leftN);
			newN.setRight(rightN);
		}
		
		
		return (AtrAnalysisNode)leafNodes[0].findRoot();
		
	}
	
	/**
	 * Aligns tree rooted at root1 to a different atr tree as best as possible.
	 * @param root1 root of the tree to align
	 * @param arrayHeader1 array header of the tree to align
	 * @param arrayHeader2 array header of the tree to align to
	 * @param ordering the ordering array which this method will fill
	 */
	private static void alignTree(AtrAnalysisNode root1, HeaderInfo arrayHeader1, HeaderInfo arrayHeader2, int [] ordering)
	{
		Vector v1 = new Vector();
		Hashtable gid2index = new Hashtable();
		int gidIndex = arrayHeader2.getIndex("GID");
		
		for(int i = 0; i < arrayHeader2.getNumHeaders(); i++)
		{
			gid2index.put(arrayHeader2.getHeader(i)[gidIndex], new Integer(i));
		}
		
		root1.indexTree(arrayHeader2, gid2index);
		
		
		root1.enumerate(v1);
			
		for(int i = 0; i < v1.size(); i++)
		{
			 ordering[i] = arrayHeader1.getHeaderIndex(((AtrAnalysisNode)v1.get(i)).getID());
		}
		
	}
}
