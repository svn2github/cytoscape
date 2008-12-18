
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

package org.cytoscape.model.internal; 

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.event.CyEventHelper;

/**
 * An interface describing a factory used for creating 
 * {@link CyDataTable} objects.  This factory will be
 * provided as a service through Spring/OSGi.
 */
class CyDataTableFactoryImpl implements CyDataTableFactory {

	private final Map<Long,CyDataTable> tables;
	private final CyEventHelper help;

	CyDataTableFactoryImpl(CyEventHelper help) {
		this.help = help;
		tables = new HashMap<Long,CyDataTable>();
	}

	/**
	 * @param name The name of the CyDataTable.
	 * @param pub Whether or not the CyDataTable should be public.
	 *
	 * @return A new {@link CyDataTable} with the specified name that is either public or not (see
	 *         {@link CyDataTable#isPublic}.
	 */
	public CyDataTable createTable(String name, boolean pub) {
		CyDataTable cdt = new CyDataTableImpl(null,name,pub,help);
		tables.put( cdt.getSUID(), cdt );
		return cdt;
	}

	/**
     * @param includePrivate Whether to include private CyDataTables
     * in the list (i.e. all possible CyDataTables) or not.
     * @return A list containing CyDataTable SUIDs either
     * including private CyDataTables (i.e. meaning all possible
     * CyDataTables) or just public CyDataTables.
     */
	public List<Long> getAllTableSUIDs(boolean includePrivate) {
		List<Long> suids = new ArrayList<Long>(tables.keySet().size());
		for ( Long key : tables.keySet() ) {
			if ( includePrivate )
				suids.add(key);
			else if ( tables.get(key).isPublic() )
				suids.add(key);
		}
		return suids;
	}

	/**
	 * @param suid The SUID identifying the CyDataTable.
	 *
	 * @return The CyDataTable identified by the suid. Will return null if a CyDataTable doesn't
	 *         exist for the  specified SUID.
	 */
	public CyDataTable getTable(long suid) {
		return tables.get(suid);
	}
}
