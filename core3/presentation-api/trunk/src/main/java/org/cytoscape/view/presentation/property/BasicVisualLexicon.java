package org.cytoscape.view.presentation.property;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

/**
 * Basic implementation of a lexicon.
 * This is simply a set of Visual Properties.
 * 
 * @author kono
 *
 */
public class BasicVisualLexicon implements VisualLexicon {

	protected static final String[] OBJ_TYPE = { NODE, EDGE, NETWORK };

	protected Map<String, Set<VisualProperty<?>>> propMap;

	protected final Set<VisualProperty<?>> visualPropertySet;

	public BasicVisualLexicon() {
		visualPropertySet = new HashSet<VisualProperty<?>>();
	}

	public Set<VisualProperty<?>> getAllVisualProperties() {
		return Collections.unmodifiableSet(visualPropertySet);
	}

	public Collection<VisualProperty<?>> getVisualProperties(String objectType) {
		if (propMap == null) {
			propMap = new HashMap<String, Set<VisualProperty<?>>>();

			for (String type : OBJ_TYPE)
				propMap.put(type, new HashSet<VisualProperty<?>>());

			for (VisualProperty<?> vp : visualPropertySet)
				propMap.get(vp.getObjectType()).add(vp);
		}

		return propMap.get(objectType);
	}
}
