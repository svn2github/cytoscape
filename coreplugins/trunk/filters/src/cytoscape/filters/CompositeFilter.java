
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.filters;

import java.util.Vector;
//import cytoscape.filters.util.FilterUtil;

import java.util.*;

import csplugins.quickfind.util.QuickFind;

public class CompositeFilter implements CyFilter {

	private List<CyFilter> children;
	private boolean negation;
	//Relation relation;
	private String name;
	private BitSet node_bits, edge_bits;
	private boolean childChanged;
	private CyFilter parent;
	private String depthString;
	private String description;
	private AdvancedSetting advancedSetting = null;

	public CompositeFilter(String pName, AdvancedSetting pSetting) {
		name = pName;
		advancedSetting = pSetting;
		if (pSetting == null) {
			advancedSetting = new AdvancedSetting();
		}
		children = new LinkedList<CyFilter>();

		// so we calculate the first time through
		childChanged = true;
	}

	//Create an empty CompositeFilter, with default Relation.AND
	public CompositeFilter(String pName) {
		name = pName;
		advancedSetting = new AdvancedSetting();
		children = new LinkedList<CyFilter>();
	}
	
	public CompositeFilter(String pName, Relation pRelation) {
		name = pName;
		advancedSetting = new AdvancedSetting();
		advancedSetting.setRelation(pRelation);
		children = new LinkedList<CyFilter>();
	}

	public boolean passesFilter(Object obj) {
		return false;
	}
	
	public void setNegation(boolean pNegation) {
		negation = pNegation;
	}
	public boolean getNegation() {
		return negation;
	}
	
	public BitSet getEdgeBits() {
		System.out.println("CompositeFilter.getEdgeBits not implemented");
		return null;
	}
	
	public BitSet getNodeBits() {
		// only recalculate the bits if the child has actually changed
		if ( !childChanged ) 
			return node_bits;

		//System.out.println(depthString + " " + name + " recalculate");
		
		// if there are no children, just return an empty bitset
		if ( children.size() <= 0 ) {
			node_bits = new BitSet();
			return node_bits;
		}

		// set the initial bits to a clone of the first child
		if (children.get(0) instanceof AtomicFilter) {
			AtomicFilter n_atomic = (AtomicFilter) children.get(0);
			node_bits = (BitSet) n_atomic.getBits().clone();			
		}

		// now perform the requested relation with each subsequent child
		for ( int i = 1; i < children.size(); i++ ) {
			CyFilter n = children.get(i);
			if ( advancedSetting.getRelation() == Relation.AND ) {	
				if (n instanceof AtomicFilter) {
					AtomicFilter n_atomic = (AtomicFilter) n;
					if (n_atomic.index_type == QuickFind.INDEX_NODES) {
						node_bits.and(n_atomic.getBits());						
					}
					else { //QuickFind.INDEX_EDGES
						return new BitSet();
					}
				}
				else if (n instanceof CompositeFilter) {
					CompositeFilter n_composite = (CompositeFilter) n;
					node_bits.and(n_composite.getNodeBits());					
				}
			} else if ( advancedSetting.getRelation() == Relation.OR ) {
				if (n instanceof AtomicFilter) {
					AtomicFilter n_atomic = (AtomicFilter) n;
					if (n_atomic.index_type == QuickFind.INDEX_NODES) {
						node_bits.or(n_atomic.getBits());
					}
					else { //QuickFind.INDEX_EDGES
						// do nothing
					}
				}
				else if (n instanceof CompositeFilter) {
					CompositeFilter n_composite = (CompositeFilter) n;
					node_bits.or(n_composite.getNodeBits());					
				}
			} else if ( advancedSetting.getRelation() == Relation.XOR ) {
				System.out.println("CompositeFilter: Relation.XOR: not implemented");
				//node_bits.xor(n.getBits());
			} else if ( advancedSetting.getRelation() == Relation.NAND ) {
				System.out.println("CompositeFilter: Relation.NAND: not implemented");
				//node_bits.andNot(n.getBits());
			}
		}

		// record that we've calculated the bits
		childChanged = false;

		return node_bits;
	}

	public void removeChild( CyFilter pChild ) {
		
		children.remove(pChild);		
		childChanged();		
	}

	public void removeChildAt( int pChildIndex ) {
		children.remove(pChildIndex);		
		childChanged();		
	}

	public void addChild( CyFilter pChild ) {
		children.add( pChild );

		// so the the child can communicate with us 
		// (i.e. so we know when the child changes)
		pChild.setParent(this);

		// to force this class to recalculate and to
		// notify parents
		childChanged();
	}

	// called by any children
	public void childChanged() {
		childChanged = true;
		// pass the message on to the parent
		if ( parent != null )
			parent.childChanged();
	}

	public void setParent(CyFilter f) {
		parent = f;
	}

	public void print(int depth) {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < depth; i++ )
	            sb.append("  ");
		depthString = sb.toString();

		System.out.println(depthString + name + "  " + advancedSetting.getRelation() ); 
		for ( CyFilter c : children )
			c.print(depth + 1);
		//System.out.println(depthString +  getBits() );
		if ( depth == 0 )
			System.out.println();
	}
	public List<CyFilter> getChildren() {
		return children;		
	}

	public String getName() {
		return name;
	}
	
	public void setName(String pName) {
		name = pName;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public Relation getRelation() {
		return advancedSetting.getRelation();
	}

	public void setRelation(Relation pRelation) {
		advancedSetting.setRelation(pRelation);
	}
	
	public AdvancedSetting getAdvancedSetting() {
		return advancedSetting;
	}

	public void setAdvancedSetting(AdvancedSetting pAdvancedSetting) {
		advancedSetting = pAdvancedSetting;
	}

	/**
	 * @return the string represention of this Filter.
	 */
	public String toString()
	{
		String retStr = "\n<Composite>\n";
		
		retStr = retStr + "name:" + name + "\n";
		retStr = retStr + advancedSetting.toString() + "\n";
		retStr = retStr + "Negation:" + negation + "\n";

		for (int i=0; i< children.size(); i++) {

			if (children.get(i) instanceof AtomicFilter) {
				AtomicFilter atomicFilter = (AtomicFilter)children.get(i);
				retStr = retStr + atomicFilter.toString()+"\n";
			}
			else  {// it is a CompositeFilter
				retStr = retStr + "CompositeFilter=" + children.get(i).getName()+"\n";
			}
		}
		retStr += "</Composite>\n";

		return retStr;
	}

	/**
	 */
	public boolean equals(Object other_object) {
		if (!(other_object instanceof CompositeFilter)) {
			return false;
		}
		CompositeFilter theOtherFilter = (CompositeFilter) other_object;
		
		if (theOtherFilter.toString().equalsIgnoreCase(this.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * CompositeFilter may be cloned.
	 */
	public Object clone() {
		return null;
	}	

}
