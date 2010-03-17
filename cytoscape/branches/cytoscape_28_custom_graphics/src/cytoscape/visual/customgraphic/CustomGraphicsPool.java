package cytoscape.visual.customgraphic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomGraphicsPool {
	
	private static final Map<String, CyCustomGraphics<?>> graphicsMap = new HashMap<String, CyCustomGraphics<?>>();
	
	
	public static void addGraphics(String id, CyCustomGraphics<?> graphics) {
		graphicsMap.put(id, graphics);
	}
	
	public static void removeGraphics(String id) {
		graphicsMap.remove(id);
	}
	
	public static CyCustomGraphics<?> get(String id) {
		return graphicsMap.get(id);
	}
	
	public static Collection<CyCustomGraphics<?>> getAll() {
		return graphicsMap.values();
	}
	
	public static Collection<String> getNames() {
		return graphicsMap.keySet();
	}
	
}
