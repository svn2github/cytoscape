package cytoscape.bubbleRouter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.DGraphView.Canvas;

/**
 * This class maintains a hash map of all regions added to the view, in addition
 * to a updated unique name list and counter.
 */
public class LayoutRegionManager {

	public static HashMap<CyNetworkView, List<LayoutRegion>> regionViewMap = new HashMap<CyNetworkView, List<LayoutRegion>>();

//	public static List<String> regionNameList = new ArrayList<String>();

	public static int regionTotalCount = 0;

	/**
	 * canvas to add regions to -- currently BACKGROUND_CANVAS
	 */
	private static final Canvas REGION_CANVAS = DGraphView.Canvas.BACKGROUND_CANVAS;

	/**
	 * 
	 * @param view
	 * @return list of regions for this NetworkView
	 */
	public static List<LayoutRegion> getRegionListForView(CyNetworkView view) {
		return (List<LayoutRegion>) regionViewMap.get(view);
	}

	/**
	 * add a region/view association in the regionViewMap data structure
	 * 
	 * @param view
	 *            the CyNetworkView to add to
	 * @param region
	 *            the region you are adding
	 */
	public static void addRegionForView(CyNetworkView view, LayoutRegion region) {
		List<LayoutRegion> regionList = regionViewMap.get(view);
		if (regionList == null) {
			regionList = new ArrayList<LayoutRegion>();
		}
		regionList.add(region);
		regionViewMap.put(view, regionList);
	}

	/**
	 * remove the region and index from the view
	 * 
	 * @param view
	 * @param index
	 */
	public static void removeRegionFromView(CyNetworkView view, Integer index) {
		List<LayoutRegion> regionList = regionViewMap.get(view);
		if (regionList == null) {
			regionList = new ArrayList<LayoutRegion>();
		}
		regionList.remove(index);
		regionViewMap.put(view, regionList);
	}

	/**
	 * add a region name to the regionNameList
	 * 
	 * @param region
	 *            the region you are adding
	 */
//	public static void addRegionNameToList(LayoutRegion region) {
//		regionNameList.add(region.getRegionAttributeValue().toString());
//	}

	/**
	 * remove the region name from the list
	 * 
	 * @param index
	 */
//	public static void removeRegionNameFromList(LayoutRegion region) {
//		regionNameList.remove(region.getRegionAttributeValue().toString());
//	}

//	public static List getRegionNameList(CyNetworkView view) {
//		List
//		List<LayoutRegion> list = getRegionListForView(view);
//		Iterator<LayoutRegion> it = list.iterator();
//		while(it.hasNext()){
//			regionNameList.add(it.next().getRegionAttributeValue().toString());
//		}
//		return regionNameList;
//	}

	public static void removeAllRegions() {
		// Iterate through regionViewMap and remove all regions from all views
		HashMap<CyNetworkView, List<LayoutRegion>> regionViewMapTemp = regionViewMap;
		Iterator<CyNetworkView> key = regionViewMapTemp.keySet().iterator();
		while (key.hasNext()) {
			CyNetworkView cnv = key.next();
			List<LayoutRegion> lrList = regionViewMapTemp.get(cnv);
			while (lrList.size() > 0){
				BubbleRouterPlugin.groupWillBeRemoved(lrList.get(0));
//				removeRegionNameFromList(lrList.get(0));
				removeRegionFromView(cnv, lrList.get(0));
			}
		}
		regionTotalCount = 0;
	}

	/**
	 * 
	 * @param view
	 * @return number of regions for this CyNetworkView
	 */
	public static int getNumRegionsForView(CyNetworkView view) {
		List<LayoutRegion> regionList = regionViewMap.get(view);
		if (regionList == null) {
			return 0;
		} else {
			return regionList.size();
		}
	}

	/**
	 * remove all the regions associated with this CyNetworkView
	 * 
	 * @param view
	 */
	public static void removeAllRegionsForView(CyNetworkView view) {
		List<LayoutRegion> regionList = regionViewMap.get(view);
		if (regionList == null) {
			return;
		}
		while (regionList.size() > 0) {
			removeRegion(view, (LayoutRegion) regionList.get(0));
		}
		regionViewMap.put(view, null);
	}

	/**
	 * 
	 * @param view
	 * @param region
	 */
	public static void removeRegionFromView(CyNetworkView view,
			LayoutRegion region) {
		List<LayoutRegion> regionList = regionViewMap.get(view);
		if (regionList == null) {
			return;
		}

		// correct for region that has already been removed
		if (regionList.contains(region)) {
			regionList.remove(region);
			if (regionList.size() > 0) {
				regionViewMap.put(view, regionList);
			} else
			// remove entry from regionViewMap if number of regions goes to zero
			{
				regionViewMap.remove(view);
			}
		}
	}

	/**
	 * Keeps count of all created regions (even ones that have been
	 * deleted/removed) to allow for unique region counter
	 * 
	 * Useful in generating a unique name for each region Group
	 * 
	 * @return
	 */
	public static int getRegionCount() {
		return regionTotalCount;
	}

	/**
	 * higher-level routine for adding a region to a view
	 * 
	 * @param view
	 * @param region
	 */
	public static void addRegionFromFile(CyNetworkView view, LayoutRegion region) {
		addRegionForView(view, region);
//		addRegionNameToList(region);
		// Add to counter
		regionTotalCount++;

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.add(region);

		// oy what a hack: do an infinitesimal change of zoom factor so that it
		// forces a viewport changed event,
		// which enables us to get original viewport centerpoint and scale
		// factor
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);

		// Do not call BubbleRouterPlugin.newGroup(region)
	}

	/**
	 * higher-level routine for adding a region to a view
	 * 
	 * @param view
	 * @param region
	 */
	public static void addRegion(CyNetworkView view, LayoutRegion region) {
		addRegionForView(view, region);
//		addRegionNameToList(region);
		// Add to counter
		regionTotalCount++;

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.add(region);

		// oy what a hack: do an infinitesimal change of zoom factor so that it
		// forces a viewport changed event,
		// which enables us to get original viewport centerpoint and scale
		// factor
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);

		BubbleRouterPlugin.newGroup(region);
	}

	/**
	 * higher-level routine for removing a region from a view
	 * 
	 * @param view
	 * @param region
	 */
	public static void removeRegion(CyNetworkView view, LayoutRegion region) {
		removeRegionFromView(view, region);
//		removeRegionNameFromList(region);
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.remove(region);
		backgroundLayer.repaint();

		BubbleRouterPlugin.groupWillBeRemoved(region);
	}

	/**
	 * return the LayoutRegion that has been mouse-clicked upon, if any
	 * 
	 * @param pt
	 * @return
	 */
	public static LayoutRegion getPickedLayoutRegion(Point pt) {
		// first see if click was on a node or edge. If so, return
		if ((((DGraphView) Cytoscape.getCurrentNetworkView())
				.getPickedNodeView(pt) != null)
				|| (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedEdgeView(pt) != null)) {
			return null;
		}

		// next go down list of regions and see if there is a hit
		DingCanvas myCanvas = ((DGraphView) Cytoscape.getCurrentNetworkView())
				.getCanvas(REGION_CANVAS);

		for (int i = myCanvas.getComponentCount(); i > 0; i--) {
			LayoutRegion region = (LayoutRegion) myCanvas.getComponent(i - 1);
			if (region != null) {
				if (isPointOnRegion(pt, region)) {
					return region;
				}
			}
		}
		// if we get to this point then we haven't a region
		return null;
	}

	/**
	 * is the input point located within the bounds of the Layout region?
	 * 
	 * @param pt
	 *            the input point, in screen coordinates
	 * @param region
	 *            the region, whose bounds are also in screen coordinates
	 * @return
	 */
	private static boolean isPointOnRegion(Point pt, LayoutRegion region) {
		return region.getBounds().contains(pt);
	}

}
