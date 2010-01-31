package cytoscape.coreplugins.biopax;

import giny.model.RootGraph;
import giny.view.GraphView;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.coreplugins.biopax.action.BiopaxNodeCtxMenuListener;
import cytoscape.coreplugins.biopax.action.NetworkListener;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.CytoscapeWrapper;
import cytoscape.coreplugins.biopax.util.LayoutUtil;
import cytoscape.coreplugins.biopax.view.BioPaxContainer;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.GraphReader;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.logger.CyLogger;

import org.biopax.paxtools.model.Model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import javax.swing.SwingUtilities;

/**
 * GraphReader Implementation for BioPAX Files.
 *
 * @author Ethan Cerami.
 * @author Igor Rodchenkov (re-factoring, using PaxTools API)
 */
public class BioPaxGraphReader implements GraphReader {
	public static final CyLogger log = CyLogger.getLogger(BioPaxGraphReader.class);
	
	private int[] nodeIndices;
	private int[] edgeIndices;
	private String fileName;
	private Model model;
	private String networkName;
	private boolean validNetworkName;
	private CyLayoutAlgorithm layout;
	private String networkId;
	private static CyLayoutAlgorithm defaultLayout = new LayoutUtil();
	
	
	/**
	 * Constructor
	 *
	 * @param fileName File Name.
	 */
	public BioPaxGraphReader(String fileName) {
		this.fileName = fileName;
		this.model = null;
		layout = getDefaultLayoutAlgorithm();
	}

	/**
	 * Constructor
	 *
	 * @param model PaxTools BioPAX Model
	 */
	public BioPaxGraphReader(Model model) {
		this.model= model;
		this.fileName=null;
		layout = getDefaultLayoutAlgorithm();
	}
	
	public String getNetworkId() {
		return networkId;
	}
	
	public static void setDefaultLayoutAlgorithm(CyLayoutAlgorithm algo) { 
		defaultLayout = algo; 
	}
	
	public static CyLayoutAlgorithm getDefaultLayoutAlgorithm() { 
		return defaultLayout; 
	}
	
	
	/**
	 * Read file.
	 *
	 * @throws IOException IO Error.
	 */
	public void read() throws IOException {

		if(model == null && fileName != null) { // import new data
			model = BioPaxUtil.readFile(fileName);
		}
		
		if(model == null) {
			log.error("Failed to read BioPAX model");
			return;
		}
		
		log.info("Model contains " + model.getObjects().size()
				+ " BioPAX elements");
		
		
		// Set network name (also checks if it exists)
		networkName = getNetworkName(model);
		
		if(networkName==null 
				|| networkName.trim().equals("")) {
			networkName = fileName;
		}

		// Map BioPAX Data to Cytoscape Nodes/Edges (run as task)
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);
		
		nodeIndices = mapper.getNodeIndices();
		if (nodeIndices.length == 0) {
			log.error("Pathway is empty!  " +
					"Please check the BioPAX source file.");
			return;
		}
		edgeIndices = mapper.getEdgeIndices();
		
	}

	private String getNetworkName(Model model) {
		// make a network name from pathway name(s) or the file name
		String candidateName;
		String networkViewTitle = System.getProperty("biopax.network_view_title");
		if (networkViewTitle != null && networkViewTitle.length() > 0) {
			validNetworkName = true;
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
			int idx = fileName.lastIndexOf('/');
			if(idx==-1) idx = fileName.lastIndexOf('\\');
			candidateName = fileName.substring(idx+1);
		}
		
		// Take appropriate adjustments, if name already exists
		return CyNetworkNaming.getSuggestedNetworkTitle(candidateName);
	}

	/**
	 * Our implementation of GraphReader.getLayoutAlgorithm().
	 */
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		return layout;
	}

	/**
	 * If yFiles available, perform organic layout,
	 * else matrix layout;  same as that provided by the SIF reader.
	 * Keep this for pre-2.5 support
	 *
	 * @param view CyNetworkView Object.
	 */
	public void layout(GraphView view) {
		layout.doLayout((CyNetworkView)view);
	}

	/**
	 * Get Node Indices.
	 *
	 * @return array of root graph node indices.
	 */
	public int[] getNodeIndicesArray() {
		return nodeIndices;
	}

	/**
	 * Get Edge Indices.
	 *
	 * @return array of root graph edge indices.
	 */
	public int[] getEdgeIndicesArray() {
		return edgeIndices;
	}

	/**
	 * Gets network name.
	 *
	 * @return network name.
	 */
	public String getNetworkName() {
		return networkName;
	}

	/**
	 * Executes Post-Processing on newly created network.
	 *
	 * @param cyNetwork CyNetwork object.
	 */
	public void doPostProcessing(CyNetwork cyNetwork) {
		// get cyNetwork id
		this.networkId = cyNetwork.getIdentifier();
		
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		
		// Sets a network attribute which indicates this network is a biopax network
		networkAttributes.setAttribute(networkId, MapBioPaxToCytoscape.BIOPAX_NETWORK, Boolean.TRUE);

		//  Repair Canonical Name 
		repairCanonicalName(cyNetwork);
		
		// repair network name
		if (!validNetworkName) {
			repairNetworkName(cyNetwork);
		}

		//  Set default Quick Find Index
		networkAttributes.setAttribute(cyNetwork.getIdentifier(), "quickfind.default_index",
		                               MapBioPaxToCytoscape.BIOPAX_SHORT_NAME);

		// set url to pathway commons -
		// used for pathway commons context menus
		String urlToBioPAXWebServices = System.getProperty("biopax.web_services_url");
		if (urlToBioPAXWebServices != null && urlToBioPAXWebServices.length() > 0) {
			networkAttributes.setAttribute(cyNetwork.getIdentifier(),
										   "biopax.web_services_url",
										   urlToBioPAXWebServices);
			System.setProperty("biopax.web_services_url", "");
		}

		// set data source attribute
		// used for pathway commons context menus
		String dataSources = System.getProperty("biopax.data_sources");
		if (dataSources != null && dataSources.length() > 0) {
			networkAttributes.setAttribute(cyNetwork.getIdentifier(),
										   "biopax.data_sources",
										   dataSources);
			System.setProperty("biopax.data_sources", "");
		}

		// associate the new network with its model
		BioPaxUtil.addNetworkModel(cyNetwork.getIdentifier(), model);
		String modelString = (fileName!=null) ? fileName : "";
		Cytoscape.getNetworkAttributes().setAttribute(
				networkId, BioPaxUtil.BIOPAX_MODEL_STRING,	modelString);
		
		//  Set-up the BioPax Visual Style
		VisualStyle bioPaxVisualStyle = BioPaxVisualStyleUtil.getBioPaxVisualStyle();
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
		view.setVisualStyle(bioPaxVisualStyle.getName());
		manager.setVisualStyle(bioPaxVisualStyle);
		view.applyVizmapper(bioPaxVisualStyle);

		//  Set up BP UI
		CytoscapeWrapper.initBioPaxPlugInUI();
		BioPaxContainer bpContainer = BioPaxContainer.getInstance();
        bpContainer.showLegend();
        
        BioPaxVisualStyleUtil.setNodeToolTips(view);
        
        // add network listener
        NetworkListener networkListener = bpContainer.getNetworkListener();
		networkListener.registerNetwork(cyNetwork);
		
		// add node's context menu
		BiopaxNodeCtxMenuListener nodeCtxMenuListener = new BiopaxNodeCtxMenuListener();
		view.addNodeContextMenuListener(nodeCtxMenuListener);
		
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
	 * Get root graph.
	 * @deprecated Use Cytoscape.getRootGraph() instead. Will be removed Dec 2006.
	 * @return RootGraph Object.
	 */
	public RootGraph getRootGraph() {
		return null;
	}

	/**
	 * Get node attributes.
	 * @deprecated Use Cytoscape.getNodeAttributes() instead. Will be removed Dec 2006.
	 * @return CyAttributes object.
	 */
	public CyAttributes getNodeAttributes() {
		return null;
	}

	/**
	 * Get edge attributes.
	 * @deprecated Use Cytoscape.getEdgeAttributes() instead. Will be removed Dec 2006.
	 * @return CyAttributes object.
	 */
	public CyAttributes getEdgeAttributes() {
		return null;
	}
		
	
	/**
	 * 
	 * Repairs Network Name.  Temporary fix to automatically set network
	 * name to match BioPAX Pathway name.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private static void repairNetworkName(final CyNetwork cyNetwork) {
		try {
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			Iterator<CyNode> iter = cyNetwork.nodesIterator();
			CyNode node = iter.next();

			if (node != null) {
				String pathwayName = 
					nodeAttributes.getStringAttribute(node.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_PATHWAY_NAME);
				if (pathwayName != null) {
					cyNetwork.setTitle(pathwayName);

					//  Update UI.  Must be done via SwingUtilities,
					// or it won't work.
					SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Cytoscape.getDesktop().getNetworkPanel().updateTitle(cyNetwork);
							}
						});
				}
			}
		}
		catch (java.util.NoSuchElementException e) {
			// network is empty, do nothing
		}
	}
	

	/**
	 * Repairs Canonical Name;  temporary fix for bug:  1001.
	 * By setting Canonical name to BIOPAX_NODE_LABEL, users can search for
	 * nodes via the Select Nodes --> By Name feature.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private static void repairCanonicalName(CyNetwork cyNetwork) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator<CyNode> iter = cyNetwork.nodesIterator();
		while (iter.hasNext()) {
			CyNode node = iter.next();
			String label = nodeAttributes.getStringAttribute(node.getIdentifier(),
			                                                 BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
			if (label != null) {
				nodeAttributes.setAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME, label);
			}
		}
	}
}
