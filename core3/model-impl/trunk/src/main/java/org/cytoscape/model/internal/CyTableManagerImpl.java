
/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model.internal; 

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableManager;

/**
 * An interface describing a factory used for managing 
 * {@link CyTable} objects.  This class will be
 * provided as a service through Spring/OSGi.
 */
public class CyTableManagerImpl implements CyTableManager {

	private final Map<String, Map<CyNetwork, Map<String,CyTable>>> map;

	public CyTableManagerImpl() {
		map = new HashMap<String, Map<CyNetwork, Map<String,CyTable>>>();	
		map.put( "NETWORK", new HashMap<CyNetwork, Map<String,CyTable>>() );
		map.put( "NODE", new HashMap<CyNetwork, Map<String,CyTable>>() );
		map.put( "EDGE", new HashMap<CyNetwork, Map<String,CyTable>>() );
	}
	

	public Map<String,CyTable> getTableMap(final String graphObjectType, final CyNetwork network) {
		if ( network == null || graphObjectType == null )
			return null;

		Map<CyNetwork, Map<String,CyTable>> tmap = map.get(graphObjectType);

		if ( tmap == null )
			throw new IllegalArgumentException("no data tables of type: " + graphObjectType + " exist");

		return map.get(graphObjectType).get(network);
	}

	
	public void setTableMap(final String graphObjectType, final CyNetwork network, final Map<String,CyTable> tm) {
		if ( network == null )
			throw new NullPointerException("CyNetwork is null");
		if ( graphObjectType == null )
			throw new NullPointerException("Type is null");

		if ( !map.containsKey(graphObjectType) )
			map.put(graphObjectType, new HashMap<CyNetwork, Map<String,CyTable>>());

		Map<CyNetwork, Map<String,CyTable>> tmap = map.get(graphObjectType);

		if ( tm == null )
			tmap.remove(network);
		else
			tmap.put(network,tm);
	}

}
