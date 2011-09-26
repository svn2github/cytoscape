
package org.cytoscape.model.internal.builder;

import org.cytoscape.model.builder.CyEdgeBuilder;
import org.cytoscape.model.builder.CyRowBuilder;
import org.cytoscape.model.builder.CyNodeBuilder;
import org.cytoscape.model.builder.CyTableBuilder;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.SUIDFactory;

public final class CyEdgeBuilderImpl implements CyEdgeBuilder {

	private final CyRowBuilder row; 
	private final long suid;
	private final CyNodeBuilder source;
	private final CyNodeBuilder target;
	private final boolean directed;

	public CyEdgeBuilderImpl(CyNodeBuilder source, CyNodeBuilder target, boolean directed, CyTableBuilder table) {
		this.source = source;
		this.target = target;
		this.directed = directed;
		this.suid = SUIDFactory.getNextSUID();
		this.row = new CyRowBuilderImpl(suid,table);
	}

	public long getSUID() {
		return suid;
	}

	public CyRowBuilder getCyRowBuilder() {
		return row; 
	}

	public CyNodeBuilder getSource() {
		return source;
	}

	public CyNodeBuilder getTarget() {
		return target;
	}

	public boolean isDirected() {
		return directed;
	}
}

