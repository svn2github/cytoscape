package org.cytoscape.cpath2.internal.biopax;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.biopax.paxtools.model.Model;
import org.cytoscape.cpath2.internal.biopax.action.NetworkListener;
import org.cytoscape.cpath2.internal.biopax.util.BioPaxUtil;
import org.cytoscape.cpath2.internal.biopax.util.BioPaxVisualStyleUtil;
import org.cytoscape.cpath2.internal.biopax.view.BioPaxContainer;
import org.cytoscape.cpath2.internal.util.AttributeUtil;
import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GraphReader Implementation for BioPAX Files.
 *
 * @author Ethan Cerami.
 * @author Igor Rodchenkov (re-factoring, using PaxTools API)
 */
public class BioPaxNetworkViewReaderTask extends AbstractTask implements CyNetworkViewReader {
	public static final Logger log = LoggerFactory.getLogger(BioPaxNetworkViewReaderTask.class);

	private String networkName;
	private String networkId;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory viewFactory;
	private final CyNetworkNaming naming;

	private final BioPaxContainer bpContainer;

	private CyNetworkView view;

	private InputStream stream;

	private String inputName;
	
	/**
	 * Constructor
	 *
	 * @param fileName File Name.
	 */
//	public BioPaxNetworkViewReaderTask(String fileName) {
//		this.fileName = fileName;
//		this.model = null;
//		layout = getDefaultLayoutAlgorithm();
//	}

	/**
	 * Constructor
	 *
	 * @param model PaxTools BioPAX Model
	 */
	public BioPaxNetworkViewReaderTask(InputStream stream, String inputName, CyNetworkFactory networkFactory, CyNetworkViewFactory viewFactory, CyNetworkNaming naming, BioPaxContainer bpContainer) {
		this.stream = stream;
		this.inputName = inputName;
		this.networkFactory = networkFactory;
		this.viewFactory = viewFactory;
		this.naming = naming;
		this.bpContainer = bpContainer;
	}
	
	public String getNetworkId() {
		return networkId;
	}
	
	@Override
	public CyNetworkView[] getNetworkViews() {
		return new CyNetworkView[] { view };
	}
	
	@Override
	public void cancel() {
	}
	
	@Override
	public VisualStyle[] getVisualStyles() {
		// TODO VisualStyles
		return new VisualStyle[0];
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		Model model = BioPaxUtil.read(stream);
		
		if(model == null) {
			log.error("Failed to read BioPAX model");
			return;
		}
		
		log.info("Model contains " + model.getObjects().size()
				+ " BioPAX elements");
		
		networkName = getNetworkName(model);
		CyNetwork network = networkFactory.getInstance();
		AttributeUtil.set(network, CyNetwork.NAME, networkName, String.class);
		
		view = viewFactory.getNetworkView(network);
		
		// Map BioPAX Data to Cytoscape Nodes/Edges (run as task)
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape(network, taskMonitor);
		mapper.doMapping(model);
		
		if (network.getNodeCount() == 0) {
			log.error("Pathway is empty!  " +
					"Please check the BioPAX source file.");
			return;
		}
	}

	private String getNetworkName(Model model) {
		// make a network name from pathway name(s) or the file name
		String candidateName;
		String networkViewTitle = System.getProperty("biopax.network_view_title");
		if (networkViewTitle != null && networkViewTitle.length() > 0) {
			System.setProperty("biopax.network_view_title", "");
			try {
				networkViewTitle = URLDecoder.decode(networkViewTitle, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// if exception occurs leave encoded string, but cmon, utf-8 not supported ??
			}
			candidateName = networkViewTitle;
		} else {
			candidateName = BioPaxUtil.getName(model);
		}
		
		if(candidateName == null || "".equalsIgnoreCase(candidateName)) {
			candidateName = inputName;
			if(log.isDebugEnabled())
				log.debug("Network name will be the file name: " + candidateName);
		} else if(candidateName.length() > 100) {
			if(log.isDebugEnabled())
				candidateName = inputName + " - " + candidateName.substring(0, 100);
				log.debug("Based on multiple pathways network name is too long; " +
					"it will be truncated: " + candidateName);
		}
		
		// Take appropriate adjustments, if name already exists
		String name = naming.getSuggestedNetworkTitle(candidateName);
		if(log.isDebugEnabled())
			log.debug("Network name is: " + name);
		
		return name;
	}

	/**
	 * Executes Post-Processing on newly created network.
	 *
	 * @param cyNetwork CyNetwork object.
	 */
	public void doPostProcessing(Model model, final CyNetwork cyNetwork) {
		// Sets a network attribute which indicates this network is a biopax network
		AttributeUtil.set(cyNetwork, MapBioPaxToCytoscape.BIOPAX_NETWORK, Boolean.TRUE, Boolean.class);

		//  Repair Canonical Name 
		repairNodesCanonicalName(cyNetwork);
		
		//  Set default Quick Find Index
		AttributeUtil.set(cyNetwork, "quickfind.default_index", MapBioPaxToCytoscape.BIOPAX_SHORT_NAME, String.class);

		// set url to pathway commons -
		// used for pathway commons context menus
		String urlToBioPAXWebServices = System.getProperty("biopax.web_services_url");
		if (urlToBioPAXWebServices != null && urlToBioPAXWebServices.length() > 0) {
			AttributeUtil.set(cyNetwork, "biopax.web_services_url", urlToBioPAXWebServices, String.class);
			System.setProperty("biopax.web_services_url", "");
		}

		// set data source attribute
		// used for pathway commons context menus
		String dataSources = System.getProperty("biopax.data_sources");
		if (dataSources != null && dataSources.length() > 0) {
			AttributeUtil.set(cyNetwork, "biopax.data_sources", dataSources, String.class);
			System.setProperty("biopax.data_sources", "");
		}

		// associate the new network with its model
		BioPaxUtil.addNetworkModel(cyNetwork.getSUID(), model);
		String modelString = (inputName!=null) ? inputName : "";
		AttributeUtil.set(cyNetwork, BioPaxUtil.BIOPAX_MODEL_STRING, modelString, String.class);
		
		//  Set-up the BioPax Visual Style
		// TODO: VisualStyle
//		VisualStyle bioPaxVisualStyle = BioPaxVisualStyleUtil.getBioPaxVisualStyle();
//		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
//		CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
//        // set tooltips
//		BioPaxVisualStyleUtil.setNodeToolTips(view);
//		// set style
//		view.setVisualStyle(bioPaxVisualStyle.getName());
//		manager.setVisualStyle(bioPaxVisualStyle);
//		view.applyVizmapper(bioPaxVisualStyle);

		//  Set up BP UI
//		CytoscapeWrapper.initBioPaxPlugInUI();
        bpContainer.showLegend();
        
        // add network listener
        NetworkListener networkListener = bpContainer.getNetworkListener();
		networkListener.registerNetwork(cyNetwork);
		
		// add node's context menu
		// TODO: NodeViewTaskFactory?
//		BiopaxNodeCtxMenuListener nodeCtxMenuListener = new BiopaxNodeCtxMenuListener();
//		view.addNodeContextMenuListener(nodeCtxMenuListener);
		
	}

	/**
	 * Read in graph;  canonicalize all names.
	 * @deprecated Use read() instead.  Will be removed Dec 2006.
	 * @param canonicalizeNodeNames flag for canonicalization.
	 * @throws IOException IO Error.
	 */
	public void read(boolean canonicalizeNodeNames) throws IOException {
	}

	/**
	 * Repairs Canonical Name;  temporary fix for bug:  1001.
	 * By setting Canonical name to BIOPAX_NODE_LABEL, users can search for
	 * nodes via the Select Nodes --> By Name feature.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private static void repairNodesCanonicalName(CyNetwork cyNetwork) {
		for (CyNode node : cyNetwork.getNodeList()) {
			CyRow row = node.getCyRow();
			String label = row.get(BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL, String.class);
			if (label != null) {
				AttributeUtil.set(node, CyNode.NAME, label, String.class);
			}
		}
	}
}
