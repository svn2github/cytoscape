package org.cytoscape.property;

/**
 * A general property service interface for providing access to different types
 * of property objects as OSGi services. The type P is generally one  of three types: 
 * {@link java.util.Properties}, 
 * {@link org.cytoscape.property.bookmark.Bookmarks}, or
 * {@link org.cytoscape.property.session.Cysession}, although it is possible for 
 * property objects of other types to be registered in this way as well.
 */
public interface CyProperty<P> {

	/**
	 * Return a property object
	 * @return A property object of type P.
	 */
	public P getProperties();
}
