package networks.denovoPGNetworkAlignment;

import networks.linkedNetworks.TypedLinkEdge;
import networks.linkedNetworks.TypedLinkNode;
import networks.linkedNetworks.TypedLinkNodeModule;

public class MergeLinkRunner implements Runnable
{
	private final TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> ed;
	
	public MergeLinkRunner(TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> ed)
	{
		this.ed = ed;
	}
	
	public void run()
	{
		HCSearch2.assignMergeLinkScore(ed);
	}
	
}
