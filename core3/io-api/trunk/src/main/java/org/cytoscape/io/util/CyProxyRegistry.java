package org.cytoscape.io.util;

import java.net.Proxy;

/**
 * For registering <code>Proxy</code>s; this interface
 * should only be used by those who set proxy
 * settings.
 *
 * @author Pasteur
 */
public interface CyProxyRegistry extends CyProxySelector
{
	/**
	 * Registers a <code>Proxy</code>.
	 *
	 * @param scheme If <code>URI</code>'s <code>getScheme()</code>
	 * matches the given <code>scheme</code>, the
	 * <code>selector</code> method returns the provided <code>proxy</code>.
	 *
	 * @param proxy The <code>Proxy</code> to be specified for the given
	 * <code>scheme</code>. If a <code>Proxy</code> is already specified for
	 * the given <code>scheme</code>, this will override the <code>Proxy</code>
	 * with the given one.
	 */
	public void register(String scheme, Proxy proxy);
}
