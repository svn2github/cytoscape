package org.cytoscape.io.internal.write.session;

import org.cytoscape.session.CySession;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.SessionWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.PropertyWriterManager;
import java.io.OutputStream;

public class SessionWriterFactoryImpl implements SessionWriterFactory {
	
	private final CyFileFilter thisFilter;
	private final CyFileFilter xgmmlFilter;
	private final CyFileFilter bookmarksFilter;
	private final CyFileFilter cysessionFilter;
	private final CyFileFilter propertiesFilter;
	private final CyNetworkViewWriterManager networkViewWriterMgr;
	private final PropertyWriterManager propertyWriterMgr;

	private OutputStream outputStream;
	private CySession session;



	public SessionWriterFactoryImpl(final CyFileFilter thisFilter, 
	                                final CyFileFilter xgmmlFilter, 
	                                final CyFileFilter bookmarksFilter, 
	                                final CyFileFilter cysessionFilter, 
	                                final CyFileFilter propertiesFilter, 
	                                final CyNetworkViewWriterManager networkViewWriterMgr, 
	                                final PropertyWriterManager propertyWriterMgr) {
		this.thisFilter = thisFilter;
		this.xgmmlFilter = xgmmlFilter;
		this.bookmarksFilter = bookmarksFilter;
		this.cysessionFilter = cysessionFilter;
		this.propertiesFilter = propertiesFilter;
		this.networkViewWriterMgr = networkViewWriterMgr;
		this.propertyWriterMgr = propertyWriterMgr;
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new SessionWriterImpl(outputStream, session, networkViewWriterMgr, 
		                             propertyWriterMgr, xgmmlFilter, bookmarksFilter, 
		                             cysessionFilter, propertiesFilter);
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
