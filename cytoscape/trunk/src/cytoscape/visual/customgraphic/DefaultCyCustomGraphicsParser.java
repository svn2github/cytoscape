package cytoscape.visual.customgraphic;

import cytoscape.Cytoscape;

public class DefaultCyCustomGraphicsParser implements CyCustomGraphicsParser {

	@Override
	public CyCustomGraphics<?> getInstance(String entry) {
		// Check this is URL or not
		if (entry == null)
			return null;

		String[] parts = entry.split(",");
		if (parts == null || parts.length < 3)
			return null;
		
		final String className = parts[0];

		CyCustomGraphics<?> cg = Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool().getByID(Integer.parseInt(parts[1]));
		
		if(cg == null) {
			// Create new one by reflection
			try {
				final Class<?> cls = Class.forName(className);
				cg = (CyCustomGraphics<?>) cls.newInstance();
				cg.setDisplayName(parts[2]);
				Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool().addGraphics(cg.hashCode(), cg, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return cg;
	}

	@Override
	public Class<? extends CyCustomGraphics<?>> getTargetClass() {
		return null;
	}

	@Override
	public String getVizMapPropsString(CyCustomGraphics<?> customGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

}
