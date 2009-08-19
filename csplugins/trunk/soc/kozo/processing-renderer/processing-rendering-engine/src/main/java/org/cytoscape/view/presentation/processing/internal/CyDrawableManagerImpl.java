package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.CyDrawableFactory;
import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.processing.internal.drawable.CubeFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.LineFactory;

import processing.core.PApplet;

/**
 * This will be an OSGi service.
 * 
 * @author kono
 * 
 */
public class CyDrawableManagerImpl implements CyDrawableManager {

	private final Map<Class<? extends CyDrawable>, CyDrawableFactory<?>> factoryMap;

	private final Map<String, CyDrawableFactory<?>> defaultFactory;

	public CyDrawableManagerImpl() {
		this.factoryMap = new HashMap<Class<? extends CyDrawable>, CyDrawableFactory<?>>();
		this.defaultFactory = new HashMap<String, CyDrawableFactory<?>>();

		final CubeFactory nodeDefFactory = new CubeFactory();
		final LineFactory edgeDefFactory = new LineFactory();

		this.factoryMap.put(nodeDefFactory.getDrawableClass(), nodeDefFactory);
		this.factoryMap.put(edgeDefFactory.getDrawableClass(), edgeDefFactory);

		this.defaultFactory.put(NODE, nodeDefFactory);
		this.defaultFactory.put(EDGE, edgeDefFactory);

	}

	@SuppressWarnings("unchecked")
	public <T extends CyDrawable> T getDrawable(Class<T> drawable) {
		final CyDrawableFactory<?> factory = factoryMap.get(drawable);
		if (factory != null)
			return (T) factory.getInstance();
		else
			return null;
	}

	public void registerDrawableFactory(CyDrawableFactory<? extends CyDrawable> factory) {

		if (factory == null)
			throw new IllegalArgumentException(
					"Factory cannot be null.");

		this.factoryMap.put(factory.getDrawableClass(), factory);
	}

	public CyDrawableFactory<?> getDefaultFactory(String objectType) {
		return this.defaultFactory.get(objectType);
	}

	public void setFactoryParent(PApplet parent) {
		for(CyDrawableFactory<?> factory: factoryMap.values())
			factory.setPaernt(parent);
	}


	public List<P5Shape> getP5Shapes() {
		
		final List<P5Shape> list = new ArrayList<P5Shape>();
		for(Class<? extends CyDrawable> type :factoryMap.keySet()) {
			list.add(new P5Shape(type.toString(), type));
		}
		return list;
	}
}
