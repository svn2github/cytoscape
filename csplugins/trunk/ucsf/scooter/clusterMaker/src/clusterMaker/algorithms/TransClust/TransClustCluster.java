package clusterMaker.algorithms.TransClust;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import de.layclust.start.TransClust;
import de.layclust.taskmanaging.TaskConfig;


public class TransClustCluster extends AbstractClusterAlgorithm implements ActionListener {
	private CyLogger logger = null;
	private	TaskMonitor monitor = null;

	private JPanel resultsPanel;

	private CyAttributes cyNodeAttributes;
	private CyAttributes cyEdgeAttributes;

	private List<CyNode> nodes;
	private List<CyEdge> edges;
	
	private static boolean closed;
	private static final long serialVersionUID = 1L;
	private Vector<String> validEdgeAttributes = new Vector<String>();
	private Vector<String> validNodeAttributes = new Vector<String>();
	private String groupAttributeCC = "__TransClustccGroups";
	private String groupAttribute = "__TransClustGroups";

	private double threshold;
	private Hashtable<String, Double> similaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	private Hashtable<String, Double> normalizedSimilaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	private double maxSim = Double.NEGATIVE_INFINITY;
	private double minSim = Double.POSITIVE_INFINITY;
	private boolean isDistanceFunction = false;
	private double normalizedThreshold = 0;
	private int step = 0, i = 0;

	// Tunable values
	protected String edgeAttribute = null;
	protected String goldStandardAttribute = null;
	protected boolean mergeSimilar = false;
	protected int mergeThreshold;
	protected boolean evolutionaryTraining = false;
	protected int generations;
	protected double stepSize;
	protected int maxSubclusterSize;
	protected int maxTime;
	protected String tempDir = System.getProperty("java.io.tmpdir");
	
	private int iterations;

	/**
 	 * Main constructor -- calls constructor of the superclass and initializes
 	 * all of our tunables.
 	 */
	public TransClustCluster() {
		super();
		logger = CyLogger.getLogger(TransClustCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "transclust";};
	public String getName() {return "TransClust cluster";};

	public JPanel getSettingsPanel() {
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

		Tunable t = clusterProperties.get("edgeAttribute");
		if ((t != null) && (t.valueChanged() || force))
			edgeAttribute = (String) t.getValue();

		t = clusterProperties.get("weightType");
		if ((t != null) && (t.valueChanged() || force)) {
			int val = ((Integer) t.getValue()).intValue();
			if (val == 0) 
				this.isDistanceFunction = false;
			else
				this.isDistanceFunction = true;
		}

		t = clusterProperties.get("threshold");
		if ((t != null) && (t.valueChanged() || force))
			threshold = ((Double) t.getValue()).doubleValue();

		// Advanced Settings

		// Node Attribute Settings
		t = clusterProperties.get("componentAttribute");
		if ((t != null) && (t.valueChanged() || force))
			groupAttributeCC = (String)t.getValue();

		t = clusterProperties.get("clusterAttribute");
		if ((t != null) && (t.valueChanged() || force))
			groupAttribute = (String)t.getValue();

		// Find Exect Solution
		t = clusterProperties.get("maxSubclusterSize");
		if ((t != null) && (t.valueChanged() || force))
			maxSubclusterSize = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("maxTime");
		if ((t != null) && (t.valueChanged() || force))
			maxTime = ((Integer) t.getValue()).intValue();

		// Merge Nodes
		t = clusterProperties.get("mergeSimilar");
		if ((t != null) && (t.valueChanged() || force))
			mergeSimilar = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("mergeThreshold");
		if ((t != null) && (t.valueChanged() || force))
			mergeThreshold = ((Integer) t.getValue()).intValue();

		// Parameter Training
		t = clusterProperties.get("evolutionaryTraining");
		if ((t != null) && (t.valueChanged() || force))
			evolutionaryTraining = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("generations");
		if ((t != null) && (t.valueChanged() || force))
			generations = ((Integer) t.getValue()).intValue();

		// Set temp directory
		t = clusterProperties.get("tempDirectory");
		if ((t != null) && (t.valueChanged() || force))
			tempDir = (String) t.getValue();

		t = clusterProperties.get("goldStandardAttributes");
		if ((t != null) && (t.valueChanged() || force))
			goldStandardAttribute = (String) t.getValue();

		t = clusterProperties.get("minThreshold");
		if ((t != null) && (t.valueChanged() || force))
			minSim = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("maxThreshold");
		if ((t != null) && (t.valueChanged() || force))
			maxSim = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("stepsize");
		if ((t != null) && (t.valueChanged() || force))
			stepSize = ((Double) t.getValue()).doubleValue();

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

		updateSettings();

		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();

		cyNodeAttributes.deleteAttribute(groupAttribute);

		nodes = Cytoscape.getCurrentNetwork().nodesList();
		edges = Cytoscape.getCurrentNetwork().edgesList();

		try {
			setIterations(1);
			step = 0;

			// Calculate all of the connected components
			boolean suc = runCC();

			// Cluster
			if (suc) runTransClust();
			CyLayoutAlgorithm l = CyLayouts.getLayout("attributes-layout");
			l.setLayoutAttribute(edgeAttribute);
			l.doLayout(Cytoscape.getCurrentNetworkView());

		} catch (IOException e) {
			StackTraceElement stack[] = e.getStackTrace();
			logger.error("IOException: "+e.getMessage()+" at: ");
			for (int i = 0; i < stack.length; i++) {
				logger.error("      "+stack[i].toString());
			}
		}

		// Set up the appropriate attributes
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "transclust");
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_ATTRIBUTE, edgeAttribute);

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
	protected void initializeProperties() {
		super.initializeProperties();

		// Attributes group
		clusterProperties.add(new Tunable("attributesGroup", 
		                                  "Attributes",
		                                  Tunable.GROUP, new Integer(3)));
		{
			// 	Edge weight attribute (Edge attribute combo-box)
    	clusterProperties.add(new Tunable("edgeAttribute",
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
 	                                     Tunable.DOUBLE, new Double(10)));
		}

		// Advanced attributes group
		clusterProperties.add(new Tunable("advancedAttributesGroup", 
		                                  "Advanced Attributes",
		                                  Tunable.GROUP, new Integer(4),
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));
		{

			//	Cytoscape
			clusterProperties.add(new Tunable("cytoscapeGroup", 
			                                  "Cytoscape",
			                                  Tunable.GROUP, new Integer(2)));
			{
				//		Node attribute connected component (string)
				clusterProperties.add(new Tunable("componentAttribute", 
			 		                                "Node attribute connected component:",
			 		                                Tunable.STRING, groupAttributeCC));
				//		Node attribute TransClust cluster (string)
				clusterProperties.add(new Tunable("clusterAttribute", 
			 		                                "Node attribute TransClust cluster:",
			 		                                Tunable.STRING, groupAttribute));
			}

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
			// TMP directory group
			clusterProperties.add(new Tunable("tmpDirectoryGroup", 
			                                  "Set Temporary Directory",
			                                  Tunable.GROUP, new Integer(1)));
			{
				//		Node attribute connected component (string)
				clusterProperties.add(new Tunable("tempDirectory", 
			 		                                "",
			 		                                Tunable.STRING, tempDir));
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
	 		                                  Tunable.DOUBLE, new Double(10)));
			//	 Maximal threshold (integer)
			clusterProperties.add(new Tunable("maxThreshold", 
			                                  "Maximal Threshold:",
	 		                                  Tunable.DOUBLE, new Double(100)));
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

	private void runTransClust() throws IOException {
		
		cyNodeAttributes.deleteAttribute(groupAttribute);
		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		
		TaskConfig.fixedParameter = true;
		
		try{
			TaskConfig.fixedParameterMax = new Integer(maxSubclusterSize);
		} catch (Exception e){
			TaskConfig.fixedParameterMax = 20;
			e.printStackTrace();
		}
		try{
			TaskConfig.fpMaxTimeMillis = new Integer(maxTime)*1000;
		} catch (Exception e){
			TaskConfig.fixedParameterMax = 1000;
			e.printStackTrace();
		}
		
		Vector<Vector<CyNode>> connectedComponents = new Vector<Vector<CyNode>>();
		//Hashtable<String, Integer> ccNrForGivenCCName = Hashtable<String, Integer>();
		
		for (CyNode n: nodes) {
			
			int ccNr = cyNodeAttributes.getIntegerAttribute(n.getIdentifier(), groupAttributeCC);
			try {
				connectedComponents.get(ccNr).add(n);
			} catch (Exception e) {
				Vector<CyNode> v = new Vector<CyNode>();
				v.add(n);
				connectedComponents.add(ccNr, v);
			}
		}
		
		monitor.setStatus("Executing TransClust Clustering...");
		Date date = new Date(System.currentTimeMillis());
		String dateTimeStr = date.toString().replaceAll(" ", "_");
		dateTimeStr = dateTimeStr.replaceAll(":", "-");
		
		String cmSubTempDirName = "cm_" + dateTimeStr;
		String cmTempDir = tempDir + File.separator+ cmSubTempDirName;
		
		boolean suc = (new File(cmTempDir)).mkdir();
		if (!suc) {
			throw new IOException("Can't write to temp directory.");
		}
		for (i = 0; i < connectedComponents.size(); i++) {
			
			if (getIterations() == 1)
			{
				monitor.setPercentCompleted(((i+1)*100)/connectedComponents.size());
			}
			
			Vector<CyNode> cc = connectedComponents.get(i);
			
			if(mergeSimilar){
				writeCCtoTempDirWithMergedNodes(cmTempDir, cc, i);
			}else{
				writeCCtoTempDir(cmTempDir, cc, i);
			}
		}
		String resultsFileName = cmTempDir + "_results.txt";
		
		monitor.setStatus("Clustering...");
		
		if(evolutionaryTraining){
			String[] args = {"-i",cmTempDir,"-o",resultsFileName,"-cf","FALSE","-lp",TaskConfig.parameterTrainingClass,"-lpn",""+generations};
			TransClust.main(args);
			TaskConfig.doLayoutParameterTraining = false;
		}else{
			String[] args = {"-i",cmTempDir,"-o",resultsFileName,"-cf","FALSE"};
			TransClust.main(args);
		}
		if (getIterations() == 1)
		{
			monitor.setStatus("Finishing...");
		}
		readTransClustresults(resultsFileName);
		deleteDirectory(new File(cmTempDir));
		new File(resultsFileName).delete();
	}
	
	private void writeCCtoTempDirWithMergedNodes(String cmTempDir, 
	                                             Vector<CyNode> cc, int i) throws IOException {
		
		double normalizedUpperBound = 0;
		
		if (!this.isDistanceFunction) { // is sim function
			normalizedUpperBound = getNormalizedValue(minSim, maxSim, mergeThreshold);
		} else {
			normalizedUpperBound = maxSim-getNormalizedValue(minSim, maxSim, mergeThreshold);
		}
		
		// find connected components with upperBound to merge them to one node
		Vector<Vector<CyNode>> upperBoundMergedNodes = new Vector<Vector<CyNode>>();
		
		Hashtable<String, Boolean> already = new Hashtable<String, Boolean>();
		
		for (int j = 0; j < cc.size(); j++) {
			
			CyNode n = cc.get(j);
			
			if(!already.containsKey(n.getIdentifier())){
			
				Vector<CyNode> v = new Vector<CyNode>();
				
				findMergeNodes(n,cc,v,already,normalizedUpperBound);
				upperBoundMergedNodes.add(v);
				
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter((cmTempDir + "/costmatrix_nr_" + i + "_size_" + cc.size() + ".rcm")));
		
		bw.write("0");
		bw.newLine();
		bw.write("" + upperBoundMergedNodes.size());
		bw.newLine();
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			Vector<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = 0; k < v.size()-1; k++) {
				bw.write(v.get(k).getIdentifier() + "\t");
			}
			bw.write(v.get(v.size()-1).getIdentifier());
			bw.newLine();
		}
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			Vector<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = j+1; k < upperBoundMergedNodes.size(); k++) {			
				Vector<CyNode> v2 = upperBoundMergedNodes.get(k);
				double sim = calculateSimilarityForMergeNodes(v,v2);
				bw.write(Double.toString(sim));
				if(k<upperBoundMergedNodes.size()-1){
					bw.write("\t");
				}else if(j<upperBoundMergedNodes.size()-1){
					bw.write("\n");
				}
			}	
		}
		bw.close();
	}

	private double calculateSimilarityForMergeNodes(Vector<CyNode> v, Vector<CyNode> v2) {
		
		double value = 0;
		
		for (int i = 0; i < v.size(); i++) {
			CyNode n = v.get(i);
			for (int j = 0; j < v2.size(); j++) {
				CyNode n2 = v2.get(j);
				String key = n.getIdentifier() + "#" + n2.getIdentifier();
				String keyI = n2.getIdentifier() + "#" + n.getIdentifier();
				if(this.normalizedSimilaritiesForGivenEdges.containsKey(key)){
					value+=(this.normalizedSimilaritiesForGivenEdges.get(key)-threshold);
				}else if(this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)){
					value+=(this.normalizedSimilaritiesForGivenEdges.get(keyI)-threshold);
				}
			}
		}
		return value;
	}

	private void findMergeNodes(CyNode n, Vector<CyNode> cc, Vector<CyNode> v, Hashtable<String, Boolean> already, double normalizedUpperBound) {
		
		v.add(n);
		already.put(n.getIdentifier(), true);
		
		for (int i = 0; i < cc.size(); i++) {
			
			CyNode n2 = cc.get(i);
			String key = n.getIdentifier() + "#" + n2.getIdentifier();
			String keyI = n2.getIdentifier() + "#" + n.getIdentifier();
			
			if(!already.containsKey(n2.getIdentifier())){
				
				if(this.normalizedSimilaritiesForGivenEdges.containsKey(key)){
					if(this.normalizedSimilaritiesForGivenEdges.get(key)>normalizedUpperBound){
						findMergeNodes(n2, cc, v, already, normalizedUpperBound);
					}
				}else if(this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)){
					if(this.normalizedSimilaritiesForGivenEdges.get(keyI)>normalizedUpperBound){
						findMergeNodes(n2, cc, v, already, normalizedUpperBound);
					}
				}
			}
		}
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	private void readTransClustresults(String resultsFileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(resultsFileName));
		String line;
		while ((line=br.readLine()) != null) {
			
			String[] d = line.split("\t");
			cyNodeAttributes.setAttribute(d[0].trim(), groupAttribute, 
			                              Integer.parseInt(d[1].trim()));
		}
		br.close();
	}

	private void writeCCtoTempDir(String cmTempDir, Vector<CyNode> cc, int ccNr) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter((cmTempDir + "/costmatrix_nr_" + ccNr + "_size_" + cc.size() + ".cm")));
		
		bw.write("" + cc.size());
		bw.newLine();
		
		for (int i = 0; i < cc.size(); i++) {
			CyNode n = cc.get(i);
			bw.write(n.getIdentifier());
			bw.newLine();
		}
		
		for (int i = 0; i < cc.size(); i++) {
			String s = cc.get(i).getIdentifier();
			for (int j = i+1; j < cc.size(); j++) {
				String t = cc.get(j).getIdentifier();
				
				double cost = -this.normalizedThreshold;
				if (this.normalizedSimilaritiesForGivenEdges.containsKey(s + "#" + t)) {
					cost = this.normalizedSimilaritiesForGivenEdges.get(s + "#" + t) - this.normalizedThreshold;
				} else if (this.normalizedSimilaritiesForGivenEdges.containsKey(t + "#" + s)) {
					cost = this.normalizedSimilaritiesForGivenEdges.get(t + "#" + s) - this.normalizedThreshold;
				}
				
				if (j != cc.size()-1) {
					bw.write(cost + "\t");
				} else {
					bw.write("" + cost);
				}
			}
			
			if (i != cc.size()-1) {
				bw.newLine();
			}
			
		}
		
		bw.close();
		
	}
	
	private double getNormalizedValue(double min, double max, double value) {
//		double span = max-min;
//		return ((value-min)/span)*100;
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private boolean runCC() {
		
		nodes = Cytoscape.getCurrentNetwork().nodesList();
		edges = Cytoscape.getCurrentNetwork().edgesList();

		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		
		int max = (this.edges.size()*2 + this.nodes.size())*getIterations();
		monitor.setStatus("Assigning Connected Components...");
		
		byte type = cyEdgeAttributes.getType(edgeAttribute);
		
		for (CyEdge e: edges) {
			
			String sourceID = e.getSource().getIdentifier();
			String targetID = e.getTarget().getIdentifier();
			
			// THIS IS A STRANGE BUG IN CYTOSCAPE; it always give u 2 additional nodes :-(
			if (!sourceID.equalsIgnoreCase("Source") && !sourceID.equalsIgnoreCase("Target") && !targetID.equalsIgnoreCase("Source") && !targetID.equalsIgnoreCase("Target")) {
				
				double sim = 0;
				if (type == CyAttributes.TYPE_INTEGER) {
					try {
						sim = (double) cyEdgeAttributes.getIntegerAttribute(e.getIdentifier(), edgeAttribute);
					} catch(NullPointerException e1) {
						continue;
					}
					
				} else {
					try {
						sim = cyEdgeAttributes.getDoubleAttribute(e.getIdentifier(), edgeAttribute);
					} catch(NullPointerException e1) {
						continue;
					}
					
				}
				
				similaritiesForGivenEdges.put((sourceID + "#" + targetID), sim);
				
				if (sim < minSim) {
					minSim = sim;
				}
				if (sim > maxSim) {
					maxSim = sim;
				}
				
			}
			
			step++;
			if ((step%10) == 0) {
				monitor.setPercentCompleted((step*100)/max);
			}
		}
		
		if (!isDistanceFunction) { // is sim function
			normalizedThreshold = getNormalizedValue(minSim, maxSim, threshold);
		} else {
			normalizedThreshold = maxSim-getNormalizedValue(minSim, maxSim, threshold);
		}
		
		for (String key: similaritiesForGivenEdges.keySet()) {
			
			double sd = similaritiesForGivenEdges.get(key);
			double s;
			if (!isDistanceFunction) { // is sim function
				s = getNormalizedValue(minSim, maxSim, sd);
			} else {
				s = maxSim-getNormalizedValue(minSim, maxSim, sd);
			}
			
			normalizedSimilaritiesForGivenEdges.put(key, s);
			
			step++;
			if ((step%10) == 0) {
				monitor.setPercentCompleted((step*100)/max);
			}
		}
			
		Hashtable<String, Boolean> already = new Hashtable<String, Boolean>();
		
		int clusterNr = 0;
		
		// TODO: metanode stuff
		
		String networkID = Cytoscape.getCurrentNetwork().getIdentifier();
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		if (netAttributes.hasAttribute(networkID, groupAttributeCC)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, groupAttributeCC);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}
		
		List<String>groupList = new ArrayList<String>();
		for (CyNode node: nodes) {
			String id = node.getIdentifier();
			
			if (!already.containsKey(id)) {
				
				Vector<CyNode> nodesInThisCluster = new Vector<CyNode>();
				assingNodeToCluster(already, node, id, clusterNr, nodesInThisCluster);
				clusterNr++;
				
				String groupName = groupAttributeCC +"_" + clusterNr;
				
				CyGroup newgroup = CyGroupManager.createGroup(groupName, nodesInThisCluster, null);
				if (newgroup != null) {
					CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), true);
					groupList.add(groupName);
				}
				
			}
			
			step++;
			if ((step%10) == 0) {
				monitor.setPercentCompleted((step*100)/max);
			}
		}
		
		netAttributes.setListAttribute(networkID, groupAttributeCC, groupList);
		
		return true;
	}
	
	private void assingNodeToCluster(Hashtable<String, Boolean> already, CyNode node, String id, int clusterNr, Vector<CyNode> nodesInThisCluster) {
		
		already.put(id, true);
		
		cyNodeAttributes.setAttribute(id, groupAttributeCC, clusterNr);
		
		for (CyNode node2: nodes) {
			String id2 = node2.getIdentifier();
			
			if (!id.equalsIgnoreCase("Source") && !id.equalsIgnoreCase("Target") && !id2.equalsIgnoreCase("Source") && !id2.equalsIgnoreCase("Target")) {
				
				if (!already.containsKey(id2)) {
					
					String key = id + "#" + id2;
					String keyI = id2 + "#" + id;
					
					double sim = this.normalizedThreshold -1;
					if (this.normalizedSimilaritiesForGivenEdges.containsKey(key)) {
						sim = this.normalizedSimilaritiesForGivenEdges.get(key);
					} else if (this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)) {
						sim = this.normalizedSimilaritiesForGivenEdges.get(keyI);
					}
					
					// System.out.println("sim: " + sim);
					
					if (sim >= this.normalizedThreshold) {
						assingNodeToCluster(already, node2, id2, clusterNr, nodesInThisCluster);
					}
				}
			}
		}
	}
	
	
	// setze das max der progressbar im thread aufruf, bei normalem clustering auf 1, ansonsten auf die anzahl der durchlaeufe, die entsprechend min/max/stepsize gebraucht werden
	// ansonsten macht sich die progressbar in jedem run hier neu auf...
	private void setIterations(int i)
	{
		iterations = i;
	}
	private int getIterations()
	{
		return iterations;
	}
	
	private void runThresholdDetermination(){
		
		double minThreshold = minSim;
		double maxThreshold = maxSim;
		double stepsize = stepSize;
		double thresh = minThreshold;
		
		setIterations(new Double(Math.floor(maxThreshold-minThreshold)/stepsize+1.0).intValue());
		step = 0;
		
//		System.out.println("minThreshold = " + minThreshold + " maxThreshold = " + maxThreshold + " stepsize = " + stepsize);
		thresh = minThreshold;
		Tunable t = clusterProperties.get("threshold");
		t.setValue(thresh);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Threshold");
		columnNames.add("Recall");
		columnNames.add("Precision");
		columnNames.add("F-measure I");
		columnNames.add("F-measure II");
		columnNames.add("Rel. Edit Distance");
		
		ComparisonResultsTable crt = new ComparisonResultsTable(columnNames);
		
		while(thresh<=maxThreshold){
			t.setValue(thresh);
			try {
				boolean suc = runCC();
				if(suc) runTransClust();
				ComparisonResults cr = runComparison();
				Vector<String> v = cr.toVector(Double.toString(thresh));
				crt.addRow(v);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - " + e1.getMessage());
			}
			
			thresh += stepsize;
		}
		
		crt.initializeTable();
		
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
		
		if (resultsPanel != null) {
				
			cytoPanel.remove(resultsPanel);
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
            	cytoPanel.setState(CytoPanelState.HIDE);
            }
            
		}
		
		resultsPanel = crt;
		
		cytoPanel.add("Comparison results", resultsPanel);
        cytoPanel.setState(CytoPanelState.DOCK);
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Threshold determination completed.\nThe results are shown in the panel on the right.");
        resultsPanel.updateUI();
	}
	
	
	
	private ComparisonResults runComparison() {
		
		nodes = Cytoscape.getCurrentNetwork().nodesList();
		edges = Cytoscape.getCurrentNetwork().edgesList();

		CyAttributes cyNodeAttributes = Cytoscape.getNodeAttributes();
		
		Hashtable<String,Hashtable<String, CyNode>> goldStandard = new Hashtable<String,Hashtable<String, CyNode>>();
		Hashtable<String,Hashtable<String, CyNode>> compareCluster = new Hashtable<String,Hashtable<String, CyNode>>();
			
		double editDist = 0, editDistance = 0;
			
		for (int i = 0; i < nodes.size(); i++) {
			CyNode n = nodes.get(i);
			String id = n.getIdentifier();
			String goldStandardClusterID = getAttributeString(cyNodeAttributes, id, goldStandardAttribute);	
			String compareClusterID = getAttributeString(cyNodeAttributes, id, groupAttribute);
			if(goldStandard.containsKey(goldStandardClusterID)){
				Hashtable<String,CyNode> dummy = goldStandard.get(goldStandardClusterID);
				dummy.put(id, n);
				goldStandard.put(goldStandardClusterID, dummy);
			}else{
				Hashtable<String, CyNode> dummy = new Hashtable<String, CyNode>();
				dummy.put(id, n);
				goldStandard.put(goldStandardClusterID, dummy);
			}
				
			if(compareCluster.containsKey(compareClusterID)){
				Hashtable<String,CyNode> dummy = compareCluster.get(compareClusterID);
				dummy.put(id, n);
				compareCluster.put(compareClusterID, dummy);
			}else{
				Hashtable<String, CyNode> dummy = new Hashtable<String, CyNode>();
				dummy.put(id, n);
				compareCluster.put(compareClusterID, dummy);
			}
			
			for (int j = i+1; j != nodes.size(); j++)
			{
				if (i == j) break;
				int nCl = 0, n2Cl = 0;;
				CyNode n2 = nodes.get(j);
				// same cluster?
				if (getAttributeString(cyNodeAttributes, id, goldStandardAttribute).equals(getAttributeString(cyNodeAttributes, n2.getIdentifier(), goldStandardAttribute)))
				{
					nCl = 1;
				}
				if (getAttributeString(cyNodeAttributes, id, groupAttribute).equals(getAttributeString(cyNodeAttributes, n2.getIdentifier(), groupAttribute)))
				{
					n2Cl = 1;
				}
				editDist += Math.abs(nCl - n2Cl);
			}
		}
			
		// edit distance
		int nn = nodes.size();
		double max =  nn*(nn-1)/2;
		editDistance = editDist / max;
		
		// recall = tp/tp+fn
			
	double recall = calculateRecall(goldStandard,compareCluster);
		
			
		//precision = tp/tp+fp
		double precision = calculatePrecision(goldStandard,compareCluster);
	
		double fMeasure = calculateFmeasure(goldStandard,compareCluster);
		
		double fMeasureUsingSensitivitySpecifity = (2*recall*precision)/(recall+precision);
		
		ComparisonResults cr = new ComparisonResults(recall,precision,fMeasureUsingSensitivitySpecifity,fMeasure, editDistance);
			
		return cr;
			
	}
	
	
	@SuppressWarnings("unchecked")
	private double calculatePrecision(Hashtable<String, Hashtable<String, CyNode>> goldStandard2, Hashtable<String, Hashtable<String, CyNode>> compareCluster) {
		double precision = 0;
		double truePositives = 0;
		double falseNegatives = 0;
		double falsePositives = 0;
		double numberOfNodes = 0;
		
		for (String element: goldStandard2.keySet()) {
			Hashtable<String , CyNode> goldStandard = goldStandard2.get(element);
			numberOfNodes+=goldStandard.size();
			double maxTruePositivesForCurrentCluster = 0;
			double currentFalseNegatives = 0;
			double currentFalsePositives = 0;
			for (String element2: compareCluster.keySet()) {
				Hashtable<String , CyNode> compare = compareCluster.get(element2);
				double truePositivesForThisCluster = 0;
				//true positives
				for (String key: compare.keySet()) {
					if(goldStandard.containsKey(key)) truePositivesForThisCluster++;
				}
				if(truePositivesForThisCluster>maxTruePositivesForCurrentCluster) {
					maxTruePositivesForCurrentCluster = truePositivesForThisCluster;
					currentFalsePositives = compare.size()-truePositivesForThisCluster;
					currentFalseNegatives = goldStandard.size()-truePositivesForThisCluster;
				}
			}
			truePositives += maxTruePositivesForCurrentCluster;
			falsePositives += currentFalsePositives;
			falseNegatives += currentFalseNegatives;
		}			
		precision = truePositives/(truePositives+falsePositives);
		return precision;
	}

	@SuppressWarnings("unchecked")
	private double calculateRecall(Hashtable<String, Hashtable<String, CyNode>> goldStandard2, Hashtable<String, Hashtable<String, CyNode>> compareCluster) {
		double sensitivity = 0;
		double truePositives = 0;
		double numberOfNodes = 0;
		
		for (String element: goldStandard2.keySet()) {
			Hashtable<String , CyNode> goldStandard = goldStandard2.get(element);
			numberOfNodes+=goldStandard.size();
			double maxTruePositivesForCurrentCluster = 0;
			for (String element2: compareCluster.keySet()) {
				Hashtable<String , CyNode> compare = compareCluster.get(element2);
				double truePositivesForThisCluster = 0;
				for (String key: compare.keySet()) {
					if(goldStandard.containsKey(key)) truePositivesForThisCluster++;
				}
				if(truePositivesForThisCluster>maxTruePositivesForCurrentCluster) maxTruePositivesForCurrentCluster = truePositivesForThisCluster;
			}
			truePositives += maxTruePositivesForCurrentCluster;
		}
		
		sensitivity = truePositives/numberOfNodes;
		
		return sensitivity;
	}

	@SuppressWarnings("unchecked")
	private double calculateFmeasure(Hashtable<String, Hashtable<String, CyNode>> goldStandard, Hashtable<String, Hashtable<String, CyNode>> compare) {
		
		double fmeasure = 0;
		double numberOfNodes = 0;
		
		for (String element: goldStandard.keySet()) {
			Hashtable<String, CyNode> goldStandardCluster = goldStandard.get(element);
			numberOfNodes+=goldStandardCluster.size();
			double nh = goldStandardCluster.size();
			double max = 0;
			for (String element2: compare.keySet()) {
				Hashtable<String, CyNode> compareCluster = compare.get(element2);
				double nl = compareCluster.size();
				double common = 0;
				for (String key: compareCluster.keySet()) {
					if(goldStandardCluster.containsKey(key)) common++;
				}
				double dummy = (2*common)/(nl+nh);
				if(dummy>max) max = dummy;
			}
			fmeasure+=(max*nh);
		}
		fmeasure/=numberOfNodes;
		
		return fmeasure;
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
	
	private class ComparisonResults{
		
		private double recall,precision,fm1,fm2, ed;
		
		
		public ComparisonResults(double recall, double precision, double fm1, double fm2, double ed){
			this.recall = recall;
			this.precision = precision;
			this.fm1 = fm1;
			this.fm2 = fm2;
			this.ed = ed;
		}
		
		public Vector<String> toVector(String firstEntry){
			Vector<String> v = new Vector<String>();
			
			v.add(firstEntry);
			v.add(Double.toString(recall));
			v.add(Double.toString(precision));
			v.add(Double.toString(fm1));
			v.add(Double.toString(fm2));
			v.add(Double.toString(ed));
			
			return v;
		}
		
	}
	
}
