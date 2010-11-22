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

		CyCustomGraphics cg = null;

		// Create new one by reflection
		try {
			final Class<?> cls = Class.forName(className);
			final Constructor<?> ct = cls.getConstructor(Long.class,
					String.class);
			cg = (CyCustomGraphics) ct.newInstance(id, name);
			cg.setDisplayName(parts[2]);
			Cytoscape.getVisualMappingManager().getCustomGraphicsManager()
					.addGraphics(cg, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		return cg;
	}

	public Class<? extends CyCustomGraphics> getTargetClass() {
		return null;
	}

}
