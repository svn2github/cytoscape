package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.util.Collection;
import java.util.Map;

import cytoscape.visual.ObjectPosition;

public interface CyCustomGraphics <T> {
	/**
	 * Display name is a simple description of this image object.
	 * 
	 * May not be unique.
	 * 
	 * @return display name as String.
	 */
	public String getDisplayName();
	public void setDisplayName(final String displayName);
	
	public Collection<T> getCustomGraphics();
	
	public Image getImage();
	public Image resizeImage(int width, int height);
	
	/**
	 * Map of properties, i.e., details of this object.
	 * Key is the property name, and Value is prop object.
	 * 
	 * @return
	 */
	public Map<String, CustomGraphicsProperty<?>> getProps();
	
	public void update();
	
	/**
	 * Set posiiton of this graphics.
	 * 
	 * @param position
	 */
	public void setPosition(final ObjectPosition position);
	
	
	/**
	 * Current position
	 * 
	 * @return position of graphics as ObjectPosition.
	 */
	public ObjectPosition getPosition();
}
