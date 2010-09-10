
package org.cytoscape.io.write;

import org.cytoscape.session.CySessionManager;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.AbstractTask;
import java.io.File;
import java.util.List;

/**
 */
public final class SessionWriter extends AbstractTask implements CyWriter {

	private final CySessionManager sessionMgr; 
	private final SessionWriterManager writerMgr; 
	private final File outputFile; 

	private boolean cancelTask;

	public SessionWriter(SessionWriterManager writerMgr, CySessionManager sessionMgr, File outputFile) {

		if ( writerMgr == null )
			throw new NullPointerException("Writer Manager is null");
		this.writerMgr = writerMgr;

		if ( sessionMgr == null )
			throw new NullPointerException("Session Manager is null");
		this.sessionMgr = sessionMgr;

		if ( outputFile == null )
			throw new NullPointerException("Output File is null");
		this.outputFile = outputFile;
	}

	public final void run(TaskMonitor tm) {

		List<CyFileFilter> filters = writerMgr.getAvailableWriters();
		if ( filters == null || filters.size() < 1)
			throw new NullPointerException("No Session file filters found");
		if ( filters.size() > 1 )
			throw new IllegalArgumentException("Found too many session filters!");

		CyWriter writer = writerMgr.getWriter(sessionMgr,filters.get(0),outputFile); 
		if ( writer == null )
			throw new NullPointerException("No CyWriter found for specified file type!");

		insertTasksAfterCurrentTask( writer );
	}

	public void cancel() {
		cancelTask = true;
	}
}
