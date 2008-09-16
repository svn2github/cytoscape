

package org.cytoscape.model.internal;

import org.cytoscape.model.GraphObject;
import org.cytoscape.model.Identifiable;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;

import java.util.Map;

class GraphObjImpl implements GraphObject, Identifiable {

	private final long suid;
	private final Map<String,CyDataTable> attrMgr;

	GraphObjImpl(final Map<String,CyDataTable> attrMgr) {
		suid = IdFactory.getNextSUID();
		this.attrMgr = attrMgr;
	}

	public long getSUID() {
		return suid;
	}

	public CyRow attrs() {
		return getCyRow("USER");
	}

	public CyRow getCyRow(String namespace) {
        if ( namespace == null )
            throw new NullPointerException("namespace is null");

        // argh!
        CyDataTable table = attrMgr.get(namespace);
        if ( table == null )
            throw new NullPointerException("attribute manager is null for namespace: " + namespace);

        return table.getRow(suid);
	}
}
