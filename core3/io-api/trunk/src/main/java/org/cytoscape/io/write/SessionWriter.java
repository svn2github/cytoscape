
package org.cytoscape.io.write;

import org.cytoscape.session.CySession;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.AbstractTask;
import java.io.File;
import java.util.List;

/**
 */
public final class SessionWriter extends AbstractTask implements CyWriter {

	private final CySession session; 
	private final SessionWriterManager writerMgr; 
	private final File outputFile; 

	public SessionWriter(SessionWriterManager writerMgr, CySession session, File outputFile) {

		if ( writerMgr == null )
			throw new NullPointerException("Writer Manager is null");
		this.writerMgr = writerMgr;

		if ( session == null )
			throw new NullPointerException("Session Manager is null");
		this.session = session;

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

		CyWriter writer = writerMgr.getWriter(session,filters.get(0),outputFile); 
		if ( writer == null )
			throw new NullPointerException("No CyWriter found for specified file type!");

		insertTasksAfterCurrentTask( writer );
	}

}
