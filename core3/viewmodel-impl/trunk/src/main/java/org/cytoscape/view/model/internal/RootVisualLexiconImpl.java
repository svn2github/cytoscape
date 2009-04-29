package org.cytoscape.view.model.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.DependentVisualPropertyCallback;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

public class RootVisualLexiconImpl implements RootVisualLexicon {

	private final Map<String, VisualLexicon> lexiconMap;
	private final Map<String, VisualProperty<?>> vpMap;

	/**
	 * Constructor. Just initializes collections for currently available
	 * renderers and VPs
	 */
	public RootVisualLexiconImpl() {
		lexiconMap = new HashMap<String, VisualLexicon>();
		vpMap = new HashMap<String, VisualProperty<?>>();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param serializableName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualProperty<?> getVisualProperty(final String id) {
		return vpMap.get(id);
	}

	/* return collection of only those that have a matching objectType */
	private Set<VisualProperty<?>> filterForObjectType(
			final Collection<VisualProperty<?>> vps, final String objectType) {

		final Set<VisualProperty<?>> result = new HashSet<VisualProperty<?>>();

		for (VisualProperty<?> vp : vps) {
			if (vp.getObjectType().equals(objectType))
				result.add(vp);
		}

		return result;
	}

	public VisualLexicon getVisualLexicon(String lexiconName) {
		return lexiconMap.get(lexiconName);
	}

	@SuppressWarnings("unchecked")
	public void addVisualLexicon(VisualLexicon lexicon, Map props) {
		final String presentationName = lexicon.toString();

		// If it already exists, replace it by the new one.
		if (lexiconMap.containsKey(presentationName))
			lexiconMap.remove(presentationName);

		// Add the new lexicon to the map.
		lexiconMap.put(presentationName, lexicon);

		// Update set of VisualProperties
		Set<VisualProperty<?>> vps = lexicon.getAllVisualProperties();
		for (VisualProperty<?> vp : vps)
			this.vpMap.put(vp.getIdString(), vp);
	}

	@SuppressWarnings("unchecked")
	public void removeVisualLexicon(VisualLexicon lexicon, Map props) {
		final String presentationName = lexicon.toString();
		

		this.lexiconMap.remove(presentationName);
	}

	public Collection<VisualLexicon> getAllVisualLexicons() {
		return this.lexiconMap.values();
	}

	public Set<VisualProperty<?>> getAllVisualProperties() {
		return new HashSet<VisualProperty<?>>(vpMap.values());
	}

	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if both args are
	 * null.
	 * 
	 * @param views
	 *            DOCUMENT ME!
	 * @param objectType
	 *            DOCUMENT ME!
	 * @return VisualProperties
	 */
	public Set<VisualProperty<?>> getVisualProperties(
			Collection<? extends View<?>> views, String objectType) {

		if (views == null)
			return filterForObjectType(vpMap.values(), objectType);

		// System.out.println("making list of VisualProperties in use:");
		final Set<VisualProperty<?>> toRemove = new HashSet<VisualProperty<?>>();

		/* apply DependentVisualPropertyCallbacks */
		for (VisualProperty<?> vp : vpMap.values()) {
			final DependentVisualPropertyCallback callback = vp
					.dependentVisualPropertyCallback();

			if (callback != null) {
				toRemove.addAll(callback.changed(views, vpMap.values()));
			}
		}

		// System.out.println("removing:"+toRemove.size());
		final Set<VisualProperty<?>> result = new HashSet<VisualProperty<?>>(
				vpMap.values());
		result.removeAll(toRemove);

		// System.out.println("len of result:"+result.size());
		return filterForObjectType(result, objectType);
	}

	public Set<VisualProperty<?>> getVisualProperties(
			CyNetworkView networkview, String objectType) {
		if (networkview != null) {
			// FIXME: could filter Views based on objectType, right here
			final Collection<View<?>> views = new HashSet<View<?>>(networkview
					.getNodeViews());
			views.addAll(networkview.getEdgeViews());

			return getVisualProperties(views, objectType);
		} else {
			return filterForObjectType(vpMap.values(), objectType);
		}
	}

	public Collection<VisualProperty<?>> getVisualProperties(String objectType) {
		return filterForObjectType(vpMap.values(), objectType);
	}

}
