package org.genmapp.cellularlayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import cytoscape.Cytoscape;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * This class maintains a hashmap of regions by attribute value, plus methods
 * that operate over all regions.
 * 
 */
public class RegionManager {

	private static HashMap<String, Region> regionAttMap = new HashMap<String, Region>();
	private static TreeMap<Integer, Region> sortedRegionMap = new TreeMap<Integer, Region>();

	public static void addRegion(String attValue, Region region) {
		regionAttMap.put(attValue, region);
		checkForOverlap(region);
	}

	public static Region getRegionByAtt(String attValue) {
		return regionAttMap.get(attValue);
	}

	public static Collection<Region> getAllRegions() {
		return regionAttMap.values();
	}

	public static void clearRegionAttMap() {
		regionAttMap.clear();
		sortedRegionMap.clear();
		DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
		DingCanvas bCanvas = dview
				.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		bCanvas.removeAll();

	}

	/**
	 * Checks each new region against existing regions for overlap. If
	 * overlapping, region is added to a sorted tree map.
	 * 
	 * Also hacks area values when found to be equivalent to an existing region.
	 * This helps make it sortable.
	 * 
	 * @param r
	 */
	private static void checkForOverlap(Region newRegion) {
		double newLeft = newRegion.getRegionLeft();
		double newRight = newRegion.getRegionRight();
		double newTop = newRegion.getRegionTop();
		double newBottom = newRegion.getRegionBottom();

		for (Region r : regionAttMap.values()) {
			if (newRegion.getArea() == r.getArea()) {
				newRegion.setArea(newRegion.getArea() + 1); // hack!
			}
			if ((newLeft > r.getRegionLeft() && newLeft < r.getRegionRight())
					|| (newRight > r.getRegionLeft() && newRight < r
							.getRegionRight()) || newLeft < r.getRegionLeft()
					&& newRight > r.getRegionRight()) {
				if ((newTop > r.getRegionTop() && newTop < r.getRegionBottom())
						|| (newBottom > r.getRegionTop() && newBottom < r
								.getRegionBottom())
						|| newTop < r.getRegionTop()
						&& newBottom > r.getRegionBottom()) {
					// If we got this far, it means one region is overlapping
					// the other. Now we want to flag the smaller one so we
					// know when and where to apply "oil & water" exclusion.
					if (r.getArea() > newRegion.getArea()){
						newRegion.setRegionsOverlapped(r);
						r.setOverlappingRegions(newRegion);
					} else {
						r.setRegionsOverlapped(newRegion);
						newRegion.setOverlappingRegions(r);
					}
				}
			}
		}
		sortedRegionMap.put(newRegion.getArea(), newRegion);
	}

	/**
	 * @return sra the sortedRegionArray
	 */
	public static Region[] getSortedRegionArray() {
		Region[] sra = new Region[sortedRegionMap.size()];
		int i = 0;
		for (Region r : sortedRegionMap.values()) {
			sra[i] = r;
			i++;
		}
		return sra;
	}
}