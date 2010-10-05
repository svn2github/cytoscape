package org.idekerlab.PanGIAPlugin;

import cytoscape.CyNetwork;

public class PanGIAOutput
{
	private boolean available = false;
	private CyNetwork origPhysNetwork = null;
	private CyNetwork origGenNetwork = null;
	
	public void initialize(CyNetwork origPhysNetwork, CyNetwork origGenNetwork)
	{
		this.available = true;
		this.origPhysNetwork = origPhysNetwork;
		this.origGenNetwork = origGenNetwork;
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
}
