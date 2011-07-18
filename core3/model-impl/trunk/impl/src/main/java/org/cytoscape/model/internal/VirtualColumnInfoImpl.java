package org.cytoscape.model.internal;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;

public class VirtualColumnInfoImpl implements VirtualColumnInfo {

	private final boolean isVirtual;
	private final CyTable sourceTable;
	private final String sourceColumn;
	private final String sourceJoinKey;
	private final String targetJoinKey;

	public VirtualColumnInfoImpl(boolean isVirtual, CyTable sourceTable, String sourceColumn, String sourceJoinKey, String targetJoinKey) {
		this.isVirtual = isVirtual;
		this.sourceTable = sourceTable;
		this.sourceColumn = sourceColumn;
		this.sourceJoinKey = sourceJoinKey;
		this.targetJoinKey = targetJoinKey;
	}
	
	@Override
	public boolean isVirtual() {
		return isVirtual;
	}

	@Override
	public String getSourceColumn() {
		return sourceColumn;
	}

	@Override
	public String getSourceJoinKey() {
		return sourceJoinKey;
	}

	@Override
	public String getTargetJoinKey() {
		return targetJoinKey;
	}

	@Override
	public CyTable getSourceTable() {
		return sourceTable;
	}

}
