package org.cytoscape.io.read;

import org.cytoscape.work.Task;

/**
 * 
 */
public interface CyPropertyReader extends Task {

	/**
	 * @return A property object of type T.  Type T can be
	 * be generally be any object, but in practice will be
	 * {@link java.util.Properties}, 
	 * {@link org.cytoscape.property.bookmark.Bookmarks}, and
	 * {@link org.cytoscape.property.session.Cysession}.
	 */
    Object getProperty();

}

