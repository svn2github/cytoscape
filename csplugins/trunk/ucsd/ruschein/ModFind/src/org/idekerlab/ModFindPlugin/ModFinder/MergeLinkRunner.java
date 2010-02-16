package org.idekerlab.ModFindPlugin.ModFinder;

import org.idekerlab.ModFindPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.TypedLinkNodeModule;

public class MergeLinkRunner implements Runnable {
	private final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed;

	public MergeLinkRunner(
			TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> ed) {
		this.ed = ed;
	}

	public void run() {
		HCSearch2.assignMergeLinkScore(ed);
	}

}
