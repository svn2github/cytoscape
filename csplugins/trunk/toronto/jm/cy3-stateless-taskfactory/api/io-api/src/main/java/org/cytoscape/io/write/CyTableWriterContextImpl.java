package org.cytoscape.io.write;

import org.cytoscape.model.CyTable;

public class CyTableWriterContextImpl extends CyWriterContextImpl implements CyTableWriterContext {
	private CyTable table;

	@Override
	public CyTable getTable() {
		return table;
	}
	
	@Override
	public void setTable(CyTable table) {
		this.table = table;
	}
}
