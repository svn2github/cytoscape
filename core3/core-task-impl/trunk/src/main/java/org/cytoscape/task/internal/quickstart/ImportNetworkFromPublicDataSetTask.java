package org.cytoscape.task.internal.quickstart;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.internal.loadnetwork.AbstractLoadNetworkTask;
import org.cytoscape.task.internal.quickstart.remote.InteractionFilePreprocessor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class ImportNetworkFromPublicDataSetTask extends AbstractLoadNetworkTask {

	@Tunable(description = "Select Data Source")
	public ListSingleSelection<String> dataSource;

	private final Map<String, URL> sourceMap;

	final Set<InteractionFilePreprocessor> processors;
	private final StreamUtil streamUtil;

	ImportNetworkFromPublicDataSetTask(
			final Set<InteractionFilePreprocessor> processors,
			final CyNetworkViewReaderManager mgr,
			final CyNetworkManager networkManager,
			final CyNetworkViewManager networkViewManager,
			final Properties props, final CyNetworkNaming namingUtil,
			final StreamUtil streamUtil) {
		super(mgr, networkManager, networkViewManager, props, namingUtil);

		this.streamUtil = streamUtil;
		sourceMap = new HashMap<String, URL>();

		this.processors = processors;
		if (processors.size() == 0)
			throw new NullPointerException("Could not found data preprocessor.");

		final List<String> sourceList = new ArrayList<String>();
		for (InteractionFilePreprocessor processor : processors) {

			try {
				processor.processFile(null);
			} catch (IOException e) {
				throw new IllegalStateException("Could not init processor");
			}
			Map<String, URL> map = processor.getDataSourceMap();
			sourceList.addAll(map.keySet());
			sourceMap.putAll(map);
		}

		this.dataSource = new ListSingleSelection<String>(sourceList);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setStatusMessage("Update is done.");
		final String selected = this.dataSource.getSelectedValue();
		final URL url = this.sourceMap.get(selected);

		if (url == null)
			throw new NullPointerException("url is null");

		this.taskMonitor = taskMonitor;
		name = url.toString();

		taskMonitor
				.setTitle(String.format("Loading Network from \'%s\'", name));

		taskMonitor.setStatusMessage("Checking URL...");
		try {
			streamUtil.getURLConnection(url).connect();
		} catch (IOException e) {
			throw new Exception("Could not open local file.", e);
		}

		if (cancelled)
			return;

		taskMonitor.setStatusMessage("Finding network reader...");
		reader = mgr.getReader(url.toURI());

		if (cancelled)
			return;

		if (reader == null)
			throw new NullPointerException(
					"Failed to find reader for specified URL: " + name);
		
		insertTasksAfterCurrentTask(new SetNetworkNameTask(reader, selected));

		taskMonitor.setStatusMessage("Loading network...");
		loadNetwork(reader);
		
		
	}

}
