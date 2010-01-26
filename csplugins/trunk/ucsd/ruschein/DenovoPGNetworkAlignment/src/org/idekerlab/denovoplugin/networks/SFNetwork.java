package networks;

import java.util.*;

import utilities.IIterator;
import networks.linkedNetworks.*;
import networks.hashNetworks.*;

public abstract class SFNetwork extends SNetwork
{
	public SFNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
	}
	
	public SFNetwork(SNetwork net)
	{
		super(net);
	}
	
	public abstract float edgeValue(String n1, String n2);
		
	public abstract int numEdges();
	public abstract int numNodes();
	
	public abstract IIterator<? extends SFEdge> edgeIterator();
	public abstract IIterator<String> nodeIterator();

	public abstract Set<String> getNodes();
	public abstract boolean contains(String n1, String n2);
	public abstract SFNetwork subNetwork(Set<String> nodes);
	
	public abstract SFNetwork shuffleNodes();
	public abstract TypedLinkNetwork<String,Float> asTypedLinkNetwork();
}
