package cytoscape.util;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

public class NestedNetworkImageManager implements PropertyChangeListener {
	
	private static final Image DEF_IMAGE;
	
	private static final int DEF_WIDTH = 100;
	private static final int DEF_HEIGHT = 100;
	
	private static NestedNetworkImageManager networkImageGenerator;
	
	private final Map<CyNetwork, ImageAndReferenceCount> networkToImageMap;
	
	static {
		networkImageGenerator = new NestedNetworkImageManager();
		DEF_IMAGE = (new ImageIcon(Cytoscape.class.getResource("/cytoscape/images/default_network.png"))).getImage();
	}
	
	public static NestedNetworkImageManager getNetworkImageGenerator() {
		return networkImageGenerator;
	}

	private NestedNetworkImageManager() {
		networkToImageMap = new HashMap<CyNetwork, ImageAndReferenceCount>();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}
	
	public Image getImage(final CyNetwork network) {
		return networkToImageMap.get(network).getImage();
	}
	
	public int getImageCount() {
		return this.networkToImageMap.size();
	}

	public void propertyChange(final PropertyChangeEvent evt) {		
		if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_CREATED)) {
			final CyNetwork network = (CyNetwork) evt.getNewValue();
			if (this.networkToImageMap.containsKey(network)) {
				this.networkToImageMap.get(network).incRefCount();
				return;
			}
			
			final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			if (view == Cytoscape.getNullNetworkView()) {
				// View does not exist
				networkToImageMap.put(network, new ImageAndReferenceCount(DEF_IMAGE));
			} else {
				// Create image from this view.
				final DGraphView dView = (DGraphView) view;
				networkToImageMap.put(network, new ImageAndReferenceCount(dView.createImage(DEF_WIDTH, DEF_HEIGHT, 1.0)));
			}
			
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_DESTROYED)) {
			final CyNetwork network = (CyNetwork) evt.getNewValue();
			final ImageAndReferenceCount imageAndRefCount = networkToImageMap.get(network);
			imageAndRefCount.decRefCount();
			if (imageAndRefCount.getRefCount() == 0) {
				this.networkToImageMap.remove(network);
			}
		}
	}
	
	private void addCustomGraphics(final CyNetwork network, final CyNode parentNode) {
		
	}
	
	
	static class ImageAndReferenceCount {
		private Image image;
		private int refCount;
		
		
		public ImageAndReferenceCount(final Image image) {
			this.image = image;
			this.refCount = 1;
		}
		
		
		public void incRefCount() {
			this.refCount++;
		}
		
		
		public void decRefCount() {
			this.refCount--;
		}
		
		
		public int getRefCount() {
			return refCount;
		}
		
		
		public Image getImage() {
			return image;
		}
	}
	

}
