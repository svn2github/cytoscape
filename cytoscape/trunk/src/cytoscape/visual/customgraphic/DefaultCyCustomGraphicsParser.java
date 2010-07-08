package cytoscape.visual.customgraphic;

import cytoscape.Cytoscape;

public class DefaultCyCustomGraphicsParser implements CyCustomGraphicsParser {

	public CyCustomGraphics<?> getInstance(String entry) {
		// Check this is URL or not
		if (entry == null)
			return null;

		String[] parts = entry.split(",");
		if (parts == null || parts.length < 3)
			return null;
		
		final String className = parts[0];
		final Long id = Long.parseLong(parts[1]);

		CyCustomGraphics<?> cg = Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool().getByID(id);
		
		if(cg == null) {
			// Create new one by reflection
			try {
				final Class<?> cls = Class.forName(className);
				cg = (CyCustomGraphics<?>) cls.newInstance();
				cg.setDisplayName(parts[2]);
				Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool().addGraphics(cg, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		return cg;
	}

	public Class<? extends CyCustomGraphics<?>> getTargetClass() {
		return null;
	}

	public String getVizMapPropsString(CyCustomGraphics<?> customGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

}
