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

	final Proxy proxy;

	boolean cancel = false;

	public TestProxySettings(Proxy proxy)
	{
		this.proxy = proxy;
	}

	public Exception run(TaskMonitor taskMonitor)
	{
		taskMonitor.setTitle("Testing Proxy Settings");
		try
		{
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
