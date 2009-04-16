package org.cytoscape.task.internal.proxysettings;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

import java.net.Proxy;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.ListSingleSelection;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.ValuedTaskExecutor;

class ProxySettings implements TunableValidator
{
	static final Map<String, Proxy.Type> types = new HashMap<String, Proxy.Type>(4, 1.0f);
	static
	{
		types.put("direct", Proxy.Type.DIRECT);
		types.put("http", Proxy.Type.HTTP);
		types.put("socks", Proxy.Type.SOCKS);
	}

	@Tunable(description="Type")
	public ListSingleSelection<String> type = new ListSingleSelection<String>(new ArrayList<String>(types.keySet()));

	@Tunable(description="Proxy Server",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public String hostname="";

	@Tunable(description="Port",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public int port = 0;

	@Tunable(description="Check proxy settings now")
	public boolean checkSettings = false;

	final TaskManager taskManager;

	public ProxySettings(final TaskManager taskManager)
	{
		this.taskManager = taskManager;
	}

	public String validate()
	{
		if (!checkSettings) return null;
		ValuedTaskExecutor<Exception> executor = new ValuedTaskExecutor<Exception>(new TestProxySettings(types.get(type), hostname, port));
		taskManager.execute(executor);
		Exception exception = null;
		try
		{
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
}
