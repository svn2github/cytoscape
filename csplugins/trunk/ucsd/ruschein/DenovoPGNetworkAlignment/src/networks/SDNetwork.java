package networks;

import java.util.*;

import utilities.IIterator;
import networks.linkedNetworks.*;
import networks.hashNetworks.*;

public abstract class SDNetwork extends SNetwork
{
	public abstract double edgeValue(String n1, String n2);
	public abstract TypedLinkNetwork<String,Double> asTypedLinkNetwork();
	
	public abstract IIterator<? extends SDEdge> edgeIterator();
	
	public SDNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
	}
	
	public SDNetwork(SNetwork net)
	{
		super(net);
	}
}
