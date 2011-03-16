package org.cytoscape.provenance.internal;


import java.util.Properties;

import org.cytoscape.work.TunableHandler;


public interface ProvenanceHandler extends TunableHandler {
	public void record();
}
