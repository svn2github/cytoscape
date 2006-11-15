package cytoscape.bubbleRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cytoscape.view.CyNetworkView;

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
//		System.out.println("Index of region added: "
//				+ regionList.indexOf(region));
		regionViewMap.put(view, regionList);
	}

	 //APico 9.16.06
	//TODO: use this to delete regions
	 public static void removeRegionForView(CyNetworkView view, Integer index)
	 {
	 List regionList = (List) regionViewMap.get(view);
	 if (regionList == null){
	 regionList = new ArrayList();
	 }
	 regionList.remove(index);
	 regionViewMap.put(view, regionList);
	 }
	
	public static void putAttributeForView(CyNetworkView view, Object attribute) {
		attributeViewMap.put(view, attribute);
	}

}
