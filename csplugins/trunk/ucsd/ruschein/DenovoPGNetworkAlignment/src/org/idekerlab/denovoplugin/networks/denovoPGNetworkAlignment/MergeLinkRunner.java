package org.idekerlab.denovoplugin.networks.denovoPGNetworkAlignment;

import org.idekerlab.denovoplugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.denovoplugin.networks.linkedNetworks.TypedLinkNode;
import org.idekerlab.denovoplugin.networks.linkedNetworks.TypedLinkNodeModule;

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
