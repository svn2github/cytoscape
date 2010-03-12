package cytoscape.visual.customgraphic;

import java.util.Collection;

public interface CyCustomGraphics <T> {
	public String getDisplayName();
	
	public Collection<T> getCustomGraphics();
}
