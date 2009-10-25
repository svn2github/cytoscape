package org.cytoscape.task.internal.loadnetwork;

import java.io.InputStream;
import java.io.File;
import java.util.Set;
import java.net.URI;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.io.CyIOFactoryManager;
import org.cytoscape.io.read.CyNetworkViewReaderFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

public class LoadNetworkTask implements Task
{
	@Tunable(description = "Network file to load", flag={Param.network})
	public File file;

	CyNetworkFactory networkFactory;
	CyNetworkViewFactory networkViewFactory;
	CyLayouts layouts;
	CyIOFactoryManager<CyNetworkViewReaderFactory> manager;
	StreamUtil streamUtil;
	CyNetworkManager netManager;
	CyNetworkNaming networkNaming;

	public LoadNetworkTask(CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory, CyLayouts layouts, CyIOFactoryManager<CyNetworkViewReaderFactory> manager, StreamUtil streamUtil, CyNetworkManager netManager, CyNetworkNaming networkNaming)
	{
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.layouts = layouts;
		this.manager = manager;
		this.streamUtil = streamUtil;
		this.netManager = netManager;
		this.networkNaming = networkNaming;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		URI uri = file.toURI();
		CyNetworkViewReaderFactory networkViewReaderFactory = manager.getFactoryFromURI(uri);
		if (networkViewReaderFactory == null)
			throw new Exception(makeReaderNotFoundMessage());
		InputStream input = streamUtil.getInputStream(uri.toURL());
		CyNetwork network = networkFactory.getInstance();
		network.attrs().set("name", networkNaming.getSuggestedNetworkTitle(file.getName()));
		CyNetworkView view = networkViewFactory.getNetworkViewFor(network);
		Task reader = networkViewReaderFactory.getReader(input, view);
		reader.run(taskMonitor);
		layouts.getDefaultLayout().doLayout(view);
		view.fitContent();
		view.updateView();
		netManager.addNetwork(network);
		netManager.addNetworkView(view);
	}

	public void cancel()
	{
	}

	String makeReaderNotFoundMessage()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><p>Could not open specified file \'" + file.getName() + "\' because the file type is not supported.</p>");
		Set<CyNetworkViewReaderFactory> factories = manager.getAllFactories();
		if (factories.size() == 0)
		{
			builder.append("<p>No readers for networks could be found.</p>");
		}
		else
		{
			builder.append("<p>The following file types are supported:</p><p><ul>");
			for (CyNetworkViewReaderFactory factory : factories)
			{
				CyFileFilter fileFilter = factory.getCyFileFilter();
				builder.append("<li>");
				builder.append(fileFilter.getDescription());
				builder.append("</li>");
			}
			builder.append("</ul></p>");
		}
		builder.append("</html>");
		return builder.toString();
	}
}
