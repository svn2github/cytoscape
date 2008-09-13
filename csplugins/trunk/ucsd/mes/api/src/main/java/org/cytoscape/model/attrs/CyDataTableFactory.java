package org.cytoscape.model.attrs;

import java.util.List;

public interface CyDataTableFactory {
	CyDataTable createTable(String name, boolean pub);
	List<String> getAllTableNames(boolean includePrivate);
}

