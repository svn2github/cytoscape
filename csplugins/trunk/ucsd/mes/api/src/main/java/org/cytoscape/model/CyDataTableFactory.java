package org.cytoscape.model;

import java.util.List;

/**
 * An interface describing a factory used for creating 
 * {@link CyDataTable} objects.  This factory will be
 * provided as a service through Spring/OSGi.
 */
public interface CyDataTableFactory {
	/**
	 * @param name The name of the CyDataTable.
	 * @param pub Whether or not the CyDataTable should be public.
	 * @return A new {@link CyDataTable} with the specified name
	 * that is either public or not (see {@link CyDataTable#isPublic}.
	 */
	CyDataTable createTable(String name, boolean pub);

	/**
	 * @param includePrivate Whether to include private CyDataTables
	 * in the list (i.e. all possible CyDataTables) or not.
	 * @return A list containing CyDataTable SUIDs either
	 * including private CyDataTables (i.e. meaning all possible
	 * CyDataTables) or just public CyDataTables.
	 */
	List<Long> getAllTableSUIDs(boolean includePrivate);

	/**
	 * @param suid The SUID identifying the CyDataTable.
	 * @return The CyDataTable identified by the suid. Will
	 * return null if a CyDataTable doesn't exist for the 
	 * specified SUID.
	 */
	CyDataTable getTable(long suid);
}

