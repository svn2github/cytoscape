package org.idekerlab.PanGIAPlugin;

import cytoscape.CyNetwork;

public class PanGIAOutput
{
	private final CyNetwork origPhysNetwork ;
	private final CyNetwork origGenNetwork;
	private final String physAttrName ;
	private final String genAttrName;
	private final CyNetwork overviewNetwork;
	
	public PanGIAOutput(CyNetwork overviewNetwork, CyNetwork origPhysNetwork, CyNetwork origGenNetwork, String physAttrName, String genAttrName)
	{
		this.overviewNetwork = overviewNetwork;
		this.origPhysNetwork = origPhysNetwork;
		this.origGenNetwork = origGenNetwork;
		this.physAttrName = physAttrName;
		this.genAttrName = genAttrName;
	}
	
	public CyNetwork getOrigPhysNetwork()
	{
		return origPhysNetwork;
	}
	
	public CyNetwork getOrigGenNetwork()
	{
		return origGenNetwork;
	}
	
	public String getPhysAttrName()
	{
		return physAttrName;
	}
	
	public String getGenAttrName()
	{
		return genAttrName;
	}
	
	public CyNetwork getOverviewNetwork()
	{
		return overviewNetwork;
	}
}
