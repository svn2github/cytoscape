
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;

import java.util.List;
import java.util.ArrayList;


/** 
 * 
 */
public class CyDataTableUtil {
	private CyDataTableUtil() {}

	/**
	 * A utility method that returns a list of nodes that have a boolean attribute
	 * in the CyNetwork.DEFAULT_ATTRS namespace specified by columnName and are in 
	 * the specified state.  If the attribute doesn't exist or is not of type 
	 * Boolean an IllegalArgumentException will be thrown.
	 */
	public static List<CyNode> getNodesInState(final CyNetwork net, final String columnName, final boolean state) {
		if ( net == null )
			throw new NullPointerException("network is null");
		CyDataTable table = net.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		if ( table.getColumnTypeMap().get(columnName) != Boolean.class )
			throw new IllegalArgumentException("colunm name is not a Boolean type");
		List<CyNode> ret = new ArrayList<CyNode>();
		for ( CyNode node : net.getNodeList() )
			if ( node.attrs().get(columnName,Boolean.class) == state )
				ret.add( node );
		return ret;
	}
	
	/**
	 * A utility method that returns a list of edges that have a boolean attribute
	 * in the CyNetwork.DEFAULT_ATTRS namespace specified by columnName and are in 
	 * the specified state.  If the attribute doesn't exist or is not of type 
	 * Boolean an IllegalArgumentException will be thrown.
	 */
	public static List<CyEdge> getEdgesInState(final CyNetwork net, final String columnName, final boolean state) {
		if ( net == null )
			throw new NullPointerException("network is null");
		CyDataTable table = net.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		if ( table.getColumnTypeMap().get(columnName) != Boolean.class )
			throw new IllegalArgumentException("colunm name is not a Boolean type");
		List<CyEdge> ret = new ArrayList<CyEdge>();
		for ( CyEdge edge : net.getEdgeList() )
			if ( edge.attrs().get(columnName,Boolean.class) == state )
				ret.add( edge );
		return ret;
	}
}
