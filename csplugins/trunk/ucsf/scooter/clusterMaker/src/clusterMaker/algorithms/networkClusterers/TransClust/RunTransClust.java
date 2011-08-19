package clusterMaker.algorithms.networkClusterers.TransClust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import cern.colt.matrix.DoubleMatrix2D;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.networkClusterers.TransClust.de.costmatrixcreation.dataTypes.Edges;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.iterativeclustering.IteratorThread;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.taskmanaging.TaskConfig;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

public class RunTransClust {

	private List<CyNode> nodes;
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__TransClustGroups";
	protected int clusterCount = 0;
	private DistanceMatrix distanceMatrix = null;
	private double threshold;

	public RunTransClust( DistanceMatrix dMat,double threshold, CyLogger logger)
	{
		this.distanceMatrix = dMat;
		this.threshold = threshold;
		this.logger = logger;
	}
	
	public void halt () { canceled = true; }

	public List<NodeCluster> run(TaskMonitor monitor)
	{
		DoubleMatrix2D matrix = this.distanceMatrix.getDistanceMatrix(threshold, true);

		nodes = distanceMatrix.getNodes();
		HashMap<String, CyNode> nodeHash = new HashMap<String, CyNode>();
		for (CyNode node : nodes) {
			nodeHash.put(node.getIdentifier(), node);
		}

		HashMap<String,Integer> integers2proteins = new HashMap<String, Integer>();
		HashMap<Integer,String>  proteins2integers = new HashMap<Integer, String>();
		int count = 0;
		for (CyNode node : this.nodes) {
			integers2proteins.put(node.getIdentifier(), count);
			proteins2integers.put(count, node.getIdentifier());
			count++;
		}
		
		Edges es = new Edges(this.nodes.size()*this.nodes.size(), this.nodes.size());
		count = 0;
		for (int i = 0; i < this.nodes.size(); i++) {
			CyNode cyNodeI = this.nodes.get(i);
			es.startPositions[integers2proteins.get(cyNodeI.getIdentifier())] = count;
			for (int j = 0; j < this.nodes.size(); j++) {
				CyNode cyNodeJ = this.nodes.get(j);
					es.sources[count] = i;
					es.targets[count] = j;
					es.values[count] = (float) distanceMatrix.getEdgeValueFromMatrix(i, j);
					count++;
			}
			es.endPositions[integers2proteins.get(cyNodeI.getIdentifier())] = count-1;
		}
		
		Semaphore s = new Semaphore(1);
		TaskConfig.mode = TaskConfig.COMPARISON_MODE;
		IteratorThread it = new IteratorThread(es,integers2proteins,proteins2integers,s);
		TaskConfig.minThreshold = threshold;
		TaskConfig.maxThreshold = threshold;
		try {
			s.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		it.start();
		monitor.setStatus("Executing TransClust Clustering...");
		
		try {
			s.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		monitor.setStatus("Assigning nodes to clusters");

		String result = it.resultsStringBuffer.toString();
		String clusters[] = result.split("\t")[2].split(";");
		
		
		Map<Integer, NodeCluster> clusterMap = getClusterMap(clusters,nodeHash);

		
		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterMap.size()+" clusters");
	       
		if (clusterCount == 0) {
			logger.error("Created 0 clusters!!!!");
			return null;
		}

		int clusterNumber = 1;
		Map<NodeCluster,NodeCluster> cMap = new HashMap();
		for (NodeCluster cluster: NodeCluster.sortMap(clusterMap)) {
			if (cMap.containsKey(cluster))
				continue;

			cMap.put(cluster,cluster);

			cluster.setClusterNumber(clusterNumber);
			clusterNumber++;
		}

		Set<NodeCluster>clusters2 = cMap.keySet();
		return new ArrayList<NodeCluster>(clusters2);
	}
	
private Map<Integer, NodeCluster> getClusterMap(String[] clusters, HashMap<String, CyNode> nodeHash){
	    
		HashMap<Integer, NodeCluster> clusterMap = new HashMap<Integer, NodeCluster>();
		
		for (int i = 0; i < clusters.length; i++) {
			String elements[] = clusters[i].split(",");
			NodeCluster nc = new NodeCluster();
			for (int j = 0; j < elements.length; j++) {
				if(nodeHash.containsKey(elements[j].trim())){
					nc.add(nodeHash.get(elements[j].trim()));	
				}
			}
			clusterCount++;
			updateClusters(nc, clusterMap);
		}
		return clusterMap;
	}

	private void updateClusters(NodeCluster cluster, Map<Integer, NodeCluster> clusterMap) {
		for (CyNode node: cluster) {
			clusterMap.put(nodes.indexOf(node), cluster);
		}
	}
	
}
