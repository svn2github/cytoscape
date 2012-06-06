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

import org.biopax.paxtools.controller.ModelUtils;
import org.biopax.paxtools.converter.OneTwoThree;
import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.EntityReference;
import org.biopax.paxtools.model.level3.Named;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.SimplePhysicalEntity;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Exception;
import org.cytoscape.cpathsquared.internal.util.AttributeUtil;
import org.cytoscape.cpathsquared.internal.util.BioPaxUtil;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.service.OutputFormat;

/**
 * Controller for Executing a Get Record(s) by CPath ID(s) command.
 * 
 */
public class ExecuteGetRecordByCPathIdTask extends AbstractTask {
	private String ids[];
	private String networkTitle;
	private boolean haltFlag = false;
	private OutputFormat format;
	private final static String CPATH_SERVER_NAME_ATTRIBUTE = "CPATH_SERVER_NAME";
	private final static String CPATH_SERVER_DETAILS_URL = "CPATH_SERVER_DETAILS_URL";
	private static final Logger logger = LoggerFactory.getLogger(ExecuteGetRecordByCPathIdTask.class);

	/**.
	 * Constructor.
	 * @param ids Array of cPath IDs.
	 * @param format Output Format.
	 * @param networkTitle Tentative Network Title.
	 */
	public ExecuteGetRecordByCPathIdTask(String[] ids, OutputFormat format, String networkTitle) {
		this.ids = ids;
		this.format = format;
		this.networkTitle = networkTitle;
	}

	/**
	 * Our implementation of Task.abort()
	 */
	public void cancel() {
		haltFlag = true;
	}

	/**
	 * Our implementation of Task.getTitle.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return "Retrieving " + networkTitle + " from " + CPath2Factory.serverName + "...";
	}

	/**
	 * Our implementation of Task.run().
	 * 
	 * @throws Exception
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		String title = "Retrieving " + networkTitle + " from " + CPath2Factory.serverName
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
			String data = CPath2Factory.getRecordsByIds(ids, format);
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

			CyNetworkReader reader = CPath2Factory.getCyNetworkViewReaderManager().getReader(tmpFile.toURI(),
					tmpFile.getName());
			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Creating Cytoscape Network...");
				taskMonitor.setProgress(0);
			}

			reader.run(taskMonitor);
			final CyNetwork cyNetwork = reader.getNetworks()[0];
            final CyNetworkView view = reader.buildCyNetworkView(cyNetwork);

            CPath2Factory.getCyNetworkManager().addNetwork(cyNetwork);
            CPath2Factory.getCyNetworkViewManager().addNetworkView(view);

            CPath2Factory.getCyNetworkManager().addNetwork(cyNetwork);
            CPath2Factory.getCyNetworkViewManager().addNetworkView(view);

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

			CyLayoutAlgorithmManager layoutManager = CPath2Factory.getCyLayoutAlgorithmManager();
			CyLayoutAlgorithm layout = layoutManager.getDefaultLayout();
			Object context = layout.getDefaultLayoutContext();
			insertTasksAfterCurrentTask(layout.createTaskIterator(view, context, CyLayoutAlgorithm.ALL_NODE_VIEWS,""));

		} catch (IOException e) {
			throw new Exception("Failed to retrieve records.", e);
		} catch (EmptySetException e) {
			throw new Exception("No matches found for your request.  ", e);
		} catch (CPath2Exception e) {
			if (e.getErrorCode() != CPath2Exception.ERROR_CANCELED_BY_USER) {
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
		String serverName = CPath2Factory.serverName;
		String serverURL = CPath2Factory.cPathUrl;
		CyRow row = cyNetwork.getRow(cyNetwork);
		String cPathServerDetailsUrl = row.get(ExecuteGetRecordByCPathIdTask.CPATH_SERVER_DETAILS_URL, String.class);
		if (cPathServerDetailsUrl == null) {
			AttributeUtil.set(cyNetwork, cyNetwork, ExecuteGetRecordByCPathIdTask.CPATH_SERVER_NAME_ATTRIBUTE,
					serverName, String.class);
			String url = serverURL.replaceFirst("webservice.do", "record2.do?id=");
			AttributeUtil.set(cyNetwork, cyNetwork, ExecuteGetRecordByCPathIdTask.CPATH_SERVER_DETAILS_URL, url, String.class);
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
		// BioPaxMapper.initAttributes(nodeAttributes);

		final CyNetwork cyNetwork = view.getModel();

		// Set the Quick Find Default Index
		AttributeUtil.set(cyNetwork, cyNetwork, "quickfind.default_index", CyNetwork.NAME, String.class);

		// Specify that this is a BINARY_NETWORK
		AttributeUtil.set(cyNetwork, cyNetwork, "BIOPAX_NETWORK", Boolean.TRUE, Boolean.class);

		// Get all node details.
		getNodeDetails(cyNetwork, taskMonitor);

		if (haltFlag == false) {
			if (taskMonitor != null) {
				taskMonitor.setStatusMessage("Creating Network View...");
				taskMonitor.setProgress(0);
			}

			VisualStyle visualStyle = CPath2Factory.getBinarySifVisualStyleUtil().getVisualStyle();
			CPath2Factory.getMappingManager().setVisualStyle(visualStyle, view);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String networkTitleWithUnderscores = networkTitle.replaceAll(": ", "");
					networkTitleWithUnderscores = networkTitleWithUnderscores.replaceAll(" ", "_");
					CyNetworkNaming naming = CPath2Factory.getCyNetworkNaming();
					networkTitleWithUnderscores = naming.getSuggestedNetworkTitle(networkTitleWithUnderscores);
					AttributeUtil.set(cyNetwork, cyNetwork, CyNetwork.NAME, networkTitleWithUnderscores, String.class);
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
				String name = cyNetwork.getRow(node).get(CyNetwork.NAME, String.class);
				nodes.put(name, node);
				ids[j] = name;
			}
			try {
				final String xml = CPath2Factory.getRecordsByIds(ids, OutputFormat.BIOPAX);
				Model model = new SimpleIOHandler().convertFromOWL(new ByteArrayInputStream(xml.getBytes()));
				// convert L2 to L3 if required (L1 is converted to L2 always anyway - by the handler)
				if(BioPAXLevel.L2.equals(model.getLevel())) { // 
					model = (new OneTwoThree()).filter(model);
				}
				//map biopax properties to Cy attributes for SIF nodes
				for (BioPAXElement e : model.getObjects()) {
					if(e instanceof EntityReference 
							|| e instanceof Complex 
								|| e.getModelInterface().equals(PhysicalEntity.class)) 
					{
						CyNode node = nodes.get(e.getRDFId());
						if(node != null)
							BioPaxUtil.createAttributesFromProperties(e, node, cyNetwork);
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
			} catch (Exception e) {
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
			CyRow row = cyNetwork.getRow(node);
			String label = row.get(CyNetwork.NAME, String.class);

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

	
	private void fixDisplayName(Model model) {
		if (logger.isInfoEnabled())
			logger.info("Trying to auto-fix 'null' displayName...");
		// where it's null, set to the shortest name if possible
		for (Named e : model.getObjects(Named.class)) {
			if (e.getDisplayName() == null) {
				if (e.getStandardName() != null) {
					e.setDisplayName(e.getStandardName());
				} else if (!e.getName().isEmpty()) {
					String dsp = e.getName().iterator().next();
					for (String name : e.getName()) {
						if (name.length() < dsp.length())
							dsp = name;
					}
					e.setDisplayName(dsp);
				}
			}
		}
		// if required, set PE name to (already fixed) ER's name...
		for(EntityReference er : model.getObjects(EntityReference.class)) {
			for(SimplePhysicalEntity spe : er.getEntityReferenceOf()) {
				if(spe.getDisplayName() == null || spe.getDisplayName().trim().length() == 0) {
					if(er.getDisplayName() != null && er.getDisplayName().trim().length() > 0) {
						spe.setDisplayName(er.getDisplayName());
					}
				}
			}
		}
	}
		
}	

