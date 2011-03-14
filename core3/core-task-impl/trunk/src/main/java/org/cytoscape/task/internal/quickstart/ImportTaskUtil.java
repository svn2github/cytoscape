package org.cytoscape.task.internal.quickstart;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.internal.loadnetwork.LoadNetworkFileTask;
import org.cytoscape.task.internal.loadnetwork.LoadNetworkURLTask;
import org.cytoscape.task.internal.quickstart.remote.InteractionFilePreprocessor;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

public class ImportTaskUtil {
	
	private CyNetworkViewReaderManager mgr;
	private CyNetworkManager netmgr;
	private final CyNetworkViewManager networkViewManager;
	private Properties props;
	private StreamUtil streamUtil;

	private CyNetworkNaming cyNetworkNaming;
	
	private final Set<InteractionFilePreprocessor> processors;
	
	private InputStreamTaskFactory sifReaderFactory;

	public ImportTaskUtil(
			CyNetworkViewReaderManager mgr,
		     CyNetworkManager netmgr,
		     final CyNetworkViewManager networkViewManager,
		     CyProperty<Properties> cyProps, CyNetworkNaming cyNetworkNaming,
		     StreamUtil streamUtil) {
		this.mgr = mgr;
		this.netmgr = netmgr;
		this.networkViewManager = networkViewManager;
		this.props = cyProps.getProperties();
		this.cyNetworkNaming = cyNetworkNaming;
		this.streamUtil = streamUtil;
		this.processors = new HashSet<InteractionFilePreprocessor>();
		this.sifReaderFactory = sifReaderFactory;
		
	}

	public Task getURLImportTask() {
		return new LoadNetworkURLTask(mgr, netmgr, networkViewManager, props, cyNetworkNaming, streamUtil);
	}
	
	public Task getFileImportTask() {
		return new LoadNetworkFileTask(mgr, netmgr, networkViewManager, props, cyNetworkNaming);
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

}
