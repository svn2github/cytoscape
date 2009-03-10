
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

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.Identifiable;

import java.util.Map;


class GraphObjImpl implements GraphObject, Identifiable {
	private final long suid;
	private final Map<String, CyDataTable> attrMgr;

	GraphObjImpl(final Map<String, CyDataTable> attrMgr) {
		suid = IdFactory.getNextSUID();
		this.attrMgr = attrMgr;
		attrs().set("name","");
		attrs().set("selected",Boolean.FALSE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public long getSUID() {
		return suid;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param namespace DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyRow getCyRow(String namespace) {
		if (namespace == null)
			throw new NullPointerException("namespace is null");

		CyDataTable mgr = attrMgr.get(namespace);

		if (mgr == null)
			throw new NullPointerException("attribute manager is null for namespace: " + namespace);

		return mgr.getRow(suid);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyRow attrs() {
		return getCyRow(CyNetwork.DEFAULT_ATTRS);
	}

    public @Override
    boolean equals(Object o) {
        if (!(o instanceof GraphObjImpl))
            return false;

        GraphObjImpl ir = (GraphObjImpl) o;

        if (ir.suid == this.suid)
            return true;
        else

            return false;
    }

    public @Override
    int hashCode() {
        return (int) (suid ^ (suid >>> 32));
    }
}
