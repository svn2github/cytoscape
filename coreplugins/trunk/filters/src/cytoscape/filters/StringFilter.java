
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

import giny.model.Node;
import giny.model.Edge;
import java.util.*;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.TextIndex;
import cytoscape.Cytoscape;
import csplugins.quickfind.util.QuickFind;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class StringFilter extends AtomicFilter {

	private String searchStr = null;
	private TextIndex textIndex = null;

	//---------------------------------------//
	// Constructor
	//----------------------------------------//

	/**
	 * Creates a new StringPatternFilter object.
	 *
	 * @param desc  DOCUMENT ME!
	 */
	
	public StringFilter() {
		super();
	}
	
	//public StringFilter(String pName, String pControllingAttribute, String pSearchStr) {
	//	super(pName, pControllingAttribute,QuickFind.INDEX_NODES);
		//controllingAttribute = pControllingAttribute;
	//	searchStr = pSearchStr;
	//}

	public String getSearchStr() {
		return searchStr;
	}
	
	public void setSearchStr(String pSearchStr) {
		searchStr = pSearchStr;
	}

	//public TextIndex getTextIndex() {
	//	return textIndex;
	//}
	
	//public void setTextIndex(TextIndex pTextIndex) {
	//	textIndex = pTextIndex;
	//}


	public boolean passesFilter(Object obj) {
		return false;
	}

	/**
	 * Caculate the bitSet based on the existing TextIndex and search string.
	 * The size of the bitSet is the number of nodes in the given network,
	 * All bits are initially set to false, those with hits are set to true.
	 * @param none.
	 * @return bitSet Object.
	 */	
	
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
		
		bits = new BitSet(objectCount); // all the bits are false at very beginning

		Hit[] hits = textIndex.getHits(searchStr, Integer.MAX_VALUE);

		if (hits.length == 0) {
			return;
		}
		Hit hit0 = hits[0];

		Object[] hit_objs = hit0.getAssociatedObjects();

		int index=-1;
		if (index_type == QuickFind.INDEX_NODES) {
			for (Object obj : hit_objs) {
				index = nodes_list.lastIndexOf((Node) obj);	
				bits.set(index, true);
			}
		} else if (index_type == QuickFind.INDEX_EDGES){
			for (Object obj : hit_objs) {
				index = edges_list.lastIndexOf((Edge) obj);
				bits.set(index, true);
			}
		}
		
		if (negation) {
			bits.flip(0, objectCount);
		}
	}
		
	/**
	 * @return the name of this Filter and the search string (keyword).
	 */
	public String toString() {
		return "StringFilter="+controllingAttribute+":" + negation+ ":"+searchStr+":"+index_type;
	}
/*
	public StringFilter clone() {
		StringFilter newStringFilter = new StringFilter("copy_of_"+name, controllingAttribute, searchStr);
		newStringFilter.setNegation(negation);
		return newStringFilter;
	}
*/
	
}
