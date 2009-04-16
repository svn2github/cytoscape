package org.cytoscape.task.internal.proxysettings;

import org.cytoscape.work.ValuedTask;
import org.cytoscape.work.TaskMonitor;

import java.net.Proxy;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

class TestProxySettings implements ValuedTask<Exception>
{
	static String TEST_URL = "http://www.google.com";
	static int CONNECT_TIMEOUT_IN_MILLISECONDS = 5000;

	final Proxy.Type proxytype;
	final String hostname;
	final int port;

	boolean cancel = false;

	public TestProxySettings(Proxy.Type proxytype, String hostname, int port)
	{
		this.proxytype = proxytype;
		this.hostname = hostname;
		this.port = port;
	}

	public Exception run(TaskMonitor taskMonitor)
	{
		taskMonitor.setTitle("Testing Proxy Settings");
		try
		{
			taskMonitor.setStatusMessage("Resolving proxy server address...");

			Proxy proxy = null;
			if (proxytype == Proxy.Type.DIRECT)
			{
				proxy = Proxy.NO_PROXY;
			}
			else
			{
				SocketAddress address = new InetSocketAddress(hostname, port);
				proxy = new Proxy(proxytype, address);
			}

			if (cancel) return null;
			taskMonitor.setStatusMessage("Attempting to open the URL connection...");
			
			URL url = new URL(TEST_URL);
			URLConnection urlConnection = url.openConnection(proxy);
			urlConnection.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLISECONDS);
			urlConnection.setUseCaches(false);

			if (cancel) return null;
			taskMonitor.setStatusMessage("Attempting to connect to the URL...");

			urlConnection.connect();

			if (cancel) return null;
			taskMonitor.setStatusMessage("Attempting to read from the URL...");

			urlConnection.getInputStream().close();
		}
		catch (Exception ex)
		{
			return ex;
		}

		return null;
	}

	public void cancel()
	{
		cancel = true;
	}
}
