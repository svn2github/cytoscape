package org.cytoscape.io.internal.read.xgmml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.internal.CyFileFilterImpl;

public class XGMMLFileFilter extends CyFileFilterImpl {
	
	private static final String XGMML_NAMESPACE_STRING = "http://www.cs.rpi.edu/XGMML";
	

	public XGMMLFileFilter(Set<String> extensions, Set<String> contentTypes,
			String description, StreamUtil streamUtil) {
		super(extensions, contentTypes, description, streamUtil);
	}

	public boolean accept(URI uri)
			throws IOException {
		
		InputStream stream = streamUtil.getInputStream(uri.toURL());
		final String header = this.getHeader(stream);
		if(header.contains(XGMML_NAMESPACE_STRING))
			return true;
		
		return false;
	}

}
