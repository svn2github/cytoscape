package org.cytoscape.task.internal.quickstart;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.task.internal.quickstart.remote.InteractionFilePreprocessor;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class ImportNetworkFromPublicDataSetTask extends AbstractTask {
	
	@Tunable(description = "Select Data Source")
	public ListSingleSelection<String> dataSource;
	
	private final Map<String, URL> sourceMap;
	
	ImportNetworkFromPublicDataSetTask(ImportTaskUtil util) {
		super();	
		
		sourceMap = new HashMap<String, URL>();
		
		final Set<InteractionFilePreprocessor> processors = util.getProcessors();
		if(processors.size() == 0)
			throw new NullPointerException("Could not found data preprocessor.");
		
		final List<String> sourceList = new ArrayList<String>();
		for(InteractionFilePreprocessor processor: processors) {
			
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
		
		System.out.println("Need to load SIF (not implemented yet): " + this.sourceMap.get(selected));

	}

}
