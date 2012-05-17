package org.cytoscape.extras.event_tracker.internal;

import javax.swing.event.TableModelEvent;

public final class EventDataTableModel extends AbstractTableModel<EventData> {
	boolean useShortName;
	
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		case 2:
			return Integer.class;
		}
		return null;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Event";
		case 1:
			return "Occurrences";
		case 2:
			return "Payloads";
		}
		return null;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		EventData data = getRow(rowIndex);
		switch (columnIndex) {
		case 0:
			if (useShortName) {
				return data.getShortName();
			} else {
				return data.getFullName();
			}
		case 1:
			return data.getCount();
		case 2:
			return data.getPayloadCount();
		}
		return null;
	}
	
	public void setUseShortName(boolean useShortName) {
		if (useShortName == this.useShortName) {
			return;
		}
		this.useShortName = useShortName;
		notifyListeners(new TableModelEvent(this, 0, getRowCount() - 1, 0));
	}

	public void reset() {
		synchronized (this) {
			for (EventData row : rows) {
				row.reset();
			}
		}
		notifyListeners(new TableModelEvent(this, 0, getRowCount() - 1));
	}
}