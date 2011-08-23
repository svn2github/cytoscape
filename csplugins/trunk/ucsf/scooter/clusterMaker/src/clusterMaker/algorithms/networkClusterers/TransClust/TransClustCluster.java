package clusterMaker.algorithms.networkClusterers.TransClust;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

import clusterMaker.algorithms.networkClusterers.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.edgeConverters.EdgeAttributeHandler;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.taskmanaging.TaskConfig;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;


public class TransClustCluster extends AbstractNetworkClusterer{
	private List<CyNode> nodes;
	
	private static final long serialVersionUID = 1L;
	private String groupAttribute = "__TransClustGroups";

	private double threshold;

	// Tunable values
	protected String edgeAttribute = null;
	protected boolean mergeSimilar = false;
	protected int mergeThreshold;
	protected int maxSubclusterSize;
	protected int maxTime;
	
	private RunTransClust runTransClust;
	

	/**
 	 * Main constructor -- calls constructor of the superclass and initializes
 	 * all of our tunables.
 	 */
	public TransClustCluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_TransClust_cluster";
		logger = CyLogger.getLogger(TransClustCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "transclust";};
	public String getName() {return "Transitivity Clustering";};

	public JPanel getSettingsPanel() {
		edgeAttributeHandler.updateAttributeList();
		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return new NewNetworkView(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	/**
 	 * Update all of our tunables
 	 */
	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);
		edgeAttributeHandler.updateSettings(force);
		
		threshold = ((Double) clusterProperties.get("edgeCutOff").getValue()).doubleValue();
		
		
		// Advanced Settings

		// Find Exact Solution
		Tunable t = clusterProperties.get("maxSubclusterSize");
		if ((t != null) && (t.valueChanged() || force)){
			maxSubclusterSize = ((Integer) t.getValue()).intValue();
			try {
				TaskConfig.fixedParameterMax = new Integer(maxSubclusterSize);
			} catch (Exception e) {
				TaskConfig.fixedParameterMax = 20;
			}
		}
		

		t = clusterProperties.get("maxTime");
		if ((t != null) && (t.valueChanged() || force)){
			maxTime = ((Integer) t.getValue()).intValue();
			try {
				TaskConfig.fpMaxTimeMillis = new Integer(maxTime)*1000;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		

		// Merge Nodes
		t = clusterProperties.get("mergeSimilar");
		if ((t != null) && (t.valueChanged() || force)){
			mergeSimilar = ((Boolean) t.getValue()).booleanValue();
			if(!mergeSimilar){
				TaskConfig.upperBound = Float.MAX_VALUE;
			}
		}
			

		t = clusterProperties.get("mergeThreshold");
		if ((t != null) && (t.valueChanged() || force)){
			mergeThreshold = ((Integer) t.getValue()).intValue();
			try {
				if(mergeSimilar){
					TaskConfig.upperBound = new Float(mergeThreshold);
				}else{
					TaskConfig.upperBound = Float.MAX_VALUE;
				}
			} catch (Exception e) {
				TaskConfig.upperBound = Float.MAX_VALUE;
			}
		}
		
		t = clusterProperties.get("numberOfThreads");
			if ((t != null) && (t.valueChanged() || force)){
				TaskConfig.maxNoThreads = ((Integer) t.getValue()).intValue();
			}
		
	}

	/**
 	 * Perform the actual clustering.  For TransClust, there are really
 	 * two steps:
 	 * 	1) Assign all of the connected components
 	 * 	2) Do the TransClust clustering.
 	 *
 	 * There is also an optional approach called evolutionary parameter
 	 * tuning, which takes a lot longer and is probably less relevant for
 	 * the Cytoscape integration.
 	 *
 	 * @param monitor the TaskMonitor to use
 	 */
	public void doCluster(TaskMonitor monitor) {
		
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		DistanceMatrix matrix = edgeAttributeHandler.getMatrix();

		updateSettings(true);
		
		//Cluster the nodes
		
		runTransClust = new RunTransClust(matrix,threshold,logger);

		List<NodeCluster> clusters = runTransClust.run(monitor);

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  TransClust results:\n"+results);


		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	/**
 	 * This is called if the user requests evolutionary parameter
 	 * tuning.
 	 */
	public void actionPerformed(ActionEvent e) {
	}

	/**
 	 * Initialize all of our tunables
 	 */
	public void initializeProperties() {
		super.initializeProperties();

		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, true);
		
		// Advanced attributes group
		clusterProperties.add(new Tunable("advancedAttributesGroup", 
		                                  "Advanced Attributes",
		                                  Tunable.GROUP, new Integer(4),
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));
		{

		  super.advancedProperties();

			//	Find Exact Solution group
			clusterProperties.add(new Tunable("findExactGroup", 
			                                  "Find Exact Solution",
			                                  Tunable.GROUP, new Integer(2)));
			{
				//		Max subcluster size (Integer)
				clusterProperties.add(new Tunable("maxSubclusterSize", 
			 		                                "Max. Subcluster Size",
			 		                                Tunable.INTEGER, new Integer(20)));
				//		Max time (Integer)
				clusterProperties.add(new Tunable("maxTime", 
			 		                                "Max. Time (secs)",
			 		                                Tunable.INTEGER, new Integer(1)));
			}

			//	Merge nodes
			clusterProperties.add(new Tunable("mergeNodesGroup", 
			                                  "Merge nodes",
			                                  Tunable.GROUP, new Integer(2)));
			{
				//		Merge very similar nodes to one (boolean)
				clusterProperties.add(new Tunable("mergeSimilar", 
				                                  "Merge very similar nodes to one?",
		 		                                  Tunable.BOOLEAN, new Boolean(false)));
				//		Threshold (integer)
				clusterProperties.add(new Tunable("mergeThreshold", 
				                                  "Threshold:",
		 		                                  Tunable.INTEGER, new Integer(100)));
			}
			
//			Parallel computing
			clusterProperties.add(new Tunable("parallelGroup", 
			                                  "Parallel computing",
			                                  Tunable.GROUP, new Integer(1)));
			{
				clusterProperties.add(new Tunable("numberOfThreads", 
                        "Number of Processors:",
                         Tunable.INTEGER, new Integer(Runtime.getRuntime().availableProcessors()-1)));
			}

			
		}
		// Use the standard edge attribute handling stuff....
		

		clusterProperties.initializeProperties();
		updateSettings(true);
		
	}

	
	public static String getAttributeString(CyAttributes attributes, String id, String attributeName) {
		
		byte type = attributes.getType(attributeName);
		String value = "";
		
		if (type == CyAttributes.TYPE_INTEGER) {
			int b = attributes.getIntegerAttribute(id, attributeName);
			value = "" + b;
		} else if (type == CyAttributes.TYPE_BOOLEAN) {
			boolean b = attributes.getBooleanAttribute(id, attributeName);
			value = "" + b;
		} else if (type == CyAttributes.TYPE_FLOATING) {
			double b = attributes.getDoubleAttribute(id, attributeName);
			value = "" +  b;
		} else if (type == CyAttributes.TYPE_STRING) {
			String b = attributes.getStringAttribute(id, attributeName);
			value = "" +  b;
		}
		
		return value;
	}
	
	public void halt() {
		runTransClust.halt();
	}
	
	public void setParams(List<String>params) {
		params.add("mergeSimilar="+mergeSimilar);
		params.add("mergeThreshold="+mergeThreshold);
		params.add("maxSubclusterSize="+maxSubclusterSize);
		params.add("maxTime="+maxTime);
		super.setParams(params);
	}
}
