package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.ObjectPosition;
import cytoscape.visual.ObjectPositionImpl;
import cytoscape.visual.customgraphic.experimental.CustomGraphicsProperty;

public abstract class AbstractCyCustomGraphics implements CyCustomGraphics<CustomGraphic>, Taggable {

	protected Collection<CustomGraphic> cgList;
	protected String displayName;
	protected CyCustomGraphicsParser parser;
	
	protected ObjectPosition position;
	
	protected final Map<String, CustomGraphicsProperty<?>> props;
	
	// For tags
	protected final SortedSet<String> tags;
	
	public AbstractCyCustomGraphics(String displayName) {
		this.cgList = new ArrayList<CustomGraphic>();
		this.displayName = displayName;
		
		this.tags = new TreeSet<String>();
		this.props = new HashMap<String, CustomGraphicsProperty<?>>();
		
		this.position = new ObjectPositionImpl();
	}
	
	@Override
	public Collection<CustomGraphic> getCustomGraphics() {
		return cgList;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public Image resizeImage(int width, int height) {
		return null;
	}

	@Override
	public Collection<String> getTags() {
		return tags;
	}
	
	@Override
	public Map<String, CustomGraphicsProperty<?>> getProps() {
		return this.props;
	}
	
	public void update() {
		// By default, do nothing.
	}

	@Override
	public ObjectPosition getPosition() {
		return position;
	}

	@Override
	public void setPosition(final ObjectPosition position) {
		this.position = position;
	}

}
