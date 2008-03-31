/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: avsegal
 * $RCSfile: AtrAnalysisNode.java
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
 * Binary tree node for analysis of array trees. The tree is parent-linked.
 */

import edu.stanford.genetics.treeview.*;
import java.util.*;

public class AtrAnalysisNode extends TreeAnalysisNode {

	/**
	 * Creates a new node.
	 * @param pID ID of the node in the ATR file
	 */
	public AtrAnalysisNode(String pID) {
		super(pID);
		leafCount = -1;
		averageIndex = -1;
		name = "";
	}

	/**
	 * Creates a new node with a given parent.
	 * @param pID ID of the node in the ATR file
	 * @param pParent parent of this node
	 */
	public AtrAnalysisNode(String pID, TreeAnalysisNode pParent) {
		super(pID, pParent);
		leafCount = -1;
		averageIndex = -1;
		name = "";
	}
	
	/**
	 * Sets the name of this node.
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Returns the node's name.
	 * @return the node's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the average of all leaf weights in this subtree. This is used in the alignment algorithm.
	 * @return the average index of leaves
	 */
	public double getAverageSubtreeIndex()
	{
		double sum = 0;
		double num = 0;
		
		Vector v = new Vector();
		
		enumerate(v);
		
		for(int i = 0; i < v.size(); i++)
		{
			if(((TreeAnalysisNode)v.elementAt(i)).isLeaf())
			{
				sum += ((TreeAnalysisNode)v.elementAt(i)).getIndex();
				num++;
			}
		}
		
		return sum/num;
	}
	
	/**
	 * Returns a vector of all leaves in this subtree, in order.
	 * @param v the vector to fill with leaves
	 */
	
	public void enumerate(Vector v)
	{
		if(left != null)
		{
			left.enumerate(v);
		}
	
		if(isLeaf())
		{
			v.add(this);
		}
	
		if(right != null)
		{
			right.enumerate(v);
		}		
	}

	/**
	 * Gets the number of leaves.
	 * @return the number of leaves in this subtree
	 */
	public int getLeafCount()
	{
		if(leafCount == -1)
		{
			if(isLeaf())
			{
				leafCount = 1;
			}
			else	
			{
				leafCount = 0;
				if(left != null)
				{
					leafCount += ((AtrAnalysisNode)left).getLeafCount();
				}
				else if(right != null)
				{
					leafCount += ((AtrAnalysisNode)right).getLeafCount();
				}
			}
			
		}
		
		return leafCount;
	}
	
	/**
	 * Calculates the average index of all nodes.
	 * @param arrayHeader the arrayHeader to use for index look up
	 * @param gid2index hashtable for reverse index look up (by array name)
	 * @return the average index for this subtree
	 */
	private double computeAverageIndexTree(HeaderInfo arrayHeader, Hashtable gid2index)
	{
		
		double leftSum = 0, rightSum = 0;
		if(isLeaf())
		{
			int val = 0;
			try {
				val = ((Integer)gid2index.get(getName())).intValue();
			} catch (java.lang.NullPointerException ex) {
				leafCount = 0;
				
				//do nothing, since we want to ignore non-matched aspects of mostly equivelent trees.
			}
			setIndex(val);
			averageIndex = val;
		}
		else
		{
			leftSum = ((AtrAnalysisNode)left).computeAverageIndexTree(arrayHeader, gid2index);
			rightSum = ((AtrAnalysisNode)right).computeAverageIndexTree(arrayHeader, gid2index);
	
			
			leftSum *= ((AtrAnalysisNode)left).getLeafCount();
			rightSum *= ((AtrAnalysisNode)right).getLeafCount();	
			
			averageIndex = (leftSum + rightSum)/(((AtrAnalysisNode)left).getLeafCount() + ((AtrAnalysisNode)right).getLeafCount());
			
			
		}

		return averageIndex;
	}
	
	/**
	 * Rearranged the tree by average index.
	 *
	 */
	private void arrangeByAverageIndex()
	{
		if(left == null || right == null)
		{
			return;
		}
		
		AtrAnalysisNode temp;
		
		if(((AtrAnalysisNode)left).getAverageIndex() > ((AtrAnalysisNode)right).getAverageIndex())
		{
			temp = (AtrAnalysisNode)left;
			left = right;
			right = temp;
		}
		
		((AtrAnalysisNode)left).arrangeByAverageIndex();
		((AtrAnalysisNode)right).arrangeByAverageIndex();
	}
	
	/**
	 * Calculates all the indecies.
	 * @param arrayHeader the arrayHeader to use for index look up
	 * @param gid2index hashtable for reverse index look up (by array name)
	 */
	public void indexTree(HeaderInfo arrayHeader, Hashtable gid2index)
	{
		computeAverageIndexTree(arrayHeader, gid2index);
		arrangeByAverageIndex();
	}
	
	/**
	 * Gets the average index of this subtree.
	 * @return the average index of this subtree.
	 */
	public double getAverageIndex()
	{
		return averageIndex;
	}
	
	int leafCount;
	double averageIndex;
	String name;
	
}