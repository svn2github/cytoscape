

package org.cytoscape.model.network.internal;

import org.cytoscape.model.network.GraphObject;
import org.cytoscape.model.network.Identifiable;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesManager;

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
