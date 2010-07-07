package cytoscape.visual.customgraphic;

import giny.view.ObjectPosition;

import java.awt.Image;
import java.util.Collection;

public interface CyCustomGraphics <T> {
	
	/**
	 * Immutable session-unique identifier of image generated in constructor.
	 * 
	 * NOT globally unique.
	 * 
	 * @return Immutable ID as Long.
	 */
	public Long getIdentifier();
	
	/**
	 * Display name is a simple description of this image object.
	 * 
	 * May not be unique and mutable.
	 * 
	 * @return display name as String.
	 */
	public String getDisplayName();
	public void setDisplayName(final String displayName);
	
	
	/**
	 * Get layers belongs to this object.
	 * In current Implementation, ti's always Ding's CustomGraphic object.
	 * 
	 * @return Collection of layer objects (in this version, it's CustomGraphics in Ding)
	 * 
	 */
	public Collection<T> getLayers();
	
	/**
	 * Return size of current object.
	 * 
	 * @return
	 */
	public int getWidth();
	public int getHeight();
	
	public void setWidth(final int width);
	public void setHeight(final int height);
	
	public float getFitRatio();
	public void setFitRatio(float ratio);
	
	/**
	 * From layers of graphics objects, render scaled Image object. 
	 * 
	 * @return rendered image object.
	 */
	public Image getRenderedImage();

	
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
