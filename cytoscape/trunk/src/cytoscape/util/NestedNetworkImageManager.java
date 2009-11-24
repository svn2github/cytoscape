package cytoscape.util;

import giny.model.GraphPerspective;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.render.stateful.NodeDetails;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.DNodeView;

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
		if (networkToImageMap.get(network) == null) {
			return null;
		} else {
			return networkToImageMap.get(network).getImage();
		}
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
			updateImage(network, Cytoscape.getNetworkView(network.getIdentifier()));
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_DESTROYED)) {
			final CyNetwork network = (CyNetwork) evt.getNewValue();
			final ImageAndReferenceCount imageAndRefCount = networkToImageMap.get(network);
			imageAndRefCount.decRefCount();
			if (imageAndRefCount.getRefCount() == 0) {
				this.networkToImageMap.remove(network);
			}
		} else if(CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(evt.getPropertyName())) {
			//TODO: Need to sync. image update timing.
			final CyNetworkView view = (CyNetworkView)evt.getNewValue();
			final CyNetwork network = view.getNetwork();
			for(Object node: network.nodesList()) {
				CyNode cyNode = (CyNode) node;
				GraphPerspective nestedNetwork = cyNode.getNestedNetwork();
				if (nestedNetwork != null) {
					updateImage((CyNetwork) nestedNetwork, Cytoscape.getNetworkView(((CyNetwork)nestedNetwork).getIdentifier()));
					
					addCustomGraphics(network, Cytoscape.getNetworkView(network.getIdentifier()), cyNode);
				}
			}
		}
	}
	
	private void updateImage(CyNetwork network, CyNetworkView view) {
		if (view == Cytoscape.getNullNetworkView()) {
			// View does not exist
			System.out.println("*************View does not exist for: " + network.getTitle() +" == " + view.getNetwork().getTitle());
			networkToImageMap.put(network, new ImageAndReferenceCount(DEF_IMAGE));
		} else {
			// Create image from this view.
			System.out.println("*************View FOUND for: " + network.getTitle() +" == " + view.getNetwork().getTitle());
			final DGraphView dView = (DGraphView) view;
			networkToImageMap.put(network, new ImageAndReferenceCount(dView.createImage(DEF_WIDTH, DEF_HEIGHT, 1.0)));
		}
	}
	
	private void addCustomGraphics(final CyNetwork network, final CyNetworkView dView, final CyNode parentNode) {
		System.out.println("*** Adding custom graphics: Count = " + dView.getNodeViewCount() + " node = " + parentNode.getIdentifier());
		Image networkImage = getImage(network);
		BufferedImage img;
		if (networkImage == null) {
			img = toBufferedImage(DEF_IMAGE);
		} else {
			img = toBufferedImage(networkImage);
		}
		
		DNodeView nodeView =(DNodeView) dView.getNodeView(parentNode);
		final Rectangle2D rect = new Rectangle2D.Double(-50.0, -50.0, 50.0, 50.0);
		
		System.out.println("*** ADD CUSTOM: " + nodeView);
		if(nodeView !=null)
		nodeView.addCustomGraphic(rect, new TexturePaint(img, rect), NodeDetails.ANCHOR_CENTER);
	}
	
	 public BufferedImage toBufferedImage(Image image) {
	        if (image instanceof BufferedImage) {
	            return (BufferedImage)image;
	        }
	    
	        // This code ensures that all the pixels in the image are loaded
	        image = new ImageIcon(image).getImage();
	    
	        // Determine if the image has transparent pixels; for this method's
	        // implementation, see e661 Determining If an Image Has Transparent Pixels
	        boolean hasAlpha = hasAlpha(image);
	    
	        // Create a buffered image with a format that's compatible with the screen
	        BufferedImage bimage = null;
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        try {
	            // Determine the type of transparency of the new buffered image
	            int transparency = Transparency.OPAQUE;
	            if (hasAlpha) {
	                transparency = Transparency.BITMASK;
	            }
	    
	            // Create the buffered image
	            GraphicsDevice gs = ge.getDefaultScreenDevice();
	            GraphicsConfiguration gc = gs.getDefaultConfiguration();
	            bimage = gc.createCompatibleImage(
	                image.getWidth(null), image.getHeight(null), transparency);
	        } catch (HeadlessException e) {
	            // The system does not have a screen
	        }
	    
	        if (bimage == null) {
	            // Create a buffered image using the default color model
	            int type = BufferedImage.TYPE_INT_RGB;
	            if (hasAlpha) {
	                type = BufferedImage.TYPE_INT_ARGB;
	            }
	            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	        }
	    
	        // Copy image to buffered image
	        Graphics g = bimage.createGraphics();
	    
	        // Paint the image onto the buffered image
	        g.drawImage(image, 0, 0, null);
	        g.dispose();
	    
	        return bimage;
	    }
	 
	 public boolean hasAlpha(Image image) {
	        // If buffered image, the color model is readily available
	        if (image instanceof BufferedImage) {
	            BufferedImage bimage = (BufferedImage)image;
	            return bimage.getColorModel().hasAlpha();
	        }
	    
	        // Use a pixel grabber to retrieve the image's color model;
	        // grabbing a single pixel is usually sufficient
	         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	        try {
	            pg.grabPixels();
	        } catch (InterruptedException e) {
	        }
	    
	        // Get the image's color model
	        ColorModel cm = pg.getColorModel();
	        return cm.hasAlpha();
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
