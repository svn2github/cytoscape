package cytoscape.visual.customgraphic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomGraphicsPool {

	private static final CustomGraphicsPool pool;
	
	static {
		pool = new CustomGraphicsPool();
		
		// Just for texting
		pool.addGraphics("Image 1", new URLImageCustomGraphics("http://icons2.iconarchive.com/icons/conor-egan-wylie/iphone/128/Finder-icon.png"));
		pool.addGraphics("Image 2", new URLImageCustomGraphics("http://www.kegg.jp/Fig/compound/C00221.gif"));
		pool.addGraphics("Image 3", new URLImageCustomGraphics("https://s3.amazonaws.com/twitter_production/profile_images/419770006/cat1.jpg"));
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
