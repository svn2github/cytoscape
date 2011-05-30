package org.cytoscape.io.internal.read;

import java.io.InputStream;

import org.cytoscape.io.read.VizmapReader;
import org.cytoscape.view.vizmap.model.Vizmap;
import org.cytoscape.work.AbstractTask;


public abstract class AbstractVizmapReader extends AbstractTask implements VizmapReader {

    protected InputStream inputStream;
    protected Vizmap vizmap;
    
    public AbstractVizmapReader(InputStream inputStream) {
        if ( inputStream == null )
            throw new NullPointerException("InputStream is null");
        this.inputStream = inputStream;
    }
    
    @Override
    public Vizmap getVizmap() {
        return this.vizmap;
    }
}
