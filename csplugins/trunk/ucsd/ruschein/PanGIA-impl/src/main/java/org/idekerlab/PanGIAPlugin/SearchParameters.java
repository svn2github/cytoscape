package org.idekerlab.PanGIAPlugin;


import cytoscape.CyNetwork;
//import cytoscape.util.ScalingMethod;


public final class SearchParameters {
	private CyNetwork physicalNetwork;
	private CyNetwork geneticNetwork;
	
	private String nodeAttrName;
	
	private String physicalEdgeAttrName;
	private String geneticEdgeAttrName;
	
	
	private String physicalScalingMethod;
	private String geneticScalingMethod;
		
	private double alpha;
	private double alphaMultiplier;
	private int physicalNetworkFilterDegree;
	
	private double pValueThreshold;
	private int numberOfSamples;
	
	private boolean complexTrainingPhysical;
	private boolean complexTrainingGenetic;
	private boolean complexAnnotation;
	private String annotationAttrName;
	private double annotationThreshold;
	
	private String reportPath;
	
	public SearchParameters() {
	}

	public void setPhysicalNetwork(CyNetwork network) {
		this.physicalNetwork = network;
	}

	public CyNetwork getPhysicalNetwork() {
		return physicalNetwork;
	}

	public void setGeneticNetwork(CyNetwork network) {
		this.geneticNetwork = network;
	}

	public CyNetwork getGeneticNetwork() {
		return geneticNetwork;
	}

	public void setPhysicalEdgeAttrName(String physicalEdgeAttrName) {
		this.physicalEdgeAttrName = physicalEdgeAttrName;
	}

	public String getPhysicalEdgeAttrName() {
		return physicalEdgeAttrName;
	}

	public void setNodeAttrName(String geneticNodeAttrName) {
		this.nodeAttrName = geneticNodeAttrName;
	}

	public String getNodeAttrName() {
		return nodeAttrName;
	}
		
	public void setGeneticEdgeAttrName(String geneticEdgeAttrName) {
		this.geneticEdgeAttrName = geneticEdgeAttrName;
	}

	public String getGeneticEdgeAttrName() {
		return geneticEdgeAttrName;
	}

	public void setPhysicalScalingMethod(final String physicalScalingMethod) {
		this.physicalScalingMethod = physicalScalingMethod;
	}

	public ScalingMethodX getPhysicalScalingMethod() {
		return ScalingMethodX.getEnumValue(physicalScalingMethod);
	}

	public void setGeneticScalingMethod(final String geneticScalingMethod) {
		this.geneticScalingMethod = geneticScalingMethod;
	}

	public ScalingMethodX getGeneticScalingMethod() {
		return ScalingMethodX.getEnumValue(geneticScalingMethod);
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlphaMultiplier(final double alphaMultiplier) {
		this.alphaMultiplier = alphaMultiplier;
	}

	public double getAlphaMultiplier() {
		return alphaMultiplier;
	}

	public void setPhysicalNetworkFilterDegree(final int physicalNetworkFilterDegree) {
		this.physicalNetworkFilterDegree = physicalNetworkFilterDegree;
	}

	public int getPhysicalNetworkFilterDegree() {
		return physicalNetworkFilterDegree;
	}

	public void setPValueThreshold(final double pValueThreshold) {
		this.pValueThreshold = pValueThreshold;
	}

	public double getPValueThreshold() {
		return pValueThreshold;
	}

	public void setNumberOfSamples(final int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}
	
	public void setComplexTrainingPhysical(boolean value)
	{
		this.complexTrainingPhysical = value;
	}
	
	public boolean getComplexTrainingPhysical()
	{
		return complexTrainingPhysical;
	}
	
	public void setComplexTrainingGenetic(boolean value)
	{
		this.complexTrainingGenetic = value;
	}
	
	public boolean getComplexTrainingGenetic()
	{
		return complexTrainingGenetic;
	}
	
	public void setComplexAnnotation(boolean value)
	{
		this.complexAnnotation = value;
	}
	
	public boolean getComplexAnnotation()
	{
		return complexAnnotation;
	}
	
	public void setAnnotationAttrName(String value)
	{
		this.annotationAttrName = value;
	}
	
	public String getAnnotationAttrName()
	{
		return annotationAttrName;
	}
	
	public void setAnnotationThreshold(double value)
	{
		this.annotationThreshold = value;
	}
	
	public double getAnnotationThreshold()
	{
		return annotationThreshold;
	}
	
	public void setReportPath(String p)
	{
		this.reportPath = p;
	}
	
	public String getReportPath()
	{
		return this.reportPath;
	}
}
