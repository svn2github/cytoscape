package org.idekerlab.PanGIAPlugin;

import cytoscape.CyNetwork;

public class PanGIAOutput
{
	private final CyNetwork origPhysNetwork ;
	private final CyNetwork origGenNetwork;
	private final String nodeAttrName ;
	private final String physEdgeAttrName ;
	private final String genEdgeAttrName;
	private final CyNetwork overviewNetwork;
	private final boolean signed;
	
	public PanGIAOutput(CyNetwork overviewNetwork, CyNetwork origPhysNetwork, CyNetwork origGenNetwork, String nodeAttrName, String physEdgeAttrName, String genEdgeAttrName, boolean signed)
	{
		this.overviewNetwork = overviewNetwork;
		this.origPhysNetwork = origPhysNetwork;
		this.origGenNetwork = origGenNetwork;
		this.nodeAttrName = nodeAttrName;
		this.physEdgeAttrName = physEdgeAttrName;
		this.genEdgeAttrName = genEdgeAttrName;
		this.signed = signed;
	}
	
	public CyNetwork getOrigPhysNetwork()
	{
		return origPhysNetwork;
	}
	
	public CyNetwork getOrigGenNetwork()
	{
		return origGenNetwork;
	}
	
	public String getNodeAttrName()
	{
		return nodeAttrName;
	}
	
	public String getPhysEdgeAttrName()
	{
		return physEdgeAttrName;
	}
	
	public String getGenEdgeAttrName()
	{
		return genEdgeAttrName;
	}
	
	public CyNetwork getOverviewNetwork()
	{
		return overviewNetwork;
	}
	
	public boolean isSigned()
	{
		return signed;
	}
}
