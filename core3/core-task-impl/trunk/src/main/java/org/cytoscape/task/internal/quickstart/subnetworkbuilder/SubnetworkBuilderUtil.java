package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.property.CyProperty;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.internal.creation.NewNetworkSelectedNodesOnlyTask;
import org.cytoscape.task.internal.quickstart.ImportNetworkFromPublicDataSetTask;
import org.cytoscape.task.internal.quickstart.remote.InteractionFilePreprocessor;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMappingFactory;
import org.cytoscape.work.Task;

class SubnetworkBuilderUtil {

    private CyNetworkViewReaderManager mgr;
    private CyNetworkManager netmgr;
    protected final CyNetworkViewManager networkViewManager;
    private Properties props;
    private StreamUtil streamUtil;

    protected final CyEventHelper eventHelper;
    protected final CyApplicationManager appManager;

    private CyNetworkNaming cyNetworkNaming;

    private final CyRootNetworkFactory crnf;
    private final CyNetworkViewFactory cnvf;

    private final Set<InteractionFilePreprocessor> processors;

    VisualStyleBuilder vsBuilder;
    final VisualMappingManager vmm;

    // For mapping generator
    final VisualStyleFactory vsFactory;
    private VisualMappingFunctionFactory discFactory;
    private VisualMappingFunctionFactory ptFactory;

    public SubnetworkBuilderUtil(CyNetworkViewReaderManager mgr, CyNetworkManager netmgr,
	    final CyNetworkViewManager networkViewManager, CyProperty<Properties> cyProps,
	    CyNetworkNaming cyNetworkNaming, StreamUtil streamUtil, final CyEventHelper eventHelper,
	    final CyApplicationManager appManager, CyRootNetworkFactory crnf, CyNetworkViewFactory cnvf,
	    VisualMappingManager vmm, final VisualStyleFactory vsFactory) {

	this.mgr = mgr;
	this.netmgr = netmgr;
	this.networkViewManager = networkViewManager;
	this.crnf = crnf;
	this.cnvf = cnvf;
	this.vmm = vmm;
	this.props = cyProps.getProperties();
	this.cyNetworkNaming = cyNetworkNaming;
	this.streamUtil = streamUtil;
	this.processors = new HashSet<InteractionFilePreprocessor>();
	this.eventHelper = eventHelper;
	this.appManager = appManager;
	this.vsFactory = vsFactory;
    }

    public void addProcessor(InteractionFilePreprocessor processor, Map props) {
	if (processor != null)
	    this.processors.add(processor);
    }

    public void removeProcessor(InteractionFilePreprocessor processor, Map props) {
	if (processor != null)
	    this.processors.remove(processor);

    }

    public Task getWebServiceImportTask() {
	return new ImportNetworkFromPublicDataSetTask(processors, mgr, netmgr, networkViewManager, props,
		cyNetworkNaming, streamUtil);
    }

    Task getNewNetworkSelectedNodesOnlyTask(final CyNetwork network) {
	return new NewNetworkSelectedNodesOnlyTask(network, crnf, cnvf, netmgr, networkViewManager, cyNetworkNaming,
		vmm, appManager);
    }

    public void addFactory(VisualMappingFunctionFactory factory, Map props) {

	System.out.println("\n\n\n *********** Got Factory ***************" + factory + "\n\n\n");

	if (factory.toString().startsWith("Discrete Mapping"))
	    discFactory = factory;
	else if (factory.toString().startsWith("Passthrough Mapping"))
	    ptFactory = factory;

	if (discFactory != null && ptFactory != null) {
	    this.vsBuilder = new VisualStyleBuilder(vsFactory, discFactory, ptFactory);
	    System.out.println("\n\n\n *********** vsBuilder Created. ***************\n\n\n");
	}
    }

    void removeFactory(VisualMappingFunctionFactory factory, Map props) {

    }

}
