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
	private static TreeMap<Integer, Region> sortedOverlappingRegionMap = new TreeMap<Integer, Region>();
	private static TreeMap<Integer, Region> sortedRegionMap = new TreeMap<Integer, Region>();

	public static void addRegion(String attValue, Region region) {
		regionAttMap.put(attValue, region);
		checkForOverlap(region);
		makeSortedList(region);
	}

	public static Region getRegionByAtt(String attValue) {
		return regionAttMap.get(attValue);
	}

	public static Collection<Region> getAllRegions() {
		return regionAttMap.values();
	}

	public static void clearRegionAttMap() {
		regionAttMap.clear();
		sortedOverlappingRegionMap.clear();
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
					sortedOverlappingRegionMap.put(r.getArea(), r);
					sortedOverlappingRegionMap
							.put(newRegion.getArea(), newRegion);
				}

			}
		}
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
	private static void makeSortedList(Region newRegion) {

		for (Region r : regionAttMap.values()) {
			if (newRegion.getArea() == r.getArea()) {
				newRegion.setArea(newRegion.getArea() + 1); // hack!
			}
		}
		
		sortedRegionMap.put(newRegion.getArea(), newRegion);
	}

	/**
	 * @return the sortedOverlappingRegions from smallest to largest
	 */
	public static Collection<Region> getSortedOverlappingRegions() {
		return sortedOverlappingRegionMap.values();
	}
	
	 /**
	 * @return sora the sortedOverlappingRegionArray
	 */
	public static Region[] getSortedOverlappingRegionArray() {
		 Region[] sora = new Region[sortedOverlappingRegionMap.size()];
		 int i = 0;
		 for (Region r : sortedOverlappingRegionMap.values()){
			 sora[i] = r;
			 i++;
		 }
		 return sora;
	 }
	
	 /**
	 * @return sra the sortedRegionArray
	 */
	public static Region[] getSortedRegionArray() {
		 Region[] sra = new Region[sortedRegionMap.size()];
		 int i = 0;
		 for (Region r : sortedRegionMap.values()){
			 sra[i] = r;
			 i++;
		 }
		 return sra;
	 }
}