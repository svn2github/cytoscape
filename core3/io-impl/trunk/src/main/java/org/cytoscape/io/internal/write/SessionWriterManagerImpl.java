package org.cytoscape.io.internal.write;

import java.io.File;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.SessionWriterFactory;
import org.cytoscape.io.write.SessionWriterManager;
import org.cytoscape.session.CySession;

public class SessionWriterManagerImpl extends
		AbstractWriterManager<SessionWriterFactory> implements
		SessionWriterManager {

	public SessionWriterManagerImpl(DataCategory category) {
		super(category);
	}

	@Override
	public CyWriter getWriter(CySession session, CyFileFilter filter, File file) {
		SessionWriterFactory factory = getMatchingFactory(filter, file);
		if (factory == null) {
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		}
		factory.setSession(session);
		return factory.getWriter();
	}

}
