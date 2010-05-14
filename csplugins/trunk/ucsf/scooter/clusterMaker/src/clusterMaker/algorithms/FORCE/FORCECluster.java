package clusterMaker.algorithms.FORCE;

import java.io.IOException;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
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

import clusterMaker.algorithms.FORCE.Parameters;
import clusterMaker.algorithms.FORCE.RunFORCE;




public class FORCECluster extends AbstractNetworkClusterer {

	private CyLogger logger = null;
	private	TaskMonitor monitor = null;
        private EdgeAttributeHandler edgeAttributeHandler = null;

	// Tunable values
    int dimension;
    int iterations;
    double attractionFactor;
    double repulsionFactor;
    double maximalDisplacement;
    float temperature;
    double influenceOfGraphSizeToForces;
    double singleLinkageDistance;

	

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
			dimension = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("iterations");
		if ((t != null) && (t.valueChanged() || force))
			iterations = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("attractionFactor");
		if ((t != null) && (t.valueChanged() || force))
			attractionFactor = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("repulsionFactor");
		if ((t != null) && (t.valueChanged() || force))
			repulsionFactor = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("maximalDisplacement");
		if ((t != null) && (t.valueChanged() || force))
			maximalDisplacement = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("temperature");
		if ((t != null) && (t.valueChanged() || force))
			temperature = ((Double) t.getValue()).floatValue();
		
		t = clusterProperties.get("influenceOfGraphSizeToForces");
		if ((t != null) && (t.valueChanged() || force))
			influenceOfGraphSizeToForces = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("singleLinkageDistance");
		if ((t != null) && (t.valueChanged() || force))
			singleLinkageDistance = ((Double) t.getValue()).doubleValue();

		


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

		 clusterProperties.add(new Tunable("tunables_panel",
                                                  "FORCE Tuning",
                                                  Tunable.GROUP, new Integer(8)) );

		
			
				// 		Dimension (slider 2-6)
		    clusterProperties.add(new Tunable("dimension",
   		 	                                  "Dimension:",
     			                                 Tunable.INTEGER, new Integer(3),
		   			                               new Integer(2), new Integer(6), Tunable.USESLIDER));
		
				// 		Iterations (slider 50-250)
 		   	clusterProperties.add(new Tunable("iterations",
   		 	                                  "Iterations:",
   		 	                                  Tunable.INTEGER, new Integer(150),
		 			                                 new Integer(50), new Integer(250), Tunable.USESLIDER));
				// 		Attraction factor (double)
   		 	clusterProperties.add(new Tunable("attractionFactor",
     			                                 "Attraction Factor:",
       			                               Tunable.DOUBLE, new Double(1.2448524402942829)));

				// 		Repulsion factor (double)
   		 	clusterProperties.add(new Tunable("repulsionFactor",
     			                                 "Repulsion Factor:",
       			                               Tunable.DOUBLE, new Double(1.6866447301914302)));
			
				// 		Maximal Displacement (double)
   		 	clusterProperties.add(new Tunable("maximalDisplacement",
     			                                 "Maximal Displacement:",
       			                               Tunable.DOUBLE, new Double(1000)));

				// 		Temperature (float)
   		 	clusterProperties.add(new Tunable("temperature",
     			                                 "Temperature:",
       			                               Tunable.DOUBLE, new Double(633)));

				// 		Influence of Graph Size to Forces (double)
   		 	clusterProperties.add(new Tunable("influenceOfGraphSizeToForces",
     			                                 "Influence of Graph Size to Forces:",
       			                               Tunable.DOUBLE, new Double(1.3198015648987826)));

			      // 		Single Linkage Distance (slider .01-5)
		        clusterProperties.add(new Tunable("singleLinkageDistance",
   		 	                                  "Single Linkage Distance:",
     			                                 Tunable.DOUBLE, new Double(2),
		   			                               new Double(.01), new Double(5), Tunable.USESLIDER));
			
	

		// Use the standard edge attribute handling stuff....
		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, true);

		super.advancedProperties();

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	/**
 	 * Perform the actual clustering.  For FORCE, there are really
 	 * two steps:
 	 * 	1) Normalize the Distance Matrix
 	 * 	
 	 * 	2) Do the FORCE clustering.
 	 *
 	 * 
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

		Parameters params = new Parameters(iterations,attractionFactor,repulsionFactor,maximalDisplacement,temperature,influenceOfGraphSizeToForces);
		// Now, run FORCE to cluster each connected component
		RunFORCE runFORCE = new RunFORCE(matrix,dimension,singleLinkageDistance, params, logger);

		List<NodeCluster> clusters = null;
		//try {
			clusters = runFORCE.run(monitor);
		//} catch (IOException e) {}

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		ClusterResults results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  FORCE results:\n"+results);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

}
