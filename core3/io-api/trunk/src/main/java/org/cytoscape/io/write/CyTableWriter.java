
package org.cytoscape.io.write;

import org.cytoscape.model.CyTable;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 */
public final class CyTableWriter extends AbstractCyWriter<CyTableWriterManager> {

	private final CyTable table;

    public CyTableWriter(CyTableWriterManager writerManager, CyTable table ) {
		super(writerManager);
		if ( table == null )
			throw new NullPointerException("Table is null");
		this.table = table;
	}

	protected CyWriter getWriter(CyFileFilter filter, File file)  throws Exception{
		return writerManager.getWriter(table,filter,file);
	}
}
