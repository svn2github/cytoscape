

package org.cytoscape.model.network.impl;

import org.cytoscape.model.network.GraphObject;
import org.cytoscape.model.network.Identifiable;

import org.cytoscape.model.attrs.CyAttributes;
import org.cytoscape.model.attrs.impl.CyAttributesManagerImpl;

import java.util.Map;

class GraphObjImpl implements GraphObject, Identifiable {

	private final long suid;
	private final Map<String,CyAttributesManagerImpl> attrMgr;

	GraphObjImpl(final Map<String,CyAttributesManagerImpl> attrMgr) {
		suid = IdFactory.getNextSUID();
		this.attrMgr = attrMgr;
	}

	public long getSUID() {
		return suid;
	}

	public CyAttributes getCyAttributes(String namespace) {
        if ( namespace == null )
            throw new NullPointerException("namespace is null");

        // argh!
        CyAttributesManagerImpl mgr = attrMgr.get(namespace);
        if ( mgr == null )
            throw new NullPointerException("attribute manager is null for namespace: " + namespace);

        return mgr.getAccess(suid);
	}
}
