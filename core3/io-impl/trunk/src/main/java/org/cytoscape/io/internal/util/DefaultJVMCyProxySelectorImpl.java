package org.cytoscape.io.internal.util;

import org.cytoscape.io.util.CyProxySelector;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;

public class DefaultJVMCyProxySelectorImpl implements CyProxySelector
{
	public Proxy select(URI uri)
	{
		return ProxySelector.getDefault().select(uri).get(0);
	}
}
