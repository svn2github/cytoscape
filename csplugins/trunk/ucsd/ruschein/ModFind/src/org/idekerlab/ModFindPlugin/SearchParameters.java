package org.idekerlab.ModFindPlugin;

import cytoscape.CyNetwork;

public final class SearchParameters {
	
	private CyNetwork network;
	private String physicalEdgeAttrName;
	private String geneticEdgeAttrName;
		
	private double alpha;
	private double alphaMultiplier;
	private double physicalNetworkFilterDegree;
	
	private double pValueThreshold;
	
	public SearchParameters() {
	}

	public void setNetwork(CyNetwork network) {
		this.network = network;
	}

	public CyNetwork getNetwork() {
		return network;
	}

	public void setPhysicalEdgeAttrName(String physicalEdgeAttrName) {
		this.physicalEdgeAttrName = physicalEdgeAttrName;
	}

	public String getPhysicalEdgeAttrName() {
		return physicalEdgeAttrName;
	}

	public void setGeneticEdgeAttrName(String geneticEdgeAttrName) {
		this.geneticEdgeAttrName = geneticEdgeAttrName;
	}

	public String getGeneticEdgeAttrName() {
		return geneticEdgeAttrName;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlphaMultiplier(double alphaMultiplier) {
		this.alphaMultiplier = alphaMultiplier;
	}

	public double getAlphaMultiplier() {
		return alphaMultiplier;
	}

	public void setPhysicalNetworkFilterDegree(double physicalNetworkFilterDegree) {
		this.physicalNetworkFilterDegree = physicalNetworkFilterDegree;
	}

	public double getPhysicalNetworkFilterDegree() {
		return physicalNetworkFilterDegree;
	}

	public void setPValueThreshold(double pValueThreshold) {
		this.pValueThreshold = pValueThreshold;
	}

	public double getPValueThreshold() {
		return pValueThreshold;
	}
}
