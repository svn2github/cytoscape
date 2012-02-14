package org.cytoscape.io.write;

import org.cytoscape.session.CySession;

public class CySessionWriterContextImpl extends CyWriterContextImpl implements
		CySessionWriterContext {

	private CySession session;

	@Override
	public void setSession(CySession session) {
		this.session = session;
	}

	@Override
	public CySession getSession() {
		return session;
	}

}
