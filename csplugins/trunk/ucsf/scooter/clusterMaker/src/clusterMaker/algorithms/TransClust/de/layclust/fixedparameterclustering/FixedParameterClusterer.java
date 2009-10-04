package de.layclust.fixedparameterclustering;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.taskmanaging.TaskConfig;

public class FixedParameterClusterer {

	private ConnectedComponent cc;
	private double maxK;
	private FixedParameterTreeNode solution;
	private long startTime;

	public FixedParameterClusterer(ConnectedComponent cc) {
		this.cc = cc;
		this.maxK = 0;
		startTime = System.currentTimeMillis();
		while (solution == null) {
			if(System.currentTimeMillis()-startTime>TaskConfig.fpMaxTimeMillis){
				TaskConfig.fpStopped = true;
				return;
			}
			FixedParameterTreeNode fptn = initFirstTreeNode();
			cluster(fptn);
			this.maxK += 10;
		}
		buildClusters(solution);
	}

	public FixedParameterClusterer(ConnectedComponent cc, double maxK) {
		this.cc = cc;
		this.maxK = maxK/2;
		startTime = System.currentTimeMillis();
		while (solution == null) {
			if(System.currentTimeMillis()-startTime>TaskConfig.fpMaxTimeMillis){
				TaskConfig.fpStopped = true;
				return;
			}
			FixedParameterTreeNode fptn = initFirstTreeNode();
			cluster(fptn);
			this.maxK += maxK/10;
		}
		buildClusters(solution);
	}

	private void assingCluster(int[] nodes2clusters, int clusterNr, int node_i,
			boolean[] already, float[][] edges) {

		for (int i = 0; i < edges.length; i++) {
			if (already[i])
				continue;
			if (edges[node_i][i] > 0) {
				nodes2clusters[i] = clusterNr;
				already[i] = true;
				assingCluster(nodes2clusters, clusterNr, i, already, edges);
			}
		}
	}

	private void buildClusters(FixedParameterTreeNode solution) {

		float edges[][] = solution.getEdgeCosts();

		int[] nodes2clustersReduced = new int[solution.size];
		int clusterNr = 0;
		boolean[] already = new boolean[this.cc.getNodeNumber()];

		for (int i = 0; i < edges.length; i++) {
			if (already[i])
				continue;
			nodes2clustersReduced[i] = clusterNr;
			already[i] = true;
			assingCluster(nodes2clustersReduced, clusterNr, i, already, edges);
			clusterNr++;
		}

		int[] nodes2clusters = new int[this.cc.getNodeNumber()];
		for (int i = 0; i < nodes2clustersReduced.length; i++) {
			for (int j = 0; j < solution.getClusters()[i].length; j++) {
				if (solution.getClusters()[i][j])
					nodes2clusters[j] = nodes2clustersReduced[i];
			}
		}

		this.cc.initialiseClusterInfo(clusterNr);
		this.cc.setClusteringScore(this.cc
				.calculateClusteringScore(nodes2clusters));
		this.cc.setClusters(nodes2clusters);
		this.cc.calculateClusterDistribution();
	}

	private float calculateCostsForMerging(FixedParameterTreeNode fptn,
			int node_i, int node_j) {
		float costsForMerging = 0;

		for (int i = 0; i < fptn.size; i++) {
			if (i == node_i || i == node_j)
				continue;
			if ((fptn.getEdgeCosts()[i][node_i] > 0 && fptn.getEdgeCosts()[i][node_j] > 0)
					|| (fptn.getEdgeCosts()[i][node_i] <= 0 && fptn
							.getEdgeCosts()[i][node_j] <= 0))
				continue;
			costsForMerging += Math.min(Math
					.abs(fptn.getEdgeCosts()[i][node_i]), Math.abs(fptn
					.getEdgeCosts()[i][node_j]));
		}

		return costsForMerging;
	}

	private float calculateCostsForSetForbidden(FixedParameterTreeNode fptn,
			int node_i, int node_j) {

		float costs = 0;

		for (int i = 0; i < fptn.size; i++) {
			if (fptn.getEdgeCosts()[node_i][i] > 0
					&& fptn.getEdgeCosts()[node_j][i] > 0) {
				costs += Math.min(fptn.getEdgeCosts()[node_i][i], fptn
						.getEdgeCosts()[node_j][i]);
			}
		}

		costs += fptn.getEdgeCosts()[node_i][node_j];
		return costs;
	}

	private void cluster(FixedParameterTreeNode fptn) {
		
		if(System.currentTimeMillis()-startTime>TaskConfig.fpMaxTimeMillis){
			TaskConfig.fpStopped = true;
			return;
		}
		fptn = reductionicf(fptn);
		int[] edge = findNextConflictTriple2(fptn);
		if (edge == null) {
			maxK = fptn.getCosts();
			solution = fptn.copy();
			return;
		}
		// branch 1 (merge)
		float costsForMerging = calculateCostsForMerging(fptn, edge[0], edge[1]);
		if (costsForMerging + fptn.getCosts() <= maxK) {
			FixedParameterTreeNode fptn2 = mergeNodes(fptn, edge[0], edge[1],
					costsForMerging);
			cluster(fptn2);
		}
		// branch 2 (forbidden)
		float costsForSetForbidden = calculateCostsForSetForbidden(fptn,
				edge[0], edge[1]);
		if (fptn.getCosts() + costsForSetForbidden <= maxK) {
			setForbiddenAndCluster(fptn, edge[0], edge[1],
					fptn.getEdgeCosts()[edge[0]][edge[1]]);
		}
	}

	private int[] findNextConflictTriple2(FixedParameterTreeNode fptn) {

		float[][] numberOfOccurencesInConflictTriples = new float[fptn.size][fptn.size];
		for (int i = 0; i < fptn.size; i++) {
			for (int j = i + 1; j < fptn.size; j++) {
				if (fptn.getEdgeCosts()[i][j] > 0) {
					numberOfOccurencesInConflictTriples[i][j] = numberOfOccurencesInConflictTriples[j][i] = Math
							.abs(calculateCostsForMerging(fptn, i, j)
									- calculateCostsForSetForbidden(fptn, i, j));
				}
			}
		}
		int[] bestEdge = new int[2];
		float highestOccurence = 0;
		for (int i = 0; i < fptn.size; i++) {
			for (int j = i + 1; j < fptn.size; j++) {
				if (numberOfOccurencesInConflictTriples[i][j] > highestOccurence) {
					highestOccurence = numberOfOccurencesInConflictTriples[i][j];
					bestEdge[0] = i;
					bestEdge[1] = j;
				}
			}
		}
		if (highestOccurence == 0)
			return null;

		return bestEdge;
	}

	private FixedParameterTreeNode initFirstTreeNode() {

		FixedParameterTreeNode fptn = new FixedParameterTreeNode(this.cc
				.getNodeNumber(), 0, this.cc.getNodeNumber());

		for (int i = 0; i < fptn.size; i++) {
			fptn.getClusters()[i][i] = true;
			for (int j = i + 1; j < fptn.size; j++) {
				fptn.getEdgeCosts()[i][j] = fptn.getEdgeCosts()[j][i] = this.cc
						.getCCEdges().getEdgeCost(i, j);
			}
		}
		fptn = reductionicf(fptn);
		return fptn;
	}

	private FixedParameterTreeNode mergeNodes(FixedParameterTreeNode fptn,
			int node_i, int node_j, float costsForMerging) {

		FixedParameterTreeNode fptnNew = new FixedParameterTreeNode(
				fptn.size - 1, fptn.getCosts(), this.cc.getNodeNumber());
		fptnNew.setCosts(fptn.getCosts() + costsForMerging);

		int mappingOld2New[] = new int[fptn.size];
		for (int i = 0, j = 0; i < fptn.size; i++) {
			if (i == node_i || i == node_j)
				continue;
			mappingOld2New[i] = j;
			fptnNew.getClusters()[j] = fptn.getClusters()[i];
			j++;
		}

		for (int i = 0; i < mappingOld2New.length; i++) {
			if (i == node_i || i == node_j)
				continue;
			for (int j = i + 1; j < mappingOld2New.length; j++) {
				if (j == node_i || j == node_j)
					continue;
				fptnNew.getEdgeCosts()[mappingOld2New[i]][mappingOld2New[j]] = fptnNew
						.getEdgeCosts()[mappingOld2New[j]][mappingOld2New[i]] = fptn
						.getEdgeCosts()[i][j];
			}
		}

		for (int i = 0; i < this.cc.getNodeNumber(); i++) {
			fptnNew.getClusters()[fptnNew.size - 1][i] = (fptn.getClusters()[node_i][i] || fptn
					.getClusters()[node_j][i]);
		}

		for (int i = 0; i < fptn.size; i++) {
			if (i == node_i || i == node_j)
				continue;
			fptnNew.getEdgeCosts()[mappingOld2New[i]][fptnNew.size - 1] = fptnNew
					.getEdgeCosts()[fptnNew.size - 1][mappingOld2New[i]] = fptn
					.getEdgeCosts()[i][node_i]
					+ fptn.getEdgeCosts()[i][node_j];
		}
		return fptnNew;
	}

	private FixedParameterTreeNode reductionicf(FixedParameterTreeNode fptnNew) {

		if (fptnNew.getCosts() > maxK) {
			return fptnNew;
		}

		for (int i = 0; i < fptnNew.size; i++) {
			for (int j = i + 1; j < fptnNew.size; j++) {
				if (fptnNew.getEdgeCosts()[i][j] <= 0)
					continue;
				float sumIcf = calculateCostsForSetForbidden(fptnNew, i, j);
				float sumIcp = calculateCostsForMerging(fptnNew, i, j);
				if (sumIcf + fptnNew.getCosts() > maxK
						&& sumIcp + fptnNew.getCosts() > maxK) {
					fptnNew.setCosts(Float.POSITIVE_INFINITY);
					return fptnNew;
				} else if (sumIcf + fptnNew.getCosts() > maxK) {
					float costsForMerging = calculateCostsForMerging(fptnNew,
							i, j);
					FixedParameterTreeNode fptnNew2 = mergeNodes(fptnNew, i, j,
							costsForMerging);
					fptnNew2 = reductionicf(fptnNew2);
					return fptnNew2;
				} else if (sumIcp + fptnNew.getCosts() > maxK) {
					fptnNew.setCosts(fptnNew.getCosts()
							+ fptnNew.getEdgeCosts()[i][j]);
					fptnNew.getEdgeCosts()[i][j] = fptnNew.getEdgeCosts()[j][i] = Float.NEGATIVE_INFINITY;
					fptnNew = reductionicf(fptnNew);
					return fptnNew;
				}
			}
		}
		return fptnNew;
	}

	private void setForbiddenAndCluster(FixedParameterTreeNode fptn,
			int node_i, int node_j, float costsForSetForbidden) {
		fptn.setCosts(fptn.getCosts() + fptn.getEdgeCosts()[node_i][node_j]);
		fptn.getEdgeCosts()[node_i][node_j] = fptn.getEdgeCosts()[node_j][node_i] = Float.NEGATIVE_INFINITY;
		cluster(fptn);
		fptn.setCosts(fptn.getCosts() - costsForSetForbidden);
		fptn.getEdgeCosts()[node_i][node_j] = fptn.getEdgeCosts()[node_j][node_i] = costsForSetForbidden;
	}

}
