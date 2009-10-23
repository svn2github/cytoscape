package org.cytoscape.task.internal.loadnetwork;

import java.io.InputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.FileTypeChoice;
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

public class LoadSIFNetworkTask implements Task
{
	@Tunable(description = "Network file to load", fileTypeChoiceName="choices")
	public File file;
	public FileTypeChoice choices;

	CyNetworkFactory networkFactory;
	CyNetworkViewFactory networkViewFactory;
	CyLayouts layouts;
	CyNetworkViewReaderFactory networkViewReaderFactory;
	StreamUtil streamUtil;
	CyNetworkManager netManager;
	CyNetworkNaming networkNaming;

	public LoadSIFNetworkTask(CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory, CyLayouts layouts, CyNetworkViewReaderFactory networkViewReaderFactory, StreamUtil streamUtil, CyNetworkManager netManager, CyNetworkNaming networkNaming)
	{
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.layouts = layouts;
		this.networkViewReaderFactory = networkViewReaderFactory;
		this.streamUtil = streamUtil;
		this.netManager = netManager;
		this.networkNaming = networkNaming;
		List<CyFileFilter> list = new ArrayList<CyFileFilter>();
		list.add(networkViewReaderFactory.getCyFileFilter());
		this.choices = new FileTypeChoice(list);
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		System.out.println("Selected file type: " + choices.getSelectedFileFilter());
		InputStream input = streamUtil.getInputStream(file.toURI().toURL());
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
}
