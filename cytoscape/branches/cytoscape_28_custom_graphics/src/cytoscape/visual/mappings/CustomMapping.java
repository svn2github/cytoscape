package cytoscape.visual.mappings;

import java.awt.Component;
import java.util.Collection;
import java.util.Map;

public interface CustomMapping<V> {
	
	public Component getEditor();
	
	public Collection<String> getControllingAttributeNames();

	public V calculateRangeValueFromMultipleAttr(final Map<String, ?> attributeValues);	
}
