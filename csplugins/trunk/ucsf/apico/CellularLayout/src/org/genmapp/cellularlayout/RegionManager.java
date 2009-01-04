package org.genmapp.cellularlayout;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class maintains a hashmap of regions by attribute value and methods
 * that operate over all regions.
 *
 */
public class RegionManager {
	
	public static HashMap<String, Region> regionAttMap = new HashMap<String, Region>();

	public static void addRegion(String attValue, Region region){
		regionAttMap.put(attValue, region);
	}
	
	public static Region getRegionByAtt(String attValue){
		return regionAttMap.get(attValue);
	}
	
	public static Collection<Region> getAllRegions(){
		return regionAttMap.values();
	}
	
	public static void clearRegionAttMap(){
		regionAttMap.clear();
	}
	
	/**
	 * returns the largest Width value among all regions with 'fillWidth' set to true.
	 * 
	 * @return maxFillWidth the maximum width among full width regions
	 */
	public static double getMaxFillWidth(){
		double maxFillWidth = 0.0d;
		
		Collection<Region> allRegions = getAllRegions();
		for (Region r: allRegions){
			if (r.isFillWidth()) {
				if (r.getWidth() > maxFillWidth)
					maxFillWidth = r.getWidth();
			}
		}
		return maxFillWidth;
	}
	/**
	 * returns the largest Height value among all regions with 'fillHeight' set to true.
	 * 
	 * @return maxFillHeight the maximum Height among full Height regions
	 */
	public static double getMaxFillHeight(){
		double maxFillHeight = 0.0d;
		
		Collection<Region> allRegions = getAllRegions();
		for (Region r: allRegions){
			if (r.isFillHeight()) {
				if (r.getHeight() > maxFillHeight)
					maxFillHeight = r.getHeight();
			}
		}
		return maxFillHeight;
	}
}