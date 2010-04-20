package cytoscape.visual.customgraphic.experimental;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.mappings.CustomGraphicsRangeValueRenderer;

public class GoogleChartRenderer<T extends Collection<?>> implements CustomGraphicsRangeValueRenderer<T> {

	private final Class<T> type;
	
	private final Map<T, CyCustomGraphics<?>> graphicsMap;
	
//	private static final String url = "http://chart.apis.google.com/chart?cht=ls&chs=300x300&chds=0,1,0,1&chxr=0,0,1&chma=5,5,5,5&" +
//	"chd=t:" + dataString + "&chdl=wt|mutant&chco=FF0000,00FF00&chxt=y&chtt=" + node.getIdentifier();
//	
	public GoogleChartRenderer(final Class<T> compatibleDataType) {
		this.type = compatibleDataType;
		graphicsMap = new HashMap<T, CyCustomGraphics<?>>();
	}
	
	
	@Override
	public CyCustomGraphics<?> create(T value) {
		for(Object v: value) {
			
		}
		return null;
	}
	
	public void flush() {
		
	}
	
	

}
