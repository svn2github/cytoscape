package org.cytoscape.io.internal.write.session;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CyPropertyWriterManager;
import org.cytoscape.io.write.CySessionWriterContext;
import org.cytoscape.io.write.CySessionWriterContextImpl;
import org.cytoscape.io.write.CySessionWriterFactory;
import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.VizmapWriterManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.work.TaskIterator;

public class SessionWriterFactoryImpl implements CySessionWriterFactory {
	
	private final CyFileFilter thisFilter;
	private final CyFileFilter xgmmlFilter;
	private final CyFileFilter bookmarksFilter;
	private final CyFileFilter cysessionFilter;
	private final CyFileFilter propertiesFilter;
	private final CyFileFilter tableFilter;
	private final CyFileFilter vizmapFilter;
	private final CyNetworkViewWriterManager networkViewWriterMgr;
	private final CyRootNetworkManager rootNetworkManager;
	private final CyPropertyWriterManager propertyWriterMgr;
	private final CyTableWriterManager tableWriterMgr;
	private final VizmapWriterManager vizmapWriterMgr;



	public SessionWriterFactoryImpl(final CyFileFilter thisFilter, 
	                                final CyFileFilter xgmmlFilter, 
	                                final CyFileFilter bookmarksFilter, 
	                                final CyFileFilter cysessionFilter, 
	                                final CyFileFilter propertiesFilter,
	                                final CyFileFilter tableFilter,
	                                final CyFileFilter vizmapFilter,
	                                final CyNetworkViewWriterManager networkViewWriterMgr,
	                                final CyRootNetworkManager rootNetworkManager,
	                                final CyPropertyWriterManager propertyWriterMgr,
	                                final CyTableWriterManager tableWriterMgr,
	                                final VizmapWriterManager vizmapWriterMgr) {
		this.thisFilter = thisFilter;
		this.xgmmlFilter = xgmmlFilter;
		this.bookmarksFilter = bookmarksFilter;
		this.cysessionFilter = cysessionFilter;
		this.propertiesFilter = propertiesFilter;
		this.tableFilter = tableFilter;
		this.vizmapFilter = vizmapFilter;
		this.networkViewWriterMgr = networkViewWriterMgr;
		this.rootNetworkManager = rootNetworkManager;
		this.propertyWriterMgr = propertyWriterMgr;
		this.tableWriterMgr = tableWriterMgr;
		this.vizmapWriterMgr = vizmapWriterMgr;
	}

	@Override
	public CySessionWriterContext createTaskContext() {
		return new CySessionWriterContextImpl();
	}
	
	@Override
	public CyWriter createWriterTask(CySessionWriterContext context) {
		return new SessionWriterImpl(context.getOutputStream(), context.getSession(), networkViewWriterMgr, rootNetworkManager,
		                             propertyWriterMgr, tableWriterMgr, vizmapWriterMgr, xgmmlFilter,
		                             bookmarksFilter, cysessionFilter, propertiesFilter,
		                             tableFilter, vizmapFilter);
	}

	@Override
	public TaskIterator createTaskIterator(CySessionWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return thisFilter;
	}

}
