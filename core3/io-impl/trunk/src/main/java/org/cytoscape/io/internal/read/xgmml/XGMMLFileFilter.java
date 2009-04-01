package org.cytoscape.io.internal.read.xgmml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.cytoscape.io.DataCategory;
import org.cytoscape.io.internal.CyFileFilterImpl;

public class XGMMLFileFilter extends CyFileFilterImpl {
	
	private static final String XGMML_NAMESPACE_STRING = "http://www.cs.rpi.edu/XGMML";
	

	public XGMMLFileFilter(Set<String> extensions, Set<String> contentTypes,
			String description, DataCategory category) {
		super(extensions, contentTypes, description, category);
	}

	public boolean accept(InputStream stream, DataCategory category)
			throws IOException {

		// Check data category
		if (category != this.category)
			return false;
		
		final String header = this.getHeader(stream);
		if(header.contains(XGMML_NAMESPACE_STRING))
			return true;
		
		return false;
	}

}
