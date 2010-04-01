package clusterMaker.algorithms.FORCE;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.EdgeAttributeHandler;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.taskmanaging.TaskConfig;


public class FORCECluster extends AbstractNetworkClusterer {
	private CyLogger logger = null;
	private	TaskMonitor monitor = null;

	// Tunable values
	private boolean mergeSimilar = false;
	private double mergeThreshold = 0;
	private boolean evolutionaryTraining = false;

	private EdgeAttributeHandler edgeAttributeHandler = null;

	/**
 	 * Main constructor -- calls constructor of the superclass and initializes
 	 * all of our tunables.
 	 */
	public FORCECluster() {
		super();
		logger = CyLogger.getLogger(FORCECluster.class);
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_FORCE_cluster";
		initializeProperties();
	}

	public String getShortName() {return "force";};
	public String getName() {return "FORCE cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		edgeAttributeHandler.updateAttributeList();

		return clusterProperties.getTunablePanel();
	}

	/**
 	 * At this point, we don't have a specific visualizer, although
 	 * there might be some value at some juncture to use a heatmap to
 	 * view the resulting matrix.
 	 */
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

		Tunable t = clusterProperties.get("dimension");
		if ((t != null) && (t.valueChanged() || force))
			TaskConfig.dimension = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("iterations");
		if ((t != null) && (t.valueChanged() || force))
			FORCEnDLayoutConfig.iterations = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("attractionFactor");
		if ((t != null) && (t.valueChanged() || force))
			FORCEnDLayoutConfig.attractionFactor = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("repulsionFactor");
		if ((t != null) && (t.valueChanged() || force))
			FORCEnDLayoutConfig.repulsionFactor = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("mergeSimilar");
		if ((t != null) && (t.valueChanged() || force))
			mergeSimilar = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("mergeThreshold");
		if ((t != null) && (t.valueChanged() || force))
			mergeThreshold = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("evolutionaryTraining");
		if ((t != null) && (t.valueChanged() || force))
			evolutionaryTraining = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("generations");
		if ((t != null) && (t.valueChanged() || force))
			TaskConfig.noOfGenerations = ((Integer) t.getValue()).intValue();

		edgeAttributeHandler.updateSettings(force);

	}

	/**
 	 * Initialize all of our tunables
 	 */
	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tunable values
		 */

		// Advanced attributes group
		clusterProperties.add(new Tunable("advancedAttributesGroup", 
		                                  "Advanced Tuning",
		                                  Tunable.GROUP, new Integer(4),
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));
		{
			// 	Layouter options group
			clusterProperties.add(new Tunable("layouterOptionsGroup", 
			                                  "Layouter Options",
			                                  Tunable.GROUP, new Integer(4)));
			{
				// 		Dimension (slider 2-6)
		    clusterProperties.add(new Tunable("dimension",
   		 	                                  "Dimension:",
     			                                 Tunable.INTEGER, new Integer(3),
		   			                               new Integer(2), new Integer(6), Tunable.USESLIDER));
		
				// 		Iterations (slider 50-250)
 		   	clusterProperties.add(new Tunable("iterations",
   		 	                                  "Iterations:",
   		 	                                  Tunable.INTEGER, new Integer(FORCEnDLayoutConfig.iterations),
		 			                                 new Integer(50), new Integer(250), Tunable.USESLIDER));
				// 		Attraction factor (double)
   		 	clusterProperties.add(new Tunable("attractionFactor",
     			                                 "Attraction Factor:",
       			                               Tunable.DOUBLE, new Double(FORCEnDLayoutConfig.attractionFactor)));
				// 		Repulsion factor (double)
   		 	clusterProperties.add(new Tunable("repulsionFactor",
     			                                 "Repulsion Factor:",
       			                               Tunable.DOUBLE, new Double(FORCEnDLayoutConfig.repulsionFactor)));
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

			//	Parameter training
			clusterProperties.add(new Tunable("parameterGroup", 
			                                  "Parameter training",
			                                  Tunable.GROUP, new Integer(2)));
			{
				//		Evolutionary parameter training? (boolean)
				clusterProperties.add(new Tunable("evolutionaryTraining", 
				                                  "Evolutionary parmeter training?",
		 		                                  Tunable.BOOLEAN, new Boolean(false)));
				//		Generations (1-10)
				clusterProperties.add(new Tunable("generations", 
				                                  "Generations:",
		 		                                  Tunable.INTEGER, new Integer(3),
				                                  new Integer(1), new Integer(10), Tunable.USESLIDER));
			}

		}

		// Use the standard edge attribute handling stuff....
		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, true);

		super.advancedProperties();

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	/**
 	 * Perform the actual clustering.  For FORCE, there are really
 	 * two steps:
 	 * 	1) Assign all of the connected components
 	 * 	   -- isn't this just normalizing the distance matrix?
 	 * 	2) Do the FORCE clustering.
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

		// Get the Distance Matrix
		DistanceMatrix matrix = edgeAttributeHandler.getMatrix();

		// Trim the matrix by setting all cells that don't meet our
		// cut-off criteria to zero

		// Now, run FORCE to cluster each connected component
		RunFORCE runFORCE = new RunFORCE(matrix, evolutionaryTraining, mergeSimilar, mergeThreshold, logger);

		List<NodeCluster> clusters = null;
		try {
			clusters = runFORCE.run(monitor);
		} catch (IOException e) {}

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		ClusterResults results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  FORCE results:\n"+results);

		// Set up the appropriate attributes
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "force");
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_ATTRIBUTE, clusterAttributeName);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

}
