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
	private final CyNetworkViewWriterManager nvwm;
	private final PropertyWriterManager propertyWriterMgr;
	private final CyFileFilter bookmarksFilter;

	private OutputStream outputStream;
	private CySession session;

	public SessionWriterFactoryImpl(CyFileFilter thisFilter, CyFileFilter xgmmlFilter, CyFileFilter bookmarksFilter, CyNetworkViewWriterManager nvwm, PropertyWriterManager propertyWriterMgr) {
		this.thisFilter = thisFilter;
		this.xgmmlFilter = xgmmlFilter;
		this.bookmarksFilter = bookmarksFilter;
		this.nvwm = nvwm;
		this.propertyWriterMgr = propertyWriterMgr;
	}
	
	@Override
	public CyWriter getWriter() {
		return new SessionWriterImpl(outputStream, session, nvwm, propertyWriterMgr, xgmmlFilter, bookmarksFilter);
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
