package cytoscape.util;

import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.render.stateful.NodeDetails;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.DNodeView;


/** This class manages images that represent nested networks.  This "management" includes creation, updating and destruction of such images as well
 *  as updating network views when any of their nodes nested networks have changed.
 */
public class NestedNetworkImageManager implements PropertyChangeListener {
	private final Image DEF_IMAGE;
	
	private static final int DEF_WIDTH = 500;
	private static final int DEF_HEIGHT = 500;
	
	private static NestedNetworkImageManager theNestedNetworkImageManager = null;
	private static Map<CyNetwork, ImageAndReferenceCount> networkToImageMap;

	
	public static void instantiateNestedNetworkImageManagerSingleton() {
		try {
			if (theNestedNetworkImageManager == null)
				theNestedNetworkImageManager = new NestedNetworkImageManager();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}


	private NestedNetworkImageManager() throws IOException {
		DEF_IMAGE = ImageIO.read(Cytoscape.class.getResource("/cytoscape/images/default_network.png"));
		NestedNetworkImageManager.networkToImageMap = new HashMap<CyNetwork, ImageAndReferenceCount>();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}


	private Image getImage(final CyNetwork network) {
		if (networkToImageMap.get(network) == null) {
			return null;
		} else {
			return networkToImageMap.get(network).getImage();
		}
	}
	
	
	/** Used for unit tests. */
	static int getImageCount() {
		return NestedNetworkImageManager.networkToImageMap.size();
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
		} else if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(evt.getPropertyName())) {
			// Here we have to do 2 things:
			// 1) if the newly created view is that of a network that is a nested network of any node, we need to recreate an image for
			//    this network based on the newly created view and then rerender all the views that contain parent nodes of this nested
			//    network
			// 2) We may have to rerender the newly created view if any of its nodes have a nested network and therefore need custom
			//    graphics
			
			final CyNetworkView view = (CyNetworkView)evt.getNewValue();
			final CyNetwork viewNetwork = view.getNetwork();
			if (networkToImageMap.containsKey(viewNetwork)) {
				// implement 1)
				updateImage(viewNetwork, view);
				refreshViews(viewNetwork);
			}

			// implement 2)
			refreshView(viewNetwork, view);
		}
	}
	

	/** Redraws the view "networkView" if any of the nodes in the network "viewNetwork" contain a nested network.
	 *
	 * @param viewNetwork  the network that will be scanned for nodes that contain a nested network
	 * @param networkView  the view that may be rerendered if any of its nodes contain a nested network
	 */
	private void refreshView(final CyNetwork viewNetwork, final CyNetworkView networkView) {
		boolean updateView = false;
		for (final CyNode node : (List<CyNode>)viewNetwork.nodesList()) {
			final CyNetwork nestedNetwork = (CyNetwork)node.getNestedNetwork();
			if (nestedNetwork != null) {
				updateView = true;
				addCustomGraphics(nestedNetwork, networkView, node);
			}
		}
		if (updateView) {
			networkView.updateView();
		}
	}
	
	
	/** Redraws all views that contain any node which is a parent to "nestedNetwork."
	 *
	 * @param nestedNetwork  the network for which we recently generated a new image
	 */
	private void refreshViews(final CyNetwork nestedNetwork) {
		final List<CyNode> nodes = Cytoscape.getRootGraph().nodesList();
		final Set<CyNetworkView> updateViews = new HashSet<CyNetworkView>();
		for (final CyNode node : nodes) {
			if (node.getNestedNetwork() == nestedNetwork) {
				for (final CyNetworkView view: Cytoscape.getNetworkViewMap().values()) {
					if (view.getNodeView(node) != null) {
						updateViews.add(view);						
					}
				}
			}
		}
		
		for (final CyNetworkView view : updateViews) {
			refreshView(view.getNetwork(), view);
		}
	}

	
	/** Generates an image for a nested network and stores it in "networkToImageMap."
	 *
	 * @param nestedNetwork  the network for which an image will be created
	 * @param view           either a view displaying the network "nestedNetwork", or the null network view as returned by
	 *                       Cytoscape.getNullNetworkView()
	 *
	 * Please note that when "view" is the null network view the image mapped to "nestedNetwork" will be a default image.
	 */
	private void updateImage(final CyNetwork nestedNetwork, final CyNetworkView view) {
		if (view == Cytoscape.getNullNetworkView()) {
			// View does not exist => use a default graphic
			networkToImageMap.put(nestedNetwork, new ImageAndReferenceCount(DEF_IMAGE));
		} else {
			// Create image from this view.
			view.applyLayout(CyLayouts.getLayout("force-directed"));
			final DGraphView dView = (DGraphView) view;
			dView.fitContent();
			dView.updateView();
			final Image image = dView.createImage(DEF_WIDTH, DEF_HEIGHT, 1.0);
			networkToImageMap.put(nestedNetwork, new ImageAndReferenceCount(image));
		}
	}

	
	/** Assigns a custom image representing a nested network to a node.
	 *
	 * @param nestedNetwork  the network whose corresponding image we will be assigning to the node "parentNode"
	 * @param networkView    the view that contains the node with the nested network
	 * @param parentNode     the node that contains the nested network "nestedNetwork" and displayed in the view "networkView"
	 */
	private void addCustomGraphics(final CyNetwork nestedNetwork, final CyNetworkView networkView, final CyNode parentNode) {
		Image networkImage = getImage(nestedNetwork);
		final DNodeView nodeView = (DNodeView)networkView.getNodeView(parentNode);
		final Rectangle2D rect = new Rectangle2D.Double(-DEF_WIDTH/2, -DEF_HEIGHT/2, DEF_WIDTH, DEF_HEIGHT);		
		nodeView.addCustomGraphic(rect, new TexturePaint((BufferedImage) networkImage, rect), NodeDetails.ANCHOR_CENTER);
	}
	

	/** Helper class that keeps track of how many references exist to a given image.  This allows removal of an image when the reference
	 *  count drops to zero.
	 */
	private static class ImageAndReferenceCount {
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
