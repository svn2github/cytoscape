package org.cytoscape.io.internal.util;

import org.cytoscape.io.util.CyProxyRegistry;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.SocketAddress;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation for <code>CyProxySelector</code> and <code>CyProxyRegistry</code>.
 *
 * <p>
 * If a <code>URI</code>'s scheme is not specified by <code>register</code>,
 * this class will fall back on the JVM's default <code>ProxySelector</code>.
 * This is done because the JVM's <code>ProxySelector</code> can look up
 * the System's properties' proxy settings for us.
 * </p>
 *
 * <p>
 * An instance of this class will register itself as <code>ProxySelector</code>'s
 * default. That way, one can call <code>ProxySelector.getDefault().select()</code>
 * or <code>CyProxySelector.select()</code>, and they both produce the same
 * <code>Proxy</code>. This is also done to catch calls to
 * <code>connectFailed</code> and report it to the user.
 * </p>
 */
public class CyProxyRegistryImpl implements CyProxyRegistry
{
	final Map<String,Proxy> schemes;
	final ProxySelector JVMProxySelector;

	public CyProxyRegistryImpl()
	{
		schemes = new HashMap<String,Proxy>();
		JVMProxySelector = ProxySelector.getDefault();
		ProxySelector.setDefault(new InternalProxySelector());
	}

	public void register(String scheme, Proxy proxy)
	{
		if (scheme == null || scheme.length() == 0)
			throw new IllegalArgumentException("scheme cannot be null or an empty string");
		if (proxy == null)
			throw new IllegalArgumentException("proxy cannot be null");

		schemes.put(scheme, proxy);
	}

	public Proxy select(URI uri)
	{
		if (uri == null)
			throw new IllegalArgumentException("uri cannot be null");

		final String scheme = uri.getScheme();
		if (scheme == null || scheme.length() == 0)
			throw new IllegalArgumentException("uri's scheme cannot be null or an empty string");

		if (schemes.containsKey(scheme))
		{
			return schemes.get(scheme);
		}
		else
		{
			if (JVMProxySelector == null)
			{
				return Proxy.NO_PROXY;
			}
			else
			{
				return JVMProxySelector.select(uri).get(0);
			}
		}
	}

	class InternalProxySelector extends ProxySelector
	{
		public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
		{
			// TODO
		}

		public List<Proxy> select(URI uri)
		{
			List<Proxy> list = new ArrayList<Proxy>(1);
			list.add(CyProxyRegistryImpl.this.select(uri));
			return list;
		}
	}
}
