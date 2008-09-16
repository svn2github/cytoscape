
package org.cytoscape.model.events;

import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnCreatedEvent;

import org.cytoscape.model.CyDataTable;

public class AttrEvent 
	implements ColumnCreatedEvent, 
			   ColumnDeletedEvent {

	public static ColumnDeletedEvent getAttributeDeletedEvent(CyDataTable source, String columnName) {
		return new AttrEvent(source,columnName,null,null);
	}

	public static ColumnCreatedEvent getAttributeCreatedEvent(CyDataTable source, String columnName) {
		return new AttrEvent(source,columnName,null,null);
	}

	final Object oldValue;
	final Object newValue;
	final CyDataTable source;
	final String columnName; 

	private AttrEvent(CyDataTable source, String columnName, Object oldValue, Object newValue) {
		this.source = source;
		this.columnName = columnName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public CyDataTable getSource() {
		return source;
	}

	public String getColumnName() {
		return columnName;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}
}
