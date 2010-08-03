package cytoscape.visual.mappings;

import java.awt.Component;

/**
 * User-definable visual mapping.
 * 
 * @author kono
 *
 * @param <V>
 */
public interface CustomMapping {
	
	/**
	 * Returns GUI component to edit detail of this mapping.
	 * 
	 * @return - Component: GUI for editing this mapping.
	 */
	public Component getMappingEditor();
	
}
