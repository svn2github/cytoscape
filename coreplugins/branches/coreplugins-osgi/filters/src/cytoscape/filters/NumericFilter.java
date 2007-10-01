
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

import giny.model.Edge;
import giny.model.Node;

import java.util.*;

import csplugins.quickfind.util.QuickFind;
import csplugins.widgets.autocomplete.index.TextIndex;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.NumberIndex;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NumericFilter<T extends Number> extends AtomicFilter {

	private T lowBound, highBound;
	private NumberIndex numberIndex = null;

	/**
	 * Creates a new NumericFilter, parameterized with interger/float/double
	 */
	public NumericFilter(String pName, String pControllingAttribute, T pLowBound, T pUpBound) {
		super(pName,pControllingAttribute);
		//controllingAttribute = pControllingAttribute;
		lowBound = pLowBound;
		highBound = pUpBound;
	}
	
	public boolean passesFilter(Object obj) {
		return false;
	}
	public T getLowBound(){
		return lowBound;
	}

	public T getHighBound(){
		return highBound;
	}

	public void setLowBound(T pLowBound){
		lowBound = pLowBound;
	}

	public void setUpBound(T pUpBound){
		highBound = pUpBound;
	}

	public void setRange(T pLowBound, T pUpBound){
		lowBound = pLowBound;
		highBound = pUpBound;
	}

	public NumberIndex getNumberIndex() {
		return numberIndex;
	}
	
	public void setNumberIndex(NumberIndex pNumberIndex) {
		numberIndex = pNumberIndex;
	}

	
	public void apply() {
		
		if (network == null) {
			network = Cytoscape.getCurrentNetwork();					
		}
		
		List<Node> nodes_list = null;
		List<Edge> edges_list=null;

		if (index_type == QuickFind.INDEX_NODES) {
			nodes_list = network.nodesList();
			objectCount = nodes_list.size();
		}
		else if (index_type == QuickFind.INDEX_EDGES) {
			edges_list = network.edgesList();
			objectCount = edges_list.size();
		}
		else {
			System.out.println("StringFilter: Index_type is undefined.");
			return;
		}

		bits = new BitSet(objectCount); // all the bits are false initially

		//System.out.println(" NumberFilter.apply(): objectCount = " + objectCount);
		List list = numberIndex.getRange(lowBound, highBound);

		if (list.size() == 0) {
			return;
		}

		int index;		
		if (index_type == QuickFind.INDEX_NODES) {
			for (Object obj : list) {
				index = nodes_list.lastIndexOf((Node) obj);
				bits.set(index, true);
			}
		} else if (index_type == QuickFind.INDEX_EDGES) {
			for (Object obj : list) {
				index = edges_list.lastIndexOf((Edge) obj);
				bits.set(index, true);
			}
		}		
		
		if (negation) {
			bits.flip(0, objectCount);
		}		
	}

	/**
	 * 
	 */
	public String toString() {
		return "NumericFilter="+name+":"+controllingAttribute + ":" + negation+ ":"+lowBound+":" + highBound+ ":"+index_type;
	}
	
	//public NumericFilter clone() {
	//	return new NumericFilter(attributeName, searchValues);
	//}

}
