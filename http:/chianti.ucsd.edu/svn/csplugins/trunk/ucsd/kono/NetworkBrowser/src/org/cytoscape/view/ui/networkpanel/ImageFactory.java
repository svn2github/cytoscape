package org.cytoscape.view.ui.networkpanel;

import java.awt.Image;

public interface ImageFactory {
	
	public Image getImage(Object viewObject);
	
	// For adding/removing OSGi services.  Will be used in 3.0
	public void addImageRenderer();
	public void removeImageRenderer();

}
