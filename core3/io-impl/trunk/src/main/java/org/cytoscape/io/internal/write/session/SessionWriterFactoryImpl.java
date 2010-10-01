package org.cytoscape.io.internal.write.session;

import org.cytoscape.session.CySession;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.SessionWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import java.io.OutputStream;

public class SessionWriterFactoryImpl implements SessionWriterFactory {
	
	private final CyFileFilter thisFilter;
	private final CyFileFilter xgmmlFilter;
	private final CyNetworkViewWriterManager nvwm;

	private OutputStream outputStream;
	private CySession session;

	public SessionWriterFactoryImpl(CyFileFilter thisFilter, CyFileFilter xgmmlFilter, CyNetworkViewWriterManager nvwm) {
		this.thisFilter = thisFilter;
		this.xgmmlFilter = xgmmlFilter;
		this.nvwm = nvwm;
	}
	
	@Override
	public CyWriter getWriter() {
		return new SessionWriterImpl(outputStream, session, nvwm, xgmmlFilter);
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
