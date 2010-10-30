package org.idekerlab.PanGIAPlugin;

import cytoscape.CyNetwork;

public class PanGIAOutput
{
	private boolean available = false;
	private CyNetwork origPhysNetwork = null;
	private CyNetwork origGenNetwork = null;
	private String physAttrName = null;
	private String genAttrName = null;
	
	public void initialize(CyNetwork origPhysNetwork, CyNetwork origGenNetwork, String physAttrName, String genAttrName)
	{
		this.available = true;
		this.origPhysNetwork = origPhysNetwork;
		this.origGenNetwork = origGenNetwork;
		this.physAttrName = physAttrName;
		this.genAttrName = genAttrName;
	}
	
	public void reset()
	{
		available = false;
		origPhysNetwork = null;
		origGenNetwork = null;
	}
	
	public boolean isAvailable()
	{
		return available;
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
}
