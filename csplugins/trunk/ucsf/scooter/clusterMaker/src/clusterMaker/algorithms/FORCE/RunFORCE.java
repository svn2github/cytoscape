package clusterMaker.algorithms.FORCE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.CyNode;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;

import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.layout.parameter_training.ParameterTrainingFactory;
import de.layclust.taskmanaging.ClusteringManagerTask;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;


public class RunFORCE {
	private	TaskMonitor monitor = null;
	private	CyLogger logger = null;

	private boolean mergeSimilar = false;
	private double mergeThreshold = 0;
	private boolean evolutionaryTraining = false;

	public RunFORCE(DistanceMatrix matrix, boolean evolutionaryTraining, boolean mergeSimilar, 
	                double mergeThreshold, CyLogger logger) {

		this.evolutionaryTraining = evolutionaryTraining;
		this.matrix = matrix;
		this.mergeSimilar = mergeSimilar;
		this.mergeThreshold = mergeThreshold;
		this.logger = logger;
	}

	/**
	 * Execute the FORCE algorithm on the current dataset.  Note that this does
	 * assume that the connected components have already been calculated.
	 */
	public List<NodeCluster> run(TaskMonitor monitor) throws IOException {

		monitor.setPercentCompleted(1);
		monitor.setStatus("FORCE");
		
		Date date = new Date(System.currentTimeMillis());
		String dateTimeStr = date.toString().replaceAll(" ", "_");
		dateTimeStr = dateTimeStr.replaceAll(":", "-");
		
		File cmSubTempDir = File.createTempFile("cm_"+dateTimeStr, null);
		// This creates the temp file, but we want a directory
		cmSubTempDir.delete(); // Delete the tmp file

		// Now make a directory
		boolean suc = cmSubTempDir.mkdir();
		if (!suc) {
			throw new IOException("Can't write to temp directory: "+cmSubTempDir.toString());
		}

		Map<Integer, List<CyNode>> connectedComponents = matrix.findConnectedComponents();
		
		for (int i = 0; i < connectedComponents.size(); i++) {
			monitor.setPercentCompleted((i/connectedComponents.size()*2)*100);
			monitor.setStatus("Writing temp file nr. " + i + " of " + connectedComponents.size());

			
			List<CyNode> cc = connectedComponents.get(i);
			
			if(mergeSimilar){
				writeCCtoTempDirWithMergedNodes(cmSubTempDir, cc, i);
			}else{
				writeCCtoTempDir(cmSubTempDir, cc, i);
			}
			
			
		}
		
		monitor.setStatus("Running FORCE clustering (might take a while)...");
		
		// Copy over any necessary configuration values
		File resultsFileName = new File(cmSubTempDir, "_results.txt");
		TaskConfig.cmPath = cmSubTempDir.toString();
		TaskConfig.useConfigFile = false;
		TaskConfig.clustersPath = resultsFileName.toString();

		if(evolutionaryTraining) {
			TaskConfig.doLayoutParameterTraining = true;
			try {
				TaskConfig.parameterTrainingEnum = ParameterTrainingFactory
							.getParameterTrainingEnumByClass(TaskConfig.parameterTrainingClass);
			} catch (InvalidTypeException e) {
				logger.warning("Illegal parameter training class: "+TaskConfig.parameterTrainingClass);
				return;
			}
		}

		ClusteringManagerTask manageTask = new ClusteringManagerTask();
		manageTask.run(); //run without initialising new thread. [why? -- SM]

		if (evolutionaryTraining)
			TaskConfig.doLayoutParameterTraining = false;
		
		List<NodeCluster> r = readFORCEresults(resultsFileName);
		
		deleteDirectory(cmSubTempDir);
		resultsFileName.delete();
		return r;
		
	}


	/**
 	 * As part of the FORCE algorithm, we write out information into a temporary file.
 	 * This routine writes that file with the merged nodes.
 	 *
 	 * @param cmTempDir a File representing the temporary directory
 	 * @param cc the List of CyNodes
 	 * @param ccNr the component number of this file
 	 */
	private void writeCCtoTempDirWithMergedNodes(File cmTempDir, 
	                                             List<CyNode> cc, 
	                                             int ccNr) throws IOException {
		
		double normalizedUpperBound = 0;
		
		if (!isDistanceFunction) { // is sim function
			normalizedUpperBound = getNormalizedValue(minSim, maxSim, mergeThreshold);
		} else {
			normalizedUpperBound = 100-getNormalizedValue(minSim, maxSim, mergeThreshold);
		}
		
		// find connected components with upperBound to merge them to one node
		List<List<CyNode>> upperBoundMergedNodes = new ArrayList<List<CyNode>>();
		
		Map<String, Boolean> already = new HashMap<String, Boolean>();
		
		for (int j = 0; j < cc.size(); j++) {
			
			CyNode n = cc.get(j);
			
			if(!already.containsKey(n.getIdentifier())){
			
				List<CyNode> v = new ArrayList<CyNode>();
				
				findMergeNodes(n,cc,v,already,normalizedUpperBound);
				upperBoundMergedNodes.add(v);
				
			}
		}

		File tmpFile = new File(cmTempDir, "costmatrix_nr_"+ccNr+"_size+"+cc.size()+".rcm");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
		
		bw.write("0");
		bw.newLine();
		bw.write("" + upperBoundMergedNodes.size());
		bw.newLine();
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			List<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = 0; k < v.size()-1; k++) {
				bw.write(v.get(k).getIdentifier() + "\t");
			}
			bw.write(v.get(v.size()-1).getIdentifier());
			bw.newLine();
		}
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			List<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = j+1; k < upperBoundMergedNodes.size(); k++) {			
				List<CyNode> v2 = upperBoundMergedNodes.get(k);
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
		
	/**
 	 * As part of the FORCE algorithm, we write out information into a temporary file.
 	 * This routine writes that file without mering the nodes.
 	 *
 	 * @param cmTempDir a File representing the temporary directory
 	 * @param cc the List of CyNodes
 	 * @param ccNr the component number of this file
 	 */
	private void writeCCtoTempDir(File cmTempDir, 
	                              List<CyNode> cc, int ccNr) throws IOException {

		File tmpFile = new File(cmTempDir, "costmatrix_nr_"+ccNr+"_size+"+cc.size()+".cm");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
		
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
		double span = max-min;
		return ((value-min)/span)*100;
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

	private List<NodeCluster> readFORCEresults(File resultsFileName) throws IOException {
		
		Map<String, Integer> clusterForGivenNode = new HashMap<String, Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(resultsFileName));
		
		String line;
		while ((line=br.readLine()) != null) {
			
			String[] d = line.split("\t");
			
			clusterForGivenNode.put(d[0].trim(), Integer.parseInt(d[1].trim()));
		}
		
		br.close();
		
		Map<Integer, List<CyNode>> nodeListForGivenClusterNumber = new HashMap<Integer, List<CyNode>>();
		
		for (CyNode n: nodes) {
			
			int clusterNr =  clusterForGivenNode.get(n.getIdentifier());
			
			List<CyNode> v = new ArrayList<CyNode>();
			if (nodeListForGivenClusterNumber.containsKey(clusterNr)) {
				v = nodeListForGivenClusterNumber.get(clusterNr);
			}
			v.add(n);
			nodeListForGivenClusterNumber.put(clusterNr, v);
		}
		
		return null;
	}

}
