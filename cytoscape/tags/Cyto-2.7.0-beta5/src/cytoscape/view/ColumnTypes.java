package cytoscape.view;

import javax.swing.Icon;

import cytoscape.util.swing.TreeTableModel;

public enum ColumnTypes {
	NETWORK("Network", TreeTableModel.class), NETWORK_ICONS("Overview",
			Icon.class), NODES("Nodes", String.class), EDGES("Edges",
			String.class);

	private final String displayName;
	private final Class<?> type;

	private ColumnTypes(final String displayName, final Class<?> type) {
		this.displayName = displayName;
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public String getDisplayName() {
		return displayName;
	}
}
