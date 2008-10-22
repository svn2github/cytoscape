

package org.cytoscape.model.internal;

import org.cytoscape.model.GraphObject;
import org.cytoscape.model.Identifiable;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;

import java.util.Map;

class GraphObjImpl implements GraphObject, Identifiable {

	private final long suid;
	private final Map<String,CyAttributesManager> attrMgr;

	GraphObjImpl(final Map<String,CyAttributesManager> attrMgr) {
		suid = IdFactory.getNextSUID();
		this.attrMgr = attrMgr;
	}

	public long getSUID() {
		return suid;
	}

	public CyAttributes getCyAttributes(String namespace) {
        if ( namespace == null )
            throw new NullPointerException("namespace is null");

        CyAttributesManager mgr = attrMgr.get(namespace);
        if ( mgr == null )
            throw new NullPointerException("attribute manager is null for namespace: " + namespace);

        return mgr.getCyAttributes(suid);
	}
}
