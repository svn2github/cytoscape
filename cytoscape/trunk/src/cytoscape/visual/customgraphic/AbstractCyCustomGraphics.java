package cytoscape.visual.customgraphic;

import giny.view.ObjectPosition;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.experimental.CustomGraphicsProperty;
import ding.view.ObjectPositionImpl;

public abstract class AbstractCyCustomGraphics implements
		CyCustomGraphics<CustomGraphic>, Taggable {

	protected static final String DELIMITER = ",";
	public static final String LIST_DELIMITER = "|";

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

	public Collection<CustomGraphic> getCustomGraphics() {
		return cgList;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public Image getImage() {
		return null;
	}

	public Image resizeImage(int width, int height) {
		return null;
	}

	public Collection<String> getTags() {
		return tags;
	}

	public Map<String, CustomGraphicsProperty<?>> getProps() {
		return this.props;
	}

	public void update() {
		// By default, do nothing.
	}

	public ObjectPosition getPosition() {
		return position;
	}

	public void setPosition(final ObjectPosition position) {
		this.position = position;
	}

	// This will be used prop file.
	public String toString() {
		String tagStr = "";
		// Build tags as a string
		if (tags.size() != 0) {
			final StringBuilder builder = new StringBuilder();
			for (String tag : tags)
				builder.append(tag + LIST_DELIMITER);
			String temp = builder.toString();
			tagStr = temp.substring(0, temp.length() - 1);
		}

		String name = displayName;
		if (displayName.contains(",")) {
			// Replace delimiter
			name = displayName.replace(",", "___");
		}

		return this.getClass().getName() + DELIMITER + this.hashCode()
				+ DELIMITER + name + DELIMITER + tagStr;
	}

}
