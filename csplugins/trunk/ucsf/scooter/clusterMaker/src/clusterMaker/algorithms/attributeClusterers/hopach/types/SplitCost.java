package clusterMaker.algorithms.attributeClusterers.hopach.types;

public enum SplitCost {
	AVERAGE_SPLIT_SILHOUETTE("Average split silhouette"),
	AVERAGE_SILHOUETTE("Average silhouette");

	private String name;

	SplitCost(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

}
