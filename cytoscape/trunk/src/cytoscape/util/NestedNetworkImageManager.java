package cytoscape.util;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

public class NestedNetworkImageManager implements PropertyChangeListener {
	
	private static final Image DEF_IMAGE;
	
	private static final int DEF_WIDTH = 100;
	private static final int DEF_HEIGHT = 100;
	
	private static NestedNetworkImageManager networkImageGenerator;
	
	private final Map<CyNetwork, Image> networkToImageMap;
	
	static {
		networkImageGenerator = new NestedNetworkImageManager();
		DEF_IMAGE = (new ImageIcon(Cytoscape.class.getResource("/cytoscape/images/default_network.png"))).getImage();
	}
	
	public static NestedNetworkImageManager getNetworkImageGenerator() {
		return networkImageGenerator;
	}

	private NestedNetworkImageManager() {
		networkToImageMap = new HashMap<CyNetwork, Image>();
	}
	
	public Image getImage(final CyNetwork network) {
		return networkToImageMap.get(network);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_CREATED)) {
			final CyNetwork network = (CyNetwork) evt.getNewValue();
			final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			
			if (view == Cytoscape.getNullNetworkView()) {
				// View does not exist
				networkToImageMap.put(network, DEF_IMAGE);
			} else {
				// Create image from this view.
				final DGraphView dView = (DGraphView) view;
				networkToImageMap.put(network, dView.createImage(DEF_WIDTH, DEF_HEIGHT, 1.0));
			}
			
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_DESTROYED)) {
			// ?
		}
	}
	

}
