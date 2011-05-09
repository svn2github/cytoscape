package org.cytoscape.task.internal.quickstart;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.internal.loadnetwork.LoadNetworkFileTask;
import org.cytoscape.task.internal.loadnetwork.LoadNetworkURLTask;
import org.cytoscape.task.internal.quickstart.datasource.InteractionFilePreprocessor;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.Task;

public class ImportTaskUtil {
	
	private CyNetworkViewReaderManager mgr;
	private CyNetworkManager netmgr;
	private final CyNetworkViewManager networkViewManager;
	private Properties props;
	private StreamUtil streamUtil;

	private CyNetworkNaming cyNetworkNaming;
	
	private final Set<InteractionFilePreprocessor> processors;
	
	private CyTableReaderManager tblReaderMgr;
	
	private final CyApplicationManager appManager;
	private CyProperty<Properties> cyProps;

	public ImportTaskUtil(
			CyNetworkViewReaderManager mgr,
		     CyNetworkManager netmgr,
		     final CyNetworkViewManager networkViewManager,
		     CyProperty<Properties> cyProps, CyNetworkNaming cyNetworkNaming,
		     StreamUtil streamUtil, CyTableReaderManager tblReaderMgr, final CyApplicationManager appManager) {
		this.mgr = mgr;
		this.netmgr = netmgr;
		this.networkViewManager = networkViewManager;
		this.props = cyProps.getProperties();
		this.cyNetworkNaming = cyNetworkNaming;
		this.streamUtil = streamUtil;
		this.processors = new HashSet<InteractionFilePreprocessor>();
		this.tblReaderMgr = tblReaderMgr;
		this.appManager = appManager;
		this.cyProps = cyProps;
	}

	public Task getURLImportTask() {
		return new LoadNetworkURLTask(mgr, netmgr, networkViewManager, props, cyNetworkNaming, streamUtil);
	}
	
	public Task getFileImportTask() {
		return new LoadNetworkFileTask(mgr, netmgr, networkViewManager, props, cyNetworkNaming);
	}

	public CyNetwork getTargetNetwork() {
		// Currently, just use currentNetwork as the target.
		return appManager.getCurrentNetwork();
	}
	
	public Task getWebServiceImportTask() {
		return new ImportNetworkFromPublicDataSetTask(processors, mgr, netmgr, networkViewManager, props, cyNetworkNaming, streamUtil);
	}
	
	
	public void addProcessor(InteractionFilePreprocessor processor, Map props) {
		if(processor != null)
			this.processors.add(processor);
	}
	
	public void removeProcessor(InteractionFilePreprocessor processor, Map props) {
		if(processor != null)
			this.processors.remove(processor);
		
	}

	public CyTableReaderManager getTableReaderManager(){
		return tblReaderMgr;
	}

	public Task getURLImportTableTask() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CyApplicationManager getAppManager() {
		return this.appManager;
	}
	
	public  CyProperty<Properties> getCyProperty()
	{
		return cyProps;
	}
}
