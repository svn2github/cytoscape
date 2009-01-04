package org.genmapp.cellularlayout;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class maintains a hashmap of regions by attribute value and methods that
 * operate over all regions.
 * 
 */
public class RegionManager {

	public static HashMap<String, Region> regionAttMap = new HashMap<String, Region>();

	public static void addRegion(String attValue, Region region) {
		regionAttMap.put(attValue, region);
	}

	public static Region getRegionByAtt(String attValue) {
		return regionAttMap.get(attValue);
	}

	public static Collection<Region> getAllRegions() {
		return regionAttMap.values();
	}

	public static void clearRegionAttMap() {
		regionAttMap.clear();
	}


}