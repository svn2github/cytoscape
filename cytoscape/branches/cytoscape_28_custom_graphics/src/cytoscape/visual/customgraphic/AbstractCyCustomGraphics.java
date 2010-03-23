package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import cytoscape.render.stateful.CustomGraphic;

public abstract class AbstractCyCustomGraphics implements CyCustomGraphics<CustomGraphic>, Taggable {

	protected Collection<CustomGraphic> cgList;
	protected String displayName;
	protected CyCustomGraphicsParser parser;
	
	// For tags
	protected final SortedSet<String> tags;
	
	public AbstractCyCustomGraphics(String displayName) {
		this.cgList = new ArrayList<CustomGraphic>();
		this.displayName = displayName;
		
		this.tags = new TreeSet<String>();
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

}
