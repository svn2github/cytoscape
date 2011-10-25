package org.idekerlab.PanGIAPlugin.networks;

import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.IIterator;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.*;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;

public abstract class SBNetwork extends SNetwork
{
	public abstract void add(SEdge e);
	
	public SBNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
	}
	
	public SBNetwork(SNetwork net)
	{
		super(net);
	}
	
	public void addAll(SNetwork net)
	{
		for (SEdge e : net.edgeIterator())
			this.add(e);
	}
}
