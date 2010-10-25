package org.cytoscape.io.internal.read.bookmarks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.internal.CyFileFilterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookmarkFileFilter extends CyFileFilterImpl {
	
	public BookmarkFileFilter(Set<String> extensions, Set<String> contentTypes,
			String description, DataCategory category, StreamUtil streamUtil) {
		super(extensions, contentTypes, description, category, streamUtil);
	}

	public boolean accept(InputStream stream, DataCategory category) {

		// Check data category
		if (category != this.category)
			return false;
		
		final String header = this.getHeader(stream,10);
		if(header.contains("<bookmarks"))
			return true;
		
		return false;
	}

	@Override
	public boolean accepts(URI uri, DataCategory category) {
		try {
			return accept(uri.toURL().openStream(), category);
		} catch (IOException e) {
			Logger logger = LoggerFactory.getLogger(getClass());
			logger.error("Error while opening stream: " + uri, e);
			return false;
		}
	}
}
