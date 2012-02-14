package org.cytoscape.io.write;

import java.util.Set;

import org.cytoscape.view.vizmap.VisualStyle;

public class VizmapWriterContextImpl extends CyWriterContextImpl implements
		VizmapWriterContext {

	private Set<VisualStyle> styles;

	@Override
	public void setVisualStyles(Set<VisualStyle> styles) {
		this.styles = styles;
	}

	@Override
	public Set<VisualStyle> getVisualStyles() {
		return styles;
	}

}
