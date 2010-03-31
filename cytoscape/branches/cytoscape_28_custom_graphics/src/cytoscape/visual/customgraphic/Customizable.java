package cytoscape.visual.customgraphic;

import java.awt.Component;

public interface Customizable<T> {
	public Component getCustomizer();
	
	public void customize(final Object context);
}
