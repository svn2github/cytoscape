package org.cytoscape.cpathsquared.internal.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.EntityReference;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.cytoscape.biopax.BioPaxContainer;
import org.cytoscape.biopax.MapBioPaxToCytoscape;
import org.cytoscape.biopax.MapBioPaxToCytoscapeFactory;
import org.cytoscape.biopax.NetworkListener;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPathException;
import org.cytoscape.cpathsquared.internal.CPathProperties;
import org.cytoscape.cpathsquared.internal.CPathWebService;
import org.cytoscape.cpathsquared.internal.util.AttributeUtil;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.cpathsquared.internal.view.BinarySifVisualStyleFactory;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.service.OutputFormat;

/**
 * Controller for Executing a Get Record(s) by CPath ID(s) command.
 * 
 * @author Ethan Cerami, Jason Montojo, Igor Rodchenkov.
 */
public class ExecuteGetRecordByCPathId extends AbstractTask {
	private CPathWebService webApi;
	private String ids[];
	private String networkTitle;
	private boolean haltFlag = false;
	private OutputFormat format;
	private final static String CPATH_SERVER_NAME_ATTRIBUTE = "CPATH_SERVER_NAME";
	private final static String CPATH_SERVER_DETAILS_URL = "CPATH_SERVER_DETAILS_URL";
	private Logger logger = LoggerFactory.getLogger(ExecuteGetRecordByCPathId.class);
	private final CPath2Factory cPathFactory;
	private final BioPaxContainer bpContainer;
	private final MapBioPaxToCytoscapeFactory mapperFactory;
	private final NetworkListener networkListener;
	private final VisualMappingManager mappingManager;

	/**.
	 * Constructor.
	 * 
	 * @param webApi
	 *            cPath Web API.
	 * @param ids
	 *            Array of cPath IDs.
	 * @param format
	 *            CPathResponseFormat Object.
	 * @param networkTitle
	 *            Tentative Network Title.
	 * @param bpContainer
	 * @param application
	 */
	public ExecuteGetRecordByCPathId(CPathWebService webApi, String[] ids, OutputFormat format,
			String networkTitle, CPath2Factory cPathFactory, BioPaxContainer bpContainer,
			MapBioPaxToCytoscapeFactory mapperFactory, NetworkListener networkListener, VisualMappingManager mappingManager) {
		this.webApi = webApi;
		this.ids = ids;
		this.format = format;
		this.networkTitle = networkTitle;
		this.cPathFactory = cPathFactory;
		this.bpContainer = bpContainer;
		this.mapperFactory = mapperFactory;
		this.networkListener = networkListener;
		this.mappingManager = mappingManager;
	}

	/**
	 * Our implementation of Task.abort()
	 */
	public void cancel() {
		webApi.abort();
		haltFlag = true;
	}

	/**
	 * Our implementation of Task.getTitle.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return "Retrieving " + networkTitle + " from " + CPathProperties.serverName + "...";
	}

	/**
	 * Our implementation of Task.run().
	 * 
	 * @throws Exception
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		String title = "Retrieving " + networkTitle + " from " + CPathProperties.serverName
				+ "...";
		taskMonitor.setTitle(title);
		try {
			// read the network from cpath instance
			if (taskMonitor != null) {
				taskMonitor.setProgress(0);
				taskMonitor.setStatusMessage("Retrieving " + networkTitle + ".");
			}

			// Store BioPAX to Temp File
			String tmpDir = System.getProperty("java.io.tmpdir");
			// Branch based on download mode setting.
			File tmpFile;
			if (format == OutputFormat.BIOPAX) {
				tmpFile = File.createTempFile("temp", ".xml", new File(tmpDir));
			} else {
				tmpFile = File.createTempFile("temp", ".sif", new File(tmpDir));
			}
			tmpFile.deleteOnExit();

			// Get Data, and write to temp file.
			String data = webApi.getRecordsByIds(ids, format, taskMonitor);
			FileWriter writer = new FileWriter(tmpFile);
			writer.write(data);
			writer.close();

			// Load up File via ImportHandler Framework
			// the biopax graph reader is going to be called
			// it will look for the network view title
			// via system properties, so lets set it now
			if (networkTitle != null && networkTitle.length() > 0) {
				System.setProperty("biopax.network_view_title", networkTitle);
			}

			CyNetworkReader reader = cPathFactory.getCyNetworkViewReaderManager().getReader(tmpFile.toURI(),
					tmpFile.getName());
			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Creating Cytoscape Network...");
				taskMonitor.setProgress(0);
			}

			reader.run(taskMonitor);
			final CyNetwork cyNetwork = reader.getCyNetworks()[0];
            final CyNetworkView view = reader.buildCyNetworkView(cyNetwork);

            cPathFactory.getCyNetworkManager().addNetwork(cyNetwork);
            cPathFactory.getCyNetworkViewManager().addNetworkView(view);

			cPathFactory.getCyNetworkManager().addNetwork(cyNetwork);
			cPathFactory.getCyNetworkViewManager().addNetworkView(view);

			// Branch, based on download mode.
			//TODO add EXTENDED_BINARY_SIF
			if (format == OutputFormat.BINARY_SIF) {
				postProcessingBinarySif(view, taskMonitor);
			} else {
				postProcessingBioPAX(view, taskMonitor);
			}

			// Add Links Back to cPath Instance
			addLinksToCPathInstance(cyNetwork);

			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Done");
				taskMonitor.setProgress(1.0);
			}

			CyLayoutAlgorithmManager layoutManager = cPathFactory.getCyLayoutAlgorithmManager();
			TaskFactory tf = layoutManager.getDefaultLayout();
			TaskIterator ti = tf.getTaskIterator();
			Task task = ti.next();
			insertTasksAfterCurrentTask(new Task[] {task});

		} catch (IOException e) {
			throw new Exception("Failed to retrieve records.", e);
		} catch (EmptySetException e) {
			throw new Exception("No matches found for your request.  ", e);
		} catch (CPathException e) {
			if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
				throw e;
			}
		}
	}

	/**
	 * Add Node Links Back to cPath Instance.
	 * 
	 * @param cyNetwork
	 *            CyNetwork.
	 */
	private void addLinksToCPathInstance(CyNetwork cyNetwork) {
		String serverName = CPathProperties.serverName;
		String serverURL = CPathProperties.cPathUrl;
		CyRow row = cyNetwork.getCyRow();
		String cPathServerDetailsUrl = row.get(ExecuteGetRecordByCPathId.CPATH_SERVER_DETAILS_URL, String.class);
		if (cPathServerDetailsUrl == null) {
			AttributeUtil.set(cyNetwork, ExecuteGetRecordByCPathId.CPATH_SERVER_NAME_ATTRIBUTE, serverName,
					String.class);
			String url = serverURL.replaceFirst("webservice.do", "record2.do?id=");
			AttributeUtil.set(cyNetwork, ExecuteGetRecordByCPathId.CPATH_SERVER_DETAILS_URL, url, String.class);
		}
	}

	/**
	 * Execute Post-Processing on BINARY SIF Network.
	 * 
	 * @param cyNetwork
	 *            Cytoscape Network Object.
	 */
	private void postProcessingBinarySif(final CyNetworkView view, TaskMonitor taskMonitor) {
		// Init the node attribute meta data, e.g. description, visibility, etc.
		// TODO: What happened to attribute descriptions?
		// MapBioPaxToCytoscape.initAttributes(nodeAttributes);

		final CyNetwork cyNetwork = view.getModel();

		// Set the Quick Find Default Index
		AttributeUtil.set(cyNetwork, "quickfind.default_index", "biopax.node_label", String.class);

		// Specify that this is a BINARY_NETWORK
		AttributeUtil.set(cyNetwork, BinarySifVisualStyleFactory.BINARY_NETWORK, Boolean.TRUE, Boolean.class);

		// Get all node details.
		getNodeDetails(cyNetwork, taskMonitor);

		if (haltFlag == false) {
			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Creating Network View...");
				taskMonitor.setProgress(0);
			}

			VisualStyle visualStyle = cPathFactory
					.getBinarySifVisualStyleUtil().getVisualStyle();
			mappingManager.setVisualStyle(visualStyle, view);
			networkListener.registerNetwork(view);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					bpContainer.showLegend();
					String networkTitleWithUnderscores = networkTitle.replaceAll(": ", "");
					networkTitleWithUnderscores = networkTitleWithUnderscores.replaceAll(" ", "_");
					CyNetworkNaming naming = cPathFactory.getCyNetworkNaming();
					networkTitleWithUnderscores = naming.getSuggestedNetworkTitle(networkTitleWithUnderscores);
					AttributeUtil.set(cyNetwork, CyNetwork.NAME, networkTitleWithUnderscores, String.class);
				}
			});
		} else {
			// If we have requested a halt, and we have a network, destroy it.
			// if (cyNetwork != null) {
			// Cytoscape.destroyNetwork(cyNetwork);
			// }
		}
	}


	//TODO may be remove this method
	private void postProcessingBioPAX(final CyNetworkView view, TaskMonitor taskMonitor) {
		final CyNetwork cyNetwork = view.getModel();
		if (haltFlag == false) {
			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Creating Network View...");
				taskMonitor.setProgress(0);
			}
		} else {
		}
	}

	/**
	 * Gets Details for Each Node from Web Service API.
	 */
	private void getNodeDetails(CyNetwork cyNetwork, TaskMonitor taskMonitor) {
		if (taskMonitor != null) {
			taskMonitor.setStatusMessage("Retrieving node details...");
			taskMonitor.setProgress(0);
		}
		List<List<CyNode>> batchList = createBatchArray(cyNetwork);
		if (batchList.size() == 0) {
			logger.info("Skipping node details.  Already have all the details new need.");
		}
		MapBioPaxToCytoscape mapBioPaxToCytoscape = mapperFactory.getInstance(null, taskMonitor);
		for (int i = 0; i < batchList.size(); i++) {
			if (haltFlag == true) {
				break;
			}
			List<CyNode> currentList = batchList.get(i);
			logger.debug("Getting node details, batch:  " + i);
			String ids[] = new String[currentList.size()];
			Map<String, CyNode> nodes = new HashMap<String, CyNode>();
			for (int j = 0; j < currentList.size(); j++) {
				CyNode node = currentList.get(j);
				String name = node.getCyRow().get(CyNode.NAME, String.class);
				nodes.put(name, node);
				ids[j] = name;
			}
			try {
				final String xml = webApi.getRecordsByIds(ids, OutputFormat.BIOPAX, new NullTaskMonitor());
				Model model = new SimpleIOHandler().convertFromOWL(new ByteArrayInputStream(xml.getBytes()));
							
				//map biopax properties to Cy attributes for SIF nodes
				for (BioPAXElement e : model.getObjects()) {
					if(e instanceof EntityReference || e instanceof Complex 
						|| e.getModelInterface().equals(PhysicalEntity.class)) 
					{
						CyNode node = nodes.get(e.getRDFId());
						if(node != null)
							mapBioPaxToCytoscape.createAttributesFromProperties(e, node, cyNetwork);
						// - this will also update the 'name' attribute (to a biol. label)
						else {
							logger.debug("Oops: no node for " + e.getRDFId());
						}
					}
				}
				
				double percentComplete = i / (double) batchList.size();
				if (taskMonitor != null) {
					taskMonitor.setProgress(percentComplete);
				}
			} catch (EmptySetException e) {
				e.printStackTrace();
			} catch (CPathException e) {
				e.printStackTrace();
			}
		}
	}

	private List<List<CyNode>> createBatchArray(CyNetwork cyNetwork) {
		int max_ids_per_request = 50;
		List<List<CyNode>> masterList = new ArrayList<List<CyNode>>();
		List<CyNode> currentList = new ArrayList<CyNode>();
		int counter = 0;
		for (CyNode node : cyNetwork.getNodeList()) {
			CyRow row = node.getCyRow();
			String label = row.get(CyNode.NAME, String.class);

			// If we already have details on this node, skip it.
			if (label == null) {
				currentList.add(node);
				counter++;
			}
			if (counter > max_ids_per_request) {
				masterList.add(currentList);
				currentList = new ArrayList<CyNode>();
				counter = 0;
			}
		}
		if (currentList.size() > 0) {
			masterList.add(currentList);
		}
		return masterList;
	}

}

class NullTaskMonitor implements TaskMonitor {
	public void setProgress(double arg0) {
	}

	public void setStatusMessage(String arg0) {
	}

	public void setTitle(String arg0) {
	}
}
