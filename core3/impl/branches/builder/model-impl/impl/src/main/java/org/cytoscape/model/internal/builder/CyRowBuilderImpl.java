
package org.cytoscape.model.internal.builder;

import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.builder.CyTableBuilder;
import org.cytoscape.model.builder.CyRowBuilder;

public final class CyRowBuilderImpl implements CyRowBuilder {

	private final CyTableBuilder tableBuilder;	
	private final Object primaryKey;

	public CyRowBuilderImpl(Object primaryKey, CyTableBuilder tableBuilder) {
		this.primaryKey = primaryKey;
		this.tableBuilder = tableBuilder;
	}

	public void set(String columnName, Object value) {
		if ( columnName != null && value != null )
			tableBuilder.setColumnValue(columnName,primaryKey,value);
	}
}

