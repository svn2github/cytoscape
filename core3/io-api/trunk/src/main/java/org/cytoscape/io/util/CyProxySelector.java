package org.cytoscape.io.util;

import java.util.List;
import java.net.URI;
import java.net.Proxy;

/**
 * Selects an appropriate <code>Proxy</code> for a given <code>URI</code>; this
 * interface should be used by anyone who needs to open connections to the Internet
 * through <code>Socket</code>s or <code>URI</code>s,
 * <i>not</i> <code>CyProxyRegistry</code>.
 *
 * <p><b>Regarding <code>java.net.ProxySelector</code></b>. Since Java 5,
 * Java has the class <code>ProxySelector</code>, which returns a <code>List</code>
 * of appropriate <code>Proxy</code>s for a given <code>URI</code>. There are several
 * reasons why one should use this interface instead:</p>
 *
 * <p><ul>
 *
 * <li><code>ProxySelector</code> returns a <code>List</code> of <code>Proxy</code>s.
 * One must determine which <code>Proxy</code> in the <code>List</code>
 * is the best. However, <code>CyProxySelector</code> returns only one <code>Proxy</code>,
 * so one does not need to decide which one is the best <code>Proxy</code>.</li>
 *
 * <li><code>ProxySelector</code> allows public access to the method
 * <code>connectFailed</code>, while classes that implement <code>CyProxySelector</code>
 * do not.</li>
 *
 * <li>One can use Spring Dynamic Modules for a <code>CyProxySelector</code>. This
 * is more flexible than <code>ProxySelector</code>, which only allows one default
 * <code>ProxySelector</code> for the entire JVM.</li>
 *
 * </ul></p>
 *
 * <p>Classes that do implement this interface may choose to set themselves as the default
 * <code>ProxySelector</code>. Such a <code>ProxySelector</code> should have this behavior:</p>
 *
 * <p><ul>
 *
 * <li><code>select</code> should return a <code>List</code> containing the single
 * <code>Proxy</code> returned by <code>CyProxySelector</code>'s <code>select</code>.</li>
 *
 * <li><code>connectFailed</code> should inform the user that the proxy settings are invalid.</li>
 * </ul></p>
 *
 * @author Pasteur
 */
public interface CyProxySelector
{
	/**
	 * Selects an appropriate <code>Proxy</code> for a given <code>URI</code>.
	 * If an appropriate <code>Proxy</code> cannot be found, this method returns
	 * <code>Proxy.NO_PROXY</code> to represent direct connections.
	 */
	public Proxy select(URI uri);
}
