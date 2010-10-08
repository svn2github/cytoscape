
package org.cytoscape.io.write;

import org.cytoscape.model.CyTable;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 * A utility Task implementation specifically for writing CyTable objects.
 */
public final class CyTableWriter extends AbstractCyWriter<CyTableWriterManager> {

	private final CyTable table;

	/**
	 * @param writerManager The CyTableWriterManager used to determine which 
	 * CyTableWriterFactory to use to write the file.
	 * @param table The CyTable to be written out. 
 	 */
    public CyTableWriter(CyTableWriterManager writerManager, CyTable table ) {
		super(writerManager);
		if ( table == null )
			throw new NullPointerException("Table is null");
		this.table = table;
	}

	/**
	 * {@inheritDoc}
	 */
	protected CyWriter getWriter(CyFileFilter filter, File file)  throws Exception{
		return writerManager.getWriter(table,filter,file);
	}
}
