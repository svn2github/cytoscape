package org.cytoscape.view.vizmap.gui.internal.task;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.gui.internal.util.VizMapperUtil;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateNewVisualStyleTaskFactory implements TaskFactory {
	
	private final VizMapperUtil vizMapperUtil;
	private final VisualStyleFactory vsFactory;
	private final VisualLexicon lexicon;
	
	public CreateNewVisualStyleTaskFactory(final VizMapperUtil vizMapperUtil,
	final VisualStyleFactory vsFactory, final VisualLexicon lexicon) {
		this.lexicon = lexicon;
		this.vizMapperUtil = vizMapperUtil;
		this.vsFactory = vsFactory;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new CreateNewVisualStyleTask(vizMapperUtil, vsFactory, lexicon));
	}

}
