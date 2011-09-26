
package org.cytoscape.model.internal.builder;

import org.cytoscape.model.builder.*;

import org.cytoscape.model.builder.CyRowBuilder;
import org.cytoscape.model.builder.CyNodeBuilder;
import org.cytoscape.model.builder.CyTableBuilder;
import org.cytoscape.model.SUIDFactory;

public final class CyNodeBuilderImpl implements CyNodeBuilder {

	private final CyRowBuilder row; 
	private final long suid;

	public CyNodeBuilderImpl(CyTableBuilder table) {
		suid = SUIDFactory.getNextSUID();
		row = new CyRowBuilderImpl(suid,table);
	}

	public long getSUID() {
		return suid;
	}

	public CyRowBuilder getCyRowBuilder() {
		return row; 
	}
}

