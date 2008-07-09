package clusterMaker.algorithms.FORCE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.ui.ClusterViz;


public class FORCECluster extends AbstractClusterAlgorithm implements ActionListener {
	private CyLogger logger = null;
	private	TaskMonitor monitor = null;

	private String groupAttributeCC = "__FORCEccGroups";
	private String groupAttribute = "__FORCEGroups";

	public FORCECluster() {
		super();
		logger = CyLogger.getLogger(FORCECluster.class);
		initializeProperties();
	}

	public String getShortName() {return "force";};
	public String getName() {return "FORCE cluster";};

	public JPanel getSettingsPanel() {
		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return null;
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
	}

	public void actionPerformed(ActionEvent e) {
	}

	protected void initializeProperties() {
		super.initializeProperties();
		// Attributes group
		clusterProperties.add(new Tunable("attributesGroup", 
		                                  "Attributes",
		                                  Tunable.GROUP, new Integer(3)));
		{
			// 	Edge weight attribute (Edge attribute combo-box)
    	clusterProperties.add(new Tunable("edgeAttributes",
	                                      "Edge weight attribute:",
 	                                     Tunable.EDGEATTRIBUTE, "",
 	                                     (Object)null, (Object)null, 
			                                  Tunable.NUMERICATTRIBUTE));
	
			// 	Edge weights correspond to: Similarity|Distance combo box
			String[] weightTypes = { "Similarity", "Distance" };
 	   clusterProperties.add(new Tunable("weightType",
 	                                     "Edge weights correspond to:",
 	                                     Tunable.LIST, new Integer(0),
 	                                     (Object)weightTypes, (Object)null, 0));
			// 	Threshold integer
 	   clusterProperties.add(new Tunable("threshold",
 	                                     "Threshold:",
 	                                     Tunable.INTEGER, new Integer(10)));
		}

		// Advanced attributes group
		clusterProperties.add(new Tunable("advancedAttributesGroup", 
		                                  "Advanced Attributes",
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
   		 	                                  Tunable.INTEGER, new Integer(150),
		 			                                 new Integer(50), new Integer(250), Tunable.USESLIDER));
				// 		Attraction factor (double)
   		 	clusterProperties.add(new Tunable("attractionFactor",
     			                                 "Attraction Factor:",
       			                               Tunable.DOUBLE, new Double(1.245)));
				// 		Repulsion factor (double)
   		 	clusterProperties.add(new Tunable("repulsionFactor",
     			                                 "Repulsion Factor:",
       			                               Tunable.DOUBLE, new Double(1.687)));
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

			//	Cytoscape
			clusterProperties.add(new Tunable("cytoscapeGroup", 
			                                  "Cytoscape",
			                                  Tunable.GROUP, new Integer(2)));
			{
				//		Node attribute connected component (string)
				clusterProperties.add(new Tunable("componentAttribute", 
			 		                                "Node attribute connected component:",
			 		                                Tunable.STRING, "FORCE_connected_component"));
				//		Node attribute FORCE cluster (string)
				clusterProperties.add(new Tunable("clusterAttribute", 
			 		                                "Node attribute FORCE cluster:",
			 		                                Tunable.STRING, "FORCE_cluster"));
			}
		}
		// Determine optimal threshold
		clusterProperties.add(new Tunable("optimalGroup", 
		                                  "Determine optimal threshold",
		                                  Tunable.GROUP, new Integer(5)));
		{
			//	 Gold standard attribute (node attribute)
    	clusterProperties.add(new Tunable("goldStandardAttributes",
	                                      "Gold standard attribute:",
 	                                     Tunable.NODEATTRIBUTE, "",
 	                                     (Object)null, (Object)null, 
			                                  Tunable.NUMERICATTRIBUTE));
			//	 Minimal threshold (integer)
			clusterProperties.add(new Tunable("minThreshold", 
			                                  "Minimal Threshold:",
	 		                                  Tunable.INTEGER, new Integer(10)));
			//	 Maximal threshold (integer)
			clusterProperties.add(new Tunable("maxThreshold", 
			                                  "Maximal Threshold:",
	 		                                  Tunable.INTEGER, new Integer(100)));
			// 	 Stepsize (float)
			clusterProperties.add(new Tunable("stepsize", 
			                                  "Stepsize:",
	 		                                  Tunable.DOUBLE, new Double(0.5)));
			// 	 Run Comparison (button)
			clusterProperties.add(new Tunable("runComparison", 
			                                  "",
	 		                                  Tunable.BUTTON, "Run Comparison", 
			                                  this, null, 0));
		}
	}

}
