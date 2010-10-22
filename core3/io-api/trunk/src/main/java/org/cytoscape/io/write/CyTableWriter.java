
package org.cytoscape.io.write;

import org.cytoscape.model.CyTable;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 * A utility Task implementation specifically for writing {@link org.cytoscape.model.CyTable} objects.
 */
public final class CyTableWriter extends AbstractCyWriter<CyTableWriterManager> {

	private final CyTable table;

	/**
	 * @param writerManager The {@link org.cytoscape.io.write.CyTableWriterManager} used to determine which 
	 * {@link org.cytoscape.io.write.CyTableWriterFactory} to use to write the file.
	 * @param table The {@link org.cytoscape.model.CyTable} to be written out. 
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
