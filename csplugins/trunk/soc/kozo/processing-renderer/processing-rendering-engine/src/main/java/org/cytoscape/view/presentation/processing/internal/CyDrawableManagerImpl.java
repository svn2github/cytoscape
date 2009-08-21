package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.CyDrawableFactory;
import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.processing.internal.drawable.CubeFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.DataPlotRectangleFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.LineFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.RectangleFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.SphereFactory;
import org.cytoscape.view.presentation.processing.internal.drawable.TexturedRectangleFactory;


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
	
	private final List<P5Shape> shapes;

	public CyDrawableManagerImpl() {
		
		this.factoryMap = new HashMap<Class<? extends CyDrawable>, CyDrawableFactory<?>>();
		this.defaultFactory = new HashMap<String, CyDrawableFactory<?>>();
		this.shapes = new ArrayList<P5Shape>();

		final CubeFactory nodeDefFactory = new CubeFactory();
		final LineFactory edgeDefFactory = new LineFactory();

		this.factoryMap.put(nodeDefFactory.getDrawableClass(), nodeDefFactory);
		this.factoryMap.put(edgeDefFactory.getDrawableClass(), edgeDefFactory);

		this.defaultFactory.put(NODE, nodeDefFactory);
		this.defaultFactory.put(EDGE, edgeDefFactory);
		
		
		// Add some default drawables
		final RectangleFactory rectFactory = new RectangleFactory();
		this.factoryMap.put(rectFactory.getDrawableClass(), rectFactory);
		final TexturedRectangleFactory texRectFactory = new TexturedRectangleFactory();
		this.factoryMap.put(texRectFactory.getDrawableClass(), texRectFactory);
		final SphereFactory sphereFactory = new SphereFactory();
		this.factoryMap.put(sphereFactory.getDrawableClass(), sphereFactory);
		
		final DataPlotRectangleFactory dataPlotRectFactory = new DataPlotRectangleFactory();
		this.factoryMap.put(dataPlotRectFactory.getDrawableClass(), dataPlotRectFactory);
		
		
		this.shapes.add(new P5Shape(nodeDefFactory.getDrawableClass().getSimpleName(), nodeDefFactory.getDrawableClass()));
		this.shapes.add(new P5Shape(rectFactory.getDrawableClass().getSimpleName(), rectFactory.getDrawableClass()));
		this.shapes.add(new P5Shape(sphereFactory.getDrawableClass().getSimpleName(), sphereFactory.getDrawableClass()));
		this.shapes.add(new P5Shape(texRectFactory.getDrawableClass().getSimpleName(), texRectFactory.getDrawableClass()));
		this.shapes.add(new P5Shape(dataPlotRectFactory.getDrawableClass().getSimpleName(), dataPlotRectFactory.getDrawableClass()));
		
		this.shapes.add(new P5Shape(edgeDefFactory.getDrawableClass().getSimpleName(), edgeDefFactory.getDrawableClass()));
		
		
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
		this.shapes.add(new P5Shape(factory.getDrawableClass().getSimpleName(), factory.getDrawableClass()));
	}

	public CyDrawableFactory<?> getDefaultFactory(String objectType) {
		return this.defaultFactory.get(objectType);
	}

	public void setFactoryParent(PApplet parent) {
		for(CyDrawableFactory<?> factory: factoryMap.values())
			factory.setPaernt(parent);
	}


	public List<P5Shape> getP5Shapes() {
		return shapes;
	}
}
