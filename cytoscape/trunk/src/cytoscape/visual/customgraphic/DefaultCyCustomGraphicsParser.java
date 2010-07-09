package cytoscape.visual.customgraphic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cytoscape.Cytoscape;

public class DefaultCyCustomGraphicsParser implements CyCustomGraphicsParser {

	public CyCustomGraphics getInstance(String entry) {
		// Check this is URL or not
		if (entry == null)
			return null;

		String[] parts = entry.split(",");
		if (parts == null || parts.length < 3)
			return null;
		
		final String className = parts[0];
		final Long id = Long.parseLong(parts[1]);
		final String name = parts[2];

//		CyCustomGraphics cg = Cytoscape.getVisualMappingManager()
//				.getCustomGraphicsManager().getByID(id);
		
		
		CyCustomGraphics cg = null;
		if(cg == null) {
			// Create new one by reflection
			try {
				final Class<?> cls = Class.forName(className);
				final Constructor<?> ct = cls.getConstructor(Long.class, String.class);
				cg = (CyCustomGraphics) ct.newInstance(id, name);
				cg.setDisplayName(parts[2]);
				Cytoscape.getVisualMappingManager()
				.getCustomGraphicsManager().addGraphics(cg, null);
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
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return cg;
	}

	public Class<? extends CyCustomGraphics> getTargetClass() {
		return null;
	}

	public String getVizMapPropsString(CyCustomGraphics customGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

}
