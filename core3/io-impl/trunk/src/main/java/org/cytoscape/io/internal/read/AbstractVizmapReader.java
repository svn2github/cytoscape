package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.util.Set;

import org.cytoscape.io.internal.util.vizmap.VizmapAdapter;
import org.cytoscape.io.read.VizmapReader;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;


public abstract class AbstractVizmapReader extends AbstractTask implements VizmapReader {

    protected final InputStream inputStream;
    protected final VizmapAdapter vizmapAdapter;
    protected Set<VisualStyle> visualStyles;
    
    public AbstractVizmapReader(InputStream inputStream, VizmapAdapter vizmapAdapter) {
        if ( inputStream == null )
            throw new NullPointerException("InputStream is null");
        this.inputStream = inputStream;
        this.vizmapAdapter = vizmapAdapter;
    }

	@Override
	public Set<VisualStyle> getVisualStyles() {
		return visualStyles;
	}
}
