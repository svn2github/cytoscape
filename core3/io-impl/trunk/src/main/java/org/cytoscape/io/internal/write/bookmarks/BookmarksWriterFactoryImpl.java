package org.cytoscape.io.internal.write.bookmarks;

import java.io.OutputStream;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.internal.write.AbstractPropertyWriterFactory;

public class BookmarksWriterFactoryImpl extends AbstractPropertyWriterFactory {
	
	public BookmarksWriterFactoryImpl(CyFileFilter filter) {
		super(filter);
	}
	
	@Override
	public CyWriter getWriter() {
		return new BookmarksWriterImpl(outputStream, props);
	}
}
