package cytoscape.visual.mappings;

import java.awt.Component;
import java.util.Collection;
import java.util.Map;

public interface CustomMapping<V> extends ObjectMapping<V> {
	
	public Component getEditor();
	
	public Collection<String> getControllingAttributeNames();
	
	/**
	 * 
	 * @param attributeValues - Key-value pair: attribute name to its value
	 * 
	 * @return
	 */
	public V calculateRangeValue(final Map<String, ?> attributeValues);
	
	
}
