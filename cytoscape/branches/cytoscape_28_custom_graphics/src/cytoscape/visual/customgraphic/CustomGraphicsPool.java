package cytoscape.visual.customgraphic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomGraphicsPool {

	private static final CustomGraphicsPool pool;
	
	static {
		pool = new CustomGraphicsPool();
		
		// Just for texting
		pool.addGraphics("exp Mapper", new BarChartCustomGraphics("gal4RGexp"));
		pool.addGraphics("degree Mapper", new DegreeCircleCustomGraphics("Degree"));
		pool.addGraphics("Image 1", new URLImageCustomGraphics("http://icons2.iconarchive.com/icons/conor-egan-wylie/iphone/128/Finder-icon.png"));
		pool.addGraphics("Image 2", new URLImageCustomGraphics("http://www.kegg.jp/Fig/compound/C00221.gif"));
	}
	
	public static CustomGraphicsPool getPool() {
		return pool;
	}
	
	private final Map<String, CyCustomGraphics> graphicsMap;
	
	public CustomGraphicsPool() {
		graphicsMap = new HashMap<String, CyCustomGraphics>();
	}
	
	public void addGraphics(String id, CyCustomGraphics graphics) {
		graphicsMap.put(id, graphics);
	}
	
	public void removeGraphics(String id) {
		graphicsMap.remove(id);
	}
	
	public CyCustomGraphics get(String id) {
		return graphicsMap.get(id);
	}
	
	public Collection<CyCustomGraphics> getAll() {
		return graphicsMap.values();
	}
	
	public Collection<String> getNames() {
		return graphicsMap.keySet();
	}
	
}
