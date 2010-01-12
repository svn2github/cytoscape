package org.cytoscape.DenovoPGNetworkAlignmentPlugin;


/**
 * A data class that collects various properties of a module edge.
 */
class EdgeProperties {
	final int sourceNodeIndex;
	final int targetNodeIndex;
	final float edgeScore; // a.k.a. "link"

	EdgeProperties(final int sourceNodeIndex, final int targetNodeIndex, final float edgeScore) {
		this.sourceNodeIndex = sourceNodeIndex;
		this.targetNodeIndex = targetNodeIndex;
		this.edgeScore = edgeScore;
	}

	int getSourceNodeIndex() { return sourceNodeIndex; }
	int getTargetNodeIndex() { return targetNodeIndex; }
	float getEdgeScore() { return edgeScore; }
}

