package networks;

import java.util.*;

import utilities.IIterator;
import networks.linkedNetworks.*;
import networks.hashNetworks.*;

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
