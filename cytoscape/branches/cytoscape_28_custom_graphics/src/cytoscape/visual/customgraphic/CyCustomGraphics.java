package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.util.Collection;

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
	
}
