package org.cytoscape.vizmap;

public class VMMFactory {

	private static VisualMappingManager vmm; 
	
	public static VisualMappingManager getVisualMappingManager() {
		if ( vmm == null )
			vmm = new VisualMappingManagerImpl();

		return vmm; 
	}
}
