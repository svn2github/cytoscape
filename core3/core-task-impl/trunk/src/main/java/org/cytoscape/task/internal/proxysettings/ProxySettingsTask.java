package org.cytoscape.task.internal.proxysettings;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

import java.net.Proxy;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.ValuedTaskExecutor;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.io.util.CyProxyRegistry;

/**
 * Dialog for assigning proxy settings.
 * @author Pasteur
 */
class ProxySettingsTask implements Task, TunableValidator
{
	static final Map<String, Proxy.Type> types = new HashMap<String, Proxy.Type>(4, 1.0f);
	static
	{
		types.put("direct",	Proxy.Type.DIRECT);
		types.put("http",	Proxy.Type.HTTP);
		types.put("socks",	Proxy.Type.SOCKS);
	}

	@Tunable(description="Type")
	public ListSingleSelection<String> type = new ListSingleSelection<String>(new ArrayList<String>(types.keySet()));

	@Tunable(description="Proxy Server",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public String hostname="";

	@Tunable(description="Port",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public int port = 0;

	@Tunable(description="Check connectivity now")
	public boolean checkSettings = false;

	final TaskManager taskManager;
	final CyProxyRegistry proxyRegistry;
	Proxy proxy = Proxy.NO_PROXY;

	public ProxySettingsTask(final TaskManager taskManager, final CyProxyRegistry proxyRegistry)
	{
		this.taskManager = taskManager;
		this.proxyRegistry = proxyRegistry;
	}

	public String validate()
	{
		Proxy.Type proxytype = types.get(type.getSelectedValue());
		if (proxytype == Proxy.Type.DIRECT)
		{
			proxy = Proxy.NO_PROXY;
		}
		else
		{
			try
			{
				SocketAddress address = new InetSocketAddress(hostname, port);
				proxy = new Proxy(proxytype, address);
			}
			catch (Exception ex)
			{
				return String.format("The proxy settings specified are invalid: %s", ex.getMessage());
			}
		}

		Exception exception = null;
		try
		{
			ValuedTaskExecutor<Exception> executor = new ValuedTaskExecutor<Exception>(new TestProxySettings(proxy));
			taskManager.execute(executor);
			exception = executor.get();
		}
		catch (Exception ex)
		{
			return null;
		}

		if (exception == null)
			return null;
		else
			return String.format("Cytoscape was unable to connect to the Internet.\nPlease make sure the proxy settings are correct and try again.\n\n%s", exception.getMessage());
	}

	public void run(TaskMonitor taskMonitor)
	{
		taskMonitor.setTitle("Proxy Settings");
		taskMonitor.setStatusMessage("Registering proxy settings...");
		proxyRegistry.register("http", proxy);
	}

	public void cancel()
	{
	}
}
