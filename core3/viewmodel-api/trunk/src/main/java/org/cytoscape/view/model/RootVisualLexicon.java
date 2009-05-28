package org.cytoscape.view.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Collection of Lexicons. This is the superset of VisualLexicons. All Visual
 * Properties will be available from this root object.
 * 
 */
public interface RootVisualLexicon extends VisualLexicon {

	/**
	 * OSGi service listener method. Of course, this can be called from any POJO
	 * (without OSGi).
	 * 
	 * @param lexicon
	 * @param props
	 */

	@SuppressWarnings("unchecked")
	// This for Spring DM.
	public void addVisualLexicon(VisualLexicon lexicon, Map props);

	@SuppressWarnings("unchecked")
	public void removeVisualLexicon(VisualLexicon lexicon, Map props);

	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if arg is null.
	 * 
	 * Note: using VisualProperty.GraphObjectType.NETWORK for objectType is not
	 * really useful. For network VPs, use
	 * collectionOfVisualProperties(VisualProperty.GraphObjectType objectType)
	 * instead.
	 * 
	 * @param views
	 *            for which the filtering is to be done
	 * @param objectType
	 *            for which to filter
	 * @return VisualProperties, filtered with the DependentVisualProperty
	 *         callbacks
	 */
	Set<VisualProperty<?>> getVisualProperties(
			Collection<? extends View<?>> views, String objectType);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param networkview
	 *            DOCUMENT ME!
	 * @param objectType
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	Set<VisualProperty<?>> getVisualProperties(CyNetworkView networkview,
			String objectType);


	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualProperty<?> getVisualProperty(String idString);
}
