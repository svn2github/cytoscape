package org.cytoscape.psi_mi.internal.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class PsiMiTabReader extends AbstractTask implements CyNetworkViewReader {

	private InputStream inputStream;

	private final CyNetworkViewFactory cyNetworkViewFactory;

	private CyNetworkView[] cyNetworkViews;
	private VisualStyle[] visualstyles;
	
	private final CyLayouts layouts;

	private final PsiMiTabParser parser;

	public PsiMiTabReader(InputStream is,
			CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory, final CyLayouts layouts) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		this.inputStream = is;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.layouts = layouts;

		parser = new PsiMiTabParser(is, cyNetworkFactory);
	}

	@Override
	public CyNetworkView[] getNetworkViews() {
		return cyNetworkViews;
	}

	@Override
	public VisualStyle[] getVisualStyles() {
		return visualstyles;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		try {
			createNetwork(taskMonitor);
		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}

	private void createNetwork(TaskMonitor taskMonitor) throws IOException {
		
		taskMonitor.setProgress(0.0);
		
		final CyNetwork network = parser.parse();

		final CyNetworkView view = cyNetworkViewFactory.getNetworkView(network);

		TaskFactory tf = layouts.getDefaultLayout(view);
		TaskIterator ti = tf.getTaskIterator();
		Task task = ti.next();
		insertTasksAfterCurrentTask(task);

		// SIF always creates only one network.
		this.cyNetworkViews = new CyNetworkView[] { view };

		taskMonitor.setProgress(1.0);

	}
}
