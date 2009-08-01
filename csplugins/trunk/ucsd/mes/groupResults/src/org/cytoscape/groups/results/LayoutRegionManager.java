package org.cytoscape.groups.results;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cytoscape.CyNode;
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
	
	public static HashMap<CyNetworkView, Integer> viewIdMap = new HashMap<CyNetworkView, Integer>();

	public static HashMap<CyNetworkView, List<LayoutRegion>> regionViewMap = new HashMap<CyNetworkView, List<LayoutRegion>>();

	/**
	 * canvas to add regions to -- currently BACKGROUND_CANVAS
	 */
	public static final Canvas REGION_CANVAS = DGraphView.Canvas.BACKGROUND_CANVAS;

	/**
	 * Returns a list of of regions per view.
	 * 
	 * @param view
	 * @return list of regions for this NetworkView
	 */
	public static List<LayoutRegion> getRegionListForView(CyNetworkView view) {
		return (List<LayoutRegion>) regionViewMap.get(view);
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
	 * higher-level routine for removing a region from a view
	 * 
	 * @param view
	 * @param region
	 */
	public static void removeRegion(CyNetworkView view, LayoutRegion region) {
		removeRegionFromView(view, region);
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.remove(region);
		backgroundLayer.repaint();

		//BubbleRouterPlugin.groupWillBeRemoved(region);
	}

	/**
	 * remove given region from view
	 * (use removeRegion outside of this class)
	 * 
	 * @param view
	 * @param region
	 */
	private static void removeRegionFromView(CyNetworkView view,
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
	 * higher-level routine for adding a region to a view from xGMML
	 * 
	 * Note: does not call BubbleRouterPlugin.newGroup(region);
	 * 
	 * @param view
	 * @param region
	 */
	public static void addRegionFromFile(CyNetworkView view, LayoutRegion region) {
		addRegionForView(view, region);

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.add(region, 0);
		
		// oy what a hack: do an infinitesimal change of zoom factor so that it
		// forces a viewport changed event,
		// which enables us to get original viewport centerpoint and scale
		// factor
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);

		// Do not call BubbleRouterPlugin.newGroup(region)
		
		// update viewID for the group node identifier
		Integer viewID = viewIdMap.get(view);
		CyNode groupNode = region.getMyGroup().getGroupNode();
		String name = groupNode.getIdentifier();
		String nameNew = name.substring(0, name.indexOf("]_"));
		nameNew += "]_" + viewID;
		region.getMyGroup().getGroupNode().setIdentifier(nameNew);
	}

	/**
	 * higher-level routine for adding a region to a view
	 * 
	 * @param view
	 * @param region
	 */
	public static void addRegion(CyNetworkView view, LayoutRegion region) {
		addRegionForView(view, region);

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) view;
		DingCanvas backgroundLayer = dview.getCanvas(REGION_CANVAS);
		backgroundLayer.add(region, 0);
		
		// oy what a hack: do an infinitesimal change of zoom factor so that it
		// forces a viewport changed event,
		// which enables us to get original viewport centerpoint and scale
		// factor
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);

		// generate unique group name by combining region name and view ID
		//BubbleRouterPlugin.newGroup(region, viewIdMap.get(view));
	}

	/**
	 * add a region/view association in the regionViewMap data structure
	 * (use addRegion outside of this class)
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

		for (int i = 0; i < myCanvas.getComponentCount(); i++) {
			LayoutRegion region = (LayoutRegion) myCanvas.getComponent(i);
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

	public static Integer getIdForView(CyNetworkView view) {
		return viewIdMap.get(view);
	}
	
	public static Integer getViewIdMapSize(){
		return viewIdMap.size();
	}

	public static void setViewIdMap(CyNetworkView view, Integer id) {
		LayoutRegionManager.viewIdMap.put(view, id);
	}
	
	public static void removeViewId(CyNetworkView view) {
		LayoutRegionManager.viewIdMap.remove(view);
	}

}
