package org.cytoscape.io.internal.write.session;

import org.cytoscape.session.CySession;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.CySessionWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CyPropertyWriterManager;
import java.io.OutputStream;

public class SessionWriterFactoryImpl implements CySessionWriterFactory {
	
	private final CyFileFilter thisFilter;
	private final CyFileFilter xgmmlFilter;
	private final CyFileFilter bookmarksFilter;
	private final CyFileFilter cysessionFilter;
	private final CyFileFilter propertiesFilter;
	private final CyFileFilter tableFilter;
	private final CyNetworkViewWriterManager networkViewWriterMgr;
	private final CyPropertyWriterManager propertyWriterMgr;
	private final CyTableWriterManager tableWriterMgr;

	private OutputStream outputStream;
	private CySession session;



	public SessionWriterFactoryImpl(final CyFileFilter thisFilter, 
	                                final CyFileFilter xgmmlFilter, 
	                                final CyFileFilter bookmarksFilter, 
	                                final CyFileFilter cysessionFilter, 
	                                final CyFileFilter propertiesFilter,
	                                final CyFileFilter tableFilter,
	                                final CyNetworkViewWriterManager networkViewWriterMgr, 
	                                final CyPropertyWriterManager propertyWriterMgr,
	                                final CyTableWriterManager tableWriterMgr) {
		this.thisFilter = thisFilter;
		this.xgmmlFilter = xgmmlFilter;
		this.bookmarksFilter = bookmarksFilter;
		this.cysessionFilter = cysessionFilter;
		this.propertiesFilter = propertiesFilter;
		this.tableFilter = tableFilter;
		this.networkViewWriterMgr = networkViewWriterMgr;
		this.propertyWriterMgr = propertyWriterMgr;
		this.tableWriterMgr = tableWriterMgr;
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new SessionWriterImpl(outputStream, session, networkViewWriterMgr, 
		                             propertyWriterMgr, tableWriterMgr, xgmmlFilter,
		                             bookmarksFilter, cysessionFilter, propertiesFilter,
		                             tableFilter);
	}

	@Override
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return thisFilter;
	}

	@Override
	public void setSession(CySession session) {
		this.session = session;
	}
}
