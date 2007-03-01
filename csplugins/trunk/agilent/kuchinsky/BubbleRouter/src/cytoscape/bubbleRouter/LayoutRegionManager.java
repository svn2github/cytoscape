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

public class LayoutRegionManager {

	public static HashMap regionViewMap = new HashMap();

	public static HashMap attributeViewMap = new HashMap();

	public static List getRegionListForView(CyNetworkView view) {
		return (List) regionViewMap.get(view);
	}

	public static Object getAttributeForView(CyNetworkView view) {
		return attributeViewMap.get(view);
	}

	public static void addRegionForView(CyNetworkView view, LayoutRegion region) {
		List regionList = (List) regionViewMap.get(view);
		if (regionList == null) {
			regionList = new ArrayList();
		}
		regionList.add(region);
		// System.out.println("Index of region added: "
		// + regionList.indexOf(region));
		regionViewMap.put(view, regionList);
	}

	// APico 9.16.06
	// TODO: use this to delete regions
	public static void removeRegionFromView(CyNetworkView view, Integer index) {
		List regionList = (List) regionViewMap.get(view);
		if (regionList == null) {
			regionList = new ArrayList();
		}
		regionList.remove(index);
		regionViewMap.put(view, regionList);
	}
	
	// AJK: 02/20/07 BEGIN
	//     get number of regions for the view
	public static int getNumRegionsForView (CyNetworkView view)
	{
		List regionList = (List) regionViewMap.get(view);
		if (regionList == null) {
			return 0;
		}
		else 
		{
			return regionList.size();
		}
	}
	
	public static void removeAllRegionsForView (CyNetworkView view)
	{
		List regionList = (List) regionViewMap.get(view);
		if (regionList == null) {
			return;
		}
		while (regionList.size() > 0)
		{
			removeRegion (view, (LayoutRegion) regionList.get(0));
		}
		regionViewMap.put(view, null);
	}
	
	// AJK: 02/20/07 END

	// AJK: 11/15/06 BEGIN
	// for undo/redo
	public static void removeRegionFromView(CyNetworkView view,
			LayoutRegion region) {
		List regionList = (List) regionViewMap.get(view);
		if (regionList == null) {
			return;
		}

		// AJK: 12/01/06 BEGIN
		// correct for region that has already been removed
		if (regionList.contains(region)) {
			regionList.remove(region);
			if (regionList.size() > 0)
			{
				regionViewMap.put(view, regionList);
			}
			else // remove entry from regionViewMap if number regions goes to zero
			{
				regionViewMap.remove(view);
			}
		}
		// regionList.remove(regionList.indexOf(region));
		// AJK: 12/1/06 END

		

	}

	// AJK: 12/02/06 BEGIN
	// higher-level routines for adding/removing regions
	public static void addRegion(CyNetworkView view, LayoutRegion region) {
		addRegionForView(view, region);

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
		DingCanvas backgroundLayer = dview
				.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		backgroundLayer.add(region);
		
		// AJK: 01/07/2007 BEGIN
		//    oy what a hack: do an infinitesimal change of zoom factor so that it
		//    forces a viewport changed event, 
		//    which enables us to get original viewport centerpoint and scale factor
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);
//		System.out.println ("View bounds = " + dview.getComponent().getBounds());
//		System.out.println("View scale factor = " + dview.getZoom());
//		
//		System.out.println("Canvas bounds = " + dview.getCanvas().getBounds());
		// AJK: 01/07/2007 END
	}
	
	public static void removeRegion(CyNetworkView view, LayoutRegion region) {
		removeRegionFromView(view, region);

		// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
		// layout region
		DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
		DingCanvas backgroundLayer = dview
				.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		backgroundLayer.remove(region);
		backgroundLayer.repaint();
	}
	
	/**
	 * return the LayoutRegion that has been mouse-clicked upon, if any
	 * @param pt
	 * @return
	 */
	public static LayoutRegion getPickedLayoutRegion(Point pt)
	{
		// first see if click was on a node or edge.  If so, return
		if ((((DGraphView) 
				Cytoscape.getCurrentNetworkView()).getPickedNodeView(pt) != null) ||
				(((DGraphView) 
						Cytoscape.getCurrentNetworkView()).
						getPickedNodeView(pt) != null))
		{
			return null;
		}
		
		// next go down list of regions and see if there is a hit
		List regionList = getRegionListForView (Cytoscape.getCurrentNetworkView());
		if (regionList == null)
		{
			return null;
		}
		Iterator it = regionList.iterator();
		while (it.hasNext())
		{
			LayoutRegion region = (LayoutRegion) it.next();
			if (region != null)
			{
				if (isPointOnRegion (pt, region))
				{
					return region;
				}
			}
		}
		// if we get to this point then we haven't a region
		return null;
	}
	
	/**
	 * is the input point located within the bounds of the Layout region?
	 * @param pt the input point, in screen coordinates
	 * @param region the region, whose bounds are also in screen coordinates
	 * @return
	 */
	private static boolean isPointOnRegion (Point pt, LayoutRegion region)
	{
		return region.getBounds().contains(pt);
	}

	// AJK: 12/02/06 END

	public static void putAttributeForView(CyNetworkView view, Object attribute) {
		attributeViewMap.put(view, attribute);
	}

}
